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
package org.obm.push.protocol.request;

public enum Base64ParameterCodes {
    
	AttachmentName,//0
	CollectionId,//1
	CollectionName,//2
	ItemId,//3
	LongId,//4
	ParentId,//5
	Occurrence,//6
	Options,//7
	User;//8

	public static Base64ParameterCodes getParam(int value) {
		switch (value) {
		case 0:
			return AttachmentName;
		case 1:
			return CollectionId;
		case 2:
			return CollectionName;
		case 3:
			return ItemId;
		case 4:
			return LongId;
		case 5:
			return ParentId;
		case 6:
			return Occurrence;
		case 7:
			return Options;
		case 9:
			return User;
		default:
			return null;
		}
	}

}
