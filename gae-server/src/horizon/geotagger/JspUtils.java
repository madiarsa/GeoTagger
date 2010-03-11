package horizon.geotagger;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class JspUtils 
{
	public static String serializeToJSON(Object o) 
	throws	JsonGenerationException,
			JsonMappingException,
			IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(o);
	}
}
