package android.media.player.listeners;

import android.media.MediaPlayer;

/**
 * Interface definition of a callback to be invoked to communicate some
 * info and/or warning about the media or its playback.
 */
public interface OnInfoListener
{
	/* Do not change these values without updating their counterparts
     * in include/media/mediaplayer.h!
     */
    /** Unspecified media player info.
     * @see android.media.player.listeners.OnInfoListener
     */
    public static final int MEDIA_INFO_UNKNOWN = 1;

    /** The video is too complex for the decoder: it can't decode frames fast
     *  enough. Possibly only the audio plays fine at this stage.
     * @see android.media.player.listeners.OnInfoListener
     */
    public static final int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700;

    /** MediaPlayer is temporarily pausing playback internally in order to
     * buffer more data.
     * @see android.media.player.listeners.OnInfoListener
     */
    public static final int MEDIA_INFO_BUFFERING_START = 701;

    /** MediaPlayer is resuming playback after filling buffers.
     * @see android.media.player.listeners.OnInfoListener
     */
    public static final int MEDIA_INFO_BUFFERING_END = 702;

    /** Bad interleaving means that a media has been improperly interleaved or
     * not interleaved at all, e.g has all the video samples first then all the
     * audio ones. Video is playing but a lot of disk seeks may be happening.
     * @see android.media.player.listeners.OnInfoListener
     */
    public static final int MEDIA_INFO_BAD_INTERLEAVING = 800;

    /** The media cannot be seeked (e.g live stream)
     * @see android.media.player.listeners.OnInfoListener
     */
    public static final int MEDIA_INFO_NOT_SEEKABLE = 801;

    /** A new set of metadata is available.
     * @see android.media.player.listeners.OnInfoListener
     */
    public static final int MEDIA_INFO_METADATA_UPDATE = 802;

    /**
     * Called to indicate an info or a warning.
     *
     * @param mp      the MediaPlayer the info pertains to.
     * @param what    the type of info or warning.
     * <ul>
     * <li>{@link #MEDIA_INFO_UNKNOWN}
     * <li>{@link #MEDIA_INFO_VIDEO_TRACK_LAGGING}
     * <li>{@link #MEDIA_INFO_BUFFERING_START}
     * <li>{@link #MEDIA_INFO_BUFFERING_END}
     * <li>{@link #MEDIA_INFO_BAD_INTERLEAVING}
     * <li>{@link #MEDIA_INFO_NOT_SEEKABLE}
     * <li>{@link #MEDIA_INFO_METADATA_UPDATE}
     * </ul>
     * @param extra an extra code, specific to the info. Typically
     * implementation dependant.
     * @return True if the method handled the info, false if it didn't.
     * Returning false, or not having an OnErrorListener at all, will
     * cause the info to be discarded.
     */
    boolean onInfo(MediaPlayer mp, int what, int extra);
}