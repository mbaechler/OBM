package org.obm.push;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.obm.push.bean.User;
import org.slf4j.MDC;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LoggerService {

	@Inject
	private LoggerService() {
	}
	
	public void initSession(User user, int requestId, String command) {
		Calendar date = Calendar.getInstance();
		SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy.MM.dd_hh:mm:ss");
		String now = dateformatter.format(date.getTime());

		//to use with technicalLoggerService
		//closePrecedentLogFile();
		String sessionId = user.getLoginAtDomain() + "-" + now;
		
		MDC.put("title", "Opush ActiveSync");
		MDC.put("user", user.getLoginAtDomain());
		MDC.put("sessionId", sessionId);
		MDC.put("threadId", String.valueOf(Thread.currentThread().getId()));
		MDC.put("requestId", String.valueOf(requestId));
		MDC.put("command", command);
	}
	

	public void closeSession() {
		MDC.clear();
	}
	
}
