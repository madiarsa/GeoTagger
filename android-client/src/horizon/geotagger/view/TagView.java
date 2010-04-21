package horizon.geotagger.view;

import horizon.geotagger.R;
import horizon.geotagger.model.Tag;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

public class TagView
extends Activity
{
	@Override
	protected void onResume()
	{
		super.onResume();
		
		final ArrayList<Tag> tags = getIntent().getParcelableArrayListExtra(TAG_LIST);		
		ArrayAdapter<Tag> adapter = new ArrayAdapter<Tag>(this, 0, tags)
		{
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				Tag tag = tags.get(position);
				
				switch(tag.getAttachment().getType())
				{
				case Text:
					TextView textView = new TextView(TagView.this);
					try
					{
						textView.setText(new String(tag.getAttachment().getData(), "UTF-8"));
					} 
					catch(UnsupportedEncodingException e)
					{
						throw new RuntimeException(e);
					}
					return textView;
				case Image:
					ImageView imageView = new ImageView(TagView.this);
					imageView.setImageBitmap(BitmapFactory.decodeStream(
							new ByteArrayInputStream(tag.getAttachment().getData())));
					return imageView;
				default:
					throw new RuntimeException("Unknown attachment type: " + tag.getAttachment().getType());
				}
			}	
		};
		
		Gallery gallery = (Gallery)findViewById(R.id.tagview);
		gallery.setAdapter(adapter);
	}

	public static final String TAG_LIST = "tag_list";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tagview);
		
		
	}
}
