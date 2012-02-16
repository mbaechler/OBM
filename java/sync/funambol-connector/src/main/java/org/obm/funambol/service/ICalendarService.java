package org.obm.funambol.service;

import java.sql.Timestamp;
import java.util.List;

import org.obm.funambol.exception.OBMException;
import org.obm.funambol.model.SyncSession;

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.framework.engine.SyncItemKey;


public interface ICalendarService {

	Calendar addItem(SyncSession syncBean, Calendar event)
			throws OBMException;

	List<String> getAllItemKeys(SyncSession syncBean) throws OBMException;

	List<String> getDeletedItemKeys(SyncSession syncBean, Timestamp since) throws OBMException;

	List<String> getEventTwinKeys(SyncSession syncBean, Calendar event) throws OBMException;

	List<String> getUpdatedItemKeys(SyncSession syncBean, Timestamp since)
			throws OBMException;

	void removeItem(SyncSession syncBean, SyncItemKey syncItemKey) throws OBMException;

	Calendar updateItem(SyncSession syncBean, Calendar calendar) throws OBMException;

	Calendar getItemFromId(SyncSession syncBean, SyncItemKey syncItemKey) throws OBMException;

}
