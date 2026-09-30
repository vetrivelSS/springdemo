package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Dao.AdminDao;
@Service
public class AdminService {
	  @Autowired
	    private AdminDao adminDAO;

	    public boolean login(String email, String password) {
	        return adminDAO.validateAdmin(email, password);
	    }

}
