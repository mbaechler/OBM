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

package org.minig.imap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Ignore;

import junit.framework.TestCase;

@Ignore("It's necessary to do again all tests")
public abstract class IMAPTestCase extends TestCase {

	protected String confValue(String key) {
		InputStream is = getClass().getClassLoader().getResourceAsStream(
				"data/test.properties.sample");
		Properties props = new Properties();
		if (is != null) {
			try {
				props.load(is);
				return props.getProperty(key);
			} catch (IOException e) {
				return null;
			} finally {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		} else {
			return null;
		}
	}
	
}
