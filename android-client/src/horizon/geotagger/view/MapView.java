package horizon.geotagger.view;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;

import horizon.geotagger.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MapView 
extends MapActivity
{
	private MyLocationOverlay myLocationOverlay;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.layout.mapview_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(!isGpsEnabled())
		{
			displayGpsDisabledAlert();
			return true;
		}
		
		switch (item.getItemId())
		{
		case R.id.mapview_menu_mylocation:
			gotoMyLocation();
			return true;
		case R.id.mapview_menu_newtag:
			return true;
		default:
			return false;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapview);
		com.google.android.maps.MapView mv = 
				(com.google.android.maps.MapView)findViewById(R.id.mapview);
		mv.setBuiltInZoomControls(true);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		myLocationOverlay.disableMyLocation();
		com.google.android.maps.MapView mv = 
				(com.google.android.maps.MapView)findViewById(R.id.mapview);
		mv.getOverlays().remove(myLocationOverlay);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		if(!isGpsEnabled())
			displayGpsDisabledAlert();
		
		com.google.android.maps.MapView mv = 
				(com.google.android.maps.MapView)findViewById(R.id.mapview);
		if(myLocationOverlay == null)
			myLocationOverlay = new MyLocationOverlay(this, mv);
		myLocationOverlay.enableMyLocation();
		mv.getOverlays().add(myLocationOverlay);
		mv.postInvalidate();
	}
	
	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}
	
	private void gotoMyLocation()
	{
		MapController mc = ((com.google.android.maps.MapView)
				findViewById(R.id.mapview)).getController();
		mc.animateTo(myLocationOverlay.getMyLocation());
	}
	
	private void displayGpsDisabledAlert()
	{
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setMessage(R.string.gps_disabled_warning)
				.setCancelable(true)
				.setNegativeButton(R.string.close_gps_disabled_warning, 
						new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();		
					}
				})
				.create()
				.show();			
	}
	
	private boolean isGpsEnabled()
	{
		LocationManager lm = (LocationManager)getSystemService(LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
}
