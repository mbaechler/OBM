/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2011-2012  Linagora
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version, provided you comply 
 * with the Additional Terms applicable for OBM connector by Linagora 
 * pursuant to Section 7 of the GNU Affero General Public License, 
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain 
 * the “Message sent thanks to OBM, Free Communication by Linagora” 
 * signature notice appended to any and all outbound messages 
 * (notably e-mail and meeting requests), (ii) retain all hypertext links between 
 * OBM and obm.org, as well as between Linagora and linagora.com, and (iii) refrain 
 * from infringing Linagora intellectual property rights over its trademarks 
 * and commercial brands. Other Additional Terms apply, 
 * see <http://www.linagora.com/licenses/> for more details. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License 
 * for more details. 
 *
 * You should have received a copy of the GNU Affero General Public License 
 * and its applicable Additional Terms for OBM along with this program. If not, 
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License version 3 
 * and <http://www.linagora.com/licenses/> for the Additional Terms applicable to 
 * OBM connectors. 
 * 
 * ***** END LICENSE BLOCK ***** */
package org.obm.push.protocol.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.obm.DateUtils;
import org.obm.filter.SlowFilterRunner;
import org.obm.push.bean.MSAddress;
import org.obm.push.bean.MSEmailHeader;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@RunWith(SlowFilterRunner.class)
public class MSEmailHeaderSerializingTest {

	private SimpleDateFormat sdf;
	private SerializingTest serializingTest;

	@Before
	public void setUp() {
		sdf = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'");
		serializingTest = new SerializingTest();
	}
	
	@Test
	public void testSerializeFullMSEmailHeaderObject() {
		Date date = DateUtils.date("2012-02-05T11:46:32");
		
		Element parentElement = createRootDocument();
		MSEmailHeader msEmailHeader = new MSEmailHeader.Builder()
			.from(new MSAddress("from@obm.lng.org"))
			.replyTo(new MSAddress("from@mydomain.org"))
			.cc(new MSAddress("cc@obm.lng.org"))
			.to(new MSAddress("to.1@obm.lng.org"), new MSAddress("to.2@obm.lng.org"))
			.date(date)
			.subject("Subject").build();
		
		new MSEmailHeaderSerializer(parentElement, msEmailHeader).serializeMSEmailHeader();
		
		Assertions.assertThat(tagValue(parentElement, ASEMAIL.FROM)).isEqualTo(" <from@obm.lng.org> ");
		Assertions.assertThat(tagValue(parentElement, ASEMAIL.REPLY_TO)).isEqualTo(" <from@mydomain.org> ");
		Assertions.assertThat(tagValue(parentElement, ASEMAIL.CC)).isEqualTo(" <cc@obm.lng.org> ");
		Assertions.assertThat(tagValue(parentElement, ASEMAIL.TO)).isEqualTo(" <to.1@obm.lng.org> , <to.2@obm.lng.org> ");
		Assertions.assertThat(tagValue(parentElement, ASEMAIL.DISPLAY_TO)).isEqualTo(" <to.1@obm.lng.org> ");
		Assertions.assertThat(tagValue(parentElement, ASEMAIL.SUBJECT)).isEqualTo("Subject");
		Assertions.assertThat(tagValue(parentElement, ASEMAIL.DATE_RECEIVED)).isEqualTo(sdf.format(date));
	}

	@Test
	public void testSerializeFrom() {
		Element parentElement = createRootDocument();
		MSEmailHeader msEmailHeader = new MSEmailHeader.Builder()
			.from(new MSAddress("from@obm.lng.org")).build();
		
		new MSEmailHeaderSerializer(parentElement, msEmailHeader).serializeMSEmailHeader();
		
		Assertions.assertThat(tagValue(parentElement, ASEMAIL.FROM)).isEqualTo(" <from@obm.lng.org> ");
	}
	
	@Test
	public void testSerializeEmptyFrom() {
		Element parentElement = createRootDocument();
		MSEmailHeader msEmailHeader = new MSEmailHeader.Builder().build();
		
		new MSEmailHeaderSerializer(parentElement, msEmailHeader).serializeMSEmailHeader();
		
		Assertions.assertThat(tagValue(parentElement, ASEMAIL.FROM)).isEqualTo("\"Empty From\" <o-push@linagora.com> ");
	}
	
	@Test
	public void testSerializeEmptySubject() {
		Element parentElement = createRootDocument();
		MSEmailHeader msEmailHeader = new MSEmailHeader.Builder().build();
		
		new MSEmailHeaderSerializer(parentElement, msEmailHeader).serializeMSEmailHeader();
		
		Assertions.assertThat(tagValue(parentElement, ASEMAIL.SUBJECT)).isEqualTo("[Empty Subject]");
	}
	
	@Test
	public void testSerializeEmptyField() {
		Element parentElement = createRootDocument();
		MSEmailHeader msEmailHeader = new MSEmailHeader.Builder().build();
		
		new MSEmailHeaderSerializer(parentElement, msEmailHeader).serializeMSEmailHeader();
		
		Assertions.assertThat(tag(parentElement, ASEMAIL.CC)).isNull();
		Assertions.assertThat(tag(parentElement, ASEMAIL.TO)).isNull();
		Assertions.assertThat(tag(parentElement, ASEMAIL.DATE_RECEIVED)).isNull();
		Assertions.assertThat(tag(parentElement, ASEMAIL.DISPLAY_TO)).isNull();
	}

	private Node tag(Element element, ASEMAIL asemail) {
		return serializingTest.tag(element, asemail);
	}

	private String tagValue(Element element, ASEMAIL asemail) {
		return serializingTest.tagValue(element, asemail);
	}

	private Element createRootDocument() {
		return serializingTest.createRootDocument();
	}
}