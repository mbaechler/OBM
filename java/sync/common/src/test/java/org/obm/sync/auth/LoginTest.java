package org.obm.sync.auth;


import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;

public class LoginTest {
	private static final String ALTERNATIVE_DOMAIN = "exemple.net";
	private static final String DOMAIN = "exemple.com";
	private static final String LOGIN = "test";
	private static final String FULL_LOGIN = LOGIN + Login.FULL_LOGIN_SEPARATOR + DOMAIN;
	private Login login;

	@Test
	public void creationByFullLogin() {
		login = Login.builder().login(FULL_LOGIN).build();
		assertLogin(LOGIN, DOMAIN, FULL_LOGIN);
	}

	@Test
	public void creationByFullLoginAndMatchingDomain() {
		login = Login.builder().login(FULL_LOGIN).domain(DOMAIN).build();
		assertLogin(LOGIN, DOMAIN, FULL_LOGIN);
	}
	
	@Test(expected=IllegalStateException.class)
	public void creationByFullLoginAndDifferentDomain() {
		login = Login.builder().login(FULL_LOGIN).domain("differentdomain").build();
	}
	
	@Test
	public void creationByLoginParts() {
		login = Login.builder().login(LOGIN).domain(DOMAIN).build();
		assertLogin(LOGIN, DOMAIN, FULL_LOGIN);
	}

	@Test
	public void nullDomain() {
		login = Login.builder().login(LOGIN).build();
		assertLogin(LOGIN, null, LOGIN);
	}
	
	@Test(expected=IllegalStateException.class)
	public void nullFullLogin() {
		login = Login.builder().build();
	}

	@Test(expected=IllegalStateException.class)
	public void nullLogin() {
		login = Login.builder().domain(DOMAIN).build();
	}

	@Test
	public void alterDomain() {
		login = Login.builder().login(FULL_LOGIN).build();
		login = login.withDomain(ALTERNATIVE_DOMAIN);
		Login alternative = new Login(LOGIN, ALTERNATIVE_DOMAIN);
		assertLogin(LOGIN, ALTERNATIVE_DOMAIN, alternative.getFullLogin());
	}

	private void assertLogin(String shortLogin, String domain, String fullLogin) {
		assertThat(login.getLogin()).isEqualTo(shortLogin);
		assertThat(login.getDomain()).isEqualTo(domain);
		assertThat(login.getFullLogin()).isEqualTo(fullLogin);
	}
}
