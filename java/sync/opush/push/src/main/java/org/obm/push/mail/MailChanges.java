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
package org.obm.push.mail;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.obm.push.bean.Email;
import org.obm.push.utils.DateUtils;
import org.obm.push.utils.index.IndexUtils;

public class MailChanges {
	
	private final Set<Email> removed;
	private final Set<Email> updatedEmailsFromIMAP;
	private final Set<Email> updatedEmailsToDB;
	
	private Date lastSync;
	
	public MailChanges(Set<Email> removedEmails, Set<Email> updatedEmailsFromImap, Set<Email> updatedEmailsToDB) {
		this.removed = removedEmails;
		this.updatedEmailsFromIMAP = updatedEmailsFromImap;
		this.updatedEmailsToDB = updatedEmailsToDB;
		this.lastSync = DateUtils.getCurrentDate();
	}
	
	public Set<Email> getRemoved() {
		return removed;
	}

	public void addRemoved(Collection<Email> removed) {
		this.removed.addAll(removed);
	}

	public Set<Email> getUpdatedEmailFromImap() {
		return updatedEmailsFromIMAP;
	}

	public void addUpdated(Collection<Email> updated) {
		this.updatedEmailsFromIMAP.addAll(updated);
	}
	
	public void addUpdated(Email uid){
		this.updatedEmailsFromIMAP.add(uid);
	}

	public void addRemoved(Email uid){
		this.removed.add(uid);
	}

	public Date getLastSync() {
		return lastSync;
	}

	public void setLastSync(Date lastSync) {
		this.lastSync = lastSync;
	}
	
	public Collection<Long> getRemovedToLong() {
		return IndexUtils.listIndexes(getRemoved());
	}
	
	public Collection<Long> getUpdatedEmailFromImapToLong() {
		return IndexUtils.listIndexes(getUpdatedEmailFromImap());
	}
	
	public Set<Email> getUpdatedEmailToDB() {
		return updatedEmailsToDB;
	}
	
}
