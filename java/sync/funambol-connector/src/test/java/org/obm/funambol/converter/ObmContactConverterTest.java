package org.obm.funambol.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.obm.funambol.converter.ObmContactConverter;
import org.obm.sync.book.Address;
import org.obm.sync.book.Contact;
import org.obm.sync.book.Email;
import org.obm.sync.book.InstantMessagingId;
import org.obm.sync.book.Phone;
import org.obm.sync.book.Website;

import com.funambol.common.pim.contact.BusinessDetail;
import com.funambol.common.pim.contact.Note;
import com.funambol.common.pim.contact.PersonalDetail;
import com.funambol.common.pim.contact.Title;
import com.funambol.common.pim.contact.WebPage;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ObmContactConverterTest {

	private ObmContactConverter createObmContactConverter() {
		return new ObmContactConverter();
	}
	
	
	@Test
	public void testFoundationContactToObmConvertName(){
		com.funambol.common.pim.contact.Contact funisContact = new com.funambol.common.pim.contact.Contact();
		final String aka = "AKA";
		final String commonName = "Common Name";
		final String firstname = "firstname";
		final String lastname = "lastname";
		final String middlename = "MiddleName";
		final String suffix = "Suffix";
		
		funisContact.getName().getDisplayName().setPropertyValue(commonName);
		funisContact.getName().getFirstName().setPropertyValue(firstname);
		funisContact.getName().getLastName().setPropertyValue(lastname);
		funisContact.getName().getMiddleName().setPropertyValue(middlename);
		funisContact.getName().getNickname().setPropertyValue(aka);
		funisContact.getName().getSuffix().setPropertyValue(suffix);
		
		ObmContactConverter converter = createObmContactConverter();
		Contact obmContact = converter.foundationContactToObm(funisContact);
		
		Assert.assertEquals(aka,funisContact.getName().getNickname().getPropertyValue());
		
		Assert.assertEquals(commonName,obmContact.getCommonname());
		Assert.assertEquals(firstname,obmContact.getFirstname());
		Assert.assertEquals(lastname,obmContact.getLastname());
		Assert.assertEquals(middlename,obmContact.getMiddlename());
		Assert.assertEquals(suffix,obmContact.getSuffix());
		
	}
	
	@Test
	public void testFoundationContactToObmConvertBusinessDetail(){
		com.funambol.common.pim.contact.Contact funisContact = new com.funambol.common.pim.contact.Contact();
		BusinessDetail bd = funisContact.getBusinessDetail();
		final String company = "Company";
		final String manager = "manager";
		final String service = "service";
		final String assistant = "assistant";
		final String title = "title";		

		bd.setAssistant(assistant);
		bd.getCompany().setPropertyValue(company);
		bd.getDepartment().setPropertyValue(service);
		bd.setManager(manager);
		Title t = new Title();
		t.setTitleType("JobTitle");
		t.setPropertyValue(title);
		bd.setTitles(Lists.newArrayList(t));

		final String street = "1 Rue OBM WORK";
		final String postalCode = "31000"; 
		final String extendedAddress = "expressPostal";
		final String city = "Toulouse";
		final String country = "France";
		final String state = "State";
		bd.getAddress().getCity().setPropertyValue(city);
		bd.getAddress().getCountry().setPropertyValue(country);
		bd.getAddress().getPostOfficeAddress().setPropertyValue(extendedAddress);
		bd.getAddress().getPostalCode().setPropertyValue(postalCode);
		bd.getAddress().getState().setPropertyValue(state);
		bd.getAddress().getStreet().setPropertyValue(street);
		
		final String email1 = "email1@obm.org";
		final String email2 = "email2@obm.org";
		final String email3 = "email3@obm.org";
		
		com.funambol.common.pim.contact.Email emailF1 = new com.funambol.common.pim.contact.Email();
		emailF1.setPropertyType("EmailAddress");
		emailF1.setPropertyValue(email1);
		bd.addEmail(emailF1);
		
		com.funambol.common.pim.contact.Email emailF2 = new com.funambol.common.pim.contact.Email();
		emailF2.setPropertyType("Email2Address");
		emailF2.setPropertyValue(email2);
		bd.addEmail(emailF2);
		
		com.funambol.common.pim.contact.Email emailF3 = new com.funambol.common.pim.contact.Email();
		emailF3.setPropertyType("Email3Address");
		emailF3.setPropertyValue(email3);
		bd.addEmail(emailF3);
		
		final String phone1 = "1111111111";
		final String phone2 = "2222222222";
		final String phone3 = "3333333333";
		final String pager1 = "4444444444";
		
		com.funambol.common.pim.contact.Phone phoneF1 = new com.funambol.common.pim.contact.Phone();
		phoneF1.setPropertyType("BusinessTelephoneNumber");
		phoneF1.setPropertyValue(phone1);
		bd.addPhone(phoneF1);
		
		com.funambol.common.pim.contact.Phone phoneF2 = new com.funambol.common.pim.contact.Phone();
		phoneF2.setPropertyType("Business2TelephoneNumber");
		phoneF2.setPropertyValue(phone2);
		bd.addPhone(phoneF2);
		
		com.funambol.common.pim.contact.Phone phoneF3 = new com.funambol.common.pim.contact.Phone();
		phoneF3.setPropertyType("Business3TelephoneNumber");
		phoneF3.setPropertyValue(phone3);
		bd.addPhone(phoneF3);
		
		com.funambol.common.pim.contact.Phone pagerF1 = new com.funambol.common.pim.contact.Phone();
		pagerF1.setPropertyType("PagerNumber");
		pagerF1.setPropertyValue(pager1);
		bd.addPhone(pagerF1);
		
		final String webpage1 = "http://www.obm.org";
		WebPage wp1 = new WebPage();
		wp1.setPropertyType("WebPage");
		wp1.setPropertyValue(webpage1);
		bd.addWebPage(wp1);
		
		ObmContactConverter converter = createObmContactConverter();
		Contact obmContact = converter.foundationContactToObm(funisContact);
		
		Assert.assertEquals(assistant, obmContact.getAssistant());
		Assert.assertEquals(company, obmContact.getCompany());
		Assert.assertEquals(manager,obmContact.getManager());
		Assert.assertEquals(service,obmContact.getService());
		Assert.assertEquals(title, obmContact.getTitle());
		
		Address addr = obmContact.getAddresses().get("WORK;X-OBM-Ref1");
		Assert.assertNotNull(addr);
		Assert.assertEquals(street, addr.getStreet());
		Assert.assertEquals(postalCode, addr.getZipCode());
		Assert.assertEquals(extendedAddress, addr.getExpressPostal());
		Assert.assertEquals(city, addr.getTown());
		Assert.assertEquals(country, addr.getCountry());
		Assert.assertEquals(state, addr.getState());
		
		Assert.assertEquals(3, obmContact.getEmails().size());
		Assert.assertEquals(email1, obmContact.getEmails().get("INTERNET;X-OBM-Ref1").getEmail());
		Assert.assertEquals(email2, obmContact.getEmails().get("INTERNET;X-OBM-Ref2").getEmail());
		Assert.assertEquals(email3, obmContact.getEmails().get("INTERNET;X-OBM-Ref3").getEmail());
		
		Assert.assertEquals(4, obmContact.getPhones().size());
		Assert.assertEquals(phone1, obmContact.getPhones().get("WORK;VOICE;X-OBM-Ref1").getNumber());
		Assert.assertEquals(phone2, obmContact.getPhones().get("WORK;VOICE;X-OBM-Ref2").getNumber());
		Assert.assertEquals(phone3, obmContact.getPhones().get("WORK;VOICE;X-OBM-Ref3").getNumber());
		Assert.assertEquals(pager1, obmContact.getPhones().get("PAGER;X-OBM-Ref1").getNumber());
		
		Assert.assertEquals(1, obmContact.getWebsites().size());
		Assert.assertEquals(webpage1, obmContact.getWebsite().getUrl());
		
	}

	@Test
	public void testFoundationContactToObmConvertPersonalDetail(){
		DateFormat funisDate = new SimpleDateFormat("yyyyMMdd");
		final String anniversary = "20000101";
		final String birthday = "19870204";
		final String spouse = "Spouse";
		
		com.funambol.common.pim.contact.Contact funisContact = new com.funambol.common.pim.contact.Contact();
		PersonalDetail personalDetail = funisContact.getPersonalDetail();
		personalDetail.setAnniversary(anniversary);
		personalDetail.setBirthday(birthday);
		personalDetail.setSpouse(spouse);
		
		final String street = "1 Rue OBM WORK";
		final String postalCode = "31000"; 
		final String extendedAddress = "expressPostal";
		final String city = "Toulouse";
		final String country = "France";
		final String state = "State";
		personalDetail.getAddress().getCity().setPropertyValue(city);
		personalDetail.getAddress().getCountry().setPropertyValue(country);
		personalDetail.getAddress().getPostOfficeAddress().setPropertyValue(extendedAddress);
		personalDetail.getAddress().getPostalCode().setPropertyValue(postalCode);
		personalDetail.getAddress().getState().setPropertyValue(state);
		personalDetail.getAddress().getStreet().setPropertyValue(street);
		
		final String email1 = "email1@obm.org";
		final String email2 = "email2@obm.org";
		final String email3 = "email3@obm.org";
		
		com.funambol.common.pim.contact.Email emailF1 = new com.funambol.common.pim.contact.Email();
		emailF1.setPropertyType("EmailAddress");
		emailF1.setPropertyValue(email1);
		personalDetail.addEmail(emailF1);
		
		com.funambol.common.pim.contact.Email emailF2 = new com.funambol.common.pim.contact.Email();
		emailF2.setPropertyType("Email2Address");
		emailF2.setPropertyValue(email2);
		personalDetail.addEmail(emailF2);
		
		com.funambol.common.pim.contact.Email emailF3 = new com.funambol.common.pim.contact.Email();
		emailF3.setPropertyType("Email3Address");
		emailF3.setPropertyValue(email3);
		personalDetail.addEmail(emailF3);
		
		final String phone1 = "1111111111";
		final String phone2 = "2222222222";
		final String phone3 = "3333333333";
		final String mobile1 = "4444444444";
		final String mobile2 = "5555555555";
		final String mobile3 = "6666666666";
		
		com.funambol.common.pim.contact.Phone phoneF1 = new com.funambol.common.pim.contact.Phone();
		phoneF1.setPropertyType("HomeTelephoneNumber");
		phoneF1.setPropertyValue(phone1);
		personalDetail.addPhone(phoneF1);
		
		com.funambol.common.pim.contact.Phone phoneF2 = new com.funambol.common.pim.contact.Phone();
		phoneF2.setPropertyType("Home2TelephoneNumber");
		phoneF2.setPropertyValue(phone2);
		personalDetail.addPhone(phoneF2);
		
		com.funambol.common.pim.contact.Phone phoneF3 = new com.funambol.common.pim.contact.Phone();
		phoneF3.setPropertyType("Home3TelephoneNumber");
		phoneF3.setPropertyValue(phone3);
		personalDetail.addPhone(phoneF3);
		
		com.funambol.common.pim.contact.Phone mobileF1 = new com.funambol.common.pim.contact.Phone();
		mobileF1.setPropertyType("MobileTelephoneNumber");
		mobileF1.setPropertyValue(mobile1);
		personalDetail.addPhone(mobileF1);
		
		com.funambol.common.pim.contact.Phone mobileF2 = new com.funambol.common.pim.contact.Phone();
		mobileF2.setPropertyType("Mobile2TelephoneNumber");
		mobileF2.setPropertyValue(mobile2);
		personalDetail.addPhone(mobileF2);
		
		com.funambol.common.pim.contact.Phone mobileF3 = new com.funambol.common.pim.contact.Phone();
		mobileF3.setPropertyType("Mobile3TelephoneNumber");
		mobileF3.setPropertyValue(mobile3);
		personalDetail.addPhone(mobileF3);
		
		final String webpage1 = "http://www.obm.org";
		WebPage wp1 = new WebPage();
		wp1.setPropertyType("WebPage");
		wp1.setPropertyValue(webpage1);
		personalDetail.addWebPage(wp1);
		
		////////
		ObmContactConverter converter = createObmContactConverter();
		Contact obmContact = converter.foundationContactToObm(funisContact);
	
		Assert.assertEquals(anniversary, funisDate.format(obmContact.getAnniversary()));
		Assert.assertEquals(birthday, funisDate.format(obmContact.getBirthday()));
		Assert.assertEquals(spouse, obmContact.getSpouse());
		
		Address addr = obmContact.getAddresses().get("HOME;X-OBM-Ref1");
		Assert.assertNotNull(addr);
		Assert.assertEquals(street, addr.getStreet());
		Assert.assertEquals(postalCode, addr.getZipCode());
		Assert.assertEquals(extendedAddress, addr.getExpressPostal());
		Assert.assertEquals(city, addr.getTown());
		Assert.assertEquals(country, addr.getCountry());
		Assert.assertEquals(state, addr.getState());
		
		Assert.assertEquals(3, obmContact.getEmails().size());
		Assert.assertEquals(email1, obmContact.getEmails().get("INTERNET;X-OBM-Ref1").getEmail());
		Assert.assertEquals(email2, obmContact.getEmails().get("INTERNET;X-OBM-Ref2").getEmail());
		Assert.assertEquals(email3, obmContact.getEmails().get("INTERNET;X-OBM-Ref3").getEmail());
		
		Assert.assertEquals(6, obmContact.getPhones().size());
		Assert.assertEquals(phone1, obmContact.getPhones().get("HOME;VOICE;X-OBM-Ref1").getNumber());
		Assert.assertEquals(phone2, obmContact.getPhones().get("HOME;VOICE;X-OBM-Ref2").getNumber());
		Assert.assertEquals(phone3, obmContact.getPhones().get("HOME;VOICE;X-OBM-Ref3").getNumber());
		Assert.assertEquals(mobile1, obmContact.getPhones().get("CELL;VOICE;X-OBM-Ref1").getNumber());
		Assert.assertEquals(mobile2, obmContact.getPhones().get("CELL;VOICE;X-OBM-Ref2").getNumber());
		Assert.assertEquals(mobile3, obmContact.getPhones().get("CELL;CAR;VOICE;X-OBM-Ref1").getNumber());
		
		Assert.assertEquals(1, obmContact.getWebsites().size());
		Assert.assertEquals(webpage1, obmContact.getWebsite().getUrl());
	}
	
	@Test
	public void testFoundationContactToObmConvertEmailAndIM(){
		
		com.funambol.common.pim.contact.Contact funisContact = new com.funambol.common.pim.contact.Contact();
		PersonalDetail personalDetail = funisContact.getPersonalDetail();
		
		final String email1 = "email1@obm.org";
		final String email2 = "email2@obm.org";
		final String email3 = "email3@obm.org";
		
		final String xmpp1 = "xmpp1@obm.org";
		final String xmpp2 = "xmpp2@obm.org";
		final String xmpp3 = "xmpp3@obm.org";
		
		com.funambol.common.pim.contact.Email emailF1 = new com.funambol.common.pim.contact.Email();
		emailF1.setPropertyType("EmailAddress");
		emailF1.setPropertyValue(email1);
		personalDetail.addEmail(emailF1);
		
		com.funambol.common.pim.contact.Email emailF2 = new com.funambol.common.pim.contact.Email();
		emailF2.setPropertyType("Email2Address");
		emailF2.setPropertyValue(email2);
		personalDetail.addEmail(emailF2);
		
		com.funambol.common.pim.contact.Email emailF3 = new com.funambol.common.pim.contact.Email();
		emailF3.setPropertyType("Email3Address");
		emailF3.setPropertyValue(email3);
		personalDetail.addEmail(emailF3);
		
		com.funambol.common.pim.contact.Email xmppF1 = new com.funambol.common.pim.contact.Email();
		xmppF1.setPropertyType("IMAddress");
		xmppF1.setPropertyValue(xmpp1);
		personalDetail.addEmail(xmppF1);
		
		com.funambol.common.pim.contact.Email xmppF2 = new com.funambol.common.pim.contact.Email();
		xmppF2.setPropertyType("IM2Address");
		xmppF2.setPropertyValue(xmpp2);
		personalDetail.addEmail(xmppF2);
		
		com.funambol.common.pim.contact.Email xmppF3 = new com.funambol.common.pim.contact.Email();
		xmppF3.setPropertyType("IM3Address");
		xmppF3.setPropertyValue(xmpp3);
		personalDetail.addEmail(xmppF3);
		
		////////
		ObmContactConverter converter = createObmContactConverter();
		Contact obmContact = converter.foundationContactToObm(funisContact);
	
		Assert.assertEquals(3, obmContact.getEmails().size());
		Assert.assertEquals(email1, obmContact.getEmails().get("INTERNET;X-OBM-Ref1").getEmail());
		Assert.assertEquals(email2, obmContact.getEmails().get("INTERNET;X-OBM-Ref2").getEmail());
		Assert.assertEquals(email3, obmContact.getEmails().get("INTERNET;X-OBM-Ref3").getEmail());
		
		Assert.assertEquals(3, obmContact.getImIdentifiers().size());
		Assert.assertEquals(xmpp1, obmContact.getImIdentifiers().get("XMPP;X-OBM-Ref1").getId());
		Assert.assertEquals(xmpp2, obmContact.getImIdentifiers().get("XMPP;X-OBM-Ref2").getId());
		Assert.assertEquals(xmpp3, obmContact.getImIdentifiers().get("XMPP;X-OBM-Ref3").getId());
	}

	@Test
	public void testFoundationContactToObmConvertDate(){
		DateFormat funisDate = new SimpleDateFormat("yyyyMMdd");
		final String anniversary = "2001-02-03";
		final String birthday = "19870204";
		
		com.funambol.common.pim.contact.Contact funisContact = new com.funambol.common.pim.contact.Contact();
		PersonalDetail personalDetail = funisContact.getPersonalDetail();
		personalDetail.setAnniversary(anniversary);
		personalDetail.setBirthday(birthday);
		
		ObmContactConverter converter = createObmContactConverter();
		Contact obmContact = converter.foundationContactToObm(funisContact);

		Assert.assertEquals(anniversary.replaceAll("-", ""), funisDate.format(obmContact.getAnniversary()));
		Assert.assertEquals(birthday, funisDate.format(obmContact.getBirthday()));
	}
	
	/**
	 * 
	 */
	@Test
	public void testFoundationContactToObmConvertNote(){
		com.funambol.common.pim.contact.Contact funisContact = new com.funambol.common.pim.contact.Contact();
		final String comment = "comment";
		Note note = new Note();
		note.setPropertyType(ObmContactConverter.COMMENT);
		note.setPropertyValue(comment);
		funisContact.setNotes(ImmutableList.of(note));
		ObmContactConverter converter = createObmContactConverter();
		Contact obmContact = converter.foundationContactToObm(funisContact);
				
		Assert.assertEquals(comment, obmContact.getComment());
	}

	@Test
	public void testFoundationContactToObmConvertEmptyUid(){
		com.funambol.common.pim.contact.Contact funisContact = new com.funambol.common.pim.contact.Contact();
		funisContact.setUid("");
		ObmContactConverter converter = createObmContactConverter();
		Contact obmContact = converter.foundationContactToObm(funisContact);
				
		Assert.assertNull(obmContact.getUid());
	}
	
	@Test
	public void testFullConvertObmContactTofoundation() {

		Contact obmContact = new Contact();
		final String aka = "AKA";
		obmContact.setAka(aka);
		final Date anniversary = new Date();
		obmContact.setAnniversary(anniversary);
		final String assistant = "assistant";
		obmContact.setAssistant(assistant);
		final Date birthday = new Date();
		obmContact.setBirthday(birthday);
		final String calUri = "http://www.calUri.fr/calUri";
		obmContact.setCalUri(calUri);
		final Boolean collected = false;
		obmContact.setCollected(collected);
		final String comment = "comment";
		obmContact.setComment(comment);
		final String commonName = "Common Name";
		obmContact.setCommonname(commonName);
		final String company = "Company";
		obmContact.setCompany(company);
		final String firstname = "firstname";
		obmContact.setFirstname(firstname);
		final String lastname = "lastname";
		obmContact.setLastname(lastname);
		final String manager = "manager";
		obmContact.setManager(manager);
		final String middlename = "MiddleName";
		obmContact.setMiddlename(middlename);
		final String service = "service";
		obmContact.setService(service);
		final String spouse = "Spouse";
		obmContact.setSpouse(spouse);
		final String suffix = "Suffix";
		obmContact.setSuffix(suffix);
		final String title = "title";
		obmContact.setTitle(title);
		final Integer uid = 1;
		obmContact.setUid(uid);

		//
		final Address addrWork = new Address("1 Rue OBM WORK", "31000",
				"expressPostal", "Toulouse", "France", "State");
		obmContact.addAddress("WORK;X-OBM-Ref1", addrWork);
		final Address addrHome = new Address("2 Rue OBM HOME", "32000",
				"expressPostal", "Toulouse", "France", "State");
		obmContact.addAddress("HOME;X-OBM-Ref1", addrHome);
		final Address addrOther = new Address("3 Rue OBM OTHER", "33000",
				"expressPostal", "Toulouse", "France", "State");
		obmContact.addAddress("OTHER;X-OBM-Ref1", addrOther);

		final Email emailInternet = new Email("internet@email.fr");
		obmContact.addEmail("INTERNET;X-OBM-Ref1", emailInternet);
		final Email emailOther = new Email("other@email.fr");
		obmContact.addEmail("OTHER;X-OBM-Ref1", emailOther);

		final InstantMessagingId xmpp = new InstantMessagingId("XMPP",
				"xmpp@obm.org");
		obmContact.addIMIdentifier("XMPP;X-OBM-Ref1", xmpp);


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

		Website url = new Website("URL;X-OBM-Ref1", "http://url/");
		obmContact.addWebsite(url);
		Website calUriWebSite = new Website("CALURI;X-OBM-Ref1", "http://caluri/");
		obmContact.addWebsite(calUriWebSite);
		Website blog = new Website("BLOG;X-OBM-Ref1", "http://blog/");
		obmContact.addWebsite(blog);
		Website otherWebsite = new Website("OTHER;X-OBM-Ref1", "http://other/");
		obmContact.addWebsite(otherWebsite);
		
		////////////////////
		ObmContactConverter converter = createObmContactConverter();
		com.funambol.common.pim.contact.Contact funisContact = converter
				.obmContactTofoundation(obmContact);
		
		Assert.assertEquals(aka,funisContact.getName().getNickname().getPropertyValue());
		Assert.assertEquals(assistant, funisContact.getBusinessDetail().getAssistant());
		DateFormat funisDate = new SimpleDateFormat("yyyyMMdd");
		Assert.assertEquals(funisDate.format(anniversary),funisContact.getPersonalDetail().getAnniversary());
		Assert.assertEquals(funisDate.format(birthday),funisContact.getPersonalDetail().getBirthday());
		Assert.assertEquals(comment,((Note)funisContact.getNotes().get(0)).getPropertyValueAsString());
		Assert.assertEquals(commonName,funisContact.getName().getDisplayName().getPropertyValueAsString());
		Assert.assertEquals(company,funisContact.getBusinessDetail().getCompany().getPropertyValueAsString());
		Assert.assertEquals(firstname,funisContact.getName().getFirstName().getPropertyValueAsString());
		Assert.assertEquals(lastname,funisContact.getName().getLastName().getPropertyValueAsString());
		Assert.assertEquals(manager,funisContact.getBusinessDetail().getManager());
		Assert.assertEquals(middlename,funisContact.getName().getMiddleName().getPropertyValueAsString());
		Assert.assertEquals(service,funisContact.getBusinessDetail().getDepartment().getPropertyValueAsString());
		Assert.assertEquals(spouse,funisContact.getPersonalDetail().getSpouse());
		Assert.assertEquals(suffix,funisContact.getName().getSuffix().getPropertyValueAsString());
		Assert.assertEquals(title,((Title)funisContact.getBusinessDetail().getTitles().get(0)).getPropertyValueAsString());	
//		Assert.assertEquals(calUri,);
		
		assertAddress(obmContact.getAddresses().get("WORK;X-OBM-Ref1"), funisContact.getBusinessDetail().getAddress());
		assertAddress(obmContact.getAddresses().get("HOME;X-OBM-Ref1"), funisContact.getPersonalDetail().getAddress());
		//other address isn't synced obmContact.getAddresses().get("OTHER;X-OBM-Ref1");
		
		assertEmail(obmContact.getEmails().get("INTERNET;X-OBM-Ref1"), ((com.funambol.common.pim.contact.Email)funisContact.getBusinessDetail().getEmails().get(0)));
//		assertEmail(obmContact.getEmails().get("OTHER;X-OBM-Ref1"), ((com.funambol.common.pim.contact.Email)funisContact.getPersonalDetail().getEmails().get(0)));
		
		assertImpps(obmContact.getImIdentifiers(), funisContact.getPersonalDetail().getEmails());
		
		assertPhone(obmContact.getPhones().get("WORK;VOICE;X-OBM-Ref1"), funisContact.getBusinessDetail().getPhones());
		assertPhone(obmContact.getPhones().get("WORK;FAX;X-OBM-Ref1"), funisContact.getBusinessDetail().getPhones());
		assertPhone(obmContact.getPhones().get("PAGER;X-OBM-Ref1"), funisContact.getBusinessDetail().getPhones());
		assertPhone(obmContact.getPhones().get("HOME;VOICE;X-OBM-Ref1"), funisContact.getPersonalDetail().getPhones());
		assertPhone(obmContact.getPhones().get("HOME;FAX;X-OBM-Ref1"), funisContact.getPersonalDetail().getPhones());
		assertPhone(obmContact.getPhones().get("CELL;VOICE;X-OBM-Ref1"), funisContact.getPersonalDetail().getPhones());
//		OTHER;X-OBM-Ref1
		
		assertWebSite(obmContact.getWebsite(), funisContact.getPersonalDetail().getWebPages());
//		CALURI;X-OBM-Ref1
//		BLOG;X-OBM-Ref1
//		OTHER;X-OBM-Ref1
	}

	private void assertWebSite(Website obmWebsite, List<WebPage> funisWebPages) {
		for(WebPage funisObj : funisWebPages){
			Boolean find = false;
			if(	obmWebsite.getUrl().equals(funisObj.getPropertyValueAsString())){
				find = true;
				break;
			}
			Assert.assertTrue(find);
		}
	}

	/**
	 * @param obmPhone
	 * @param funisPhone
	 */
	private void assertPhone(Phone obmPhone, List<com.funambol.common.pim.contact.Phone> funisPhone) {
		Assert.assertNotNull(obmPhone);
		Boolean find = false;
		for(com.funambol.common.pim.contact.Phone funisObj : funisPhone){
			if(	obmPhone.getNumber().equals(funisObj.getPropertyValueAsString())){
				find = true;
				break;
			}
		}
		Assert.assertTrue(find);
	}

	private void assertImpps(Map<String, InstantMessagingId> obmIM,
			List<com.funambol.common.pim.contact.Email> funisIM) {
		for(InstantMessagingId im : obmIM.values()){
			com.funambol.common.pim.contact.Email findFunis = null;
			for(com.funambol.common.pim.contact.Email fim : funisIM){
				if(fim.getPropertyValueAsString().equals(im.getId())){
					findFunis = fim;
					break;
				}
			}
			Assert.assertNotNull(findFunis);
		}
	}

	private void assertEmail(Email obmEmail, com.funambol.common.pim.contact.Email funisEmail) {
		Assert.assertEquals(obmEmail.getEmail(), funisEmail.getPropertyValueAsString());
	}

	private void assertAddress(Address obmAddress,
			com.funambol.common.pim.contact.Address funisAddress) {
		Assert.assertEquals(obmAddress.getCountry(), funisAddress.getCountry().getPropertyValueAsString());
		Assert.assertEquals(obmAddress.getExpressPostal(), funisAddress.getPostOfficeAddress().getPropertyValueAsString());
		Assert.assertEquals(obmAddress.getState(), funisAddress.getState().getPropertyValueAsString());
		Assert.assertEquals(obmAddress.getStreet(), funisAddress.getStreet().getPropertyValueAsString());
		Assert.assertEquals(obmAddress.getTown(), funisAddress.getCity().getPropertyValueAsString());
		Assert.assertEquals(obmAddress.getZipCode(), funisAddress.getPostalCode().getPropertyValueAsString());
		
	}
}
