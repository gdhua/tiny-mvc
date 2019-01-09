package top.qhua.mvc.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import top.qhua.mvc.annaotation.Autowired;
import top.qhua.mvc.annaotation.Controller;
import top.qhua.mvc.annaotation.RequestMaping;
import top.qhua.mvc.annaotation.RequestParam;
import top.qhua.mvc.annaotation.Service;

public class DispatcherServlet extends HttpServlet {
	
	private static final long serialVersionUID = -3066836991617115341L;
	private List<String> classFiles=new ArrayList<String>();
    private HashMap<String,Object> handleMapping=new HashMap<String,Object>();
	private HashMap<String,Object> beans=new HashMap<String,Object>();
	public void init(ServletConfig config) throws ServletException {
        System.out.println("mini-mvc start …… ");
        scanPackage("top.qhua.mvc");
        doInstance();
        doIoc();
        buildUrlMapping();
        System.out.println("mini-mvc start OK .");
	}

	private void buildUrlMapping() {
		
		if(beans.entrySet().size()==0){
			System.out.println("木有实例化类");
			return;
		}
		
		for(Map.Entry<String, Object> entry:beans.entrySet()){
			Object instance=entry.getValue();
			Class<?> clazz=instance.getClass();
			if(clazz.isAnnotationPresent(Controller.class)){
				String classUrl="";
				RequestMaping root=	clazz.getAnnotation(RequestMaping.class);
				if(root!=null){
					classUrl = root.value();

				}
			    
			    Method[] methods=clazz.getMethods();
			    for(Method method:methods){
			    	if(method.isAnnotationPresent(RequestMaping.class)){
			    		RequestMaping requestMap=method.getAnnotation(RequestMaping.class);
			    		String url=classUrl+requestMap.value();
			    		if(handleMapping.containsKey(url)){
			    			throw new RuntimeException("存在重复 requestMapping 值 ：url"+url);
			    		}
			    		handleMapping.put(url, method);
						i("build Mapping -> "+url+" = "+clazz.getName()+" ->"+method.getName());
			    	}else{
			    		continue;
			    	}
			    }
			
			}
		}
		
	}

	private void doIoc() {
		if(beans.entrySet().size()==0){
			i("木有实例化类");
			return;
		}
		
		for(Map.Entry<String, Object> entry:beans.entrySet()){
			Object instance=entry.getValue();
			Class<?> clazz=instance.getClass();
			if(clazz.isAnnotationPresent(Controller.class)){
				Field[] fields=clazz.getDeclaredFields();
				for(Field field:fields){
					if(field.isAnnotationPresent(Autowired.class)){
						Autowired auto=field.getAnnotation(Autowired.class);
						String key=auto.value();
						
						if(null==beans.get(key)){
							i(" do ioc err  not find instance["+key+"] -> "+field.getName());
						}
						
						field.setAccessible(true);
						
						try {
							
							field.set(instance,beans.get(key));
							i(" do ioc -> "+field.getName()+" = "+beans.get(key));

						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}else{
						continue;
					}
				}
			}else{
				continue;
			}
			
			
			
		}
		
	}

	private void doInstance() {
		if(classFiles.size()==0){
			i("包扫描为0");
			return;
		}
		
		for(String className:classFiles){
			try {
				Class<?> clazz=Class.forName(className.replace(".class", ""));
			
				String key;
				if(clazz.isAnnotationPresent(Controller.class)){
					Object instance=clazz.newInstance();
					Controller c= clazz.getAnnotation(Controller.class);
					if(!"".equals(c.value())){
						key=c.value();
					}else{
						key=clazz.getName();
					}
					 
					beans.put(key, instance);
					
					i("doInstance controller -> "+key);
				}else if(clazz.isAnnotationPresent(Service.class)){
					Service c= clazz.getAnnotation(Service.class);
					Object instance=clazz.newInstance();
					if(!"".equals(c.value())){
						key=c.value();
					}else{
						key=clazz.getName();
					}
					 
					beans.put(key, instance);
					i("doInstance service -> "+key);

				}
			
			
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private void scanPackage(String basePackage) {
		URL url=this.getClass().getClassLoader().getResource("/"+basePackage.replaceAll("\\.", "/"));
		String rootPath=url.getPath();
		File dir=new File(rootPath);
		String[] files=dir.list();
		for(String fname:files){
			File fi=new File(rootPath+fname);
			if(fi.isDirectory()){
				scanPackage(basePackage+"."+fname);
			}else{
				classFiles.add(basePackage+"."+fi.getName());
				i("scanPackage -> "+basePackage+"."+fi.getName());
			}
		}
		
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
           this.doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
             String uri=req.getRequestURI();
             String context=req.getContextPath();
             String path=uri.replace(context, "");
             
             Method method=(Method) handleMapping.get(path);
             if(method!=null){
            	 
            	 Object[] param= hander(req,resp,method);                
                 
                 String packageClassName= method.getDeclaringClass().getName();
                 Object classObject= beans.get(packageClassName);
                 
                 try {
    				method.invoke(classObject, param);
    			} catch (IllegalAccessException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (InvocationTargetException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
             }else{
            	 i("无对应url->"+path);
             }
             
             
             
             
	}

	private Object[] hander(HttpServletRequest req, HttpServletResponse resp, Method method) {
		 Class<?>[] params= method.getParameterTypes();
		
			 Object[] args=new Object[params.length];
			 int args_index=0;
			 for(Class<?> clazz:params){
				 
				 Annotation[] paramAnnotations= method.getParameterAnnotations()[args_index];

				 if(ServletRequest.class.isAssignableFrom(clazz)){
					 args[args_index++]=req;
				 }else if(ServletResponse.class.isAssignableFrom(clazz)){
					 args[args_index++]=resp;
				 }
				 
				 for(Annotation paramAnnotation:paramAnnotations){
					 
					 if(RequestParam.class.isAssignableFrom(paramAnnotation.getClass())){
						 RequestParam rp=(RequestParam)paramAnnotation;
						 String key=rp.value();
						 args[args_index]=req.getParameter(key);
					 }
				 }
			 }
		return args;
		
	}
	
	public void i(String info){
		System.out.println(info);
	}

}
