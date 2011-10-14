package fr.aliasource.funambol;

import org.obm.locator.store.LocatorCache;
import org.obm.locator.store.LocatorService;

import com.google.inject.AbstractModule;

public class ObmFunambolGuiceModule extends AbstractModule { 

	@Override
	protected void configure() {
		bind(LocatorService.class).to(LocatorCache.class);
	}
    
}