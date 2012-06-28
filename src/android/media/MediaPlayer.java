package android.media;

import java.io.IOException;

import android.media.player.external.AudioManager;
import android.media.player.external.Bitmap;
import android.media.player.external.Context;
import android.media.player.external.FileDescriptor;
import android.media.player.external.Map;
import android.media.player.external.Metadata;
import android.media.player.external.Parcel;
import android.media.player.external.Set;
import android.media.player.external.Surface;
import android.media.player.external.SurfaceHolder;
import android.media.player.external.SurfaceTexture;
import android.media.player.external.Uri;
import android.media.player.listeners.OnBufferingUpdateListener;
import android.media.player.listeners.OnCompletionListener;
import android.media.player.listeners.OnErrorListener;
import android.media.player.listeners.OnInfoListener;
import android.media.player.listeners.OnPreparedListener;
import android.media.player.listeners.OnSeekCompleteListener;
import android.media.player.listeners.OnTimedTextListener;
import android.media.player.listeners.OnVideoSizeChangedListener;

public interface MediaPlayer {

	/**
	   Constant to retrieve only the new metadata since the last
	   call.
	   // FIXME: unhide.
	   // FIXME: add link to getMetadata(boolean, boolean)
	   {@hide}
	 */
	public static final boolean METADATA_UPDATE_ONLY = true;
	/**
	   Constant to retrieve all the metadata.
	   // FIXME: unhide.
	   // FIXME: add link to getMetadata(boolean, boolean)
	   {@hide}
	 */
	public static final boolean METADATA_ALL = false;
	/**
	   Constant to enable the metadata filter during retrieval.
	   // FIXME: unhide.
	   // FIXME: add link to getMetadata(boolean, boolean)
	   {@hide}
	 */
	public static final boolean APPLY_METADATA_FILTER = true;
	/**
	   Constant to disable the metadata filter during retrieval.
	   // FIXME: unhide.
	   // FIXME: add link to getMetadata(boolean, boolean)
	   {@hide}
	 */
	public static final boolean BYPASS_METADATA_FILTER = false;

	/**
	 * Create a request parcel which can be routed to the native media
	 * player using {@link #invoke(Parcel, Parcel)}. The Parcel
	 * returned has the proper InterfaceToken set. The caller should
	 * not overwrite that token, i.e it can only append data to the
	 * Parcel.
	 *
	 * @return A parcel suitable to hold a request for the native
	 * player.
	 * {@hide}
	 */
	public abstract Parcel newRequest();

	/**
	 * Invoke a generic method on the native player using opaque
	 * parcels for the request and reply. Both payloads' format is a
	 * convention between the java caller and the native player.
	 * Must be called after setDataSource to make sure a native player
	 * exists.
	 *
	 * @param request Parcel with the data for the extension. The
	 * caller must use {@link #newRequest()} to get one.
	 *
	 * @param reply Output parcel with the data returned by the
	 * native player.
	 *
	 * @return The status code see utils/Errors.h
	 * {@hide}
	 */
	public abstract int invoke(Parcel request, Parcel reply);

	/**
	 * Sets the {@link SurfaceHolder} to use for displaying the video
	 * portion of the media.
	 *
	 * Either a surface holder or surface must be set if a display or video sink
	 * is needed.  Not calling this method or {@link #setSurface(Surface)}
	 * when playing back a video will result in only the audio track being played.
	 * A null surface holder or surface will result in only the audio track being
	 * played.
	 *
	 * @param sh the SurfaceHolder to use for video display
	 */
	public abstract void setDisplay(SurfaceHolder sh);

	/**
	 * Sets the {@link Surface} to be used as the sink for the video portion of
	 * the media. This is similar to {@link #setDisplay(SurfaceHolder)}, but
	 * does not support {@link #setScreenOnWhilePlaying(boolean)}.  Setting a
	 * Surface will un-set any Surface or SurfaceHolder that was previously set.
	 * A null surface will result in only the audio track being played.
	 *
	 * If the Surface sends frames to a {@link SurfaceTexture}, the timestamps
	 * returned from {@link SurfaceTexture#getTimestamp()} will have an
	 * unspecified zero point.  These timestamps cannot be directly compared
	 * between different media sources, different instances of the same media
	 * source, or multiple runs of the same program.  The timestamp is normally
	 * monotonically increasing and is unaffected by time-of-day adjustments,
	 * but it is reset when the position is set.
	 *
	 * @param surface The {@link Surface} to be used for the video portion of
	 * the media.
	 */
	public abstract void setSurface(Surface surface);

