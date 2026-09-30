package com.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.Dao.ScheduleAdminDao;
import com.dto.ScheduleAdminDto;
import com.dto.ServiceResponseDto;
import com.service.ScheduleAdminService;
@Component
public class ScheduleAdminServiceimpl implements ScheduleAdminService {
	@Autowired
	private ScheduleAdminDao dao;

	@Override
	public String ScheduleAdminServicepublich(ScheduleAdminDto dto) {
		// TODO Auto-generated method stub
		return dao.ScheduleAdminServicepublich(dto);
	}

	@Override
	public List<ScheduleAdminDto> getAllScheduleDetails() {
		// TODO Auto-generated method stub
		return dao.getAllScheduleDetails();
	}

}
