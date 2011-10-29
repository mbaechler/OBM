package fr.aliasource.obm.items.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.obm.sync.book.Address;
import org.obm.sync.book.Contact;
import org.obm.sync.book.Email;
import org.obm.sync.book.InstantMessagingId;
import org.obm.sync.book.Website;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.contact.BusinessDetail;
import com.funambol.common.pim.contact.Note;
import com.funambol.common.pim.contact.PersonalDetail;
import com.funambol.common.pim.contact.Phone;
import com.funambol.common.pim.contact.Title;
import com.funambol.common.pim.contact.WebPage;
import com.google.common.collect.Lists;
import com.google.inject.Singleton;

import fr.aliasource.funambol.utils.ContactHelper;
import fr.aliasource.obm.items.manager.LabelMapping;

@Singleton
public class ObmContactConverter {
	
	private LabelMapping lm;
	private SimpleDateFormat funisDate;
	private final Logger logger = LoggerFactory.getLogger(ObmContactConverter.class);
	
	
	public ObmContactConverter() {
		lm = new LabelMapping();
		funisDate = new SimpleDateFormat("yyyyMMdd");
	}
	
	public com.funambol.common.pim.contact.Contact obmContactTofoundation(
			Contact obmcontact) {
		com.funambol.common.pim.contact.Contact contact = new com.funambol.common.pim.contact.Contact();

		if(obmcontact.getUid() != null){
			contact.setUid("" + obmcontact.getUid());
		}
		contact.getName().getFirstName().setPropertyValue(
				obmcontact.getFirstname());
		contact.getName().getLastName().setPropertyValue(
				obmcontact.getLastname());
		contact.getName().getDisplayName().setPropertyValue(
				obmcontact.getCommonname());
		contact.getName().getNickname().setPropertyValue(obmcontact.getAka());

		contact.getName().getMiddleName().setPropertyValue(obmcontact.getMiddlename());
		contact.getName().getSuffix().setPropertyValue(obmcontact.getSuffix());

		BusinessDetail bd = contact.getBusinessDetail();
		PersonalDetail pd = contact.getPersonalDetail();

		pd.setSpouse(obmcontact.getSpouse());
		
		bd.setAssistant(obmcontact.getAssistant());
		bd.setManager(obmcontact.getManager());
		bd.getCompany().setPropertyValue(obmcontact.getCompany());
		bd.getDepartment().setPropertyValue(obmcontact.getService());
		if (obmcontact.getTitle() != null) {
			Title t = new Title();
			t.setTitleType("JobTitle");
			t.setPropertyValue(obmcontact.getTitle());
			bd.setTitles(Lists.newArrayList(t));
		}

		for (String label : obmcontact.getEmails().keySet()) {
			String emailLabel = lm.toFunis(label);
			if(!StringUtils.isBlank(emailLabel)){
				Email e = obmcontact.getEmails().get(label);
				com.funambol.common.pim.contact.Email funisMail = new com.funambol.common.pim.contact.Email(
						e.getEmail());
				funisMail.setPropertyType(emailLabel);
				bd.addEmail(funisMail);
			}
		}

		obmToFunis(bd.getAddress(), obmcontact.getAddresses().get(
				lm.toOBM("work")));

		List<Phone> workFunambolPhone = getFiltredFunambolPhone(obmcontact.getWorkPhones());
		workFunambolPhone.addAll(getFiltredFunambolPhone(obmcontact.getPagers()));
		bd.setPhones(workFunambolPhone);
		
		List<Phone> homeFunambolPhone = getFiltredFunambolPhone(obmcontact.getHomePhones());
		homeFunambolPhone.addAll(getFiltredFunambolPhone(obmcontact.getCellPhones()));
		pd.setPhones(homeFunambolPhone);
		
		int imRec = 0;
		for (InstantMessagingId im : obmcontact.getImIdentifiers().values()){
			String label = lm.getFunisIMLabel(imRec);
			if(!StringUtils.isEmpty(label)){
				com.funambol.common.pim.contact.Email imf = new com.funambol.common.pim.contact.Email(im.getId());
				imf.setPropertyType(label);
				pd.addEmail(imf);
			}
		}
		
		for (Website ws : obmcontact.getWebsites()) {
			String webPageTypeLabel = lm.toFunis(ws.getLabel());
			if(!StringUtils.isBlank(webPageTypeLabel)){
				WebPage wp = new WebPage(ws.getUrl());
				wp.setWebPageType(webPageTypeLabel);
				bd.addWebPage(wp);
			}
		}

		obmToFunis(pd.getAddress(), obmcontact.getAddresses().get(
				lm.toOBM("home")));
		obmToFunis(pd.getOtherAddress(), obmcontact.getAddresses().get(
				lm.toOBM("other")));

		ContactHelper.setFoundationNote(contact, obmcontact.getComment(),
				ContactHelper.COMMENT);

		contact.setSensitivity(new Short((short) 2)); // olPrivate

		if (obmcontact.getBirthday() != null) {
			pd.setBirthday(funisDate.format(obmcontact.getBirthday()));
		}
		if (obmcontact.getAnniversary() != null) {
			pd.setAnniversary(funisDate.format(obmcontact.getAnniversary()));
		}
		return contact;
	}

