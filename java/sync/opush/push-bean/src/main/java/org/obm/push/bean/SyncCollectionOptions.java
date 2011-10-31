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
package org.obm.push.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.google.common.base.Objects;

public class SyncCollectionOptions implements Serializable {
	
	public static final Integer SYNC_TRUNCATION_ALL = 9;
	
	private Integer truncation;
	private Integer mimeSupport;
	private Integer mimeTruncation;
	private Integer conflict;
	private Boolean deletesAsMoves;
	private FilterType filterType;
	private Map<MSEmailBodyType,BodyPreference> bodyPreferences;
	
	public SyncCollectionOptions() {
		conflict = 1;
		truncation = SYNC_TRUNCATION_ALL;
		deletesAsMoves = true;
		this.bodyPreferences = new HashMap<MSEmailBodyType, BodyPreference>();
	}
	
	public Integer getConflict() {
		return conflict;
	}
	public void setConflict(Integer conflict) {
		this.conflict = conflict;
	}

	public Integer getTruncation() {
		return truncation;
	}

	public void setTruncation(Integer truncation) {
		this.truncation = truncation;
	}

	public boolean isDeletesAsMoves() {
		return deletesAsMoves;
	}

	public void setDeletesAsMoves(Boolean deletesAsMoves) {
		if(deletesAsMoves != null){
			this.deletesAsMoves = deletesAsMoves;
		}
	}

	public FilterType getFilterType() {
		return filterType;
	}

	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;
	}
	
	public Integer getMimeSupport() {
		return mimeSupport;
	}

	public void setMimeSupport(Integer mimeSupport) {
		this.mimeSupport = mimeSupport;
	}

	public Integer getMimeTruncation() {
		return mimeTruncation;
	}

	public void setMimeTruncation(Integer mimeTruncation) {
		this.mimeTruncation = mimeTruncation;
	}
	
	public Map<MSEmailBodyType,BodyPreference> getBodyPreferences() {
		return bodyPreferences;
	}
	
	public BodyPreference getBodyPreference(MSEmailBodyType type) {
		return bodyPreferences.get(type);
	}

	public void addBodyPreference(BodyPreference bodyPreference) {
		this.bodyPreferences.put(bodyPreference.getType(), bodyPreference);
	}

	@Override
	public final int hashCode(){
		return Objects.hashCode(truncation, mimeSupport, mimeTruncation, conflict, 
				deletesAsMoves, filterType, bodyPreferences);
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof SyncCollectionOptions) {
			SyncCollectionOptions that = (SyncCollectionOptions) object;
			return Objects.equal(this.truncation, that.truncation)
				&& Objects.equal(this.mimeSupport, that.mimeSupport)
				&& Objects.equal(this.mimeTruncation, that.mimeTruncation)
				&& Objects.equal(this.conflict, that.conflict)
				&& Objects.equal(this.deletesAsMoves, that.deletesAsMoves)
				&& Objects.equal(this.filterType, that.filterType)
				&& Objects.equal(this.bodyPreferences, that.bodyPreferences);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("SYNC_TRUNCATION_ALL", SYNC_TRUNCATION_ALL)
			.add("truncation", truncation)
			.add("mimeSupport", mimeSupport)
			.add("mimeTruncation", mimeTruncation)
			.add("conflict", conflict)
			.add("deletesAsMoves", deletesAsMoves)
			.add("filterType", filterType)
			.add("bodyPreferences", bodyPreferences)
			.toString();
	}
	
}
