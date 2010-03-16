package horizon.geotagger.server;

import horizon.geotagger.GeoUtils;
import horizon.geotagger.PMF;
import horizon.geotagger.model.Place;
import horizon.geotagger.model.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TagsAtPointServlet 
extends ServletWithView 
{
	private static final String view = "/WEB-INF/jsp/tagsAtPoint.jsp";
	
	private static final long serialVersionUID = -7887869696386109335L;
	
	private DataBinder binder = new DataBinder();
	
	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handleGet(
			HttpServletRequest request, HttpServletResponse response) 
	throws Exception 
	{
		TagsAtPointData data = new TagsAtPointData();
		binder.bind(data, request.getParameterMap());
		
		HashMap<String, Object> model = new HashMap<String, Object>();
				
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		double north = data.getLatitude() + GeoUtils.changeInLatitude(10);
		double south = data.getLatitude() - GeoUtils.changeInLatitude(10);
		
		double east = data.getLongitude() + GeoUtils.changeInLongitude(data.getLatitude(), 10);
		double west = data.getLongitude() - GeoUtils.changeInLongitude(data.getLatitude(), 10);
		
		List<Tag> tags = (List<Tag>)pm.newQuery(
				"SELECT FROM " + Tag.class.getName()).execute();
		
		ArrayList<Tag> matches = new ArrayList<Tag>();
		for(Tag t : tags)
		{
			Place p = t.getPlace();
			if(p.getLatitude() > south 
					&& p.getLatitude() < north
					&& p.getLongitude() > west
					&& p.getLongitude() < east)
				matches.add(t);
		}
		model.put("tags", matches);
				
		return new ModelAndView(view, model);
	}
}
