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

import java.util.Collection;
import java.util.List;

import org.minig.imap.impl.IMAPResponse;
import org.minig.imap.impl.MessageSet;

public class UIDCopyCommand extends BatchCommand<Long> {

	private Collection<Long> uids;
	private String destMailbox;

	public UIDCopyCommand(Collection<Long> uid, String destMailbox) {
		this.uids = uid;
		this.destMailbox = destMailbox;
	}

	@Override
	protected CommandArgument buildCommand() {
		StringBuilder sb = new StringBuilder();
		sb.append("UID COPY ");
		sb.append(MessageSet.asString(uids));
		sb.append(' ');
		sb.append(toUtf7(destMailbox));
		String cmd = sb.toString();
		CommandArgument args = new CommandArgument(cmd, null);
		return args;
	}

	@Override
	public void responseReceived(List<IMAPResponse> rs) throws ImapException {
		IMAPResponse ok = checkStatusResponse(rs);

		if (ok.getPayload().contains("[")) {
			data = wrapValues(parseMessageSet(ok.getPayload()));
		} else {
			throw new UnexpectedImapResponseException(
					"cyrus did not send [COPYUID ...] token: " + ok.getPayload());
		}
	}

	private Collection<Long> parseMessageSet(String payload) {
		int idx = payload.lastIndexOf("]");
		int space = payload.lastIndexOf(" ", idx);
		String set = payload.substring(space + 1, idx);
		Collection<Long> ret = MessageSet.asLongCollection(set, uids.size());
		return ret;
	}

}
