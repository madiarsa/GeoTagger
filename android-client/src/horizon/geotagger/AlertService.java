package horizon.geotagger;

import horizon.android.logging.Logger;
import horizon.geotagger.model.Tag;
import horizon.geotagger.view.TagView;

import java.util.ArrayList;
import java.util.Iterator;

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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

public class AlertService
extends Service
implements Runnable
{
	private static final Logger logger = Logger.getLogger(AlertService.class);
	
	private static final int LOCATION_UPDATE_WHAT = 5601;
	
	private static final int NOTIFICATION_ID = 1001;
	
	private final Notification notification = new Notification();
	{
		notification.icon = R.drawable.icon;
		notification.defaults = Notification.DEFAULT_VIBRATE;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
	}
	
	private Handler messageHandler;
	
	private LocationManager locationManager;
	
	private NotificationManager notificationManager;
	
	// N.B. Part of a very naive thread signalling mechanism
	private boolean processing = false;
		
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);
		new Thread(this).start();
		
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
				
		locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 60000, 1,
				new LocationListener()
				{
					private Location lastLocation;
					
					@Override
					public void onStatusChanged(String provider, int status, Bundle extras) { }
					@Override
					public void onProviderEnabled(String provider) { }
					@Override
					public void onProviderDisabled(String provider) { }
					@Override
					public void onLocationChanged(Location location) 
					{
						// Enforce a minimum 30 second interval between
						// updates or a minimum 5 meter travel distance
						if(lastLocation != null)
						{
							double distance = GeoUtils.distance(
									lastLocation.getLatitude(),
									lastLocation.getLongitude(),
									location.getLatitude(),
									location.getLongitude());
							long time = location.getTime() - lastLocation.getTime();
							if(distance < 5D && time < 30000)
								return;
						}
						
						lastLocation = location;
						Message msg = Message.obtain(
								messageHandler,
								LOCATION_UPDATE_WHAT,
								location);
						messageHandler.dispatchMessage(msg);						
					}
				});	
	}

	@Override
	public void run()
	{
		Looper.prepare();
		
		messageHandler = new Handler()
		{
			@Override
			public void handleMessage(final Message msg)
			{
				if(msg.what != LOCATION_UPDATE_WHAT)
				{
					super.handleMessage(msg);
					return;
				}
				
				if(hasMessages(LOCATION_UPDATE_WHAT))
					return;
				
				if(processing)
					return;
				
				new Thread(new Runnable()
				{	
					@Override
					public void run()
					{
						updateWrapper((Location)msg.obj);						
					}
				}).start();
			}	
		};
		
		Looper.loop();		
	}
	
	private void updateWrapper(Location location)
	{
		processing = true;
		try
		{
			update(location);
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		finally
		{
			processing = false;
		}
	}
	
	private void update(Location location)
	{	
		ArrayList<Tag> tags = getTags(location);
		
		if(tags.size() == 0)
		{
			notificationManager.cancel(NOTIFICATION_ID);
			return;
		}
		
		Intent view = new Intent(this, TagView.class);
		view.putParcelableArrayListExtra(TagView.TAG_LIST, tags);
		PendingIntent content = PendingIntent.getActivity(this, 0, view, PendingIntent.FLAG_UPDATE_CURRENT);
		
		notification.tickerText = "There are " + tags.size() + " geonotes here for you to view"; 
		notification.number = tags.size();
		notification.setLatestEventInfo(
				getApplicationContext(),
				"GeoTagger",
				"There are " + tags.size() + " geonotes here for you to view",
				content);
		
		
		notificationManager.notify(NOTIFICATION_ID, notification);
	}
	
	private ArrayList<Tag> getTags(Location location)
	{
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://horizon-geotagger.appspot.com/tagsAtPoint?latitude=" +
				+ location.getLatitude() 
				+ "&longitude=" + location.getLongitude() 
				+ "&accuracy=" + location.getAccuracy());
		HttpResponse response;
		try 
		{
			response = client.execute(get);
			StatusLine sl = response.getStatusLine();
			if(sl.getStatusCode() != HttpStatus.SC_OK)
			{
				logger.warn("Unable to get tags from cloud: " + sl.getReasonPhrase());
				get.abort();
				return new ArrayList<Tag>();
			}
			
			HttpEntity entity = response.getEntity();
			if(entity == null)
				return new ArrayList<Tag>();
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(EntityUtils.toString(entity, "UTF-8"));
			ArrayList<Tag> tags = new ArrayList<Tag>();
			
			if(!node.isArray() || node.isNull())
				return tags;
			
			for(Iterator<JsonNode> i = node.getElements(); i.hasNext(); )
				tags.add(mapper.treeToValue(i.next(), Tag.class));
			
			return tags;
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			return new ArrayList<Tag>();
		}
	}
}
