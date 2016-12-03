package MVC;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModelAndView {
	
	private String viewName;
	private Map<String, Object> objectList= new ConcurrentHashMap<String, Object>();
	

	public ModelAndView() {
	}

	public void setViewName(String viewName) {
		this.viewName=viewName;
	}
	
	public String getViewName() {
		return this.viewName;
	}

	public Object getMap(String string) {
		return objectList.get(string);
	}

	public void addObject(String string, Object value) {
		objectList.put(string, value);
	}
	
	public Map<String, Object> getObjectList(){
		return objectList;
		
	}
}
