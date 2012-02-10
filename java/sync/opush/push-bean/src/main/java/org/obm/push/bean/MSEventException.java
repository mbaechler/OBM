package org.obm.push.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import com.google.common.base.Objects;

public class MSEventException implements MSEventCommon, Serializable {

	protected String location;
	protected String subject;
	protected String description;
	protected Date dtStamp;
	protected Date endTime;
	protected Date startTime;
	protected Boolean allDayEvent;
	protected CalendarBusyStatus busyStatus;
	protected CalendarSensitivity sensitivity;
	protected CalendarMeetingStatus meetingStatus;
	protected Integer reminder;
	protected List<String> categories;
	private boolean deleted;
	private Date exceptionStartTime;
	
	public MSEventException() {
		super();
		this.deleted = false;
	}

	public boolean isDeletedException() {
		return deleted;
	}
	
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Date getExceptionStartTime() {
		return exceptionStartTime;
	}

	public void setExceptionStartTime(Date exceptionStartTime) {
		this.exceptionStartTime = exceptionStartTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDtStamp() {
		return dtStamp;
	}

	public void setDtStamp(Date dtStamp) {
		this.dtStamp = dtStamp;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Boolean getAllDayEvent() {
		return allDayEvent;
	}

	public void setAllDayEvent(Boolean allDayEvent) {
		this.allDayEvent = allDayEvent;
	}

	public CalendarBusyStatus getBusyStatus() {
		return busyStatus;
	}

	public void setBusyStatus(CalendarBusyStatus busyStatus) {
		this.busyStatus = busyStatus;
	}

	public CalendarSensitivity getSensitivity() {
		return sensitivity;
	}

	public void setSensitivity(CalendarSensitivity sensitivity) {
		this.sensitivity = sensitivity;
	}

	public CalendarMeetingStatus getMeetingStatus() {
		return meetingStatus;
	}

	public void setMeetingStatus(CalendarMeetingStatus meetingStatus) {
		this.meetingStatus = meetingStatus;
	}

	public Integer getReminder() {
		return reminder;
	}

	public void setReminder(Integer reminder) {
		this.reminder = reminder;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public boolean isDeleted() {
		return deleted;
	}

	@Override
	public final int hashCode(){
		return Objects.hashCode(location, subject, description, dtStamp, 
				endTime, startTime, allDayEvent, busyStatus, sensitivity, 
				meetingStatus, reminder, categories, deleted, exceptionStartTime);
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof MSEventException) {
			MSEventException that = (MSEventException) object;
			return Objects.equal(this.location, that.location)
				&& Objects.equal(this.subject, that.subject)
				&& Objects.equal(this.description, that.description)
				&& Objects.equal(this.dtStamp, that.dtStamp)
				&& Objects.equal(this.endTime, that.endTime)
				&& Objects.equal(this.startTime, that.startTime)
				&& Objects.equal(this.allDayEvent, that.allDayEvent)
				&& Objects.equal(this.busyStatus, that.busyStatus)
				&& Objects.equal(this.sensitivity, that.sensitivity)
				&& Objects.equal(this.meetingStatus, that.meetingStatus)
				&& Objects.equal(this.reminder, that.reminder)
				&& Objects.equal(this.categories, that.categories)
				&& Objects.equal(this.deleted, that.deleted)
				&& Objects.equal(this.exceptionStartTime, that.exceptionStartTime);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("location", location)
			.add("subject", subject)
			.add("description", description)
			.add("dtStamp", dtStamp)
			.add("endTime", endTime)
			.add("startTime", startTime)
			.add("allDayEvent", allDayEvent)
			.add("busyStatus", busyStatus)
			.add("sensitivity", sensitivity)
			.add("meetingStatus", meetingStatus)
			.add("reminder", reminder)
			.add("categories", categories)
			.add("deleted", deleted)
			.add("exceptionStartTime", exceptionStartTime)
			.toString();
	}
	
}
