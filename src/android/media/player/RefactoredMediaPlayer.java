package android.media.player;

import java.io.IOException;
import java.lang.ref.WeakReference;

import android.media.MediaPlayer;
import android.media.player.external.*;
import android.media.player.listeners.*;


/**
 * 
 * @author Nicholas Vaidyanathan -Lead Visionary of Visionary Software Solutions LLC
 * @see <a href="http://www.nicholasvaidyanathan.info">MY homepage</a>
 * @see <a href="http://www.visionarysoftwaresolutions.com">My software side</a>
 *
 * This is an extensive refactoring of the Android MediaPlayer leveraging concepts from 
 * Working Effectively With Legacy Code, Clean Code, and Domain Driven Design to showcase
 * how we can refactor code to be simpler to read, understand, modify, and use. 
 * 
 * @see <a href="docs/GUIDE">the original user guide</a>
 *
 */
public class RefactoredMediaPlayer implements MediaPlayer
{
    private final static String TAG = "MediaPlayer";
    // Name of the remote interface for the media player. Must be kept
    // in sync with the 2nd parameter of the IMPLEMENT_META_INTERFACE
    // macro invocation in IMediaPlayer.cpp
    private final static String IMEDIA_PLAYER = "android.media.IMediaPlayer";

    private NativeBridge bridge = new NativeBridge(this);
	private SurfaceHolder mSurfaceHolder;
    private EventHandler mEventHandler;
    private PowerManager.WakeLock mWakeLock = null;
    private boolean mScreenOnWhilePlaying;
    private boolean mStayAwake;
    

    /**
     * Default constructor. Consider using one of the create() methods for
     * synchronously instantiating a MediaPlayer from a Uri or resource.
     * <p>When done with the MediaPlayer, you should call  {@link #release()},
     * to free the resources. If not released, too many MediaPlayer instances may
     * result in an exception.</p>
     */
    public RefactoredMediaPlayer() {
    	setupEventHandler();
    }

