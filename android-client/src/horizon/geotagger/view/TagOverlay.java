package horizon.geotagger.view;

import horizon.geotagger.model.Tag;

import java.util.List;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class TagOverlay
extends ItemizedOverlay<OverlayItem>
{
	private final List<Tag> tags;
	
	public TagOverlay(Drawable defaultMarker, List<Tag> tags)
	{
		super(boundCenterBottom(defaultMarker));
		this.tags = tags;
		populate();
	}

	@Override
	protected OverlayItem createItem(int i)
	{
		Tag tag = tags.get(i);
		return new OverlayItem(new GeoPoint(
				(int)(tag.getPlace().getLatitude() * 1E6), 
				(int)(tag.getPlace().getLongitude() * 1E6)),
				"Tag", "Tag");
	}

	@Override
	public int size()
	{
		return tags.size();
	}
}
