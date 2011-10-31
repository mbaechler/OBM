/* ***** BEGIN LICENSE BLOCK *****
 *
 * %%
 * Copyright (C) 2000 - 2011 Linagora
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK ***** */
package org.obm.push.protocol.provisioning;

import java.math.BigDecimal;

import org.obm.push.utils.DOMUtils;
import org.w3c.dom.Element;

/**
 * Policy type for protocol 12.x (windows mobile 6, iPhone, etc)
 */

public class MSEASProvisioingWBXML extends Policy {

	private BigDecimal protocolVersion;

	public MSEASProvisioingWBXML(BigDecimal bigDecimal) {
		this.protocolVersion = bigDecimal;
	}

	private void p(Element provDoc, String field, String value) {
		DOMUtils.createElementAndText(provDoc, field, value);
	}

	@Override
	public void serialize(Element data) {

		Element provDoc = DOMUtils.createElement(data, "EASProvisionDoc");

		p(provDoc, "DevicePasswordEnabled", "0");
		p(provDoc, "AlphanumericDevicePasswordRequired", "0");

		p(provDoc, "PasswordRecoveryEnabled", "0");
		p(provDoc, "DeviceEncryptionEnabled", "0");
		p(provDoc, "AttachmentsEnabled", "1");
		p(provDoc, "MinDevicePasswordLength", "4");

		p(provDoc, "MaxInactivityTimeDeviceLock", "900");
		p(provDoc, "MaxDevicePasswordFailedAttempts", "8");
		DOMUtils.createElement(provDoc, "MaxAttachmentSize");

		p(provDoc, "AllowSimpleDevicePassword", "1");
		DOMUtils.createElement(provDoc, "DevicePasswordExpiration");
		p(provDoc, "DevicePasswordHistory", "0");

		if (protocolVersion.compareTo(new BigDecimal("12.0")) > 0) {
			p(provDoc, "AllowStorageCard", "1");
			p(provDoc, "AllowCamera", "1");
			p(provDoc, "RequireDeviceEncryption", "0");
			p(provDoc, "AllowUnsignedApplications", "1");
			p(provDoc, "AllowUnsignedInstallationPackages", "1");

			p(provDoc, "MinDevicePasswordComplexCharacters", "3");
			p(provDoc, "AllowWiFi", "1");
			p(provDoc, "AllowTextMessaging", "1");
			p(provDoc, "AllowPOPIMAPEmail", "1");
			p(provDoc, "AllowBluetooth", "2");
			p(provDoc, "AllowIrDA", "1");
			p(provDoc, "RequireManualSyncWhenRoaming", "0");
			p(provDoc, "AllowDesktopSync", "1");
			p(provDoc, "MaxCalendarAgeFilter", "0");
			p(provDoc, "AllowHTMLEmail", "1");
			p(provDoc, "MaxEmailAgeFilter", "0");
			p(provDoc, "MaxEmailBodyTruncationSize", "-1");
			p(provDoc, "MaxEmailHTMLBodyTruncationSize", "-1");

			p(provDoc, "RequireSignedSMIMEMessages", "0");
			p(provDoc, "RequireEncryptedSMIMEMessages", "0");
			p(provDoc, "RequireSignedSMIMEAlgorithm", "0");
			p(provDoc, "RequireEncryptionSMIMEAlgorithm", "0");
			p(provDoc, "AllowSMIMEEncryptionAlgorithmNegotiation", "2");
			p(provDoc, "AllowSMIMESoftCerts", "1");
			p(provDoc, "AllowBrowser", "1");
			p(provDoc, "AllowConsumerEmail", "1");

			p(provDoc, "AllowRemoteDesktop", "1");
			p(provDoc, "AllowInternetSharing", "1");
			DOMUtils.createElement(provDoc, "UnapprovedInROMApplicationList");
			DOMUtils.createElement(provDoc, "ApprovedApplicationList");
		}
	}

}
