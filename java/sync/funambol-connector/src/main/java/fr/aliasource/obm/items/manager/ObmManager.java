package fr.aliasource.obm.items.manager;

import org.obm.sync.auth.AccessToken;
import org.obm.sync.auth.AuthFault;
import org.obm.sync.client.login.LoginService;

import fr.aliasource.funambol.OBMException;

/**
 * The obm manager
 * 
 * @author tom
 *
 */
public abstract class ObmManager {

	protected AccessToken token;
	protected boolean syncReceived = false;
	private final LoginService loginService;
	
	protected ObmManager(final LoginService loginService) {
		this.loginService = loginService;
	}

	public void logIn(String userAtDomain, String pass) throws OBMException {
		try {
			token = loginService.login(userAtDomain, pass);
			if(token == null){
				throw new OBMException("OBM Login refused for user : " + userAtDomain);
			}
		} catch (AuthFault e) {
			throw new OBMException("OBM Login refused for user : " + userAtDomain);
		}
	}

	public void logout() {
		loginService.logout(token);
	}

}
