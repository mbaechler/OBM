package fr.aliasource.obm.items.manager;

import org.obm.sync.auth.AccessToken;
import org.obm.sync.auth.AuthFault;
import org.obm.sync.client.login.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.aliasource.funambol.OBMException;

public abstract class ObmManager {

	protected final Logger logger = LoggerFactory.getLogger(ObmManager.class);
	
	private final LoginService loginService;
	
	protected ObmManager(final LoginService loginService) {
		this.loginService = loginService;
	}

	public AccessToken logIn(String userAtDomain, String pass) throws OBMException {
		try {
			AccessToken token = loginService.login(userAtDomain, pass);
			if(token == null){
				throw new OBMException("OBM Login refused for user : " + userAtDomain);
			}
			return token;
		} catch (AuthFault e) {
			throw new OBMException("OBM Login refused for user : " + userAtDomain);
		}
	}

	public void logout(AccessToken token) {
		loginService.logout(token);
	}

}
