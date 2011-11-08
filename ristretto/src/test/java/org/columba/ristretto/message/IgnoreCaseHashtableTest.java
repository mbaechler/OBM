package org.columba.ristretto.message;

import org.junit.Assert;
import org.junit.Test;


public class IgnoreCaseHashtableTest {

	@Test
	public void test1() {
		IgnoreCaseHashtable table = new IgnoreCaseHashtable();
		table.put("my-Test", "value1");
		table.put("dummy", "value3");
		Assert.assertEquals("value1", table.get("my-test"));
		Assert.assertEquals("value3", table.get("dummy"));
		
		table.put("my-test", "value2");
		
		Assert.assertEquals("value2", table.get("my-Test"));
		Assert.assertEquals("value2", table.get("my-test"));
		Assert.assertEquals("value2", table.get("MY-TeST"));
		Assert.assertEquals("value3", table.get("duMMy"));

		Assert.assertTrue(table.containsKey("MY-TEST"));
		
		Assert.assertEquals("my-Test", table.keys().nextElement());
		
		System.out.println(table.toString());
	}
	
}
