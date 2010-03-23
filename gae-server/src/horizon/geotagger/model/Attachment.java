package horizon.geotagger.model;

import java.io.IOException;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.JsonSerializable;
import org.codehaus.jackson.map.SerializerProvider;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

@JsonIgnoreProperties({"key"})
@PersistenceCapable
public class Attachment
implements JsonSerializable
{
	public static enum Type { Text, Image; }
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private Type type;
	
	@Persistent
	private Blob data;

	public Key getKey() 
	{
		return key;
	}
	
	public void setKey(Key key)
	{
		this.key = key;
	}
	
	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public Blob getData()
	{
		return data;
	}

	public void setData(Blob data)
	{
		this.data = data;
	}

	@Override
	public void serialize(JsonGenerator gen, SerializerProvider prov)
	throws	IOException,
			JsonProcessingException
	{
		gen.writeStartObject();
		gen.writeStringField("type", type.name());
		gen.writeBinaryField("data", data.getBytes());
		gen.writeEndObject();
	}
}
