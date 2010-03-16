package horizon.geotagger.view;

import horizon.geotagger.R;

import android.app.Activity;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class DoImageTagView
extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doimagetagview);
		
		final Camera camera = Camera.open();
		
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
	
	private void store(Location location, byte[] data)
	{
		
	}
}
