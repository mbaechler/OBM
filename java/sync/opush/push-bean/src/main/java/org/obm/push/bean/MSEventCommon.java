package org.obm.push.bean;

import java.util.Date;
import java.util.List;

public interface MSEventCommon {

	String getLocation();

	void setLocation(String location);

	String getSubject();

	void setSubject(String subject);

	Boolean getAllDayEvent();

	void setAllDayEvent(Boolean allDayEvent);

	CalendarBusyStatus getBusyStatus();

	void setBusyStatus(CalendarBusyStatus busyStatus);

	CalendarSensitivity getSensitivity();

	void setSensitivity(CalendarSensitivity sensitivity);

	CalendarMeetingStatus getMeetingStatus();

	void setMeetingStatus(CalendarMeetingStatus meetingStatus);

	Integer getReminder();

	void setReminder(Integer reminder);

	List<String> getCategories();

	void setCategories(List<String> categories);

	Date getDtStamp();

	void setDtStamp(Date dtStamp);

	Date getEndTime();

	void setEndTime(Date endTime);

	Date getStartTime();

	void setStartTime(Date startTime);

	String getDescription();

	void setDescription(String description);
	
}