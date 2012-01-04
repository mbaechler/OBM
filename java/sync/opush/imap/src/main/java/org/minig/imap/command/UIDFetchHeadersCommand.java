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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.minig.imap.IMAPHeaders;
import org.minig.imap.command.parser.HeadersParser;
import org.minig.imap.impl.IMAPResponse;
import org.minig.imap.impl.MessageSet;

import com.google.common.collect.Lists;

public class UIDFetchHeadersCommand extends BatchCommand<IMAPHeaders> {

	private Collection<Long> uids;
	private String[] headers;

	public UIDFetchHeadersCommand(Collection<Long> uid, String[] headers) {
		this.uids = uid;
		this.headers = headers;
	}

	@Override
	protected CommandArgument buildCommand() {
		StringBuilder sb = new StringBuilder();
		if (!uids.isEmpty()) {
			sb.append("UID FETCH ");
			sb.append(MessageSet.asString(uids));
			sb.append(" (UID BODY.PEEK[HEADER.FIELDS (");
			for (int i = 0; i < headers.length; i++) {
				if (i > 0) {
					sb.append(" ");
				}
				sb.append(headers[i].toUpperCase());
			}
			sb.append(")])");
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
			data = Collections.emptyList();
			return;
		}
		
		checkStatusResponse(rs);
		List<ImapReturn<IMAPHeaders>> tmp = Lists.newArrayListWithExpectedSize(uids.size());
		for (IMAPResponse r: rs) {
			try {
				IMAPHeaders value = parseHeadersFromResponse(r);
				data.add(value(value));
			} catch (RuntimeException e) {
				data.add(error(e));
			} catch (IOException e) {
				data.add(error(e));
			}
		}
		data = tmp;
	}

	private IMAPHeaders parseHeadersFromResponse(IMAPResponse r) throws IOException {
		String payload = r.getPayload();
		if (!payload.contains(" FETCH")) {
			logger.warn("not a fetch: {}", payload);
			return null;
		}
		int uidIdx = payload.indexOf("(UID ") + "(UID ".length();
		int endUid = payload.indexOf(' ', uidIdx);
		String uidStr = payload.substring(uidIdx, endUid);
		long uid = Long.parseLong(uidStr);

		Map<String, String> rawHeaders = Collections.emptyMap();

		InputStream in = r.getStreamData();
		if (in != null) {
			InputStreamReader reader = new InputStreamReader(in);
			rawHeaders = new HeadersParser().parseRawHeaders(reader);
		} else {
			// cyrus search command can return uid's that no longer exist in the mailbox
			logger.warn("cyrus did not return any header for uid {}", uid);
		}

		IMAPHeaders imapHeaders = new IMAPHeaders();
		imapHeaders.setUid(uid);
		imapHeaders.setRawHeaders(rawHeaders);
		return imapHeaders;
	}
	
}
