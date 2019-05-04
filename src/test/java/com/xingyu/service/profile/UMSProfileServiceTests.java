package com.xingyu.service.profile;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import com.jayway.jsonpath.JsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UMSProfileServiceTests {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    private String adminToken;
    private String userToken;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        
        final MultiValueMap<String, String> adminLoginParams = new LinkedMultiValueMap<>();
        adminLoginParams.add("username", "admin");
        adminLoginParams.add("password", "admin");
        this.adminToken = this.fetchJWTToken(adminLoginParams);

        final MultiValueMap<String, String> userLoginParams = new LinkedMultiValueMap<>();
        userLoginParams.add("username", "ethan");
        userLoginParams.add("password", "ethan");
        this.userToken = this.fetchJWTToken(userLoginParams);
    }

    @Test
    public void adminShouldBeAbleToListProfiles() throws Exception {
        this.mockMvc.perform(get("/profiles").header("Authorization",this.userToken))
        .andExpect(status().isOk());
    }
    
    @Test
    public void userShouldNotBeAbleToListProfiles() throws Exception {
        this.mockMvc.perform(get("/profiles").header("Authorization",this.userToken))
        .andExpect(jsonPath( "$.data").doesNotExist());
    }

    @Test
    public void adminShouldBeAbleToCreateProfile() throws Exception {
    	final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("email", "amy@abc.com");
    	ResultActions action = this.mockMvc.perform(MockMvcRequestBuilders.post("/profiles").header("Authorization",this.adminToken).params(params))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath( "$.data").exists());
    	System.out.println(action.andReturn().getResponse().getContentAsString());
    }
    
    @Test
    public void adminShouldBeAbleToReadProfile() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/profiles/1").header("Authorization",this.adminToken))
    	.andExpect(status().isOk());
    }
    
    @Test
    public void adminShouldBeAbleToUpdateProfile() throws Exception {
    	final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("email", "amy@abc.com");
    	ResultActions action = this.mockMvc.perform(MockMvcRequestBuilders.put("/profiles").header("Authorization",this.adminToken).params(params))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath( "$.data").exists());
    	System.out.println(action.andReturn().getResponse().getContentAsString());
    }
    
    @Test
    public void adminShouldBeAbleToDeleteProfile() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/profiles/1").header("Authorization",this.adminToken))
    	.andExpect(status().isOk());
    }
    
    @Test
    public void userShouldBeAbleToReadOwnProfile() throws Exception {
        //TODO:
    }
    
    @Test
    public void userShouldBeAbleToDeleteOwnProfile() throws Exception {
        //TODO:
    }
    
    private String fetchJWTToken(MultiValueMap<String, String> params)  {
    	String response = null;
		try {
			response = mockMvc.perform(MockMvcRequestBuilders.post("/login")
			        .params(params)).andReturn().getResponse().getContentAsString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return JsonPath.parse(response).read("$.data").toString();
    }

}