package org.obm.push.bean;

import java.io.Serializable;
import java.util.Iterator;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Singleton;

public class User implements Serializable {

	@Singleton
	public static class Factory {
		private static final int LOGIN = 0;
		private static final int DOMAIN = 1;
		
		@Inject
		private Factory() {}
		
		public static Factory create() {
			return new Factory();
		}
		
		public String getLoginAtDomain(String userId) {
			return createUser(userId, null, null).getLoginAtDomain();
		}
		
		public User createUser(String userId, String email, String displayName) {
			String[] loginAndDomain = getLoginAndDomain(userId);
			return new User(loginAndDomain[LOGIN], loginAndDomain[DOMAIN], email, displayName);
		}
		
		private String[] getLoginAndDomain(String userId) {
			Iterable<String> parts = splitOnSlashes(userId);
			String[] loginAndDomain = buildUserFromLoginParts(parts);
			if (loginAndDomain == null) {
				parts = splitOnAtSign(userId);
				loginAndDomain = buildUserFromLoginParts(parts);
			}
			if (loginAndDomain == null) {
				throw new IllegalArgumentException();
			}
			return loginAndDomain;
		}
		
		private Iterable<String> splitOnSlashes(String userId) {
			Iterable<String> parts = Splitter.on("\\").split(userId);
			return parts;
		}

		private Iterable<String> splitOnAtSign(String userId) {
			Iterable<String> parts = Splitter.on("@").split(userId);
			return ImmutableList.copyOf(parts).reverse();
		}

		private String[] buildUserFromLoginParts(Iterable<String> parts) {
			int nbParts = Iterables.size(parts);
			if (nbParts > 2) {
				throw new IllegalArgumentException();
			} else if (nbParts == 2) {
				Iterator<String> iterator = parts.iterator();
				String domain = iterator.next();
				String login = iterator.next();
				checkField("domain", domain);
				checkField("login", login);
				return new String[]{login, domain};
			}
			return null;
		}
		
		private void checkField(String key, String field) {
			if (field.contains("@") || field.contains("\\")) {
				throw new IllegalArgumentException(key + " is invalid : " + field);
			}
		}
	}
	
	private final String login;
	private final String domain;
	
	private final String email;
	private final String displayName;

	private User(String login, String domain, String email, String displayName) {
		super();
		this.login = login;
		this.domain = domain;
		this.email = email;
		this.displayName = displayName;
	}
	
	public String getLoginAtDomain() {
		return getLogin() + "@" + getDomain();
	}

	public String getLogin() {
		return login.toLowerCase();
	}
	
	public String getDomain() {
		return domain.toLowerCase();
	}
	
	public String getEmail() {
		return email;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	@Override
	public final int hashCode(){
		return Objects.hashCode(login, domain, email, displayName);
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof User) {
			User that = (User) object;
			return Objects.equal(this.login, that.login)
				&& Objects.equal(this.domain, that.domain)
				&& Objects.equal(this.email, that.email)
				&& Objects.equal(this.displayName, that.displayName);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("login", login)
			.add("domain", domain)
			.add("email", email)
			.add("displayName", displayName)
			.toString();
	}
	
}