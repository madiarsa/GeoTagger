package horizon.geotagger.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;

import horizon.geotagger.AlertService;
import horizon.geotagger.R;
import horizon.geotagger.model.Tag;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MapView 
extends MapActivity
{
	private MyLocationOverlay myLocationOverlay;
	
	private TagOverlay tagOverlay;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.layout.mapview_menu, menu);
		return true;
	}
	
	private Timer timer;
	
	private class GetTask
	extends TimerTask
	{
		@Override
		public void run()
		{
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet("http://horizon-geotagger.appspot.com/tagsInBox?" +
					"north=90&south=-90&west=-180&east=180");
			HttpResponse response;
			try 
			{
				response = client.execute(get);
				StatusLine sl = response.getStatusLine();
				if(sl.getStatusCode() != HttpStatus.SC_OK)
				{
					get.abort();
					return;
				}
				
				HttpEntity entity = response.getEntity();
				if(entity == null)
					return;
								
				ObjectMapper mapper = new ObjectMapper();
				JsonNode node = mapper.readTree(EntityUtils.toString(entity, "UTF-8"));
				ArrayList<Tag> tags = new ArrayList<Tag>();
				
				if(!node.isArray() || node.isNull())
					return;
				
				for(Iterator<JsonNode> i = node.getElements(); i.hasNext(); )
					tags.add(mapper.treeToValue(i.next(), Tag.class));
				
				if(tags.size() == 0)
					return;
			
				com.google.android.maps.MapView mv = 
					(com.google.android.maps.MapView)findViewById(R.id.mapview);
				synchronized(mv.getOverlays())
				{
					if(tagOverlay != null)
						mv.getOverlays().remove(tagOverlay);
					tagOverlay = new TagOverlay(
							new BitmapDrawable(BitmapFactory.decodeResource(MapView.this.getResources(), R.drawable.icon)),
							tags);
					mv.getOverlays().add(tagOverlay);
				}
				mv.postInvalidate();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}	
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == R.id.mapview_menu_exit)
		{
			exit();
			return true;
		}
		
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
			Intent i = new Intent("horizon.geotagger.ACTION_DO_GEOTAG");
			i.putExtra("location", myLocationOverlay.getLastFix());
			startActivity(i);
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
		synchronized(mv.getOverlays())
		{
			mv.getOverlays().remove(myLocationOverlay);
			if(tagOverlay != null)
				mv.getOverlays().remove(tagOverlay);
		}
		timer.cancel();
		timer = null;
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		if(!isGpsEnabled())
			displayGpsDisabledAlert();
		
		com.google.android.maps.MapView mv = 
				(com.google.android.maps.MapView)findViewById(R.id.mapview);
		synchronized(mv.getOverlays())
		{
			if(myLocationOverlay == null)
				myLocationOverlay = new MyLocationOverlay(this, mv);
			myLocationOverlay.enableMyLocation();
			mv.getOverlays().add(myLocationOverlay);
		}
		mv.postInvalidate();
		
		timer = new Timer(true);
		timer.scheduleAtFixedRate(new GetTask(), 0, 60000);
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
	
	private void exit()
	{
		stopService(new Intent(getApplicationContext(), AlertService.class));
		finish();
	}
}