	/**
	 * Sets the data source as a content Uri.
	 *
	 * @param context the Context to use when resolving the Uri
	 * @param uri the Content URI of the data you want to play
	 * @throws IllegalStateException if it is called in an invalid state
	 */
	public abstract void setDataSource(Context context, Uri uri)
			throws IOException, IllegalArgumentException, SecurityException,
			IllegalStateException;

	/**
	 * Sets the data source as a content Uri.
	 *
	 * @param context the Context to use when resolving the Uri
	 * @param uri the Content URI of the data you want to play
	 * @param headers the headers to be sent together with the request for the data
	 * @throws IllegalStateException if it is called in an invalid state
	 */
	public abstract void setDataSource(Context context, Uri uri, Map headers)
			throws IOException, IllegalArgumentException, SecurityException,
			IllegalStateException;

	/**
	 * Sets the data source (file-path or http/rtsp URL) to use.
	 *
	 * @param path the path of the file, or the http/rtsp URL of the stream you want to play
	 * @throws IllegalStateException if it is called in an invalid state
	 */
	public abstract void setDataSource(String path) throws IOException,
			IllegalArgumentException, SecurityException, IllegalStateException;

	/**
	 * Sets the data source (file-path or http/rtsp URL) to use.
	 *
	 * @param path the path of the file, or the http/rtsp URL of the stream you want to play
	 * @param headers the headers associated with the http request for the stream you want to play
	 * @throws IllegalStateException if it is called in an invalid state
	 * @hide pending API council
	 */
	public abstract void setDataSource(String path, Map headers)
			throws IOException, IllegalArgumentException, SecurityException,
			IllegalStateException;

	/**
	 * Sets the data source (FileDescriptor) to use. It is the caller's responsibility
	 * to close the file descriptor. It is safe to do so as soon as this call returns.
	 *
	 * @param fd the FileDescriptor for the file you want to play
	 * @throws IllegalStateException if it is called in an invalid state
	 */
	public abstract void setDataSource(FileDescriptor fd) throws IOException,
			IllegalArgumentException, IllegalStateException;

	/**
	 * Sets the data source (FileDescriptor) to use.  The FileDescriptor must be
	 * seekable (N.B. a LocalSocket is not seekable). It is the caller's responsibility
	 * to close the file descriptor. It is safe to do so as soon as this call returns.
	 *
	 * @param fd the FileDescriptor for the file you want to play
	 * @param offset the offset into the file where the data to be played starts, in bytes
	 * @param length the length in bytes of the data to be played
	 * @throws IllegalStateException if it is called in an invalid state
	 */
	public abstract void setDataSource(FileDescriptor fd, long offset,
			long length) throws IOException, IllegalArgumentException,
			IllegalStateException;

	/**
	 * Prepares the player for playback, synchronously.
	 *
	 * After setting the datasource and the display surface, you need to either
	 * call prepare() or prepareAsync(). For files, it is OK to call prepare(),
	 * which blocks until MediaPlayer is ready for playback.
	 *
	 * @throws IllegalStateException if it is called in an invalid state
	 */
	public abstract void prepare() throws IOException, IllegalStateException;

	/**
	 * Prepares the player for playback, asynchronously.
	 *
	 * After setting the datasource and the display surface, you need to either
	 * call prepare() or prepareAsync(). For streams, you should call prepareAsync(),
	 * which returns immediately, rather than blocking until enough data has been
	 * buffered.
	 *
	 * @throws IllegalStateException if it is called in an invalid state
	 */
	public abstract void prepareAsync() throws IllegalStateException;

	/**
	 * Starts or resumes playback. If playback had previously been paused,
	 * playback will continue from where it was paused. If playback had
	 * been stopped, or never started before, playback will start at the
	 * beginning.
	 *
	 * @throws IllegalStateException if it is called in an invalid state
	 */
	public abstract void start() throws IllegalStateException;

	/**
	 * Stops playback after playback has been stopped or paused.
	 *
	 * @throws IllegalStateException if the internal player engine has not been
	 * initialized.
	 */
	public abstract void stop() throws IllegalStateException;

	/**
	 * Pauses playback. Call start() to resume.
	 *
	 * @throws IllegalStateException if the internal player engine has not been
	 * initialized.
	 */
	public abstract void pause() throws IllegalStateException;

