package android.media.player.listeners;

import android.media.MediaPlayer;

/**
 * Interface definition of a callback to be invoked indicating
 * the completion of a seek operation.
 */
public interface OnSeekCompleteListener
{
    /**
     * Called to indicate the completion of a seek operation.
     *
     * @param mp the MediaPlayer that issued the seek operation
     */
    public void onSeekComplete(MediaPlayer mp);
}