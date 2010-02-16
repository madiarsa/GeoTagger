package horizon.geotagger.sensors;

import horizon.android.logging.Logger;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

public class SensorService 
extends Service 
{
	private static Logger logger = Logger.getLogger(SensorService.class);
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
		logger.verbose("SensorService.onConfigurationChanged()");
	}

	@Override
	public void onCreate() 
	{
		super.onCreate();
		logger.verbose("SensorService.onCreate()");
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		logger.verbose("SensorService.onDestroy()");
	}

	@Override
	public void onLowMemory() 
	{
		super.onLowMemory();
		logger.verbose("SensorService.onLowMemory()");
	}

	@Override
	public void onRebind(Intent intent) 
	{
		super.onRebind(intent);
		logger.verbose("SensorService.onRebind()");
	}

	@Override
	public void onStart(Intent intent, int startId) 
	{
		super.onStart(intent, startId);
		logger.verbose("SensorService.onStart()");
	}

	@Override
	public boolean onUnbind(Intent intent) 
	{
		logger.verbose("SensorService.onUnbind()");
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent intent) 
	{
		logger.verbose("SensorService.onBind()");
		return new SensorServiceControlStub();
	}	
}
