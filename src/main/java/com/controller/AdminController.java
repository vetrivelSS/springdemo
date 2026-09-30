package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.AdminDto;
import com.service.AdminService;
@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {
	 @Autowired
	    private AdminService adminService;

	    @PostMapping("/login")
	    public String login(@RequestBody AdminDto dto) {
	        boolean status = adminService.login(dto.getEmail(), dto.getPassword());
	        if (status) {
	            return "Login Success";
	        } else {
	            return "Invalid Email or Password";
	        }
	    }

}
