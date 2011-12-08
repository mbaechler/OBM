package org.obm.sync.client.login;

import org.obm.sync.auth.AccessToken;
import org.obm.sync.auth.MavenVersion;
import org.obm.sync.client.impl.AbstractClientImpl;
import org.obm.sync.client.impl.SyncClientException;
import org.obm.sync.locators.Locator;
import org.obm.sync.utils.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;

public class LoginClient extends AbstractClientImpl implements LoginService {

	private final Locator locator;

	@Inject
	private LoginClient(SyncClientException syncClientException, Locator locator) {
		super(syncClientException);
		this.locator = locator;
	}
	
	public AccessToken login(String loginAtDomain, String password, String origin) {
		Multimap<String, String> params = ArrayListMultimap.create();
		params.put("login", loginAtDomain);
		params.put("password", password);
		params.put("origin", origin);

		AccessToken token = new AccessToken(0, 0, origin);
		token.setUser(loginAtDomain.split("@", 2)[0]);
		token.setDomain(loginAtDomain.split("@", 2)[1]);
		
		Document doc = execute(token, "/login/doLogin", params);
		Element root = doc.getDocumentElement();
		String email = DOMUtils.getElementText(root, "email");
		String sid = DOMUtils.getElementText(root, "sid");
		Element v = DOMUtils.getUniqueElement(root, "version");
		MavenVersion version = new MavenVersion();
		if (v != null) {
			version.setMajor(v.getAttribute("major"));
			version.setMinor(v.getAttribute("minor"));
			version.setRelease(v.getAttribute("release"));
		}
		token.setSessionId(sid);
		token.setVersion(version);
		token.setEmail(email);
		return token;
	}

	public void logout(AccessToken at) {
		Multimap<String, String> params = initParams(at);
		executeVoid(at, "/login/doLogout", params);
	}

	@Override
	protected Locator getLocator() {
		return locator;
	}
	
}