	/**
	 * Set the low-level power management behavior for this MediaPlayer.  This
	 * can be used when the MediaPlayer is not playing through a SurfaceHolder
	 * set with {@link #setDisplay(SurfaceHolder)} and thus can use the
	 * high-level {@link #setScreenOnWhilePlaying(boolean)} feature.
	 *
	 * <p>This function has the MediaPlayer access the low-level power manager
	 * service to control the device's power usage while playing is occurring.
	 * The parameter is a combination of {@link android.os.PowerManager} wake flags.
	 * Use of this method requires {@link android.Manifest.permission#WAKE_LOCK}
	 * permission.
	 * By default, no attempt is made to keep the device awake during playback.
	 *
	 * @param context the Context to use
	 * @param mode    the power/wake mode to set
	 * @see android.os.PowerManager
	 */
	public abstract void setWakeMode(Context context, int mode);

	/**
	 * Control whether we should use the attached SurfaceHolder to keep the
	 * screen on while video playback is occurring.  This is the preferred
	 * method over {@link #setWakeMode} where possible, since it doesn't
	 * require that the application have permission for low-level wake lock
	 * access.
	 *
	 * @param screenOn Supply true to keep the screen on, false to allow it
	 * to turn off.
	 */
	public abstract void setScreenOnWhilePlaying(boolean screenOn);

	public abstract void stayAwake(boolean awake);

	/**
	 * Returns the width of the video.
	 *
	 * @return the width of the video, or 0 if there is no video,
	 * no display surface was set, or the width has not been determined
	 * yet. The OnVideoSizeChangedListener can be registered via
	 * {@link #setOnVideoSizeChangedListener(OnVideoSizeChangedListener)}
	 * to provide a notification when the width is available.
	 */
	public abstract int getVideoWidth();

	/**
	 * Returns the height of the video.
	 *
	 * @return the height of the video, or 0 if there is no video,
	 * no display surface was set, or the height has not been determined
	 * yet. The OnVideoSizeChangedListener can be registered via
	 * {@link #setOnVideoSizeChangedListener(OnVideoSizeChangedListener)}
	 * to provide a notification when the height is available.
	 */
	public abstract int getVideoHeight();

	/**
	 * Checks whether the MediaPlayer is playing.
	 *
	 * @return true if currently playing, false otherwise
	 */
	public abstract boolean isPlaying();

	/**
	 * Seeks to specified time position.
	 *
	 * @param msec the offset in milliseconds from the start to seek to
	 * @throws IllegalStateException if the internal player engine has not been
	 * initialized
	 */
	public abstract void seekTo(int msec) throws IllegalStateException;

	/**
	 * Gets the current playback position.
	 *
	 * @return the current position in milliseconds
	 */
	public abstract int getCurrentPosition();

	/**
	 * Gets the duration of the file.
	 *
	 * @return the duration in milliseconds
	 */
	public abstract int getDuration();

	/**
	 * Gets the media metadata.
	 *
	 * @param update_only controls whether the full set of available
	 * metadata is returned or just the set that changed since the
	 * last call. See {@see #METADATA_UPDATE_ONLY} and {@see
	 * #METADATA_ALL}.
	 *
	 * @param apply_filter if true only metadata that matches the
	 * filter is returned. See {@see #APPLY_METADATA_FILTER} and {@see
	 * #BYPASS_METADATA_FILTER}.
	 *
	 * @return The metadata, possibly empty. null if an error occured.
	 // FIXME: unhide.
	 * {@hide}
	 */
	public abstract Metadata getMetadata(final boolean update_only,
			final boolean apply_filter);

	/**
	 * Set a filter for the metadata update notification and update
	 * retrieval. The caller provides 2 set of metadata keys, allowed
	 * and blocked. The blocked set always takes precedence over the
	 * allowed one.
	 * Metadata.MATCH_ALL and Metadata.MATCH_NONE are 2 sets available as
	 * shorthands to allow/block all or no metadata.
	 *
	 * By default, there is no filter set.
	 *
	 * @param allow Is the set of metadata the client is interested
	 *              in receiving new notifications for.
	 * @param block Is the set of metadata the client is not interested
	 *              in receiving new notifications for.
	 * @return The call status code.
	 *
	 // FIXME: unhide.
	 * {@hide}
	 */
	public abstract int setMetadataFilter(Set allow, Set block);

	/**
	 * Releases resources associated with this MediaPlayer object.
	 * It is considered good practice to call this method when you're
	 * done using the MediaPlayer. In particular, whenever an Activity
	 * of an application is paused (its onPause() method is called),
	 * or stopped (its onStop() method is called), this method should be
	 * invoked to release the MediaPlayer object, unless the application
	 * has a special need to keep the object around. In addition to
	 * unnecessary resources (such as memory and instances of codecs)
	 * being held, failure to call this method immediately if a
	 * MediaPlayer object is no longer needed may also lead to
	 * continuous battery consumption for mobile devices, and playback
	 * failure for other applications if no multiple instances of the
	 * same codec are supported on a device. Even if multiple instances
	 * of the same codec are supported, some performance degradation
	 * may be expected when unnecessary multiple instances are used
	 * at the same time.
	 */
	public abstract void release();

