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

import org.obm.push.bean.ItemChange;
import org.obm.push.bean.ItemOperationsStatus;
import org.obm.push.bean.SyncCollection;

public class ItemOperationsResponse {

	public static class EmptyFolderContentsResult {
		
		private ItemOperationsStatus itemOperationsStatus;
		private int collectionId;
		
		public int getCollectionId() {
			return collectionId;
		}
		
		public void setCollectionId(int collectionId) {
			this.collectionId = collectionId;
		}
		
		public ItemOperationsStatus getItemOperationsStatus() {
			return itemOperationsStatus;
		}
		
		public void setItemOperationsStatus(ItemOperationsStatus itemOperationsStatus) {
			this.itemOperationsStatus = itemOperationsStatus;
		}
	}
	
	public static class MailboxFetchResult {

		private FetchAttachmentResult fileReferenceFetch;
		private FetchItemResult fetchItemResult;

		public void setFetchAttachmentResult(FetchAttachmentResult fileReferenceFetch) {
			this.fileReferenceFetch = fileReferenceFetch;
		}
		
		public FetchAttachmentResult getFileReferenceFetch() {
			return fileReferenceFetch;
		}

		public void setFetchItemResult(FetchItemResult fetchItemResult) {
			this.fetchItemResult = fetchItemResult;
		}

		public FetchItemResult getFetchItemResult() {
			return fetchItemResult;
		}
		
		public static class FetchItemResult {
			private ItemChange itemChange;
			private ItemOperationsStatus status;
			private SyncCollection syncCollection;
			private String collectionId;
			private String serverId;
			
			public ItemChange getItemChange() {
				return itemChange;
			}
			
			public void setItemChange(ItemChange itemChange) {
				this.itemChange = itemChange;
			}
			
			public ItemOperationsStatus getStatus() {
				return status;
			}
			
			public void setStatus(ItemOperationsStatus status) {
				this.status = status;
			}
			
			public SyncCollection getSyncCollection() {
				return syncCollection;
			}
			
			public void setSyncCollection(SyncCollection syncCollection) {
				this.syncCollection = syncCollection;
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
			
			
			
		}

		
		public static class FetchAttachmentResult {
			
			private String reference;
			private ItemOperationsStatus status;
			private byte[] attch;
			private String contentType;

			public String getReference() {
				return reference;
			}
			
			public void setReference(String reference) {
				this.reference = reference;
			}
			
			public ItemOperationsStatus getStatus() {
				return status;
			}
			
			public void setStatus(ItemOperationsStatus status) {
				this.status = status;
			}
			
			public byte[] getAttch() {
				return attch;
			}
			
			public void setAttch(byte[] attch) {
				this.attch = attch;
			}
			
			public String getContentType() {
				return contentType;
			}
			
			public void setContentType(String contentType) {
				this.contentType = contentType;
			}
			
		}
		
	}
	
	private EmptyFolderContentsResult emptyFolderContentsResult;
	private MailboxFetchResult mailboxFetchResult;
	private boolean multipart;
	private boolean gzip;
	
	public MailboxFetchResult getMailboxFetchResult() {
		return mailboxFetchResult;
	}
	
	public void setMailboxFetchResult(MailboxFetchResult mailboxFetchResult) {
		this.mailboxFetchResult = mailboxFetchResult;
	}
	
	public EmptyFolderContentsResult getEmptyFolderContentsResult() {
		return emptyFolderContentsResult;
	}
	
	public void setEmptyFolderContentsResult(EmptyFolderContentsResult emptyFolderContentsResult) {
		this.emptyFolderContentsResult = emptyFolderContentsResult;
	}

	public boolean isMultipart() {
		return multipart;
	}
	
	public void setMultipart(boolean multipart) {
		this.multipart = multipart;
	}

	public boolean isGzip() {
		return gzip;
	}
	
	public void setGzip(boolean gzip) {
		this.gzip = gzip;
	}
	
}
