/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Ristretto Mail API.
 *
 * The Initial Developers of the Original Code are
 * Timo Stich and Frederik Dietz.
 * Portions created by the Initial Developers are Copyright (C) 2004
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
package org.columba.ristretto.parser;

import org.columba.ristretto.message.Address;
import org.junit.Assert;
import org.junit.Test;

public class AddressParserTest{

	@Test
	public void testSingle1() {
		String testString = "Peter ?lafton <xyt@zpt.de>";
		
		
		Address address;
		try {
			address = AddressParser.parseAddress(testString);
		} catch (ParserException e) {
			Assert.assertTrue(false);
			return;
		}
		
		Address testAddress = new Address( "Peter ?lafton", "xyt@zpt.de");
		
		Assert.assertTrue( address.equals( testAddress ));
		Assert.assertTrue( address.getDisplayName().equals("Peter ?lafton"));
		Assert.assertTrue( address.toString().equals("\"Peter ?lafton\" <xyt@zpt.de>"));
	}

	@Test
	public void testSingle8() {
		String testString = "mail@timostich.de, ";
		
		
		Address[] address;
		try {
			address = AddressParser.parseMailboxList(testString);
		} catch (ParserException e) {
			Assert.assertTrue(false);
			return;
		}
		
		Assert.assertEquals(1, address.length);
		Assert.assertEquals("mail@timostich.de", address[0].getMailAddress());
	}

	/*	
	public void testGroup() {
	    String testString = "\"GMX Kundennummer #7743037\": ;";
	    
	    
	    Address[] addressList;
	    try {
	        addressList = AddressParser.parseMailboxList(testString);
	    } catch (ParserException e) {
	        assertTrue(false);
	        return;
	    }
	    
	    assertTrue( addressList[0].getDisplayName().equals("\"GMX Kundennummer #7743037\""));
	}
*/	
	
	
	@Test
	public void testSingle2() {
		String testString = "<xyt@zpt.de>";
		
		
		Address[] addressList;
		try {
			addressList = AddressParser.parseMailboxList(testString);
		} catch (ParserException e) {
			Assert.assertTrue(false);
			return;
		}
		
		Address testAddress = new Address( "xyt@zpt.de");
		
		Assert.assertTrue( addressList.length == 1);
		Assert.assertTrue( ((Address) addressList[0]).equals( testAddress ));
		Assert.assertTrue( ((Address) addressList[0]).getDisplayName().equals(""));
		Assert.assertEquals( addressList[0].getMailAddress(), "xyt@zpt.de");
	}

	@Test
	public void testSingle3() {
		String testString = "xyt@zpt.de";
		
		
		Address[] addressList;
		try {
			addressList = AddressParser.parseMailboxList(testString);
		} catch (ParserException e) {
			Assert.assertTrue(false);
			return;
		}
		
		Address testAddress = new Address( "xyt@zpt.de");
		
		Assert.assertTrue( addressList.length == 1);
		Assert.assertTrue( ((Address) addressList[0]).equals( testAddress ));
		Assert.assertTrue( ((Address) addressList[0]).getDisplayName().equals(""));			
	}

	@Test
	public void testSingle4() {
	    String testString = "\"Peter ?lafton\" <xyt@zpt.de>";
	    
	    
	    Address[] addressList;
	    try {
	        addressList = AddressParser.parseMailboxList(testString);
	    } catch (ParserException e) {
	    	Assert.assertTrue(false);
	        return;
	    }
	    
	    Address testAddress = new Address( "Peter ?lafton", "xyt@zpt.de");
	    
	    Assert.assertTrue( addressList.length == 1);
	    Assert.assertTrue( ((Address) addressList[0]).equals( testAddress ));
	    Assert.assertTrue( ((Address) addressList[0]).getDisplayName().equals("Peter ?lafton"));	
	}
	
	@Test
	public void testMultiple1() {
		String testString = "Peter ?lafton <xyt@zpt.de>, xyt@zpt.de, <xyt@zpt.de>";		
		
		Address[] addressList;
		try {
			addressList = AddressParser.parseMailboxList(testString);
		} catch (ParserException e) {
			Assert.assertTrue(false);
			return;
		}
		
		Assert.assertTrue( addressList.length == 3);
		Assert.assertTrue( ((Address) addressList[0]).equals( new Address( "Peter ?lafton", "xyt@zpt.de") ));
		Assert.assertTrue( ((Address) addressList[0]).getDisplayName().equals("Peter ?lafton"));	
		Assert.assertTrue( ((Address) addressList[1]).equals( new Address( "xyt@zpt.de") ));
		Assert.assertTrue( ((Address) addressList[2]).equals( new Address( "xyt@zpt.de") ));
	}
	
