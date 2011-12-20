package fr.aliacom.obm.common.domain;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DomainCache implements DomainService {

	private final Cache<String, ObmDomain> domainCache;
	
	@Inject
	private DomainCache(DomainDao domainDao) {
		this.domainCache = configureObmDomainCache(domainDao);
	}

	private Cache<String,ObmDomain> configureObmDomainCache(final DomainDao domainDao) {
		return CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES)
				.concurrencyLevel(1)
				.build(new CacheLoader<String, ObmDomain>() {

					@Override
					public ObmDomain load(String domainName) throws Exception {
						return domainDao.findDomainByName(domainName);
					}
				});
	}
	
	@Override
	public ObmDomain findDomainByName(String domainName) {
		try {
			return domainCache.get(domainName);
		} catch (NullPointerException e) {
			return null;
		} catch (ExecutionException e) {
			return null;
		}
	}
}