package com.xingyu;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import com.jayway.jsonpath.JsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UMSApplicationTests {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    private String adminToken;
    private String userToken;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        
//        final MultiValueMap<String, String> adminLoginParams = new LinkedMultiValueMap<>();
//        adminLoginParams.add("username", "admin");
//        adminLoginParams.add("password", "admin");
//        this.adminToken = this.fetchJWTToken(adminLoginParams);
//
//        final MultiValueMap<String, String> userLoginParams = new LinkedMultiValueMap<>();
//        userLoginParams.add("username", "ethan");
//        userLoginParams.add("password", "ethan");
//        this.adminToken = this.fetchJWTToken(userLoginParams);
    }

    @Test
    public void loginShouldReturnJWTToken() throws Exception {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "admin");
        params.add("password", "admin");
        this.mockMvc.perform(MockMvcRequestBuilders.post("/login").params(params))
        	.andExpect(status().isOk())
        	.andExpect(jsonPath( "$.data").exists());
    }
    
//    @Test
//    public void adminShouldBeAbleToListUsers() throws Exception {
//        this.mockMvc.perform(MockMvcRequestBuilders.get("/users").header("Authorization",this.adminToken))
//        	.andExpect(status().isOk())
//        	.andExpect(jsonPath( "$.data").exists());
//    }
//
//    @Test
//    public void adminShouldBeAbleToCreateUser() throws Exception {
//    	final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("username", "Amy");
//        params.add("password", "Amy123");
//        params.add("email", "amy@abc.com");
//    	this.mockMvc.perform(MockMvcRequestBuilders.post("/users").header("Authorization",this.adminToken).params(params))
//    	.andExpect(status().isOk())
//    	.andExpect(jsonPath( "$.data").exists());
//    }
//    
    @Test
    public void adminShouldBeAbleToReadUser() throws Exception {
    	//TODO:
    }
    
    @Test
    public void adminShouldBeAbleToUpdateUser() throws Exception {
    	//TODO:
    }
    
    @Test
    public void adminShouldBeAbleToAssignRoleAdmin() throws Exception {
    	//TODO:
    }
    
    @Test
    public void adminShouldBeAbleToAssignRoleUser() throws Exception {
    	//TODO:
    }
    
    @Test
    public void userShouldAbleToReadOwnProfile() throws Exception {
    	//TODO:
    }
    
    @Test
    public void userShouldAbleToDeleteOwnProfile() throws Exception {
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