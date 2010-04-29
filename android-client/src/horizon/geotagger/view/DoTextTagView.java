package horizon.geotagger.view;

import java.io.UnsupportedEncodingException;

import horizon.geotagger.R;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DoTextTagView
extends DoTagView
{	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dotexttagview);
		
		Button doButton = (Button)findViewById(R.id.DoTextTagButton);
		doButton.setOnClickListener(new View.OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							store("text/plain", 
									((EditText)findViewById(R.id.DoTextTagEditText))
									.getText().toString().getBytes("UTF-8"));
						} 
						catch (UnsupportedEncodingException e)
						{
							throw new RuntimeException(e);
						}
						DoTextTagView.this.finish();
					}
				}).start();
			}
		});
		Button cancelButton = (Button)findViewById(R.id.CancelTextTagButton);
		cancelButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				DoTextTagView.this.finish();
			}
		});
	}
	
	@Override
	protected void onResumeInternal() { }
	
	@Override
	protected void onPauseInternal() { }
}
