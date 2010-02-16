package horizon.geotagger.sensors;

import horizon.android.logging.Logger;
import horizon.geotagger.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class SensorServiceConfigurator 
extends Activity
{
	private static Logger logger = Logger.getLogger(SensorServiceConfigurator.class);
	
	private ServiceConnection serviceConnection = new ServiceConnection() 
	{
		@Override
		public void onServiceDisconnected(ComponentName name) 
		{
			logger.verbose("SensorServiceConfigurator.ServiceConnection.onServiceDisconnected()");
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) 
		{
			logger.verbose("SensorServiceConfigurator.ServiceConnection.onServiceConnected()");
		}
	};
	
	private Intent serviceIntent;
		
    @Override
	protected void onDestroy() 
    {
		super.onDestroy();
		logger.verbose("SensorServiceConfigurator.onDestroy()");
	}

	@Override
	protected void onPause() 
	{
		super.onPause();
		logger.verbose("SensorServiceConfigurator.onPause()");
		unbindService(serviceConnection);
	}

	@Override
	protected void onRestart() 
	{
		super.onRestart();
		logger.verbose("SensorServiceConfigurator.onRestart()");
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		logger.verbose("SensorServiceConfigurator.onResume()");
		bindService(serviceIntent, serviceConnection , 0);
	}

	@Override
	protected void onStart() 
	{
		super.onStart();
		logger.verbose("SensorServiceConfigurator.onStart()");
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		logger.verbose("SensorServiceConfigurator.onStop()");
	}

	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_service_configurator);
        logger.verbose("SensorServiceConfigurator.onCreate()");
        serviceIntent = new Intent(this, SensorService.class);
        startService(serviceIntent);
    }
}