/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 2.0
 *
 * The contents of this file are subject to the GNU General Public
 * License Version 2 or later (the "GPL").
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Initial Developer of the Original Code is
 *   MiniG.org project members
 *
 * ***** END LICENSE BLOCK ***** */

package org.minig.imap.command;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.minig.imap.InternalDate;
import org.minig.imap.impl.IMAPResponse;
import org.minig.imap.impl.MessageSet;

import com.google.common.collect.Lists;

public class UIDFetchInternalDateCommand extends BatchCommand<InternalDate> {

	private Collection<Long> uids;
	DateFormat df;

	public UIDFetchInternalDateCommand(Collection<Long> uid) {
		this.uids = uid;
		//22-Mar-2010 14:26:18 +0100
		df = new SimpleDateFormat("d-MMM-yyyy HH:mm:ss Z", Locale.ENGLISH);
	}

	@Override
	protected CommandArgument buildCommand() {

		StringBuilder sb = new StringBuilder();
		if (!uids.isEmpty()) {
			sb.append("UID FETCH ");
			sb.append(MessageSet.asString(uids));
			sb.append(" (UID INTERNALDATE)");
		} else {
			sb.append("NOOP");
		}
		String cmd = sb.toString();
		CommandArgument args = new CommandArgument(cmd, null);
		return args;
	}

	@Override
	public void responseReceived(List<IMAPResponse> rs) throws UnexpectedImapErrorException {
		if (uids.isEmpty()) {
			data = Collections.emptyList();
			return;
		}
		
		checkStatusResponse(rs);
		List<ImapReturn<InternalDate>> tmp = Lists.newArrayListWithCapacity(rs.size());
		for (IMAPResponse r: rs) {
			try {
				InternalDate internalDate = parseInternalDate(r);
				tmp.add(value(internalDate));
			} catch (RuntimeException e) {
				tmp.add(error(e));
			} catch (InternalDateParserException e) {
				tmp.add(error(e));
			}
		}
		data = tmp;
	}

	private InternalDate parseInternalDate(IMAPResponse r) throws InternalDateParserException {
		String payload = r.getPayload();
		int fidx = payload.indexOf("INTERNALDATE \"") + "INTERNALDATE \"".length();
		
		if (fidx == -1 + "INTERNALDATE \"".length()) {
			return null;
		}
		
		int endDate = payload.indexOf("\"", fidx);
		String internalDate = "";
		if (fidx > 0 && endDate >= fidx) {
			internalDate = payload.substring(fidx, endDate);
		} else {
			throw new InternalDateParserException("error parsing internaldate in response : " + payload);
		}

		int uidIdx = payload.indexOf("UID ") + "UID ".length();
		int endUid = uidIdx;
		while (Character.isDigit(payload.charAt(endUid))) {
			endUid++;
		}
		long uid = Long.parseLong(payload.substring(uidIdx, endUid));

		return new InternalDate(uid,parseDate(internalDate));
	}
	
	private Date parseDate(String date) throws InternalDateParserException {
		try {
			return df.parse(date);
		} catch (ParseException e) {
			throw new InternalDateParserException("Failed to get parse date: " + date);
		}
	}

}
