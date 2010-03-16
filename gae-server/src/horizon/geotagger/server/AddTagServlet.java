package horizon.geotagger.server;

import horizon.geotagger.PMF;
import horizon.geotagger.model.Tag;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class AddTagServlet
extends HttpServlet
{
	private static final long serialVersionUID = 1030756850358949459L;
	
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
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int b;
		while((b = in.read()) != -1)
			out.write(b);
		in.close();
		String json = new String(out.toByteArray(), "UTF-8");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(json);
		if(!node.isObject() || node.isNull())
			return;
		
		Tag tag = mapper.treeToValue(node, Tag.class);
		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.makePersistent(tag);
	}
}
