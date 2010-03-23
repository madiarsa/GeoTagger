package horizon.geotagger.view;

import java.io.UnsupportedEncodingException;

import org.codehaus.jackson.map.ObjectMapper;

import horizon.android.logging.Logger;
import horizon.geotagger.R;
import horizon.perscon.IPersconService;
import horizon.perscon.model.Attachment;
import horizon.perscon.model.Person;
import horizon.perscon.model.Place;
import horizon.perscon.model.PrivacyMask;
import horizon.perscon.model.Thing;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DoTextTagView
extends Activity
{
	private static final Object serviceRegSync = new Object();
	
	private static Logger logger = Logger.getLogger(DoTextTagView.class);
	
	private IPersconService persconService;
	
	private ServiceConnection serviceConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {
        	logger.debug("Connected to the perscon service");
        	synchronized(serviceRegSync)
			{
        		persconService = IPersconService.Stub.asInterface(service);
        		serviceRegSync.notify();
			}
        }

        public void onServiceDisconnected(ComponentName className)
        {
        	logger.debug("Disconnected from the perscon service");
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dotexttagview);
		
		final Location location = getIntent().getParcelableExtra("location");
		Button doButton = (Button)findViewById(R.id.DoTextTagButton);
		doButton.setOnClickListener(new View.OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						store(location, 
								((EditText)findViewById(R.id.DoTextTagEditText))
								.getText().toString());
						DoTextTagView.this.finish();
					}
				}).start();
			}
		});
		Button cancelButton = (Button)findViewById(R.id.CancelTextTagButton);
		cancelButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				DoTextTagView.this.finish();
			}
		});
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		Intent i = new Intent(IPersconService.class.getName());
		startService(i);
		bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		unbindService(serviceConnection);
	}
	
	private void store(Location location, String text)
	{
		synchronized(serviceRegSync)
		{
			while(persconService == null)
				try { serviceRegSync.wait(); }
				catch (InterruptedException e)
				{
					logger.warn("Interrupted whilst waiting for the person service to be connected", e);
				}
		}
		
		try
		{
			Resources r = getResources();
			persconService.registerApplication(
					r.getString(R.string.application_id),
					r.getString(R.string.application_name),
					r.getString(R.string.application_version));
		}
		catch(RemoteException e)
		{
			logger.error("Unable to store tag", e);
			Toast.makeText(this, "Unable to register application", Toast.LENGTH_SHORT).show();
		}
		
		Attachment a = new Attachment();
		try
		{
			a.setBody(text.getBytes("UTF-8"));
		} 
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
		a.setMimeType("text/plain");
		a.setPermissions(new Integer(1));
				
		Thing thing = new Thing();
		thing.setOrigin("GeoTagger");
		thing.setPermissions(new Integer(1));
		thing.setAttachments(new Attachment[] { a } );
		ObjectMapper mp = new ObjectMapper();
		try
		{
			thing.setMeta("{\"mime\":\"text/plain\", \"body\":" + mp.writeValueAsString(a.getBody()) + "}");
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
		Place place = new Place();
		place.setElevation(location.getAltitude());
		place.setLatitude(location.getLatitude());
		place.setLongitude(location.getLongitude());
		place.setPermissions(new Integer(1));

		Person person = new Person();
		person.setName("Hack");
		person.setPermissions(10);
		
		PrivacyMask pm = new PrivacyMask();
		pm.setField(PrivacyMask.PRIV_PLACE, true);
		pm.setAllPrivate();
		pm.setAllPublic();
		
		try
		{
			Resources r = getResources();
			persconService.add(
					r.getString(R.string.application_id), person, place, thing, pm);
			logger.debug("Tag stored successfully");
		}
		catch(RemoteException e)
		{
			logger.error("Unable to store tag", e);
			Toast.makeText(this, "An error occurred whilst saving your tag", Toast.LENGTH_SHORT).show();
		}
	}
}
