package android.media.player.external;

public abstract class Metadata {
	public static Metadata create(){
		return new DummyMetadata();
	}
	
	public abstract boolean parse(Parcel reply);

}
