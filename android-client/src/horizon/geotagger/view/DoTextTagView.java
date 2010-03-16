package horizon.geotagger.view;

import horizon.geotagger.R;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DoTextTagView
extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dotexttagview);
		
		final Location location = getIntent().getParcelableExtra("location");
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
						store(location, 
								((EditText)findViewById(R.id.DoTextTagEditText))
								.getText().toString());
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
	protected void onResume()
	{
		super.onResume();
		((EditText)findViewById(R.id.DoTextTagEditText)).setText("");
	}
	
	private void store(Location location, String text)
	{
		
	}
}
