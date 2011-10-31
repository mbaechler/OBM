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
package org.obm.push.protocol.bean;

import org.obm.push.bean.StoreName;

public class ItemOperationsRequest {

	public static class Fetch {
		
		private StoreName storeName;
		private String fileReference;
		private String collectionId;
		private String serverId;
		private Integer type;
		
		public Fetch() {
		}
		
		public StoreName getStoreName() {
			return storeName;
		}

		public void setStoreName(StoreName storeName) {
			this.storeName = storeName;
		}
		
		public String getFileReference() {
			return fileReference;
		}

		public void setFileReference(String fileReference) {
			this.fileReference = fileReference;
		}
		
		public String getCollectionId() {
			return collectionId;
		}
		
		public void setCollectionId(String collectionId) {
			this.collectionId = collectionId;
		}
		
		public String getServerId() {
			return serverId;
		}
		
		public void setServerId(String serverId) {
			this.serverId = serverId;
		}

		public Integer getType() {
			return type;
		}
		
		public void setType(Integer type) {
			this.type = type;
		}
		
	}
	
	public static class EmptyFolderContentsRequest {

		private Integer collectionId;
		private boolean deleteSubFolderElem;

		public Integer getCollectionId() {
			return collectionId;
		}
		
		public void setCollectionId(Integer collectionId) {
			this.collectionId = collectionId;
		}

		public boolean isDeleteSubFolderElem() {
			return deleteSubFolderElem;
		}
		
		public void setDeleteSubFolderElem(boolean deleteSubFolderElem) {
			this.deleteSubFolderElem = deleteSubFolderElem;
		}

	}
	
	private boolean multipart;
	private boolean gzip;
	private Fetch fetch;
	private EmptyFolderContentsRequest emptyFolderContents;
	
	public boolean isMultipart() {
		return multipart;
	}

	public void setMultipart(boolean multipart) {
		this.multipart = multipart;
	}
	
	public Fetch getFetch() {
		return fetch;
	}

	public void setFetch(Fetch fetch) {
		this.fetch = fetch;
	}
	
	public boolean isGzip() {
		return gzip;
	}
	
	public void setGzip(boolean gzip) {
		this.gzip = gzip;
	}
	
	public void setEmptyFolderContents(EmptyFolderContentsRequest emptyFolderContents) {
		this.emptyFolderContents = emptyFolderContents;
	}
	
	public EmptyFolderContentsRequest getEmptyFolderContents() {
		return emptyFolderContents;
	}
	
}
