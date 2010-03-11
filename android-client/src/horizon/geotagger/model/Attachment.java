package horizon.geotagger.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Attachment
implements Parcelable
{
	public static enum Type { Text, Image; }
	
	public static final Parcelable.Creator<Attachment> CREATOR
			= new Parcelable.Creator<Attachment>()
	{
		@Override
		public Attachment createFromParcel(Parcel source)
		{
			return new Attachment().unparcel(source);
		}
		
		@Override
		public Attachment[] newArray(int size)
		{
			return new Attachment[size];
		}
	};
	
	private Type type;
	
	private byte[] data;
	
	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public byte[] getData()
	{
		return data;
	}

	public void setData(byte[] data)
	{
		this.data = data;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(type.name());
		dest.writeByteArray(data);
	}

	protected Attachment unparcel(Parcel source)
	{
		type = Type.valueOf(source.readString());
		data = source.createByteArray();
		return this;	
	}
}
