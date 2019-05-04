package com.xingyu.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class UserProfile {
	@Id
    @GeneratedValue
    private Integer profile_id;
	private String firstname;
	private String lastname;
	@Temporal(TemporalType.DATE)
	private Date dob;
	private String email;
	
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "uid")
    @OrderBy("id asc")
    private Set<Address> addresses;
	public Integer getProfile_id() {
		return profile_id;
	}
	public void setProfile_id(Integer profile_id) {
		this.profile_id = profile_id;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Set<Address> getAddresses() {
		return addresses;
	}
	public void setAddresses(Set<Address> addresses) {
		this.addresses = addresses;
	}

}
