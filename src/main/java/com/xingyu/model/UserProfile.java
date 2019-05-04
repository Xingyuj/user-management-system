package com.xingyu.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.google.gson.annotations.Expose;

@Entity
public class UserProfile {
	@Id
    @GeneratedValue
    private Long id;
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "uid")
    private UserAccount account;
	@Expose
	private String firstname;
	@Expose
	private String lastname;
	@Expose
	@Temporal(TemporalType.DATE)
	private Date dob;
	@Expose
	private String email;
	
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    @OrderBy("id asc")
    private Set<Address> addresses;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public UserAccount getAccount() {
		return account;
	}
	public void setAccount(UserAccount account) {
		this.account = account;
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
