package android.media.player;

import android.media.MediaPlayer;
import android.media.player.external.*;

class EventHandler extends Handler
{
    private MediaPlayer mMediaPlayer;
    /* Do not change these values without updating their counterparts
     * in include/media/mediaplayer.h!
     */
    private static final int MEDIA_NOP = 0; // interface test message
    private static final int MEDIA_PREPARED = 1;
    private static final int MEDIA_PLAYBACK_COMPLETE = 2;
    private static final int MEDIA_BUFFERING_UPDATE = 3;
    private static final int MEDIA_SEEK_COMPLETE = 4;
    private static final int MEDIA_SET_VIDEO_SIZE = 5;
    private static final int MEDIA_TIMED_TEXT = 99;
    private static final int MEDIA_ERROR = 100;
    private static final int MEDIA_INFO = 200;



    public EventHandler(MediaPlayer mp, Looper looper) {
        super(looper);
        mMediaPlayer = mp;
    }

    @Override
    public void handleMessage(Message msg) {
        if (mMediaPlayer.hasNoNativeContext()) {
            mMediaPlayer.logUnhandledEvent();
            return;
        }
        switch(msg.getWhat()) {
        case MEDIA_PREPARED:
            mMediaPlayer.handleMediaPrepared();
            return;

        case MEDIA_PLAYBACK_COMPLETE:
            mMediaPlayer.handlePlaybackComplete();
            return;

        case MEDIA_BUFFERING_UPDATE:
            mMediaPlayer.handleBufferingUpdate(msg.getArg1());
            return;

        case MEDIA_SEEK_COMPLETE:
          mMediaPlayer.handleSeekComplete();
          return;

        case MEDIA_SET_VIDEO_SIZE:
          mMediaPlayer.handleSetVideoSize(msg.getArg1(), msg.getArg2());
          return;

        case MEDIA_ERROR:
            mMediaPlayer.handleError(msg.getArg1(), msg.getArg2());
        	return;

        case MEDIA_INFO:
            mMediaPlayer.handleMediaInfo(msg.getArg1(), msg.getArg2());
            // No real default action so far.
            return;
        case MEDIA_TIMED_TEXT:
            mMediaPlayer.handleTimedText(msg.getObject());
            return;

        case MEDIA_NOP: // interface test message - ignore
            break;

        default:
            mMediaPlayer.handleUnknown(msg.getWhat());
            return;
        }
    }
}