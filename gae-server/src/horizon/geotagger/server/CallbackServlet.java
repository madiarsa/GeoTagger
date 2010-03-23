package horizon.geotagger.server;

import horizon.geotagger.Base64;
import horizon.geotagger.PMF;
import horizon.geotagger.model.Attachment;
import horizon.geotagger.model.Place;
import horizon.geotagger.model.Tag;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.appengine.api.datastore.Blob;

public class CallbackServlet
extends HttpServlet
{
	private static final long serialVersionUID = 1030756850358949459L;
	
	private static Logger logger = Logger.getLogger(CallbackServlet.class);
		
	@Override
	protected void doPost(
			HttpServletRequest request, HttpServletResponse response)
	throws	ServletException,
			IOException
	{
		doPut(request, response);
	}

	@Override
	protected void doPut(
			HttpServletRequest request, HttpServletResponse response)
	throws	ServletException,
			IOException
	{
		BufferedInputStream in = new BufferedInputStream(
				request.getInputStream());
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(in);
		
		logger.debug(root.toString());
		
		in.close();
		
		JsonNode meta = root.get("thing").get("meta");
		String mime = meta.get("mime").getTextValue();
		String body64 = meta.get("body").getTextValue();
		byte[] body = Base64.decode(body64);
		JsonNode jplace = root.get("place");

		Tag tag = new Tag();
		Place place = new Place();
		Attachment attachment = new Attachment();
		tag.setAttachment(attachment);
		tag.setPlace(place);
		attachment.setData(new Blob(body));
		
		if(mime.equals("text/plain"))
			attachment.setType(Attachment.Type.Text);
		else if(mime.equals("image/jpeg"))
			attachment.setType(Attachment.Type.Image);
		else
			throw new RuntimeException("Unknown mime-type: " + mime);
		
		place.setLatitude(jplace.get("lat").getDoubleValue());
		place.setLongitude(jplace.get("lon").getDoubleValue());
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		//pm.makePersistent(attachment);
		//pm.makePersistent(place);
		pm.makePersistent(tag);		
	}
}