    private void setupEventHandler() {
    	Looper looper;
    	if ((looper = Looper.myLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else if ((looper = Looper.getMainLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else {
            mEventHandler = null;
        }
	}

    /*
     * Update the MediaPlayer SurfaceTexture.
     * Call after setting a new display surface.
     */
    private native void _setVideoSurface(Surface surface);

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#newRequest()
	 */
    @Override
	public Parcel newRequest() {
        Parcel parcel = Parcel.obtain();
        parcel.writeInterfaceToken(IMEDIA_PLAYER);
        return parcel;
    }

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#invoke(android.media.player.external.Parcel, android.media.player.external.Parcel)
	 */
    @Override
	public int invoke(Parcel request, Parcel reply) {
        int retcode = NativeBridge.getParcelReturnCode(this, request, reply);
        reply.setDataPosition(0);
        return retcode;
    }

	/* (non-Javadoc)
	 * @see android.media.MediaPlayer#setDisplay(android.media.player.external.SurfaceHolder)
	 */
    @Override
	public void setDisplay(SurfaceHolder sh) {
        mSurfaceHolder = sh;
        Surface surface;
        if (sh != null) {
            surface = sh.getSurface();
        } else {
            surface = null;
        }
        _setVideoSurface(surface);
        updateSurfaceScreenOn();
    }

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setSurface(android.media.player.external.Surface)
	 */
    @Override
	public void setSurface(Surface surface) {
        if (mScreenOnWhilePlaying && surface != null) {
            Log.w(TAG, "setScreenOnWhilePlaying(true) is ineffective for Surface");
        }
        mSurfaceHolder = null;
        _setVideoSurface(surface);
        updateSurfaceScreenOn();
    }

    /**
     * Convenience method to create a MediaPlayer for a given Uri.
     * On success, {@link #prepare()} will already have been called and must not be called again.
     * <p>When done with the MediaPlayer, you should call  {@link #release()},
     * to free the resources. If not released, too many MediaPlayer instances will
     * result in an exception.</p>
     *
     * @param context the Context to use
     * @param uri the Uri from which to get the datasource
     * @return a MediaPlayer object, or null if creation failed
     */
    public static MediaPlayer create(Context context, Uri uri) {
        return create (context, uri, null);
    }

    /**
     * Convenience method to create a MediaPlayer for a given Uri.
     * On success, {@link #prepare()} will already have been called and must not be called again.
     * <p>When done with the MediaPlayer, you should call  {@link #release()},
     * to free the resources. If not released, too many MediaPlayer instances will
     * result in an exception.</p>
     *
     * @param context the Context to use
     * @param uri the Uri from which to get the datasource
     * @param holder the SurfaceHolder to use for displaying the video
     * @return a MediaPlayer object, or null if creation failed
     */
    public static MediaPlayer create(Context context, Uri uri, SurfaceHolder holder) {

        try {
            MediaPlayer mp = new RefactoredMediaPlayer();
            mp.setDataSource(context, uri);
            if (holder != null) {
                mp.setDisplay(holder);
            }
            mp.prepare();
            return mp;
        } catch (IOException ex) {
            Log.d(TAG, "create failed:", ex);
            // fall through
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "create failed:", ex);
            // fall through
        } catch (SecurityException ex) {
            Log.d(TAG, "create failed:", ex);
            // fall through
        }

        return null;
    }

    // Note no convenience method to create a MediaPlayer with SurfaceTexture sink.

    /**
     * Convenience method to create a MediaPlayer for a given resource id.
     * On success, {@link #prepare()} will already have been called and must not be called again.
     * <p>When done with the MediaPlayer, you should call  {@link #release()},
     * to free the resources. If not released, too many MediaPlayer instances will
     * result in an exception.</p>
     *
     * @param context the Context to use
     * @param resid the raw resource id (<var>R.raw.&lt;something></var>) for
     *              the resource to use as the datasource
     * @return a MediaPlayer object, or null if creation failed
     */
    public static MediaPlayer create(Context context, int resid) {
        try {
            Resources res = context.getResources(); 
        	AssetFileDescriptor afd = res.openRawResourceFd(resid);
            if (afd == null) return null;

            MediaPlayer mp = new RefactoredMediaPlayer();
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mp.prepare();
            return mp;
        } catch (IOException ex) {
            Log.d(TAG, "create failed:", ex);
            // fall through
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "create failed:", ex);
           // fall through
        } catch (SecurityException ex) {
            Log.d(TAG, "create failed:", ex);
            // fall through
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setDataSource(android.media.player.external.Context, android.media.player.external.Uri)
	 */
    @Override
	public void setDataSource(Context context, Uri uri)
        throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        setDataSource(context, uri, null);
    }

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setDataSource(android.media.player.external.Context, android.media.player.external.Uri, android.media.player.external.Map)
	 */
    @Override
	public void setDataSource(Context context, Uri uri, Map headers)
        throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

        String scheme = uri.getScheme();
        if(scheme == null || scheme.equals("file")) {
            setDataSource(uri.getPath());
            return;
        }

        AssetFileDescriptor fd = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            fd = resolver.openAssetFileDescriptor(uri, "r");
            if (fd == null) {
                return;
            }
            // Note: using getDeclaredLength so that our behavior is the same
            // as previous versions when the content provider is returning
            // a full file.
            if (fd.getDeclaredLength() < 0) {
                setDataSource(fd.getFileDescriptor());
            } else {
                setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getDeclaredLength());
            }
            return;
        } catch (SecurityException ex) {
        } catch (IOException ex) {
        } finally {
            if (fd != null) {
                fd.close();
            }
        }

        Log.d(TAG, "Couldn't open file on client side, trying server side");
        setDataSource(uri.toString(), headers);
        return;
    }

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setDataSource(java.lang.String)
	 */
    @Override
	public native void setDataSource(String path)
            throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setDataSource(java.lang.String, android.media.player.external.Map)
	 */
    @Override
	public void setDataSource(String path, Map headers)
            throws IOException, IllegalArgumentException, SecurityException, IllegalStateException
    {
        String[] keys = null;
        String[] values = null;

        if (headers != null) {
            keys = new String[headers.size()];
            values = new String[headers.size()];

            int i = 0;
            for (Map.Entry<String, String> entry: headers.entrySet()) {
                keys[i] = entry.getKey();
                values[i] = entry.getValue();
                ++i;
            }
        }
        _setDataSource(path, keys, values);
    }

