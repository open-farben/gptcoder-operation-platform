package cn.com.farben.gptcoder.operation.platform.user.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.util.Locale;

/*
 * This class is used to get the bean defination from xml
 */
@Component
public class SpringContextUtil implements ApplicationListener<ApplicationStartedEvent>{
	private  static  String webPath;
	private static ApplicationContext applicationContext;
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public static <T> T registerBean(String name, Class<T> clazz,Object... args) {
		if(applicationContext.containsBean(name)) {
			Object bean = applicationContext.getBean(name);
			if (bean.getClass().isAssignableFrom(clazz)) {
				return (T) bean;
			} else {
				throw new RuntimeException("BeanName 重复 " + name);
			}
		}
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
		for (Object arg : args) {
			beanDefinitionBuilder.addConstructorArgValue(arg);
		}
		BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
		BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) applicationContext.getParentBeanFactory();
		beanFactory.registerBeanDefinition(name, beanDefinition);
		return applicationContext.getBean(name, clazz);
	}

	public static void publishEvent(ApplicationEvent applicationEvent){
		applicationContext.publishEvent(applicationEvent);
	}
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static void setApplicationContext(ApplicationContext applicationContext) {
		SpringContextUtil.applicationContext = applicationContext;
	}
	public static <T> T getBean(Class<T> clazz) {
		if (applicationContext==null)
			return null;
		return applicationContext.getBean(clazz);
	}
	public static <T> T getBean(String name,Class<T> clazz) {
		if (applicationContext==null)
			return null;
		return applicationContext.getBean(name,clazz);
	}

	public static Object getBean(String clazz) {
		if (applicationContext==null)
			return null;
		return applicationContext.getBean(clazz);
	}

	public static String getMessage(String key,Object ...value){
		if (applicationContext==null)
			return null;
		return applicationContext.getMessage(key,value,null);
	}
	public static String getMessage(String key,Locale locale,Object ...value){
		if (applicationContext==null)
			return null;
		return applicationContext.getMessage(key,value,locale);
	}

	public SpringContextUtil(){
		webPath= ClassUtils.getDefaultClassLoader().getResource("").getPath();
		//webPath=event.getServletContext().getRealPath("/");
	}
	public static String getLocalPath(String... vpath) {
		String retPath=webPath;
		for (String path : vpath) {
			if (retPath.endsWith("/") ||retPath.endsWith("\\") || path.startsWith("/") || path.startsWith("\\"))
				retPath= retPath+path;
			else
				retPath= retPath+File.separator+path;
		}
		return retPath;
	}

    public static String getUrlpathBase(String retPath, String... vpath){
    	//String retPath=request.getRequestURL().toString().replace(request.getServletPath(),"");
   	 	for (String path : vpath) {
   	 		if (path.startsWith("/") || retPath.endsWith("/"))
   	 			retPath= retPath+path;
   	 		else
   	 			retPath= retPath+"/"+path;
   	 	}
   	 	return retPath;
    }

	public static void main(String[] args) {
	}

	@Override
	public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
		setApplicationContext(applicationStartedEvent.getApplicationContext());
		Environment environment=applicationContext.getEnvironment();
		logger.warn("Name:"+environment.getProperty("spring.application.name")+",Port:"+environment.getProperty("server.port")+" start,Active:"+ StringUtils.join(applicationContext.getEnvironment().getActiveProfiles(),","));
	}
}
