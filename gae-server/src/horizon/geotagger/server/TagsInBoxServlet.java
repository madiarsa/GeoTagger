package horizon.geotagger.server;

import horizon.geotagger.PMF;
import horizon.geotagger.model.Place;
import horizon.geotagger.model.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TagsInBoxServlet
extends ServletWithView 
{
	private static final String view = "/WEB-INF/jsp/tagsInBox.jsp";
	
	private static final long serialVersionUID = -5651184768656324906L;
	
	private DataBinder binder = new DataBinder();
	
	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handleGet(
			HttpServletRequest request, HttpServletResponse response) 
	throws Exception 
	{
		TagsInBoxData data = new TagsInBoxData();
		binder.bind(data, request.getParameterMap());
		
		HashMap<String, Object> model = new HashMap<String, Object>();
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		List<Tag> tags = (List<Tag>)pm.newQuery(
				"SELECT FROM " + Tag.class.getName()).execute();
		
		ArrayList<Tag> matches = new ArrayList<Tag>();
		for(Tag t : tags)
		{
			Place p = t.getPlace();
			if(p.getLatitude() > data.getSouth() 
					&& p.getLatitude() < data.getNorth()
					&& p.getLongitude() > data.getWest()
					&& p.getLongitude() < data.getEast())
			{
				t.setAttachment(null);
				matches.add(t);
			}
		}
		model.put("tags", matches);		
		
		return new ModelAndView(view, model);
	}
}
