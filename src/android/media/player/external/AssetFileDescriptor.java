package android.media.player.external;

public interface AssetFileDescriptor {

	void close();

	int getDeclaredLength();

	FileDescriptor getFileDescriptor();

	long getStartOffset();

	long getLength();

}