    private native void _setDataSource(
        String path, String[] keys, String[] values)
        throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setDataSource(android.media.player.external.FileDescriptor)
	 */
    @Override
	public void setDataSource(FileDescriptor fd)
            throws IOException, IllegalArgumentException, IllegalStateException {
        // intentionally less than LONG_MAX
        setDataSource(fd, 0, 0x7ffffffffffffffL);
    }

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setDataSource(android.media.player.external.FileDescriptor, long, long)
	 */
    @Override
	public native void setDataSource(FileDescriptor fd, long offset, long length)
            throws IOException, IllegalArgumentException, IllegalStateException;


    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#prepare()
	 */
    @Override
	public native void prepare() throws IOException, IllegalStateException;

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#prepareAsync()
	 */
    @Override
	public native void prepareAsync() throws IllegalStateException;

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#start()
	 */
    @Override
	public  void start() throws IllegalStateException {
        stayAwake(true);
        _start();
    }

    private native void _start() throws IllegalStateException;

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#stop()
	 */
    @Override
	public void stop() throws IllegalStateException {
        stayAwake(false);
        _stop();
    }

    private native void _stop() throws IllegalStateException;

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#pause()
	 */
    @Override
	public void pause() throws IllegalStateException {
        stayAwake(false);
        _pause();
    }

    private native void _pause() throws IllegalStateException;

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setWakeMode(android.media.player.external.Context, int)
	 */
    @Override
	public void setWakeMode(Context context, int mode) {
        boolean washeld = false;
        if (mWakeLock != null) {
            if (mWakeLock.isHeld()) {
                washeld = true;
                mWakeLock.release();
            }
            mWakeLock = null;
        }

        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(mode|PowerManager.ON_AFTER_RELEASE, RefactoredMediaPlayer.class.getName());
        mWakeLock.setReferenceCounted(false);
        if (washeld) {
            mWakeLock.acquire();
        }
    }

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setScreenOnWhilePlaying(boolean)
	 */
    @Override
	public void setScreenOnWhilePlaying(boolean screenOn) {
        if (mScreenOnWhilePlaying != screenOn) {
            if (screenOn && mSurfaceHolder == null) {
                //Log.w(TAG, "setScreenOnWhilePlaying(true) is ineffective without a SurfaceHolder");
            }
            mScreenOnWhilePlaying = screenOn;
            updateSurfaceScreenOn();
        }
    }

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#stayAwake(boolean)
	 */
    @Override
	public void stayAwake(boolean awake) {
        if (mWakeLock != null) {
            if (awake && !mWakeLock.isHeld()) {
                mWakeLock.acquire();
            } else if (!awake && mWakeLock.isHeld()) {
                mWakeLock.release();
            }
        }
        mStayAwake = awake;
        updateSurfaceScreenOn();
    }

