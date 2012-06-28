package android.media.player.external;

public interface Context {

	String POWER_SERVICE = null;

	PowerManager getSystemService(String powerService);

	ContentResolver getContentResolver();

	Resources getResources();

}
