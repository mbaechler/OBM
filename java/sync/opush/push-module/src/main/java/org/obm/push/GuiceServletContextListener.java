package org.obm.push;

import java.util.Collections;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.obm.sync.XTrustProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.spi.Message;

public class GuiceServletContextListener implements ServletContextListener { 

	private static final Logger logger = LoggerFactory.getLogger(GuiceServletContextListener.class);
	
	public static final String ATTRIBUTE_NAME = "OpushGuiceInjecter";
	
    public void contextInitialized(ServletContextEvent servletContextEvent) {
    	
        final ServletContext servletContext = servletContextEvent.getServletContext(); 
        try {
        	Injector injector = createInjector();
        	if (injector == null) { 
        		failStartup("Could not create injector: createInjector() returned null"); 
        	} 
        	servletContext.setAttribute(ATTRIBUTE_NAME, injector);
        	XTrustProvider.install();
        	TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        	failStartup(e.getMessage());
        } 
    } 
    
    private Injector createInjector() {
    	return Guice.createInjector(new OpushModule());
    }
    
    private void failStartup(String message) { 
        throw new CreationException(Collections.nCopies(1, new Message(this, message))); 
    }
    
    public void contextDestroyed(ServletContextEvent servletContextEvent) { 
    	servletContextEvent.getServletContext().setAttribute(ATTRIBUTE_NAME, null); 
    }
    
}