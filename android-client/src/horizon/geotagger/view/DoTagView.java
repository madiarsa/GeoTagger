package horizon.geotagger.view;

import horizon.android.logging.Logger;
import horizon.geotagger.R;
import horizon.perscon.IPersconService;
import horizon.perscon.model.Attachment;
import horizon.perscon.model.Person;
import horizon.perscon.model.Place;
import horizon.perscon.model.PrivacyMask;
import horizon.perscon.model.Thing;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.RemoteException;
import android.widget.Toast;

public abstract class DoTagView
extends Activity
{
	public static final String PARCELED_LOCATION_KEY = "location";
		
	private static Logger logger = Logger.getLogger(DoImageTagView.class);
	
	private PersconServiceConnection serviceConnection = new PersconServiceConnection();
	
	@Override
	protected final void onResume()
	{
		super.onResume();
		Intent i = new Intent(IPersconService.class.getName());
		startService(i);
		bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
		onResumeInternal();
	}
	
	protected void onResumeInternal() { }
	
	@Override
	protected final void onPause()
	{
		super.onPause();
		unbindService(serviceConnection);
		onPauseInternal();
	}
	
	protected void onPauseInternal() { }
	
	protected final Location getTagLocation()
    {
    	return getIntent().getParcelableExtra(PARCELED_LOCATION_KEY);
    }
	
	protected final IPersconService getPersconService()
	{
		return serviceConnection.getPersconService();
	}
	
	private void store(String mimeType, byte[] data)
	{
		IPersconService persconService = getPersconService();
		
		Resources r = getResources();
		
		try
		{
			
			persconService.registerApplication(
					r.getString(R.string.application_id),
					r.getString(R.string.application_name),
					r.getString(R.string.application_version));
		}
		catch(RemoteException e)
		{
			logger.error("Unable to store tag, could not register the application", e);
			Toast.makeText(this, "Unable to register application", Toast.LENGTH_SHORT).show();
		}
		
		Attachment a = new Attachment();
		a.setBody(data);
		a.setMimeType(mimeType);
		a.setPermissions(new Integer(1));
				
		Thing thing = new Thing();
		thing.setOrigin(r.getString(R.string.application_name));
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
		
		Location location = getTagLocation();
		
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
