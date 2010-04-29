package horizon.geotagger.view;

import java.util.HashMap;
import java.util.Map.Entry;

import horizon.android.logging.Logger;
import horizon.geotagger.R;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class DoImageTagView
extends DoTagView
{		
	private static Logger logger = Logger.getLogger(DoImageTagView.class);
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doimagetagview);
		
		final Camera camera = Camera.open();
		Camera.Parameters cp = camera.getParameters();
		setImageSize(cp);
		camera.setParameters(cp);
		
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
								store("image/jpeg", data);
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
	protected void onResumeInternal() { }
	
	@Override
	protected void onPauseInternal() { }
	
	private void setImageSize(Camera.Parameters cp)
	{
		for(String param : cp.flatten().split(";"))
			logger.debug("Camera parameter: " + param);
		HashMap<Integer, Integer> sizes = new HashMap<Integer, Integer>();
		int width = Integer.MAX_VALUE;
		int height = Integer.MAX_VALUE;
		
		if(cp.get("picture-size-values") == null)
		{
			// Set and hope! - works on the Hero
			cp.setPictureSize(320, 240);
			cp.setPreviewSize(320, 240);
			return;
		}
		
		// This works on the Nexus One, not on the Hero and unknown on other devices
		logger.debug("Available picture sizes are: " + cp.get("picture-size-values"));
		for(String part : cp.get("picture-size-values").split(","))
		{
			String[] s = part.split("x");
			sizes.put(new Integer(s[0]), new Integer(s[1]));
		}
		
		for(Entry<Integer, Integer> entry : sizes.entrySet())
			if(entry.getKey().intValue() < width)
			{
				width = entry.getKey().intValue();
				height = entry.getValue().intValue();
			}
		
		logger.debug("Selected picture size is: " + width + "x" + height);
			
		cp.setPictureSize(width, height);
	}
}
