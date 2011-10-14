package fr.aliasource.funambol;

import org.obm.locator.store.LocatorCache;
import org.obm.locator.store.LocatorService;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ObmFunambolGuiceInjector { 

	private static Injector injector;
	
	private ObmFunambolGuiceInjector(){
		
	}
	
	public static Injector getInjector() {
		if(injector == null){
			GuiceModule module = new GuiceModule();
			injector = Guice.createInjector(module);
		}
		return injector;
	}
	
	private static class GuiceModule extends AbstractModule{

		@Override
		protected void configure() {
			bind(LocatorService.class).to(LocatorCache.class);
		}
		
		
	}
    
}