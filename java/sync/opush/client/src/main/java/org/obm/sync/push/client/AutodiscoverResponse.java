package org.obm.sync.push.client;

import java.util.Locale;
/*
<Autodiscover xmlns:A="http://schemas.microsoft.com/exchange/autodiscover/mobilesync/responseschema/2006">
	<A:Response>
		<A:Culture>en:en</A:Culture>
		<A:User>
			<A:DisplayName>Eduard Dell</A:DisplayName>
			<A:EMailAddress>eduarddell@woodgrovebank.com</A:EMailAddress>
		</A:User>
		<A:Action>
			<A:Settings>
				<A:Server>
					<A:Type>MobileSync</A:Type>
					<A:Url>
						https://loandept.woodgrovebank.com/Microsoft-Server-ActiveSync
					</A:Url>
					<A:Name>
						https://loandept.woodgrovebank.com/Microsoft-Server-ActiveSync
					</A:Name>
				</A:Server>
			</A:Settings>
		</A:Action>
	</A:Response>
</A:Autodiscover>
*/
public class AutodiscoverResponse {
	
	private Locale culture;
	private String userDisplayName;
	private String userEMailAddress;
	private String serverType;
	private String serverUrl;
	private String serverName;
	
	public Locale getCulture() {
		return culture;
	}
	public void setCulture(Locale culture) {
		this.culture = culture;
	}
	public String getUserDisplayName() {
		return userDisplayName;
	}
	public void setUserDisplayName(String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}
	public String getUserEMailAddress() {
		return userEMailAddress;
	}
	public void setUserEMailAddress(String userEMailAddress) {
		this.userEMailAddress = userEMailAddress;
	}
	public String getServerType() {
		return serverType;
	}
	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	public String getServerUrl() {
		return serverUrl;
	}
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
}