    private void updateSurfaceScreenOn() {
        if (mSurfaceHolder != null) {
            mSurfaceHolder.setKeepScreenOn(mScreenOnWhilePlaying && mStayAwake);
        }
    }

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#getVideoWidth()
	 */
    @Override
	public native int getVideoWidth();

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#getVideoHeight()
	 */
    @Override
	public native int getVideoHeight();

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#isPlaying()
	 */
    @Override
	public native boolean isPlaying();

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#seekTo(int)
	 */
    @Override
	public native void seekTo(int msec) throws IllegalStateException;

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#getCurrentPosition()
	 */
    @Override
	public native int getCurrentPosition();

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#getDuration()
	 */
    @Override
	public native int getDuration();

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#getMetadata(boolean, boolean)
	 */
    @Override
	public Metadata getMetadata(final boolean update_only,
                                final boolean apply_filter) {
        Parcel reply = Parcel.obtain();
        Metadata data = Metadata.create();

        if (!native_getMetadata(update_only, apply_filter, reply)) {
            reply.recycle();
            return null;
        }

        // Metadata takes over the parcel, don't recycle it unless
        // there is an error.
        if (!data.parse(reply)) {
            reply.recycle();
            return null;
        }
        return data;
    }

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setMetadataFilter(android.media.player.external.Set, android.media.player.external.Set)
	 */
    @Override
	public int setMetadataFilter(Set allow, Set block) {
        // Do our serialization manually instead of calling
        // Parcel.writeArray since the sets are made of the same type
        // we avoid paying the price of calling writeValue (used by
        // writeArray) which burns an extra int per element to encode
        // the type.
        Parcel request =  newRequest();

        // The parcel starts already with an interface token. There
        // are 2 filters. Each one starts with a 4bytes number to
        // store the len followed by a number of int (4 bytes as well)
        // representing the metadata type.
        int capacity = request.dataSize() + 4 * (1 + allow.size() + 1 + block.size());

        if (request.dataCapacity() < capacity) {
            request.setDataCapacity(capacity);
        }

        request.writeInt(allow.size());
        for(Integer t: allow) {
            request.writeInt(t);
        }
        request.writeInt(block.size());
        for(Integer t: block) {
            request.writeInt(t);
        }
        return native_setMetadataFilter(request);
    }

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#release()
	 */
    @Override
	public void release() {
        stayAwake(false);
        updateSurfaceScreenOn();
        setmOnPreparedListener(null);
        mOnBufferingUpdateListener = null;
        setmOnCompletionListener(null);
        mOnSeekCompleteListener = null;
        mOnErrorListener = null;
        mOnInfoListener = null;
        mOnVideoSizeChangedListener = null;
        setmOnTimedTextListener(null);
        _release();
    }

    private native void _release();

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#reset()
	 */
    @Override
	public void reset() {
        stayAwake(false);
        _reset();
        // make sure none of the listeners get called anymore
        mEventHandler.removeCallbacksAndMessages(null);
    }

    private native void _reset();

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setAudioStreamType(int)
	 */
    @Override
	public native void setAudioStreamType(int streamtype);

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setLooping(boolean)
	 */
    @Override
	public native void setLooping(boolean looping);

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#isLooping()
	 */
    @Override
	public native boolean isLooping();

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setVolume(float, float)
	 */
    @Override
	public native void setVolume(float leftVolume, float rightVolume);

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#getFrameAt(int)
	 */
    @Override
	public native Bitmap getFrameAt(int msec) throws IllegalStateException;

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setAudioSessionId(int)
	 */
    @Override
	public native void setAudioSessionId(int sessionId)  throws IllegalArgumentException, IllegalStateException;

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#getAudioSessionId()
	 */
    @Override
	public native int getAudioSessionId();

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#attachAuxEffect(int)
	 */
    @Override
	public native void attachAuxEffect(int effectId);

    /* Do not change these values (starting with KEY_PARAMETER) without updating
     * their counterparts in include/media/mediaplayer.h!
     */
    /*
     * Key used in setParameter method.
     * Indicates the index of the timed text track to be enabled/disabled.
     * The index includes both the in-band and out-of-band timed text.
     * The index should start from in-band text if any. Application can retrieve the number
     * of in-band text tracks by using MediaMetadataRetriever::extractMetadata().
     * Note it might take a few hundred ms to scan an out-of-band text file
     * before displaying it.
     */
    private static final int KEY_PARAMETER_TIMED_TEXT_TRACK_INDEX = 1000;
    /*
     * Key used in setParameter method.
     * Used to add out-of-band timed text source path.
     * Application can add multiple text sources by calling setParameter() with
     * KEY_PARAMETER_TIMED_TEXT_ADD_OUT_OF_BAND_SOURCE multiple times.
     */
    private static final int KEY_PARAMETER_TIMED_TEXT_ADD_OUT_OF_BAND_SOURCE = 1001;

