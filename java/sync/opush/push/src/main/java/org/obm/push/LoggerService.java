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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.core.sift.AppenderTracker;
import ch.qos.logback.core.sift.SiftingAppenderBase;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LoggerService {

	@Inject
	private LoggerService() {
	}
	
	public void initSession(String loginAtDomain, int requestId, String command) {
		Calendar date = Calendar.getInstance();
		SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy.MM.dd_hh:mm:ss");
		String now = dateformatter.format(date.getTime());

		closePrecedentLogFile();
		String sessionId = loginAtDomain+"-"+now;
		
		MDC.put("title", "Opush ActiveSync");
		MDC.put("user", loginAtDomain);
		MDC.put("sessionId", sessionId);
		MDC.put("threadId", String.valueOf(Thread.currentThread().getId()));
		MDC.put("requestId", String.valueOf(requestId));
		MDC.put("command", command);
	}
	
	private String getLastSessionLogFileName(){
		String sessionId = MDC.get("sessionId");
		if(sessionId == null) {
			return "no-session";
		} else {
			return sessionId;
		}
	}

	private void closePrecedentLogFile(){
		String logFileName = getLastSessionLogFileName();
		if (logFileName != null) {

			LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
			SiftingAppenderBase<?> siftingAppender = (SiftingAppender) loggerContext
														.getLogger(Logger.ROOT_LOGGER_NAME)
														.getAppender("SIFTING");
			if (siftingAppender != null) {
				AppenderTracker<?> appenderTracker = siftingAppender.getAppenderTracker();
				appenderTracker.stopAndRemoveNow(logFileName);
			}
		}
	}

	public void closeSession() {
		MDC.clear();
	}
	
}
