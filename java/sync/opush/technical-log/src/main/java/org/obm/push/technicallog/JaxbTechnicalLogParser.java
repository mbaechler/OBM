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
package org.obm.push.technicallog;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.obm.push.technicallog.jaxb.schema.TechnicalLogs;


public class JaxbTechnicalLogParser implements ITechnicalLogParser{

	@Override
	public TechnicalLogs parse(InputStream xml) throws TechnicalLogParserException {
		try {
			JAXBContext context = JAXBContext.newInstance(TechnicalLogs.class);
			return (TechnicalLogs) context.createUnmarshaller().unmarshal(xml);
			
		} catch (JAXBException e) {
			throw new TechnicalLogParserException("Error while parsing xml", e);
		}
	}

}