	@Test
	public void testMultiple2() {
		String testString = "hans, peter, lukas";		
		
		Address[] addressList;
		try {
			addressList = AddressParser.parseMailboxList(testString);
		} catch (ParserException e) {
			Assert.assertTrue(false);
			return;
		}
		
		Assert.assertTrue( addressList.length == 3);
		Assert.assertTrue( ((Address) addressList[0]).equals( new Address( "hans") ));
		Assert.assertTrue( ((Address) addressList[1]).equals( new Address( "peter") ));
		Assert.assertTrue( ((Address) addressList[2]).equals( new Address( "lukas") ));
	}

	@Test
	public void testMultipleQuoted1() {
		String testString = "\"Rmazam, Peter\" <xyt@zpt.de>, \"Bkalbal, Olaf\" <zkn@opb.com>";
		
		
		Address[] addressList;
		try {
			addressList = AddressParser.parseMailboxList(testString);
		} catch (ParserException e) {
			Assert.assertTrue(false);
			return;
		}
		
		Assert.assertTrue( addressList.length == 2);
		Assert.assertTrue( ((Address) addressList[0]).equals( new Address( "Rmazam, Peter", "xyt@zpt.de") ));
		Assert.assertTrue( ((Address) addressList[0]).getDisplayName().equals("Rmazam, Peter"));			
		Assert.assertTrue( ((Address) addressList[1]).equals( new Address( "Bkalbal, Olaf", "zkn@opb.com") ));
		Assert.assertTrue( ((Address) addressList[1]).getDisplayName().equals("Bkalbal, Olaf"));			
	}
	
	@Test
	public void testSingle5() {
	    String testString = "columba-devel-admin@lists.sourceforge.net";
	    
	    
	    Address[] addressList;
	    try {
		        addressList = AddressParser.parseMailboxList(testString);
	    } catch (ParserException e) {
	    	Assert.assertTrue(false);
	        return;
	    }
	    
	    Address testAddress = new Address( "columba-devel-admin@lists.sourceforge.net");
	    
	    Assert.assertTrue( addressList.length == 1);
	    Assert.assertTrue( ((Address) addressList[0]).equals( testAddress ));
	}
	
	
	@Test
	public void testAddressToString1() {
		Address testAddress = new Address( "Timo Stich", "tstich@users.sourceforge.net");
		String addressString = testAddress.toString();
		Assert.assertTrue( addressString.equals("\"Timo Stich\" <tstich@users.sourceforge.net>"));
	}

	@Test
	public void testAddressToString2() {
		Address testAddress = new Address( "tstich@users.sourceforge.net");
		String addressString = testAddress.toString();
		Assert.assertTrue( addressString.equals("tstich@users.sourceforge.net"));
	}
	
	@Test
	public void testSingle6() {
		String testString = "=?ISO-8859-1?Q?J=F6rg_Tester?= <tester@test.de>";
		
		Address address;
		try {
			address = AddressParser.parseAddress(testString);
		} catch (ParserException e) {
			Assert.assertTrue(false);
			return;
		}
		
		Address testAddress = new Address( "=?ISO-8859-1?Q?J=F6rg_Tester?=", "tester@test.de");
		
		Assert.assertTrue( address.equals( testAddress ));
	}

	@Test
	public void testSingle7() {
		String testString = "\"info@spreadshirt.de\"<info@spreadshirt.de>";
		
		
		Address[] addressList;
		try {
			addressList = AddressParser.parseMailboxList(testString);
		} catch (ParserException e) {
			Assert.assertTrue(false);
			return;
		}
		
		Address testAddress = new Address( "info@spreadshirt.de", "info@spreadshirt.de");
		
		Assert.assertTrue( addressList.length == 1);
		Assert.assertTrue( ((Address) addressList[0]).equals( testAddress ));
		Assert.assertTrue( ((Address) addressList[0]).getDisplayName().equals("info@spreadshirt.de"));
		Assert.assertEquals( addressList[0].getMailAddress(), "info@spreadshirt.de");
	}

}
