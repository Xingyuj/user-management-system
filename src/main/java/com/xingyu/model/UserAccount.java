package com.xingyu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.google.gson.annotations.Expose;

@Entity
public class UserAccount implements Serializable {
	private static final long serialVersionUID = -3210565389728151509L;
	@Id
	@GeneratedValue
	@Expose
	private Long id;
	/**
	 * account
	 */
	@Column(unique = true)
	@Expose
	private String username;
	@Expose
	private String password;
	@Expose
	private String salt;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "SysUserRole", joinColumns = { @JoinColumn(name = "uid") }, inverseJoinColumns = {
			@JoinColumn(name = "roleId") })
	private List<SysRole> roleList;
	/**
	 * profile
	 */
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
	@JoinColumn(name = "userId")
	@OrderBy("id asc")
	private Set<Address> addresses;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public List<SysRole> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<SysRole> roleList) {
		this.roleList = roleList;
	}

	public String getCredentialsSalt() {
		return this.username + this.salt;
	}
	
	@Override
	public String toString() {
		return "{" +
					"id:" + id +
					", username:'" + username + '\'' +
					", firstname:'" + firstname + '\'' +
					", lastname:" + lastname + '\'' +
					", password:" + password + '\'' +
					", email:" + email + '\'' +
					", dob:" + dob + '\'' +
					", address:" + addresses + '\'' +
				'}';
	}
}