	/**
	 * Resets the MediaPlayer to its uninitialized state. After calling
	 * this method, you will have to initialize it again by setting the
	 * data source and calling prepare().
	 */
	public abstract void reset();

	/**
	 * Sets the audio stream type for this MediaPlayer. See {@link AudioManager}
	 * for a list of stream types. Must call this method before prepare() or
	 * prepareAsync() in order for the target stream type to become effective
	 * thereafter.
	 *
	 * @param streamtype the audio stream type
	 * @see android.media.AudioManager
	 */
	public abstract void setAudioStreamType(int streamtype);

	/**
	 * Sets the player to be looping or non-looping.
	 *
	 * @param looping whether to loop or not
	 */
	public abstract void setLooping(boolean looping);

	/**
	 * Checks whether the MediaPlayer is looping or non-looping.
	 *
	 * @return true if the MediaPlayer is currently looping, false otherwise
	 */
	public abstract boolean isLooping();

	/**
	 * Sets the volume on this player.
	 * This API is recommended for balancing the output of audio streams
	 * within an application. Unless you are writing an application to
	 * control user settings, this API should be used in preference to
	 * {@link AudioManager#setStreamVolume(int, int, int)} which sets the volume of ALL streams of
	 * a particular type. Note that the passed volume values are raw scalars.
	 * UI controls should be scaled logarithmically.
	 *
	 * @param leftVolume left volume scalar
	 * @param rightVolume right volume scalar
	 */
	public abstract void setVolume(float leftVolume, float rightVolume);

	/**
	 * Currently not implemented, returns null.
	 * @deprecated
	 * @hide
	 */
	public abstract Bitmap getFrameAt(int msec) throws IllegalStateException;

	/**
	 * Sets the audio session ID.
	 *
	 * @param sessionId the audio session ID.
	 * The audio session ID is a system wide unique identifier for the audio stream played by
	 * this MediaPlayer instance.
	 * The primary use of the audio session ID  is to associate audio effects to a particular
	 * instance of MediaPlayer: if an audio session ID is provided when creating an audio effect,
	 * this effect will be applied only to the audio content of media players within the same
	 * audio session and not to the output mix.
	 * When created, a MediaPlayer instance automatically generates its own audio session ID.
	 * However, it is possible to force this player to be part of an already existing audio session
	 * by calling this method.
	 * This method must be called before one of the overloaded <code> setDataSource </code> methods.
	 * @throws IllegalStateException if it is called in an invalid state
	 */
	public abstract void setAudioSessionId(int sessionId)
			throws IllegalArgumentException, IllegalStateException;

	/**
	 * Returns the audio session ID.
	 *
	 * @return the audio session ID. {@see #setAudioSessionId(int)}
	 * Note that the audio session ID is 0 only if a problem occured when the MediaPlayer was contructed.
	 */
	public abstract int getAudioSessionId();

	/**
	 * Attaches an auxiliary effect to the player. A typical auxiliary effect is a reverberation
	 * effect which can be applied on any sound source that directs a certain amount of its
	 * energy to this effect. This amount is defined by setAuxEffectSendLevel().
	 * {@see #setAuxEffectSendLevel(float)}.
	 * <p>After creating an auxiliary effect (e.g.
	 * {@link android.media.audiofx.EnvironmentalReverb}), retrieve its ID with
	 * {@link android.media.audiofx.AudioEffect#getId()} and use it when calling this method
	 * to attach the player to the effect.
	 * <p>To detach the effect from the player, call this method with a null effect id.
	 * <p>This method must be called after one of the overloaded <code> setDataSource </code>
	 * methods.
	 * @param effectId system wide unique id of the effect to attach
	 */
	public abstract void attachAuxEffect(int effectId);

	/**
	 * Sets the parameter indicated by key.
	 * @param key key indicates the parameter to be set.
	 * @param value value of the parameter to be set.
	 * @return true if the parameter is set successfully, false otherwise
	 * {@hide}
	 */
	public abstract boolean setParameter(int key, Parcel value);

	/**
	 * Sets the parameter indicated by key.
	 * @param key key indicates the parameter to be set.
	 * @param value value of the parameter to be set.
	 * @return true if the parameter is set successfully, false otherwise
	 * {@hide}
	 */
	public abstract boolean setParameter(int key, String value);

