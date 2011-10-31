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
package org.obm.push.protocol.logging;

import java.util.TreeMap;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import com.google.common.collect.Maps;

public enum TechnicalLogType {
	HTTP_REQUEST("HttpRequest"),
	ACTIVE_SYNC_REQUEST("ActiveSyncXmlRequest"),
	ACTIVE_SYNC_REQUEST_HEADERS("ActiveSyncRequestHeaders"),
	ACTIVE_SYNC_REQUEST_INFO("ActiveSyncRequestInfo"),
	ACTIVE_SYNC_RESPONSE("ActiveSyncXmlResponse"),
	ACTIVE_SYNC_RESPONSE_HEADERS("ActiveSyncResponseHeaders");

	private final Marker marker;
	private final String markerName;

	private TechnicalLogType(String markerName) {
		this.marker = MarkerFactory.getMarker(markerName);
		this.markerName = markerName;
	}

	public Marker getMarker(){
		return this.marker;
	}

	public String getMarkerName(){
		return this.markerName;
	}

	public enum Index {
		INSTANCE;

		private TreeMap<String, TechnicalLogType> index;

		private Index() {
			index = Maps.newTreeMap();
			for (TechnicalLogType logType: TechnicalLogType.values()) {
				index.put(logType.getMarkerName(), logType);
			}
		}

		public TechnicalLogType searchByName(String name) {
			return index.get(name);
		}
	}
}
