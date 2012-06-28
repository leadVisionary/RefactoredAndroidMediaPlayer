package android.media.player.listeners;

import android.media.MediaPlayer;

/**
 * Interface definition of a callback to be invoked when the
 * video size is first known or updated
 */
public interface OnVideoSizeChangedListener
{
    /**
     * Called to indicate the video size
     *
     * @param mp        the MediaPlayer associated with this callback
     * @param width     the width of the video
     * @param height    the height of the video
     */
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height);
}