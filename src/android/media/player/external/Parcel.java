package android.media.player.external;

public abstract class Parcel {

	public abstract void writeString(String value);

	public static Parcel obtain(){
		return new DummyParcel();
	}

	public abstract void recycle();

	public abstract void writeInt(int value);

	public abstract String readString();

	public abstract int readInt();

	public abstract int dataSize();

	public abstract int dataCapacity();

	public abstract void setDataCapacity(int capacity);

	public abstract void setDataPosition(int i);

	public abstract void writeInterfaceToken(String imediaPlayer);

}
