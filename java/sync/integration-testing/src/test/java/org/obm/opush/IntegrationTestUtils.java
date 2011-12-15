package org.obm.opush;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

import java.util.Collection;
import java.util.Date;
import java.util.Properties;

import org.easymock.EasyMock;
import org.obm.opush.SingleUserFixture.OpushUser;
import org.obm.push.bean.ChangedCollections;
import org.obm.push.bean.Device;
import org.obm.push.bean.SyncCollection;
import org.obm.push.exception.DaoException;
import org.obm.push.exception.activesync.CollectionNotFoundException;
import org.obm.push.store.CollectionDao;
import org.obm.push.store.DeviceDao;
import org.obm.sync.client.login.LoginService;
import org.obm.sync.push.client.OPClient;
import org.obm.sync.push.client.XmlOPClient;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class IntegrationTestUtils {

	public static void expectUserLoginFromOpush(LoginService loginService, Collection<OpushUser> users) {
		for (OpushUser user : users) {
			expectUserLoginFromOpush(loginService, user);
		}
	}
	
	public static void expectUserLoginFromOpush(LoginService loginService, OpushUser user) {
		expect(loginService.login(user.user.getLoginAtDomain(), user.password, "o-push")).andReturn(user.accessToken).anyTimes();
		loginService.logout(user.accessToken);
		expectLastCall().anyTimes();
	}


	public static void expectUserDeviceAccess(DeviceDao deviceDao, Collection<OpushUser> users) throws DaoException {
		for (OpushUser user : users) {
			expectUserDeviceAccess(deviceDao, user);
		}
	}
	
	public static void expectUserDeviceAccess(DeviceDao deviceDao, OpushUser user) throws DaoException {
		expect(deviceDao.getDevice(user.user, 
				user.deviceId, 
				user.userAgent))
				.andReturn(
						new Device(user.hashCode(), user.deviceType, user.deviceId, new Properties()))
						.anyTimes();
	}
	
	public static void expectUserCollectionsNeverChange(CollectionDao collectionDao, Collection<OpushUser> users) throws DaoException, CollectionNotFoundException {
		Date lastSync = new Date();
		ChangedCollections changed = new ChangedCollections(lastSync, ImmutableSet.<SyncCollection>of());
		expect(collectionDao.getContactChangedCollections(anyObject(Date.class))).andReturn(changed).anyTimes();
		expect(collectionDao.getCalendarChangedCollections(anyObject(Date.class))).andReturn(changed).anyTimes();

		int randomCollectionId = anyInt();
		for (OpushUser opushUser: users) {
			String collectionPath = IntegrationTestUtils.buildCalendarCollectionPath(opushUser);  
			expect(collectionDao.getCollectionPath(randomCollectionId)).andReturn(collectionPath).anyTimes();
		}
	}

	public static void replayMocks(Iterable<Object> toReplay) {
		EasyMock.replay(Lists.newArrayList(toReplay).toArray());
	}
	
	public static OPClient buildOpushClient(OpushUser user, int port) {
		String url = buildServiceUrl(port);
		return new OPClient(
				user.user.getLoginAtDomain(), 
				user.password, 
				user.deviceId, 
				user.deviceType, 
				user.userAgent, url);
	}

	public static XmlOPClient buildOpushXmlClient(OpushUser user, int port) {
		String url = buildServiceUrl(port);
		return new XmlOPClient(
				user.user.getLoginAtDomain(), 
				user.password, 
				user.deviceId, 
				user.deviceType, 
				user.userAgent, url);
	}

	public static String buildCalendarCollectionPath(OpushUser opushUser) {
		return opushUser.user.getLoginAtDomain() + "\\calendar\\" + opushUser.user.getLoginAtDomain();
	}
	
	public static String buildServiceUrl(int port) {
		return "http://localhost:" + port + "/";
	}
}
