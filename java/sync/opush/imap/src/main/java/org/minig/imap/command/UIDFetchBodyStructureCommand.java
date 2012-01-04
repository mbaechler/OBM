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
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.minig.imap.command.parser.BodyStructureParser;
import org.minig.imap.impl.IMAPResponse;
import org.minig.imap.impl.MessageSet;
import org.minig.imap.mime.MimeMessage;
import org.minig.imap.mime.impl.AtomHelper;

public class UIDFetchBodyStructureCommand extends BatchCommand<MimeMessage> {

	private TreeSet<Long> uids;
	private final BodyStructureParser bodyStructureParser;

	public UIDFetchBodyStructureCommand(BodyStructureParser bodyStructureParser, Collection<Long> uid) {
		this.bodyStructureParser = bodyStructureParser;
		this.uids = new TreeSet<Long>(uid);
	}

	@Override
	protected CommandArgument buildCommand() {
		String cmd = "UID FETCH " + MessageSet.asString(uids)
				+ " (UID BODYSTRUCTURE)";
		CommandArgument args = new CommandArgument(cmd, null);
		return args;
	}

	@Override
	public void responseReceived(List<IMAPResponse> rs) throws ImapException {
		if (logger.isDebugEnabled()) {
			for (IMAPResponse r : rs) {
				logger.debug("ri: " + r.getPayload() + " [stream:"
						+ (r.getStreamData() != null) + "]");
			}
		}
		
		checkStatusResponse(rs);
		
		List<ImapReturn<MimeMessage>> mts = new LinkedList<ImapReturn<MimeMessage>>();
		for (IMAPResponse ir: rs) {
			try {
				MimeMessage message = parseMimeMessageFromResponse(ir);
				if (ir != null) {
					mts.add(new ImapReturn<MimeMessage>(message));
				}
			} catch (RuntimeException e) {
				mts.add(error(e));
			}
		}
		data = mts;
	}

	private MimeMessage parseMimeMessageFromResponse(IMAPResponse ir) {
		String payload = ir.getPayload();

		int bsIdx = payload.indexOf(" BODYSTRUCTURE ");
		if (bsIdx == -1) {
			return null;
		}

		String bs = payload.substring(bsIdx + " BODYSTRUCTURE ".length());

		if (bs.length() < 2) {
			logger.warn("strange bs response: " + payload);
			return null;
		}

		int uidIdx = payload.indexOf("(UID ");
		long uid = Long.parseLong(payload.substring(
				uidIdx + "(UID ".length(), bsIdx));

		String bsData = AtomHelper.getFullResponse(bs, ir.getStreamData());
		String dataWithoutClosingBrace = bsData.substring(0, bsData.length() - 1);
		MimeMessage message = bodyStructureParser.parseBodyStructure(dataWithoutClosingBrace);
		message.setUid(uid);
		return message;
	}
	
}
