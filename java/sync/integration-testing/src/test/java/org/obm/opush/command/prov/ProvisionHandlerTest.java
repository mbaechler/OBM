/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2011-2012  Linagora
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version, provided you comply 
 * with the Additional Terms applicable for OBM connector by Linagora 
 * pursuant to Section 7 of the GNU Affero General Public License, 
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain 
 * the “Message sent thanks to OBM, Free Communication by Linagora” 
 * signature notice appended to any and all outbound messages 
 * (notably e-mail and meeting requests), (ii) retain all hypertext links between 
 * OBM and obm.org, as well as between Linagora and linagora.com, and (iii) refrain 
 * from infringing Linagora intellectual property rights over its trademarks 
 * and commercial brands. Other Additional Terms apply, 
 * see <http://www.linagora.com/licenses/> for more details. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License 
 * for more details. 
 *
 * You should have received a copy of the GNU Affero General Public License 
 * and its applicable Additional Terms for OBM along with this program. If not, 
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License version 3 
 * and <http://www.linagora.com/licenses/> for the Additional Terms applicable to 
 * OBM connectors. 
 * 
 * ***** END LICENSE BLOCK ***** */
package org.obm.opush.command.prov;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.obm.opush.IntegrationTestUtils.buildWBXMLOpushClient;
import static org.obm.opush.IntegrationTestUtils.expectUserCollectionsNeverChange;
import static org.obm.opush.IntegrationTestUtils.replayMocks;
import static org.obm.opush.IntegrationUserAccessUtils.mockUsersAccess;

import java.util.Arrays;
import java.util.List;

import org.fest.assertions.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.obm.PortNumber;
import org.obm.filter.Slow;
import org.obm.filter.SlowFilterRunner;
import org.obm.opush.ActiveSyncServletModule.OpushServer;
import org.obm.opush.SingleUserFixture;
import org.obm.opush.SingleUserFixture.OpushUser;
import org.obm.opush.env.AbstractOpushEnv;
import org.obm.opush.env.JUnitGuiceRule;
import org.obm.push.bean.ProvisionPolicyStatus;
import org.obm.push.bean.ProvisionStatus;
import org.obm.push.exception.DaoException;
import org.obm.push.exception.activesync.CollectionNotFoundException;
import org.obm.push.store.CollectionDao;
import org.obm.push.store.DeviceDao;
import org.obm.push.utils.collection.ClassToInstanceAgregateView;
import org.obm.sync.auth.AuthFault;
import org.obm.sync.push.client.OPClient;
import org.obm.sync.push.client.ProvisionResponse;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

@RunWith(SlowFilterRunner.class) @Slow
public class ProvisionHandlerTest {

	private static class ProvisionHandlerTestModule extends AbstractOpushEnv {}
	
	@Rule
	public JUnitGuiceRule guiceBerry = new JUnitGuiceRule(ProvisionHandlerTestModule.class);

	@Inject @PortNumber int port;
	@Inject SingleUserFixture singleUserFixture;
	@Inject OpushServer opushServer;
	@Inject ClassToInstanceAgregateView<Object> classToInstanceMap;

	private List<OpushUser> fakeTestUsers;

	@Before
	public void init() {
		fakeTestUsers = Arrays.asList(singleUserFixture.jaures);
	}
	
	@After
	public void shutdown() throws Exception {
		opushServer.stop();
	}

	@Test
	public void testFirstProvisionSendPolicy() throws Exception {
		long nextPolicyKeyGenerated = 115l;
		mockProvisionNeeds(nextPolicyKeyGenerated, singleUserFixture.jaures);
		
		opushServer.start();

		OPClient opClient = buildWBXMLOpushClient(singleUserFixture.jaures, port);
		ProvisionResponse provisionResponse = opClient.provisionStepOne();

		Assertions.assertThat(provisionResponse.getProvisionStatus()).isEqualTo(ProvisionStatus.SUCCESS);
		Assertions.assertThat(provisionResponse.getPolicyKey()).isEqualTo(nextPolicyKeyGenerated);
		Assertions.assertThat(provisionResponse.getPolicyStatus()).isEqualTo(ProvisionPolicyStatus.SUCCESS);
		Assertions.assertThat(provisionResponse.getPolicyType()).isEqualTo("MS-EAS-Provisioning-WBXML");
		Assertions.assertThat(provisionResponse.hasPolicyData()).isTrue();
	}

