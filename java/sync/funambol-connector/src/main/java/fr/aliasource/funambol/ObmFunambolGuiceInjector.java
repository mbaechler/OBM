package fr.aliasource.funambol;

import org.obm.configuration.ConfigurationService;
import org.obm.configuration.ConfigurationServiceImpl;
import org.obm.locator.store.LocatorCache;
import org.obm.locator.store.LocatorService;
import org.obm.sync.ObmSyncHttpClientModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

import fr.aliasource.obm.items.converter.IEventConverter;
import fr.aliasource.obm.items.converter.ISyncItemConverter;
import fr.aliasource.obm.items.converter.ObmEventConverter;
import fr.aliasource.obm.items.converter.SyncItemConverterImpl;

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
			install(new ObmSyncHttpClientModule());
			bind(String.class).annotatedWith(Names.named("origin")).toInstance("funis");
			bind(LocatorService.class).to(LocatorCache.class);
			bind(ConfigurationService.class).to(ConfigurationServiceImpl.class);
			bind(IEventConverter.class).to(ObmEventConverter.class);
			bind(ISyncItemConverter.class).to(SyncItemConverterImpl.class);
		}
		
	}
    
}