	private List<Phone> getFiltredFunambolPhone(Map<String, org.obm.sync.book.Phone> phones) {
		List<Phone> ret = Lists.newLinkedList();
		for (Entry<String,org.obm.sync.book.Phone> entry : phones.entrySet()) {
			String typeLabel = lm.toFunis(entry.getKey());
			if(!StringUtils.isBlank(typeLabel)){
				Phone p = new Phone(entry.getValue().getNumber());
				p.setPhoneType(typeLabel);
				ret.add(p);
			}
		}
		return ret;
	}

	private void obmToFunis(com.funambol.common.pim.contact.Address target,
			Address source) {
		if (target == null) {
			logger.warn("target addr is null");
			return;
		}
		if (source != null) {
			target.getStreet().setPropertyValue(source.getStreet());
			target.getCity().setPropertyValue(source.getTown());
			target.getCountry().setPropertyValue(source.getCountry());
			target.getState().setPropertyValue(source.getState());
			target.getPostalCode().setPropertyValue(source.getZipCode());
			target.getPostOfficeAddress().setPropertyValue(
					source.getExpressPostal());
			logger.info("copied address with street: " + source.getStreet()
					+ " to " + target);
		}
	}
	
	public Contact foundationContactToObm(
			com.funambol.common.pim.contact.Contact funis) {
		LabelMapping lm = new LabelMapping();

		Contact contact = new Contact();

		if (!StringUtils.isEmpty(funis.getUid()) && StringUtils.isNumeric(funis.getUid())) {
			contact.setUid(new Integer(funis.getUid()));
		}

		BusinessDetail bd = funis.getBusinessDetail();
		PersonalDetail pd = funis.getPersonalDetail();

		contact.setFirstname(ContactHelper.nullToEmptyString(funis.getName()
				.getFirstName().getPropertyValueAsString()));
		contact.setLastname(ContactHelper.getLastName(funis));
		contact.setCommonname(ContactHelper.nullToEmptyString(funis.getName().getDisplayName().getPropertyValueAsString()));
		contact.setMiddlename(ContactHelper.nullToEmptyString(funis.getName().getMiddleName().getPropertyValueAsString()));
		contact.setSuffix(ContactHelper.nullToEmptyString(funis.getName().getSuffix().getPropertyValueAsString()));
		contact.setAka(ContactHelper.nullToEmptyString(s(funis.getName()
				.getNickname())));
		contact.setCompany(s(bd.getCompany()));
		contact.setService(s(bd.getDepartment()));
		contact.setAssistant(bd.getAssistant());
		contact.setManager(bd.getManager());
		contact.setSpouse(pd.getSpouse());
		
		if (bd.getTitles() != null && bd.getTitles().size() > 0) {
			contact.setTitle(((Title) bd.getTitles().get(0))
					.getPropertyValueAsString());
		}

		// addresses
		if (bd.getAddress() != null) {
			contact.addAddress(lm.toOBM("work"), funisToObm(bd.getAddress()));
		}
		if (pd.getAddress() != null) {
			contact.addAddress(lm.toOBM("home"), funisToObm(pd.getAddress()));
		}
		if (pd.getOtherAddress() != null) {
			contact.addAddress(lm.toOBM("other"), funisToObm(pd
					.getOtherAddress()));
		}

		// phones
		List<Phone> lph = new LinkedList<Phone>();
		lph.addAll(bd.getPhones());
		lph.addAll(pd.getPhones());
		for (Phone p : lph) {
			String label = lm.toOBM(p.getPhoneType());
			String value = s(p);
			if (!StringUtils.isEmpty(label) && !StringUtils.isEmpty(value))
				contact.addPhone(label,
						new org.obm.sync.book.Phone(value));
		}

		// emails
		List<com.funambol.common.pim.contact.Email> lem = new LinkedList<com.funambol.common.pim.contact.Email>();
		lem.addAll(bd.getEmails());
		lem.addAll(pd.getEmails());
		for (com.funambol.common.pim.contact.Email em : lem) {
			
			if (em != null && s(em) != null && s(em).length() > 0) {
				String emailLabel = lm.toOBM(em.getEmailType());
				if(!StringUtils.isEmpty(emailLabel)){
					if(isInstantMessaging(emailLabel)){
						contact.addIMIdentifier(emailLabel, new InstantMessagingId("XMPP", s(em)));
					} else {
						contact.addEmail(emailLabel, new Email(s(em)));
					}
				}
			}
		}

		// websites
		List<WebPage> lwp = new LinkedList<WebPage>();
		lwp.addAll(bd.getWebPages());
		lwp.addAll(pd.getWebPages());
		for (WebPage wp : lwp) {
			String label = lm.toOBM(wp.getWebPageType());
			String value = s(wp);
			if (!StringUtils.isEmpty(label) && !StringUtils.isEmpty(value)) {
				contact.addWebsite(new Website(label,
						value));
			}
		}

		contact.setComment(ContactHelper.nullToEmptyString(getNote(funis.getNotes(), ContactHelper.COMMENT)));

		if (!StringUtils.isEmpty(pd.getBirthday())) {
			try {
				Date d = funisDate.parse(sanitizeFunambolDate(pd.getBirthday()));
				contact.setBirthday(d);
			} catch (ParseException e) {
				logger.error("cannot parse bday: " + pd.getBirthday(), e);
			}
		}
		
		if (!StringUtils.isEmpty(pd.getAnniversary())) {
			try {
				Date d = funisDate.parse(sanitizeFunambolDate(pd.getAnniversary()));
				contact.setAnniversary(d);
			} catch (ParseException e) {
				logger.error("cannot parse bday: " + pd.getAnniversary(), e);
			}
		}

		return contact;
	}
	
	private String sanitizeFunambolDate(String funisDate) {
		return funisDate.replaceAll("-", "");
	}

	private boolean isInstantMessaging(String emailLabel) {
		return emailLabel.startsWith(Contact.OBM_REF_IM_XMPP);
	}

	private String s(Property p) {
		return p.getPropertyValueAsString();
	}

	private Address funisToObm(com.funambol.common.pim.contact.Address funis) {
		org.obm.sync.book.Address obm = new org.obm.sync.book.Address(s(funis
				.getStreet()), s(funis.getPostalCode()), s(funis
				.getPostOfficeAddress()), s(funis.getCity()), s(funis
				.getCountry()), s(funis.getState()));
		return obm;
	}
	
	public static String getNote(List<Note> notes, String type) {
		String result = "";

		if (notes != null) {
			for (int i = 0; i < notes.size(); i++) {
				if (notes.get(i).getNoteType().equalsIgnoreCase(type)) {
					result = notes.get(i).getPropertyValueAsString();
					break;
				}
			}
		}
		return result;
	}


}
