package horizon.geotagger;

import horizon.android.logging.Logger;
import horizon.perscon.IPersconService;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.IBinder;
import android.os.RemoteException;

public class GeoTagger
extends Application 
{	
	private static final Logger logger = Logger.getLogger(GeoTagger.class);
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		startService(new Intent(getApplicationContext(), AlertService.class));
		Intent i = new Intent(IPersconService.class.getName());
		startService(i);
		bindService(i, new ServiceConnection()
		{	
			@Override
			public void onServiceDisconnected(ComponentName name)
			{
				logger.debug("Application registered and service unbound");
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service)
			{
				logger.debug("Registering application");
				Resources r = getResources();
				try
				{
					IPersconService perscon = IPersconService.Stub.asInterface(service);
					perscon.registerApplication(
							r.getString(R.string.application_id),
							r.getString(R.string.application_name),
							r.getString(R.string.application_version));
				}
				catch(RemoteException e)
				{
					logger.error("Unable to register the application", e);
					throw new RuntimeException(e);
				}
				unbindService(this);
			}
		}, Context.BIND_AUTO_CREATE);
	}
}
