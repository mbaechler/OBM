package org.obm.locator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class TimeOutMapTest {

	private final static String APPLY_VALUE = "DEFAULT-VALUE";

	@Test
	public void basicOperation() {
		Cache<String, String> localCache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.SECONDS)
				.build(new CacheLoader<String, String>() {

					@Override
					public String load(String key) throws Exception {
						return APPLY_VALUE;
					}
				});
		Map<String, String> mapCache = localCache.asMap();
		String value = "ONE-VALUE";
		String key = "ONE-KEY";
		mapCache.put(key, value);
		Assert.assertEquals(value, mapCache.get(key));
	}
	
	@Test
	public void returnApplyValue() throws ExecutionException {
		Cache<String, String> localCache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.SECONDS)
				.build(new CacheLoader<String, String>() {

					@Override
					public String load(String key) throws Exception {
						return APPLY_VALUE;
					}
				});
		Assert.assertEquals(APPLY_VALUE, localCache.get("KEY-NOT-EXIST") );
	}
	
	@Test
	public void returnApplyValueExpireAfterAccess() throws InterruptedException, ExecutionException {
		Cache<String, String> cache = CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.SECONDS)
				.build(new CacheLoader<String, String>() {

					@Override
					public String load(String key) throws Exception {
						return APPLY_VALUE;
					}
				});
		Map<String, String> mapCache = cache.asMap();
		String value = "ONE-VALUE";
		String key = "ONE-KEY";
		mapCache.put(key, value);
		Assert.assertEquals(value, cache.get(key) );
		Thread.sleep(5000);
		Assert.assertEquals(APPLY_VALUE, cache.get(key) );
	}
	
	@Test
	public void testTimeOutMap() throws Exception {
		Cache<String, Object> cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS)
				.removalListener(new RemovalListener<String, Object>() {

					@Override
					public void onRemoval(
							RemovalNotification<String, Object> notification) {
						
					}
				})
				.build(new CacheLoader<String, Object>() {

					@Override
					public Object load(String key) throws Exception {
						return APPLY_VALUE;
					}
				});
		Map<String, Object> mapCache = cache.asMap();
		mapCache.put("a", new Object());
		assertNotNull(mapCache.get("a"));
		for (int i = 0; i < 4; i++) {
			Thread.sleep(1000);
			assertNotNull(mapCache.get("a"));
		}
		Thread.sleep(1000);
		assertNull(mapCache.get("a"));
	}

}
