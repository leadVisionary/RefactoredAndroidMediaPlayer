package android.media.player;

import android.media.MediaPlayer;

public interface EventedMediaPlayer extends MediaPlayer {

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
