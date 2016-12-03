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

		//Ѱ��ע��controller��
		LoadController(java_root);
	
		//ƥ���Ӧ��RequestMapping����ȡ��Ҫ��תҳ���ֵ����Ϣ
		ModelAndView mdv=(ModelAndView) MatchRequestMapping(request.getServletPath(),getInput(request));
		
		for (Map.Entry<String, Object> entry : mdv.getObjectList().entrySet()) {  	
			request.setAttribute(entry.getKey(), entry.getValue());
		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());  	  
		}
			
		request.getRequestDispatcher(mdv.getViewName()+".jsp").forward(request, response);

	}
	
	
	/**
	 * ���������������Ժ�ֵ����װ��ModelAndView��
	 * @param request
	 * @return ModelAndView
	 */
	public ModelAndView getInput(HttpServletRequest request){		
		ModelAndView mav=new ModelAndView();
		//�õ�������������Ժ�ֵ
		Map<?, ?> map=request.getParameterMap();
		ArrayList<String> keyList = new ArrayList<String>();
		ArrayList<Object> valueList = new ArrayList<Object>();
		
		//����map		
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
	 * ��������package�µĺ���Controller��java�ļ������浽ControllerClassList
	 * @param filePath
	 */
	public void LoadController(String filePath) {
			File readFile = new File(filePath);
			File[] files = readFile.listFiles();
			
			String fileName = null;
			for (File file : files) {
				fileName = file.getName();
				if (file.isFile()) {
					
					//�õ����µ�java�ļ�
					if (fileName.endsWith(".java")) {			
						try {							
							String  str=filePath+File.separator+ fileName;
							String beanClassName=str.substring(java_root.length()+1, str.length()-5).replace('\\', '.');
							Class<?> beanClass = Class.forName(beanClassName);
						
							//�ж��Ƿ���Controllerע��,�����뵽ControllerClassList
							if(beanClass.isAnnotationPresent(Controller.class)){
								controllerClassList.add(beanClass);
							}										
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  
					}
				} else {
					//�������а��µ�java�ļ�
					LoadController(filePath + File.separator + fileName);
				}
			}
	}

	/**
	 * �����п�����controllerClassList��ƥ���Ӧ��LoadRequestMapping,�����ط����ķ���ֵ
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