	@Test
	public void testSecondProvisionDoesntSendPolicy() throws Exception {
		long nextPolicyKeyGenerated = 16510l;
		long userRegistredPolicyKey = 5410l;
		mockStepTwoNeeds(nextPolicyKeyGenerated, userRegistredPolicyKey);
		
		opushServer.start();

		OPClient opClient = buildWBXMLOpushClient(singleUserFixture.jaures, port);
		ProvisionResponse provisionResponse = opClient.provisionStepTwo(userRegistredPolicyKey);

		Assertions.assertThat(provisionResponse.getProvisionStatus()).isEqualTo(ProvisionStatus.SUCCESS);
		Assertions.assertThat(provisionResponse.getPolicyKey()).isEqualTo(nextPolicyKeyGenerated);
		Assertions.assertThat(provisionResponse.getPolicyStatus()).isEqualTo(ProvisionPolicyStatus.SUCCESS);
		Assertions.assertThat(provisionResponse.getPolicyType()).isEqualTo("MS-EAS-Provisioning-WBXML");
		Assertions.assertThat(provisionResponse.hasPolicyData()).isFalse();
	}

	@Test
	public void testSecondProvisionSentCorrectStatusWhenNotExpectedPolicyKey() throws Exception {
		long policyKeyGenerated = 16510l;
		long userRegistredPolicyKey = 4015l;
		long acknowledgingPolicyKey = 5410l;
		mockStepTwoNeeds(policyKeyGenerated, userRegistredPolicyKey);
		
		opushServer.start();

		OPClient opClient = buildWBXMLOpushClient(singleUserFixture.jaures, port);
		ProvisionResponse provisionResponse = opClient.provisionStepTwo(acknowledgingPolicyKey);

		Assertions.assertThat(provisionResponse.getProvisionStatus()).isEqualTo(ProvisionStatus.SUCCESS);
		Assertions.assertThat(provisionResponse.getPolicyKey()).isNull();
		Assertions.assertThat(provisionResponse.getPolicyStatus()).isEqualTo(ProvisionPolicyStatus.THE_CLIENT_IS_ACKNOWLEDGING_THE_WRONG_POLICY_KEY);
		Assertions.assertThat(provisionResponse.getPolicyType()).isEqualTo("MS-EAS-Provisioning-WBXML");
		Assertions.assertThat(provisionResponse.hasPolicyData()).isFalse();
	}

	private void mockStepTwoNeeds(long nextPolicyKeyGenerated, long userRegistredPolicyKey)
			throws DaoException, AuthFault, CollectionNotFoundException {
		
		OpushUser user = singleUserFixture.jaures;

		DeviceDao deviceDao = classToInstanceMap.get(DeviceDao.class);
		expect(deviceDao.getPolicyKey(user.user, user.deviceId)).andReturn(userRegistredPolicyKey).once();
		
		mockProvisionNeeds(nextPolicyKeyGenerated, user);
	}

	private void mockProvisionNeeds(long nextPolicyKeyGenerated, OpushUser user)
			throws DaoException, AuthFault, CollectionNotFoundException {
		
		mockUsersAccess(classToInstanceMap, Sets.newHashSet(user));
		expectUserCollectionsNeverChange(classToInstanceMap.get(CollectionDao.class), fakeTestUsers, Sets.<Integer>newHashSet());
		
		DeviceDao deviceDao = classToInstanceMap.get(DeviceDao.class);
		expect(deviceDao.allocateNewPolicyKey(user.user, user.deviceId)).andReturn(nextPolicyKeyGenerated).once();
		deviceDao.removePolicyKey(user.user, user.device);
		expectLastCall().once();
		
		replayMocks(classToInstanceMap);
	}
}