package android.media.player.listeners;

import android.media.MediaPlayer;
import android.media.player.external.TimedText;

/**
 * Interface definition of a callback to be invoked when a
 * timed text is available for display.
 * {@hide}
 */
public interface OnTimedTextListener
{
    /**
     * Called to indicate an avaliable timed text
     *
     * @param mp             the MediaPlayer associated with this callback
     * @param text           the timed text sample which contains the text
     *                       needed to be displayed and the display format.
     * {@hide}
     */
    public void onTimedText(MediaPlayer mp, TimedText text);
}