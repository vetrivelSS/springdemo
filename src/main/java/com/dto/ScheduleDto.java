package com.dto;

import java.sql.Date;
import java.sql.Time;

public class ScheduleDto {
	 private byte[] uploadImage;       // for LONGBLOB
	    private String title;
	    private String datePublish;
	    private String publishTime;
	    public byte[] getUploadImage() {
			return uploadImage;
		}
		public void setUploadImage(byte[] uploadImage) {
			this.uploadImage = uploadImage;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getDatePublish() {
			return datePublish;
		}
		public void setDatePublish(String datePublish) {
			this.datePublish = datePublish;
		}
		public String getPublishTime() {
			return publishTime;
		}
		public void setPublishTime(String publishTime) {
			this.publishTime = publishTime;
		}
		public String getStartLocation() {
			return startLocation;
		}
		public void setStartLocation(String startLocation) {
			this.startLocation = startLocation;
		}
		public String getEndLocation() {
			return endLocation;
		}
		public void setEndLocation(String endLocation) {
			this.endLocation = endLocation;
		}
		public double getDistance() {
			return distance;
		}
		public void setDistance(double distance) {
			this.distance = distance;
		}
		public String getOrganizedBy() {
			return organizedBy;
		}
		public void setOrganizedBy(String organizedBy) {
			this.organizedBy = organizedBy;
		}
		public String getContactMobile() {
			return contactMobile;
		}
		public void setContactMobile(String contactMobile) {
			this.contactMobile = contactMobile;
		}
		public String getContactEmail() {
			return contactEmail;
		}
		public void setContactEmail(String contactEmail) {
			this.contactEmail = contactEmail;
		}
		private String startLocation;
	    private String endLocation;
	    private double distance;
	    private String organizedBy;
	    private String contactMobile;
	    private String contactEmail;
}
