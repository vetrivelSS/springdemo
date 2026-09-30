package com.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dto.ScheduleAdminDto;
import com.dto.ServiceResponseDto;
@Service
public interface ScheduleAdminService {

	public String ScheduleAdminServicepublich(ScheduleAdminDto dto) ;

	public List<ScheduleAdminDto> getAllScheduleDetails();
		

}
