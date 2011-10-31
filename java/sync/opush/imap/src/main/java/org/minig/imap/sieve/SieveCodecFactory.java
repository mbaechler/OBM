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

package org.minig.imap.sieve;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SieveCodecFactory implements ProtocolCodecFactory {
	
	private static final Logger logger = LoggerFactory
			.getLogger(SieveClientSupport.class);

	private ProtocolDecoder decoder = new ProtocolDecoderAdapter() {

		@Override
		public void decode(IoSession arg0, ByteBuffer arg1,
				ProtocolDecoderOutput arg2) throws Exception {
			java.nio.ByteBuffer received = arg1.buf();
			java.nio.ByteBuffer copy = java.nio.ByteBuffer.allocate(received
					.remaining());
			copy.put(received);
			// copy.flip();
			byte[] data = copy.array();
			if (logger.isDebugEnabled()) {
				logger.debug("decoded: " + new String(data));
			}
			SieveMessage sm = new SieveMessage();
			sm.addLine(new String(data));
			arg2.write(sm);
		}
	};

	private ProtocolEncoder encoder = new ProtocolEncoderAdapter() {

		@Override
		public void encode(IoSession arg0, Object arg1,
				ProtocolEncoderOutput arg2) throws Exception {
			byte[] raw = (byte[]) arg1;
			ByteBuffer b = ByteBuffer.wrap(raw);
			arg2.write(b);
		}
	};

	@Override
	public ProtocolDecoder getDecoder() throws Exception {
		return decoder;
	}

	@Override
	public ProtocolEncoder getEncoder() throws Exception {
		return encoder;
	}

}