    // There are currently no defined keys usable from Java with get*Parameter.
    // But if any keys are defined, the order must be kept in sync with include/media/mediaplayer.h.
    // private static final int KEY_PARAMETER_... = ...;

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setParameter(int, android.media.player.external.Parcel)
	 */
    @Override
	public native boolean setParameter(int key, Parcel value);

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setParameter(int, java.lang.String)
	 */
    @Override
	public boolean setParameter(int key, String value) {
        Parcel p = Parcel.obtain();
        p.writeString(value);
        boolean ret = setParameter(key, p);
        p.recycle();
        return ret;
    }

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setParameter(int, int)
	 */
    @Override
	public boolean setParameter(int key, int value) {
        Parcel p = Parcel.obtain();
        p.writeInt(value);
        boolean ret = setParameter(key, p);
        p.recycle();
        return ret;
    }

    /**
     * Gets the value of the parameter indicated by key.
     * @param key key indicates the parameter to get.
     * @param reply value of the parameter to get.
     */
    private native void getParameter(int key, Parcel reply);

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#getParcelParameter(int)
	 */
    @Override
	public Parcel getParcelParameter(int key) {
        Parcel p = Parcel.obtain();
        getParameter(key, p);
        return p;
    }

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#getStringParameter(int)
	 */
    @Override
	public String getStringParameter(int key) {
        Parcel p = Parcel.obtain();
        getParameter(key, p);
        String ret = p.readString();
        p.recycle();
        return ret;
    }

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#getIntParameter(int)
	 */
    @Override
	public int getIntParameter(int key) {
        Parcel p = Parcel.obtain();
        getParameter(key, p);
        int ret = p.readInt();
        p.recycle();
        return ret;
    }

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setAuxEffectSendLevel(float)
	 */
    @Override
	public native void setAuxEffectSendLevel(float level);

    /**
     * @param request Parcel destinated to the media player. The
     *                Interface token must be set to the IMediaPlayer
     *                one to be routed correctly through the system.
     * @param reply[out] Parcel that will contain the reply.
     * @return The status code.
     */
    native final int native_invoke(Parcel request, Parcel reply);


    /**
     * @param update_only If true fetch only the set of metadata that have
     *                    changed since the last invocation of getMetadata.
     *                    The set is built using the unfiltered
     *                    notifications the native player sent to the
     *                    MediaPlayerService during that period of
     *                    time. If false, all the metadatas are considered.
     * @param apply_filter  If true, once the metadata set has been built based on
     *                     the value update_only, the current filter is applied.
     * @param reply[out] On return contains the serialized
     *                   metadata. Valid only if the call was successful.
     * @return The status code.
     */
    private native final boolean native_getMetadata(boolean update_only,
                                                    boolean apply_filter,
                                                    Parcel reply);

    /**
     * @param request Parcel with the 2 serialized lists of allowed
     *                metadata types followed by the one to be
     *                dropped. Each list starts with an integer
     *                indicating the number of metadata type elements.
     * @return The status code.
     */
    private native final int native_setMetadataFilter(Parcel request);

    private static native final void native_init();
    private native final void native_setup(Object mediaplayer_this);
    private native final void native_finalize();

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#enableTimedTextTrackIndex(int)
	 */
    @Override
	public boolean enableTimedTextTrackIndex(int index) {
        if (index < 0) {
            return false;
        }
        return setParameter(KEY_PARAMETER_TIMED_TEXT_TRACK_INDEX, index);
    }

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#enableTimedText()
	 */
    @Override
	public boolean enableTimedText() {
        return enableTimedTextTrackIndex(0);
    }

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#disableTimedText()
	 */
    @Override
	public boolean disableTimedText() {
        return setParameter(KEY_PARAMETER_TIMED_TEXT_TRACK_INDEX, -1);
    }

