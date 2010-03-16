package horizon.geotagger.view;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraSurfaceView 
extends SurfaceView
implements SurfaceHolder.Callback
{
	private Camera camera;
	
	public CameraSurfaceView(Camera camera, Context context) 
	{
		super(context);
		this.camera = camera;
		SurfaceHolder holder = getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(this);
	}

	@Override
	public void surfaceChanged(
			SurfaceHolder holder, int format, int width, int height)
	{
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		try 
		{
			camera.setPreviewDisplay(getHolder());
		}
		catch(IOException e) 
		{
			throw new RuntimeException(e);
		}
		camera.startPreview();
		camera.autoFocus(new Camera.AutoFocusCallback() 
		{	
			@Override
			public void onAutoFocus(boolean success, Camera camera) { }
		});
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		camera.stopPreview();
		camera.release();
	}
}
