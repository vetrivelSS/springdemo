package com.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.ScheduleAdminDto;
import com.dto.ServiceResponseDto;
import com.service.ScheduleAdminService;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/ScheduleAdmin/booking")



public class ScheduleAdminController {
	@Autowired
	private ScheduleAdminService sr;
	@PostMapping("/ScheduleAdmin")
	public ResponseEntity<ServiceResponseDto> ScheduleAdminServicepublich(@RequestBody  ScheduleAdminDto dto) {
		ResponseEntity<ServiceResponseDto> servResponse = null;
		ServiceResponseDto response = null;
		try {
			response = new ServiceResponseDto("0000", Arrays.asList(sr.ScheduleAdminServicepublich(dto)));
			servResponse = new ResponseEntity<ServiceResponseDto>(response, HttpStatus.OK);

		} catch (RuntimeException e) {

			response = new ServiceResponseDto("1111", Arrays.asList(e.getMessage()));
			servResponse = new ResponseEntity<ServiceResponseDto>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return servResponse;

	}
	 @GetMapping("/getAll")
	    public List<ScheduleAdminDto> getAllScheduleDetails() {
	        return sr.getAllScheduleDetails();
	    }


}
