package org.columba.ristretto.io;

import java.io.IOException;
import java.io.InputStream;

public class VariableSizeFileBufferInputStream extends InputStream {

	private VariableSizeFileBuffer buffer;
	
	public VariableSizeFileBufferInputStream(VariableSizeFileBuffer buffer) {
		this.buffer= buffer;
	}
	
	@Override
	public int read() throws IOException {
		return buffer.read();
	}

	
	@Override
	public void close() throws IOException {
		buffer.closeInput();
	}

}
