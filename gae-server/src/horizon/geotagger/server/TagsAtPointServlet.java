package horizon.geotagger.server;

import horizon.geotagger.PMF;
import horizon.geotagger.model.Tag;

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
		
		List<Tag> tags = (List<Tag>)pm.newQuery(
				"SELECT FROM " + Tag.class.getName()).execute();
		model.put("tags", tags);
				
		return new ModelAndView(view, model);
	}
}
