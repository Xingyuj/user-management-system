package com.xingyu.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.google.gson.annotations.Expose;

@Entity
public class Address {

    @Id
    @GeneratedValue
    @Expose
    private Long id;
    @Expose
    private String type;
    @Expose
    private String postcode;
    @Expose
    private String state;
    @Expose
    private String city;
    public String getType() {
		return type;
	}
    
	public void setType(String type) {
		this.type = type;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	private String street;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
