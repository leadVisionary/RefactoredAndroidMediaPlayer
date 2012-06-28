package android.media.player.external;

public interface PowerManager {

	public interface WakeLock {

		boolean isHeld();

		void release();

		void setReferenceCounted(boolean b);

		void acquire();

	}

	int ON_AFTER_RELEASE = 0;

	WakeLock newWakeLock(int i, String name);

}
