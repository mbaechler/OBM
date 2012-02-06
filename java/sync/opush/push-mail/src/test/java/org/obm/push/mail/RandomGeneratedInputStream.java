package org.obm.push.mail;

import java.io.IOException;
import java.io.InputStream;


public class RandomGeneratedInputStream extends InputStream {

	private long length;

	public RandomGeneratedInputStream(long length) {
		this.length = length;
	}
	
	@Override
	public int read() throws IOException {
		if (length-- > 0) {
			long modulo = length % 76;
			if (modulo == 74) {
				return '\r';
			} else if (modulo == 75) {
				return '\n';
			} else {
				return 66;
			}
		} else {
			return -1;
		}
	}

}