    /**
     * @param reply Parcel with audio/video duration info for battery
                    tracking usage
     * @return The status code.
     * {@hide}
     */
    public native static int native_pullBatteryData(Parcel reply);

    @Override
    protected void finalize() { native_finalize(); }


    /**
     * Called from native code when an interesting event happens.  This method
     * just uses the EventHandler system to post the event back to the main app thread.
     * We use a weak reference to the original MediaPlayer object so that the native
     * code is safe from the object disappearing from underneath it.  (This is
     * the cookie passed to native_setup().)
     */
    private static void postEventFromNative(Object mediaplayer_ref,
                                            int what, int arg1, int arg2, Object obj)
    {
        RefactoredMediaPlayer mp = (RefactoredMediaPlayer)((WeakReference)mediaplayer_ref).get();
        if (mp == null) {
            return;
        }

        if (mp.mEventHandler != null) {
            Message m = mp.mEventHandler.obtainMessage(what, arg1, arg2, obj);
            mp.mEventHandler.sendMessage(m);
        }
    }

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setOnPreparedListener(android.media.player.listeners.OnPreparedListener)
	 */
    @Override
	public void setOnPreparedListener(OnPreparedListener listener)
    {
        setmOnPreparedListener(listener);
    }

    private void setmOnPreparedListener(OnPreparedListener listener) {
		mOnPreparedListener = listener;
	}

	private OnPreparedListener mOnPreparedListener;

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setOnCompletionListener(android.media.player.listeners.OnCompletionListener)
	 */
    @Override
	public void setOnCompletionListener(OnCompletionListener listener)
    {
        setmOnCompletionListener(listener);
    }

    private void setmOnCompletionListener(OnCompletionListener listener) {
		mOnCompletionListener = listener; 
	}

