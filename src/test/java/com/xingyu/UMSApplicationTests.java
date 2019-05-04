package com.xingyu;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UMSApplicationTests {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
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
    
    @Test
    public void adminShouldBeAbleToListUsers() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/users").header("Authorization",""))
        	.andExpect(status().isOk())
        	.andExpect(jsonPath( "$.data").exists());
    }

    @Test
    public void adminShouldBeAbleToCreateUser() throws Exception {
    	//TODO:
    }
    
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

}