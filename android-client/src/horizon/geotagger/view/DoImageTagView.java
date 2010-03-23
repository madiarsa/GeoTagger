package horizon.geotagger.view;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class DoImageTagView
extends Activity
{
	private static final Object serviceRegSync = new Object();
	
	private static Logger logger = Logger.getLogger(DoImageTagView.class);
	
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
		setContentView(R.layout.doimagetagview);
		
		final Camera camera = Camera.open();
		Camera.Parameters cp = camera.getParameters();
		//cp.setPictureSize(320, 240);
		cp.setPreviewSize(1024, 768);
		cp.setPictureSize(1024, 768);
		camera.setParameters(cp);
		
		final Location location = getIntent().getParcelableExtra("location");
		Button doButton = (Button)findViewById(R.id.DoImageTagButton);
		doButton.setOnClickListener(new View.OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				camera.takePicture(null, null, new Camera.PictureCallback() 
				{
					@Override
					public void onPictureTaken(final byte[] data, Camera camera) 
					{
						new Thread(new Runnable()
						{
							@Override
							public void run()
							{
								store(location, data);
								DoImageTagView.this.finish();
							}
						}).start();
					}
				});
			}
		});
		Button cancelButton = (Button)findViewById(R.id.CancelImageTagButton);
		cancelButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				DoImageTagView.this.finish();
			}
		});
        
        LinearLayout ll = (LinearLayout)findViewById(R.id.CameraPreviewHolder);
        CameraSurfaceView cview = new CameraSurfaceView(camera, this);
        ll.addView(cview);
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
	
	private void store(Location location, byte[] data)
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
		byte[] bb = new byte[1];
		bb[0] = 0;
		a.setBody(bb);
		a.setMimeType("image/jpeg");
		a.setPermissions(new Integer(1));
				
		Thing thing = new Thing();
		thing.setOrigin("GeoTagger");
		thing.setPermissions(new Integer(1));
		thing.setAttachments(new Attachment[] { a } );
		
		String filename = "/sdcard/image" + System.currentTimeMillis() + ".jpg";
		try
		{
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename));
			out.write(data);
			out.close();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		
		try
		{
			thing.setMeta("{\"mime\":\"image/jpeg\", \"filename\":\"" + filename + "\"}");
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
			persconService.add(
					getResources().getString(R.string.application_id), person, place, thing, pm);
			logger.debug("Tag stored successfully");
		}
		catch(RemoteException e)
		{
			logger.error("Unable to store tag", e);
			Toast.makeText(this, "An error occurred whilst saving your tag", Toast.LENGTH_SHORT).show();
		}
	}
}
