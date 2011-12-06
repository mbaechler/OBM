package org.obm.push.service;

import org.obm.push.bean.Device;
import org.obm.push.bean.User;
import org.obm.push.exception.DaoException;

public interface DeviceService {
	
	boolean initDevice(User loginAtDomain, String deviceId,
			String deviceType, String userAgent);

	boolean syncAuthorized(User user, String deviceId) throws DaoException;
	Device getDevice(User user, String deviceId, String userAgent) throws DaoException;
}
