package horizon.geotagger.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Tag
implements Parcelable
{
	public static final Parcelable.Creator<Tag> CREATOR
			= new Parcelable.Creator<Tag>()
	{
		@Override
		public Tag createFromParcel(Parcel source)
		{
			return new Tag().unparcel(source);
		}

		@Override
		public Tag[] newArray(int size)
		{
			return new Tag[size];
		}
	};
	
	private Attachment attachment;
	
	private Place place;
	
	public Attachment getAttachment()
	{
		return attachment;
	}
	
	public void setAttachment(Attachment attachment)
	{
		this.attachment = attachment;
	}
	
	public Place getPlace()
	{
		return place;
	}
	
	public void setPlace(Place place)
	{
		this.place = place;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeParcelable(attachment, flags);
		dest.writeParcelable(place, flags);
	}
	
	protected Tag unparcel(Parcel source)
	{
		attachment = source.readParcelable(this.getClass().getClassLoader());
		place = source.readParcelable(this.getClass().getClassLoader());
		return this;
	}
}
