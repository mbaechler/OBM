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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.minig.imap.FastFetch;
import org.minig.imap.Flag;
import org.minig.imap.impl.IMAPResponse;
import org.minig.imap.impl.MessageSet;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * FAST
 *        Macro equivalent to: (FLAGS INTERNALDATE RFC822.SIZE)
 */
public class UIDFetchFastCommand extends BatchCommand<FastFetch> {

	private Collection<Long> uids;
	DateFormat df;

	public UIDFetchFastCommand(Collection<Long> uid) {
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
			sb.append(" FAST");
		} else {
			sb.append("NOOP");
		}
		String cmd = sb.toString();
		logger.debug("cmd: {}", cmd);
		CommandArgument args = new CommandArgument(cmd, null);
		return args;
	}

	@Override
	public void responseReceived(List<IMAPResponse> rs) throws UnexpectedImapErrorException {
		if (uids.isEmpty()) {
			data = ImmutableSet.of();
			return;
		}
		
		checkStatusResponse(rs);
		Builder<ImapReturn<FastFetch>> buildSet = ImmutableSet.builder();
		for (IMAPResponse r: rs) {
			try {
				FastFetch value = parseFastFetchFromResponse(r);
				if (value != null) {
					buildSet.add(value(value));
				}
			} catch (FlagParserException e) {
				buildSet.add(error(e));
			} catch (InternalDateParserException e) {
				buildSet.add(error(e));
			} catch (RuntimeException e) {
				buildSet.add(error(e));
			}
		}
		
		data = buildSet.build();
	}

	private FastFetch parseFastFetchFromResponse(IMAPResponse r) throws FlagParserException, InternalDateParserException {
		String payload = r.getPayload();
		if (!payload.contains(" FETCH")) {
			logger.debug("not a fetch: {}", payload);
			return null;
		}
		
		long uid = getUid(payload);
		Date internalDate = getInternalDate(payload);
		Set<Flag> flags = getFlags(payload);
		return new FastFetch(uid, internalDate, flags);
	}
	
	private long getUid(String payload) {
		int uidIdx = payload.indexOf("UID ") + "UID ".length();
		int endUid = uidIdx;
		while (Character.isDigit(payload.charAt(endUid))) {
			endUid++;
		}
		return Long.parseLong(payload.substring(uidIdx, endUid));
	}

	private Date getInternalDate(String payload) throws InternalDateParserException {
		int fidx = payload.indexOf("INTERNALDATE \"") + "INTERNALDATE \"".length();
		
		if (fidx == -1 + "INTERNALDATE \"".length()) {
			throw new InternalDateParserException("Failed to get internaldate in fetch response: " + payload);
		}
		int endDate = payload.indexOf("\"", fidx);
		String internalDate = "";
		if (fidx > 0 && endDate >= fidx) {
			internalDate = payload.substring(fidx, endDate);
		} else {
			throw new InternalDateParserException("Failed to get internaldate in fetch response: " + payload);
		}
		return parseDate(internalDate);
	}
	
	private Set<Flag> getFlags(String payload) throws FlagParserException {
		int fidx = payload.indexOf("FLAGS (") + "FLAGS (".length();
		if (fidx == -1 + "FLAGS (".length()) {
			throw new FlagParserException("payload is not a flag response : " + payload);
		}
		
		int endFlags = payload.indexOf(")", fidx);
		String flags = "";
		if (fidx > 0 && endFlags >= fidx) {
			flags = payload.substring(fidx, endFlags);
		} else {
			throw new FlagParserException("error parsing flag response : " + payload);
		}
		return parseFlags(flags);
	}
	
	private Date parseDate(String date) throws InternalDateParserException {
		try {
			return df.parse(date);
		} catch (ParseException e) {
			throw new InternalDateParserException("Failed to get parse date: " + date);
		}
	}
	
	private Set<Flag> parseFlags(String flags) {
		Set<Flag> flagsList = new HashSet<Flag>();
		if (flags.contains("\\Seen")) {
			flagsList.add(Flag.SEEN);
		}
		if (flags.contains("\\Flagged")) {
			flagsList.add(Flag.FLAGGED);
		}
		if (flags.contains("\\Deleted")) {
			flagsList.add(Flag.DELETED);
		}
		if (flags.contains("\\Answered")) {
			flagsList.add(Flag.ANSWERED);
		}
		return flagsList;
	}
	
}
