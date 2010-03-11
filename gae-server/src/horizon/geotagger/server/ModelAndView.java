package horizon.geotagger.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModelAndView 
{
	private Map<String, Object> model = new HashMap<String, Object>();
	
	private String view;
	
	public ModelAndView() { }
	
	public ModelAndView(String view)
	{
		this.view = view;
	}
	
	public ModelAndView(Map<String, Object> model)
	{
		this.model.putAll(model);
	}
	
	public ModelAndView(String view, Map<String, Object> model)
	{
		this.view = view;
		this.model.putAll(model);
	}
	
	public String getView()
	{
		return view;
	}
	
	public void setView(String view)
	{
		this.view = view;
	}
	
	public Map<String, Object> getModel()
	{
		return Collections.unmodifiableMap(model);
	}
	
	public void addToModel(String key, Object value)
	{
		model.put(key, value);
	}
	
	public void addAllToModel(Map<String, Object> objects)
	{
		model.putAll(objects);
	}
}
