package android.media.player;

import java.lang.ref.WeakReference;

import android.media.MediaPlayer;
import android.media.player.external.Parcel;

public class NativeBridge {
	private MediaPlayer mediaPlayer;
	private int mNativeContext;
	private int mNativeSurfaceTexture;
	private int mListenerContext;

	public NativeBridge(MediaPlayer player) {
		this.mediaPlayer = player;
		initialize();


        /* Native setup requires a weak reference to our object.
         * It's easier to create it here than in C++.
         */
        native_setup(new WeakReference<MediaPlayer>(mediaPlayer));
	}
	
	private static void initialize() {
        System.loadLibrary("media_jni");
        native_init();
    }

	public int getmNativeContext() {
		return mNativeContext;
	}

	public void setmNativeContext(int mNativeContext) {
		this.mNativeContext = mNativeContext;
	}

	public int getmNativeSurfaceTexture() {
		return mNativeSurfaceTexture;
	}

	public void setmNativeSurfaceTexture(int mNativeSurfaceTexture) {
		this.mNativeSurfaceTexture = mNativeSurfaceTexture;
	}

	public int getmListenerContext() {
		return mListenerContext;
	}

	public void setmListenerContext(int mListenerContext) {
		this.mListenerContext = mListenerContext;
	}

	public static int getParcelReturnCode(RefactoredMediaPlayer refactoredMediaPlayer, Parcel request, Parcel reply) {
		int retcode = refactoredMediaPlayer.native_invoke(request, reply);
		return retcode;
	}
}