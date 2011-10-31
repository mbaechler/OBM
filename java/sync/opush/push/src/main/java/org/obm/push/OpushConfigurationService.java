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
package org.obm.push;

import java.io.InputStream;

import javax.servlet.ServletContext;

import org.obm.configuration.ObmConfigurationService;
import org.obm.configuration.store.StoreNotFoundException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class OpushConfigurationService extends ObmConfigurationService {
	
	private final ServletContext servletContext;
	
	@Inject
	private OpushConfigurationService(ServletContext servletContext) {
		super();
		this.servletContext = servletContext;
	}
	
	@Override
	public InputStream getStoreConfiguration() throws StoreNotFoundException {
		InputStream storeConfigurations = servletContext.getResourceAsStream("/WEB-INF/objectStoreManager.xml");
		if (storeConfigurations == null) {
			throw new StoreNotFoundException("/WEB-INF/objectStoreManager.xml not found !");
		}
		return storeConfigurations;
	}
	
}
