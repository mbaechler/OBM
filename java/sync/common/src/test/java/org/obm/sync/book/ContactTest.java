package org.obm.sync.book;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;


public class ContactTest {
	
	@Test
	public void testGetWorkPhones(){
		Contact c = getFakeContactWithPhone();
		Map<String, Phone> workPhones = c.getWorkPhones();
		Assert.assertEquals(2, workPhones.size());
		for(String key : workPhones.keySet()){
			Assert.assertTrue(key.startsWith("WORK;VOICE;X-OBM-Ref") || key.startsWith("WORK;FAX;X-OBM-Ref"));
		}
	}
	
	@Test
	public void testGetHomePhones(){
		Contact c = getFakeContactWithPhone();
		Map<String, Phone> homePhones = c.getHomePhones();
		Assert.assertEquals(2, homePhones.size());
		for(String key : homePhones.keySet()){
			Assert.assertTrue(key.startsWith("HOME;VOICE;X-OBM-Ref") || key.startsWith("HOME;FAX;X-OBM-Ref"));
		}
	}
	
	@Test
	public void testGetCellPhones(){
		Contact c = getFakeContactWithPhone();
		Map<String, Phone> cellPhones = c.getCellPhones();
		Assert.assertEquals(1, cellPhones.size());
		for(String key : cellPhones.keySet()){
			Assert.assertTrue(key.startsWith("CELL;VOICE;X-OBM-Ref"));
		}
	}
	
	
	private Contact getFakeContactWithPhone(){
		Contact obmContact = new Contact();
		final Phone phoneHome = new Phone("1111111111");
		obmContact.addPhone("HOME;VOICE;X-OBM-Ref1", phoneHome);
		final Phone phoneWork = new Phone("2222222222");
		obmContact.addPhone("WORK;VOICE;X-OBM-Ref1", phoneWork);
		final Phone phoneCell = new Phone("33333333333");
		obmContact.addPhone("CELL;VOICE;X-OBM-Ref1", phoneCell);
		final Phone faxHome = new Phone("4444444444");
		obmContact.addPhone("HOME;FAX;X-OBM-Ref1", faxHome);
		final Phone faxWork = new Phone("5555555555");
		obmContact.addPhone("WORK;FAX;X-OBM-Ref1", faxWork);
		final Phone pager = new Phone("6666666666");
		obmContact.addPhone("PAGER;X-OBM-Ref1", pager);
		final Phone phoneOther = new Phone("7777777777");
		obmContact.addPhone("OTHER;X-OBM-Ref1", phoneOther);
		return obmContact;
	}
}
