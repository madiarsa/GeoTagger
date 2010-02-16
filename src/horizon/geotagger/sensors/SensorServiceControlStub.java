package horizon.geotagger.sensors;

import android.os.RemoteException;

public class SensorServiceControlStub
extends SensorServiceControl.Stub
{
	@Override
	public int testMethod()
	throws RemoteException 
	{
		return 0;
	}
}
