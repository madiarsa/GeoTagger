package horizon.geotagger.view;

import horizon.android.logging.Logger;
import horizon.perscon.IPersconService;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class PersconServiceConnection
implements ServiceConnection
{
	private static final Object serviceRegSync = new Object();
	
	private static Logger logger = Logger.getLogger(PersconServiceConnection.class);
	
	private IPersconService persconService;

	@Override
	public void onServiceConnected(ComponentName name, IBinder service)
	{
		logger.debug("Connected to the perscon service");
		synchronized(serviceRegSync)
		{
    		persconService = IPersconService.Stub.asInterface(service);
    		serviceRegSync.notify();
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		logger.debug("Disconnected from the perscon service");
		synchronized(serviceRegSync)
		{
			persconService = null;
			serviceRegSync.notifyAll();
		}
	}
	
	public IPersconService getPersconService()
	{
		synchronized(serviceRegSync)
		{
			while(persconService == null)
				try { serviceRegSync.wait(); }
				catch (InterruptedException e)
				{
					logger.warn("Interrupted whilst waiting for the person service to be connected", e);
				}
			return persconService;
		}
	}
}