	/**
	 * Sets the parameter indicated by key.
	 * @param key key indicates the parameter to be set.
	 * @param value value of the parameter to be set.
	 * @return true if the parameter is set successfully, false otherwise
	 * {@hide}
	 */
	public abstract boolean setParameter(int key, int value);

	/**
	 * Gets the value of the parameter indicated by key.
	 * The caller is responsible for recycling the returned parcel.
	 * @param key key indicates the parameter to get.
	 * @return value of the parameter.
	 * {@hide}
	 */
	public abstract Parcel getParcelParameter(int key);

	/**
	 * Gets the value of the parameter indicated by key.
	 * @param key key indicates the parameter to get.
	 * @return value of the parameter.
	 * {@hide}
	 */
	public abstract String getStringParameter(int key);

	/**
	 * Gets the value of the parameter indicated by key.
	 * @param key key indicates the parameter to get.
	 * @return value of the parameter.
	 * {@hide}
	 */
	public abstract int getIntParameter(int key);

	/**
	 * Sets the send level of the player to the attached auxiliary effect
	 * {@see #attachAuxEffect(int)}. The level value range is 0 to 1.0.
	 * <p>By default the send level is 0, so even if an effect is attached to the player
	 * this method must be called for the effect to be applied.
	 * <p>Note that the passed level value is a raw scalar. UI controls should be scaled
	 * logarithmically: the gain applied by audio framework ranges from -72dB to 0dB,
	 * so an appropriate conversion from linear UI input x to level is:
	 * x == 0 -> level = 0
	 * 0 < x <= R -> level = 10^(72*(x-R)/20/R)
	 * @param level send level scalar
	 */
	public abstract void setAuxEffectSendLevel(float level);

	/**
	 * @param index The index of the text track to be turned on.
	 * @return true if the text track is enabled successfully.
	 * {@hide}
	 */
	public abstract boolean enableTimedTextTrackIndex(int index);

	/**
	 * Enables the first timed text track if any.
	 * @return true if the text track is enabled successfully
	 * {@hide}
	 */
	public abstract boolean enableTimedText();

	/**
	 * Disables timed text display.
	 * @return true if the text track is disabled successfully.
	 * {@hide}
	 */
	public abstract boolean disableTimedText();

	/**
	 * Register a callback to be invoked when the media source is ready
	 * for playback.
	 *
	 * @param listener the callback that will be run
	 */
	public abstract void setOnPreparedListener(OnPreparedListener listener);

	/**
	 * Register a callback to be invoked when the end of a media source
	 * has been reached during playback.
	 *
	 * @param listener the callback that will be run
	 */
	public abstract void setOnCompletionListener(OnCompletionListener listener);

	/**
	 * Register a callback to be invoked when the status of a network
	 * stream's buffer has changed.
	 *
	 * @param listener the callback that will be run.
	 */
	public abstract void setOnBufferingUpdateListener(
			OnBufferingUpdateListener listener);

	/**
	 * Register a callback to be invoked when a seek operation has been
	 * completed.
	 *
	 * @param listener the callback that will be run
	 */
	public abstract void setOnSeekCompleteListener(
			OnSeekCompleteListener listener);

	/**
	 * Register a callback to be invoked when the video size is
	 * known or updated.
	 *
	 * @param listener the callback that will be run
	 */
	public abstract void setOnVideoSizeChangedListener(
			OnVideoSizeChangedListener listener);

	/**
	 * Register a callback to be invoked when a timed text is available
	 * for display.
	 *
	 * @param listener the callback that will be run
	 * {@hide}
	 */
	public abstract void setOnTimedTextListener(OnTimedTextListener listener);

	/**
	 * Register a callback to be invoked when an error has happened
	 * during an asynchronous operation.
	 *
	 * @param listener the callback that will be run
	 */
	public abstract void setOnErrorListener(OnErrorListener listener);

	/**
	 * Register a callback to be invoked when an info/warning is available.
	 *
	 * @param listener the callback that will be run
	 */
	public abstract void setOnInfoListener(OnInfoListener listener);

	public abstract void logUnhandledEvent();

	public abstract boolean hasNoNativeContext();

	public abstract void handleMediaPrepared();

	public abstract void handlePlaybackComplete();

	public abstract void handleBufferingUpdate(int arg1);

	public abstract void handleSeekComplete();

	public abstract void handleSetVideoSize(int arg1, int arg2);

	public abstract void handleError(int arg1, int arg2);

	public abstract void handleMediaInfo(int arg1, int arg2);

	public abstract void handleTimedText(Object object);

	public abstract void handleUnknown(int what);

}