package org.obm.push.mail;

import java.util.Arrays;
import java.util.List;

import org.fest.assertions.Assertions;
import org.junit.Test;
import org.minig.imap.command.parser.BodyStructureParser;
import org.minig.imap.mime.IMimePart;
import org.minig.imap.mime.MimeMessage;
import org.obm.mail.message.BodySelector;

public class BodySelectorTest {

	@Test
	public void testPreferHtml() {
		List<String> htmlMimeSubtypePriority = Arrays.asList("html", "plain", "calendar");
		IMimePart bodyPart = testJira2715(htmlMimeSubtypePriority);
		Assertions.assertThat(bodyPart.getFullMimeType()).isEqualTo("text/plain");
	}
	
	@Test
	public void testPreferCalendar() {
		List<String> htmlMimeSubtypePriority = Arrays.asList("calendar", "html", "plain");
		IMimePart bodyPart = testJira2715(htmlMimeSubtypePriority);
		Assertions.assertThat(bodyPart.getFullMimeType()).isEqualTo("text/calendar");
	}

	private IMimePart testJira2715(List<String> htmlMimeSubtypePriority) {
		String bodyStructure = "((\"TEXT\" \"PLAIN\" (\"CHARSET\" \"iso-8859-1\") " +
				"NIL NIL \"QUOTED-PRINTABLE\" 0 0 NIL NIL NIL NIL)" +
				"(\"TEXT\" \"CALENDAR\" (\"CHARSET\" \"utf-8\" \"METHOD\" \"REPLY\") " +
				"NIL NIL \"BASE64\" 1586 21 NIL NIL NIL NIL) \"ALTERNATIVE\" " +
				"(\"BOUNDARY\" \"_002_4CB558BA44FD6C408AFE5E5F23FDFE45058CE5WING3L4U6U4IKJtes_\")" +
				" NIL (\"FR-FR\") NIL)";
		MimeMessage mimeMessage = new BodyStructureParser().parseBodyStructure(bodyStructure);
		BodySelector bodySelector = new BodySelector(mimeMessage, htmlMimeSubtypePriority);
		IMimePart bodyPart = bodySelector.findBodyTextPart();
		return bodyPart;
	}

}
