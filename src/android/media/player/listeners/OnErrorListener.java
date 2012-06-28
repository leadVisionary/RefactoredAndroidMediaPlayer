package android.media.player.listeners;

import android.media.MediaPlayer;

/**
 * Interface definition of a callback to be invoked when there
 * has been an error during an asynchronous operation (other errors
 * will throw exceptions at method call time).
 */
public interface OnErrorListener
{
	/* Do not change these values without updating their counterparts
     * in include/media/mediaplayer.h!
     */
    /** Unspecified media player error.
     * @see android.media.player.listeners.OnErrorListener
     */
    public static final int MEDIA_ERROR_UNKNOWN = 1;

    /** Media server died. In this case, the application must release the
     * MediaPlayer object and instantiate a new one.
     * @see android.media.player.listeners.OnErrorListener
     */
    public static final int MEDIA_ERROR_SERVER_DIED = 100;

    /** The video is streamed and its container is not valid for progressive
     * playback i.e the video's index (e.g moov atom) is not at the start of the
     * file.
     * @see android.media.player.listeners.OnErrorListener
     */
    public static final int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;

    
	/**
     * Called to indicate an error.
     *
     * @param mp      the MediaPlayer the error pertains to
     * @param what    the type of error that has occurred:
     * <ul>
     * <li>{@link #MEDIA_ERROR_UNKNOWN}
     * <li>{@link #MEDIA_ERROR_SERVER_DIED}
     * </ul>
     * @param extra an extra code, specific to the error. Typically
     * implementation dependant.
     * @return True if the method handled the error, false if it didn't.
     * Returning false, or not having an OnErrorListener at all, will
     * cause the OnCompletionListener to be called.
     */
    boolean onError(MediaPlayer mp, int what, int extra);
}