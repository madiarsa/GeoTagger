package horizon.geotagger.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.google.appengine.api.datastore.Key;

@JsonIgnoreProperties({"key"})
@PersistenceCapable
public class Tag
{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private Attachment attachment;
	
	@Persistent
	private Place place;
	
	public Key getKey() 
	{
		return key;
	}
	
	public void setKey(Key key) 
	{
		this.key = key;
	}
	
	public Attachment getAttachment()
	{
		return attachment;
	}
	
	public void setAttachment(Attachment attachment)
	{
		this.attachment = attachment;
	}
	
	public Place getPlace()
	{
		return place;
	}
	
	public void setPlace(Place place)
	{
		this.place = place;
	}
}
