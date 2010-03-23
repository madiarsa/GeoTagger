package horizon.geotagger;

import android.app.Application;
import android.content.Intent;

public class GeoTagger
extends Application 
{	
	@Override
	public void onCreate()
	{
		super.onCreate();
		startService(new Intent(getApplicationContext(), AlertService.class));
	}
}
