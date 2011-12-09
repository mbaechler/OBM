package org.obm.push.protocol.bean;

public class AutodiscoverRequest {

	private final String emailAddress;
	private final String acceptableResponseSchema;
	
	public AutodiscoverRequest(String emailAddress, String acceptableResponseSchema) {
		this.emailAddress = emailAddress;
		this.acceptableResponseSchema = acceptableResponseSchema;
	}
	
	/**
	 * Is used to identify the user's mailbox in the network
	 *
	 * @return the SMTP e-mail address of the user
	 */
	public String getEmailAddress() {
		return emailAddress;
	}
	
	/**
	 * Example : "http://schemas.microsoft.com/exchange/autodiscover/mobilesync/responseschema/2006".
	 *
	 * @return schema in which the server MUST send the response
	 */
	public String getAcceptableResponseSchema() {
		return acceptableResponseSchema;
	}
	
}