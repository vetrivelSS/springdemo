package com.dto;
import java.math.BigDecimal; // <-- Add this import
import java.sql.Date;
public class UserRegistrationDto {
	private String name; 
    private String email; 
    private String phone; 
    private String password; 
    private Date dob; 
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public BigDecimal getBodyWeight() {
		return bodyWeight;
	}
	public void setBodyWeight(BigDecimal bodyWeight) {
		this.bodyWeight = bodyWeight;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getPreExistingMedicalCondition() {
		return preExistingMedicalCondition;
	}
	public void setPreExistingMedicalCondition(String preExistingMedicalCondition) {
		this.preExistingMedicalCondition = preExistingMedicalCondition;
	}
	public String getExperiencedPastThreeMonths() {
		return experiencedPastThreeMonths;
	}
	public void setExperiencedPastThreeMonths(String experiencedPastThreeMonths) {
		this.experiencedPastThreeMonths = experiencedPastThreeMonths;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getEmergencyNo() {
		return emergencyNo;
	}
	public void setEmergencyNo(String emergencyNo) {
		this.emergencyNo = emergencyNo;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	private BigDecimal bodyWeight; 
    private String height; 
    private String preExistingMedicalCondition; 
    private String experiencedPastThreeMonths; 
    private String remark; 
    private String gender; 
    private String emergencyNo; 
    private String address; 
    private String city;
    @Override
    public String toString() {
        return "UserRegistrationDto{" +
               "name='" + name + '\'' +
               ", email='" + email + '\'' +
               ", phone='" + phone + '\'' +
               ", password='" + "[HIDDEN]" + '\'' +
               ", dob=" + dob +
               ", bodyWeight=" + bodyWeight +
               ", height='" + height + '\'' +
               ", preExistingMedicalCondition='" + preExistingMedicalCondition + '\'' +
               ", experiencedPastThreeMonths='" + experiencedPastThreeMonths + '\'' +
               ", remark='" + remark + '\'' +
               ", gender='" + gender + '\'' +
               ", emergencyNo='" + emergencyNo + '\'' +
               ", address='" + address + '\'' +
               ", city='" + city + '\'' +
               '}';
    }
}
