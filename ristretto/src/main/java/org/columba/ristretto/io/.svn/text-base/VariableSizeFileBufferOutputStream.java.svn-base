package org.columba.ristretto.io;

import java.io.IOException;
import java.io.OutputStream;

public class VariableSizeFileBufferOutputStream extends OutputStream {

	private VariableSizeFileBuffer buffer;
	
	public VariableSizeFileBufferOutputStream(VariableSizeFileBuffer buffer) {
		this.buffer= buffer;
	}
	
	
	@Override
	public void write(int b) throws IOException {
		buffer.write(b);
	}


	@Override
	public void close() throws IOException {
		buffer.closeOutput();
	}
	
	

}
