package horizon.geotagger.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Place
implements Parcelable
{
	public static final Parcelable.Creator<Place> CREATOR
			= new Parcelable.Creator<Place>()
	{
		@Override
		public Place createFromParcel(Parcel source)
		{
			return new Place().unparcel(source);
		}
		
		@Override
		public Place[] newArray(int size)
		{
			return new Place[size];
		}
	};
	
	private double latitude;
	
	private double longitude;
	
	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
	}
	
	protected Place unparcel(Parcel source)
	{
		latitude = source.readDouble();
		longitude = source.readDouble();
		return this;
	}
}
