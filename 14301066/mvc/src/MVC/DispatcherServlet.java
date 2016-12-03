package MVC;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DispatcherServlet
 */
public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	String java_root = "F:\\J2EE-Eclipse\\mvc\\src";
	ArrayList<Class<?>> controllerClassList = new ArrayList<Class<?>>();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DispatcherServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//寻找注解controller类
		LoadController(java_root);
	
		//匹配对应的RequestMapping，获取需要跳转页面的值和信息
		ModelAndView mdv=(ModelAndView) MatchRequestMapping(request.getServletPath(),getInput(request));
		
		for (Map.Entry<String, Object> entry : mdv.getObjectList().entrySet()) {  	
			request.setAttribute(entry.getKey(), entry.getValue());
		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());  	  
		}
			
		request.getRequestDispatcher(mdv.getViewName()+".jsp").forward(request, response);

	}
	
	
	/**
	 * 获得输入的所有属性和值并封装到ModelAndView中
	 * @param request
	 * @return ModelAndView
	 */
	public ModelAndView getInput(HttpServletRequest request){		
		ModelAndView mav=new ModelAndView();
		//得到输入的所有属性和值
		Map<?, ?> map=request.getParameterMap();
		ArrayList<String> keyList = new ArrayList<String>();
		ArrayList<Object> valueList = new ArrayList<Object>();
		
		//遍历map		
		for (Object key : map.keySet()) { 
			keyList.add((String)key);	  
		}
		
		for (Object values : map.values()) {  
			String[]  value= (String[]) values;	
			valueList.add(value[0]);  
		}
		
		for(int i=0;i<keyList.size();i++){
			mav.addObject(keyList.get(i), valueList.get(i));
		}
		
		return mav;	
	}

	
	/**
	 * 遍历所有package下的含有Controller的java文件并保存到ControllerClassList
	 * @param filePath
	 */
	public void LoadController(String filePath) {
			File readFile = new File(filePath);
			File[] files = readFile.listFiles();
			
			String fileName = null;
			for (File file : files) {
				fileName = file.getName();
				if (file.isFile()) {
					
					//得到包下的java文件
					if (fileName.endsWith(".java")) {			
						try {							
							String  str=filePath+File.separator+ fileName;
							String beanClassName=str.substring(java_root.length()+1, str.length()-5).replace('\\', '.');
							Class<?> beanClass = Class.forName(beanClassName);
						
							//判断是否含有Controller注解,并加入到ControllerClassList
							if(beanClass.isAnnotationPresent(Controller.class)){
								controllerClassList.add(beanClass);
							}										
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  
					}
				} else {
					//遍历所有包下的java文件
					LoadController(filePath + File.separator + fileName);
				}
			}
	}

	/**
	 * 从所有控制类controllerClassList中匹配对应的LoadRequestMapping,并返回方法的返回值
	 * @param servletPath
	 * @return 
	 */
	public Object MatchRequestMapping(String servletPath,ModelAndView mav){
		
		for(Class<?> controllerClass: controllerClassList ) {
			for(Method method : controllerClass.getMethods()){      
	        	if(method.getAnnotation(RequestMapping.class).value().equals(servletPath)){
        		
	        		try {
	        			Object args[]=new Object[1];
	        			args[0]=mav;
						return method.invoke(controllerClass.newInstance(),args);
					} catch (Exception e) {
						e.printStackTrace();
					} 
	        	}  
	  
	        }  
		}
		return null;	
	}
}
