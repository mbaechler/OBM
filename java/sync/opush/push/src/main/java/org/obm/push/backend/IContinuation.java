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
package org.obm.push.backend;

import org.obm.push.bean.BackendSession;
import org.obm.push.handler.IContinuationHandler;


public interface IContinuation {

	Boolean isInitial();
	Boolean isResumed();
	
	void suspend(BackendSession bs, long secondsTimeout);

	void resume();
	
	void error(String status);

	Boolean isError();

	String getErrorStatus();

	BackendSession getBackendSession();
	
	IListenerRegistration getListenerRegistration();
	void setListenerRegistration(IListenerRegistration reg);

	CollectionChangeListener getCollectionChangeListener();
	void setCollectionChangeListener(CollectionChangeListener l);
	
	int getReqId();
	
	void setLastContinuationHandler(IContinuationHandler iContinuationHandler);
	
	IContinuationHandler getLastContinuationHandler();
}