	private OnCompletionListener mOnCompletionListener;

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setOnBufferingUpdateListener(android.media.player.listeners.OnBufferingUpdateListener)
	 */
    @Override
	public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener)
    {
        mOnBufferingUpdateListener = listener;
    }

    private OnBufferingUpdateListener mOnBufferingUpdateListener;

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setOnSeekCompleteListener(android.media.player.listeners.OnSeekCompleteListener)
	 */
    @Override
	public void setOnSeekCompleteListener(OnSeekCompleteListener listener)
    {
        mOnSeekCompleteListener = listener;
    }

    private OnSeekCompleteListener mOnSeekCompleteListener;

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setOnVideoSizeChangedListener(android.media.player.listeners.OnVideoSizeChangedListener)
	 */
    @Override
	public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener)
    {
        mOnVideoSizeChangedListener = listener;
    }

    private OnVideoSizeChangedListener mOnVideoSizeChangedListener;

    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setOnTimedTextListener(android.media.player.listeners.OnTimedTextListener)
	 */
    @Override
	public void setOnTimedTextListener(OnTimedTextListener listener)
    {
        setmOnTimedTextListener(listener);
    }

    private void setmOnTimedTextListener(OnTimedTextListener listener) {
		mOnTimedTextListener = listener;
	}

	private OnTimedTextListener mOnTimedTextListener;

	/* (non-Javadoc)
	 * @see android.media.MediaPlayer#setOnErrorListener(android.media.player.listeners.OnErrorListener)
	 */
    @Override
	public void setOnErrorListener(OnErrorListener listener)
    {
        mOnErrorListener = listener;
    }

    private OnErrorListener mOnErrorListener;


    
    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#setOnInfoListener(android.media.player.listeners.OnInfoListener)
	 */
    @Override
	public void setOnInfoListener(OnInfoListener listener)
    {
        mOnInfoListener = listener;
    }

    private OnInfoListener mOnInfoListener;
    
    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#logUnhandledEvent()
	 */
    @Override
	public void logUnhandledEvent(){
    	//Log.w(TAG, "mediaplayer went away with unhandled events");
    }
    
    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#hasNoNativeContext()
	 */
    @Override
	public boolean hasNoNativeContext(){
    	return bridge.getmNativeContext() == 0;
    }
    
    /* (non-Javadoc)
	 * @see android.media.MediaPlayer#handleMediaPrepared()
	 */
    @Override
	public void handleMediaPrepared(){
    	if (mOnPreparedListener != null)
        	mOnPreparedListener.onPrepared(this);
    }

	/* (non-Javadoc)
	 * @see android.media.MediaPlayer#handlePlaybackComplete()
	 */
	@Override
	public void handlePlaybackComplete() {
		if (mOnCompletionListener != null)
        	mOnCompletionListener.onCompletion(this);
        stayAwake(false);
	}

	/* (non-Javadoc)
	 * @see android.media.MediaPlayer#handleBufferingUpdate(int)
	 */
	@Override
	public void handleBufferingUpdate(int arg1) {
		if (mOnBufferingUpdateListener != null)
        	mOnBufferingUpdateListener.onBufferingUpdate(this, arg1);
	}

	/* (non-Javadoc)
	 * @see android.media.MediaPlayer#handleSeekComplete()
	 */
	@Override
	public void handleSeekComplete() {
		if (mOnSeekCompleteListener != null)
      	  mOnSeekCompleteListener.onSeekComplete(this);
		
	}

	/* (non-Javadoc)
	 * @see android.media.MediaPlayer#handleSetVideoSize(int, int)
	 */
	@Override
	public void handleSetVideoSize(int arg1, int arg2) {
		if (mOnVideoSizeChangedListener != null)
      	  mOnVideoSizeChangedListener.onVideoSizeChanged(this, arg1, arg2);
		
	}

	/* (non-Javadoc)
	 * @see android.media.MediaPlayer#handleError(int, int)
	 */
	@Override
	public void handleError(int arg1, int arg2) {
		// For PV specific error values (msg.arg2) look in
        // opencore/pvmi/pvmf/include/pvmf_return_codes.h
        //Log.e(TAG, "Error (" + arg1 + "," + arg2 + ")");
        boolean error_was_handled = false;
        if (mOnErrorListener != null) {
            error_was_handled = mOnErrorListener.onError(this, arg1, arg2);
        }
        if (mOnCompletionListener != null && ! error_was_handled) {
        	mOnCompletionListener.onCompletion(this);
        }
        stayAwake(false);
    }

	/* (non-Javadoc)
	 * @see android.media.MediaPlayer#handleMediaInfo(int, int)
	 */
	@Override
	public void handleMediaInfo(int arg1, int arg2) {
		if (arg1 != OnInfoListener.MEDIA_INFO_VIDEO_TRACK_LAGGING) {
            //Log.i(TAG, "Info (" + arg1 + "," + arg2 + ")");
        }
        if (mOnInfoListener != null) {
        	mOnInfoListener.onInfo(this, arg1, arg2);
        }
		
	}

	/* (non-Javadoc)
	 * @see android.media.MediaPlayer#handleTimedText(java.lang.Object)
	 */
	@Override
	public void handleTimedText(Object object) {
		if (mOnTimedTextListener != null) {
            if (object == null) {
            	mOnTimedTextListener.onTimedText(this, null);
            } else {
                if (object instanceof byte[]) {
                    //TimedText text = new TimedText((byte[])(msg.getObject()));
                	//TimedText text = TimedText.create((byte[])(object));
                    //mOnTimedTextListener.onTimedText(this, text);
                }
            }
        }
		
	}

	/* (non-Javadoc)
	 * @see android.media.MediaPlayer#handleUnknown(int)
	 */
	@Override
	public void handleUnknown(int what) {
		//Log.e(TAG, "Unknown message type " + what);
	}
    
	
}
