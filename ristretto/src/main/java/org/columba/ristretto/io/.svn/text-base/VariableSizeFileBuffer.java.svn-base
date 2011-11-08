package org.columba.ristretto.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VariableSizeFileBuffer {
	
	private static final int BLOCK_SIZE = 524288; // 500kB
	
	private List<File> blocks;	
	
	private OutputStream out;
	private int size;
	
	private InputStream in;
	private int inPos;
	
	public VariableSizeFileBuffer() throws IOException {
		blocks = new ArrayList<File>();
		
		File first = createNewBlock();
		out = new FileOutputStream(first);
		in = new TempFileInputStream(first);
	}

	public void write(int c) throws IOException {		
		out.write(c);
		advanceOutput();
	}
	
	public int read() throws IOException {
		int result;
		if(inPos < size) {
			result = in.read();
			advanceInput();
		} else {
			result = -1;
		}		
		
		return result;		
	}	
	
	private void advanceInput() throws IOException {
		inPos++;
		if( inPos % BLOCK_SIZE == 0 ) {
			in.close();			
			in = new TempFileInputStream(blocks.get(inPos / BLOCK_SIZE));
		}
	}
	
	private void advanceOutput() throws IOException {
		size++;
		if( size % BLOCK_SIZE == 0 ) {
			out.close();			
			
			File newBlockFile = createNewBlock();			
			out = new FileOutputStream(newBlockFile);
		}
	}

	public int getSize() {
		return size;
	}
	
	public void closeOutput() throws IOException {
		out.close();		
	}
	
	public void closeInput() throws IOException {
		in.close();
		
		// Remove any block that might not be deleted yet
		Iterator<File> it = blocks.iterator();
		while( it.hasNext() ) {
			it.next().delete();
		}
	}

	private File createNewBlock() throws IOException {
		File tempFile = TempSourceFactory.createTempFile();
        byte[] zeros = new byte[1024];
        
        // Fill with zeros so that the InputStream works correctly
        OutputStream out = new FileOutputStream(tempFile);
        try {
			int i;
			for( i=0; i<BLOCK_SIZE; i+=1024) {
			    out.write(zeros);
			}
			out.write(zeros,0,1024);
		} finally {
            out.close();
		}

		blocks.add(tempFile);		
		return tempFile;
	}

}
