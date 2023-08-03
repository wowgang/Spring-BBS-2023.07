package com.ys.sbbs.service;

import java.util.List;

import com.ys.sbbs.entity.SchDay;
import com.ys.sbbs.entity.Schedule;

// interface
public interface ScheduleService {

	List<Schedule> getDaySchedList(String uid, String sdate); // entity의 Schedule
	
	List<Schedule> getMonthList(String uid, String month, int lastDay);
	
	List<Schedule> getCalendarSchList(String uid, String startDate, String endDate);
	
	SchDay generateSchDay(String uid, String sdate, int date, int isOtherMonth);
	
	void insert(Schedule schedule);
	
	Schedule getSchedule(int sid);
	
	void update(Schedule schedule);
	
	void delete(int sid);
	
}
