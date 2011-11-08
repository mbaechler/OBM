package org.columba.ristretto.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

public class VariableSizeFileBufferTest extends TestCase {

	@Test
	public void testOneBlock() throws IOException {
		VariableSizeFileBuffer buffer = new VariableSizeFileBuffer();
		
		InputStream in = new VariableSizeFileBufferInputStream(buffer);
		OutputStream out = new VariableSizeFileBufferOutputStream(buffer);
		
		byte[] test = new byte[1000];
		Random rand = new Random();
		rand.nextBytes(test);
		
		// First write some stuff
		out.write(test);
		
		Assert.assertEquals(buffer.getSize(), test.length);
		
		// Read it back
		for( int i=0; i<test.length; i++) {
			Assert.assertEquals((byte)in.read(), test[i]);
		}
		
		// Must be eof
		Assert.assertEquals(-1, in.read());
	}

	@Test
	public void testMoreBlocks() throws IOException {
		VariableSizeFileBuffer buffer = new VariableSizeFileBuffer();
		
		InputStream in = new VariableSizeFileBufferInputStream(buffer);
		OutputStream out = new VariableSizeFileBufferOutputStream(buffer);
		
		byte[] test = new byte[600000];
		Random rand = new Random();
		rand.nextBytes(test);
		
		// First write some stuff
		out.write(test);
		
		Assert.assertEquals(buffer.getSize(), test.length);
		
		// Read it back
		for( int i=0; i<test.length; i++) {
			Assert.assertEquals((byte)in.read(), test[i]);
		}
		
		// Must be eof
		Assert.assertEquals(-1, in.read());
	}
	
	@Test
	public void testInterleaved() throws IOException {
		VariableSizeFileBuffer buffer = new VariableSizeFileBuffer();
		
		InputStream in = new VariableSizeFileBufferInputStream(buffer);
		OutputStream out = new VariableSizeFileBufferOutputStream(buffer);
		
		byte[] test = new byte[1000];
		Random rand = new Random();
		
		for( int j=1; j <= 1000; j++) {
		rand.nextBytes(test);
		
		// First write some stuff
		out.write(test);
		
		Assert.assertEquals(buffer.getSize(), test.length * j);
		
		// Read it back
		for( int i=0; i<test.length; i++) {
			Assert.assertEquals((byte)in.read(), test[i]);
		}

		// Must be eof
		Assert.assertEquals(-1, in.read());
		}
	}	
}
