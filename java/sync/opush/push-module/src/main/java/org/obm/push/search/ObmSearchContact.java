package org.obm.push.search;

import java.util.LinkedList;
import java.util.List;

import org.obm.push.bean.BackendSession;
import org.obm.push.bean.SearchResult;
import org.obm.push.bean.StoreName;
import org.obm.push.contacts.ContactConverter;
import org.obm.sync.auth.AccessToken;
import org.obm.sync.auth.ServerFault;
import org.obm.sync.book.Contact;
import org.obm.sync.client.login.LoginService;
import org.obm.sync.services.IAddressBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ObmSearchContact implements ISearchSource {

	private final static Logger logger = LoggerFactory.getLogger(ObmSearchContact.class);
	private final IAddressBook bookClient;
	private final LoginService login;
	
	@Inject
	private ObmSearchContact(IAddressBook bookClient, LoginService login) {
		super();
		this.bookClient = bookClient;
		this.login = login;
	}
	
	@Override
	public StoreName getStoreName() {
		return StoreName.GAL;
	}

	@Override
	public List<SearchResult> search(BackendSession bs, String query, Integer limit) {
		IAddressBook bc = getBookClient();
		AccessToken token = login.login(bs.getUser().getLoginAtDomain(), bs.getPassword(), "o-push");
		List<SearchResult> ret = new LinkedList<SearchResult>();
		ContactConverter cc = new ContactConverter();
		try {
			List<Contact> contacts = bc.searchContact(token, query, limit);
			for (Contact contact: contacts) {
				ret.add(cc.convertToSearchResult(contact));
			}
		} catch (ServerFault e) {
			logger.error(e.getMessage(), e);
		} finally {
			login.logout(token);
		}
		return ret;
	}
	
	private IAddressBook getBookClient() {
		return bookClient;
	}
	
}
