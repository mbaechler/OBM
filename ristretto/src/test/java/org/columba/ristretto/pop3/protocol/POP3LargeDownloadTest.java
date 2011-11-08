package org.columba.ristretto.pop3.protocol;

import java.io.IOException;

import org.columba.ristretto.io.CharSequenceSource;
import org.columba.ristretto.io.Source;
import org.columba.ristretto.io.StreamUtils;
import org.columba.ristretto.pop3.POP3Exception;
import org.columba.ristretto.pop3.POP3Protocol;
import org.columba.ristretto.testserver.SimpleTestServerSession;
import org.columba.ristretto.testserver.TestServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class POP3LargeDownloadTest {

	private static final int SIZE = 1000000;
	private String largeMessage;
	
	@Test
	public void testLargerAsExpected() throws IOException, POP3Exception {
			SimpleTestServerSession testSession = new SimpleTestServerSession("+OK <test@timestamp>\r\n", "QUIT\r\n");
			testSession.addDialog("USER test\r\n", "+OK\r\n");
			testSession.addDialog("PASS star\r\n", "+OK user authenticated\r\n");
			testSession.addDialog("RETR 1\r\n", "+OK 150 octets\r\n"+ largeMessage + ".\r\n");
			testSession.addDialog("QUIT\r\n", "+OK und tschuess\r\n");

			TestServer testServer = new TestServer(50110, testSession);

			POP3Protocol protocol = new POP3Protocol("localhost", 50110);
			
			protocol.openPort();
			Assert.assertTrue( protocol.getState() == POP3Protocol.AUTHORIZATION);
			
			protocol.userPass("test", "star".toCharArray());
			Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
			
			
			Source message = new CharSequenceSource( StreamUtils.readInString( protocol.retr(1 , 600000) ) );		
			Assert.assertEquals( largeMessage, message.toString());
			Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
			
			testServer.stop();
	}
	
	@Test
	public void testSmallerAsExpected() throws IOException, POP3Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("+OK <test@timestamp>\r\n", "QUIT\r\n");
		testSession.addDialog("USER test\r\n", "+OK\r\n");
		testSession.addDialog("PASS star\r\n", "+OK user authenticated\r\n");
		testSession.addDialog("RETR 1\r\n", "+OK 150 octets\r\n"+ largeMessage + ".\r\n");
		testSession.addDialog("QUIT\r\n", "+OK und tschuess\r\n");

		TestServer testServer = new TestServer(50110, testSession);

		POP3Protocol protocol = new POP3Protocol("localhost", 50110);
		
		protocol.openPort();
		Assert.assertTrue( protocol.getState() == POP3Protocol.AUTHORIZATION);
		
		protocol.userPass("test", "star".toCharArray());
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		
		Source message = new CharSequenceSource( StreamUtils.readInString( protocol.retr(1 , 6000000) ) );		
		Assert.assertEquals( largeMessage, message.toString());
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		testServer.stop();
}

	
	@Before
	public void setUp() throws Exception {
		StringBuffer largeTemp = new StringBuffer();
		
		for( int i=1; i< SIZE; i++) {
			largeTemp.append((char)('A' + i % 48));
			if( i % 75 == 0) {
				largeTemp.append('\r');
				largeTemp.append('\n');
			}
		}
		largeTemp.append("\r\n");
		
		largeMessage = largeTemp.toString();
	}
	
}
