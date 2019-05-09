package com.xingyu.service.account;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

import javax.servlet.Filter;
import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.xingyu.model.Address;
import com.xingyu.model.SysRole;
import com.xingyu.model.UserAccount;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableWebMvc
public class UMSAccountServiceTests {
	@Autowired
	private WebApplicationContext wac;
	private MockMvc mockMvc;
	private String adminToken;
	private String userToken;
	private ObjectMapper objectMapper;

	@Before
	public void setup() {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
		builder.addFilters((Filter) this.wac.getBean("shiroFilter"));
		this.mockMvc = builder.build();
		final MultiValueMap<String, String> adminLoginParams = new LinkedMultiValueMap<>();
		adminLoginParams.add("username", "admin");
		adminLoginParams.add("password", "admin");
		this.adminToken = this.fetchJWTToken(adminLoginParams);

		final MultiValueMap<String, String> userLoginParams = new LinkedMultiValueMap<>();
		userLoginParams.add("username", "ethan");
		userLoginParams.add("password", "ethan");
		this.userToken = this.fetchJWTToken(userLoginParams);
		this.objectMapper = new MappingJackson2HttpMessageConverter().getObjectMapper();
	}

	@Test
	public void adminShouldBeAbleToListUsers() throws Exception {
		String result = this.mockMvc.perform(get("/accounts").header("Authorization", this.adminToken))
				.andExpect(status().isOk()).andExpect(jsonPath("$.data").exists()).andReturn().getResponse()
				.getContentAsString();
		System.out.println("adminShouldBeAbleToListUsers: results: " + result);
	}

	@Test
	public void adminShouldBeAbleToCreateUser() throws Exception {
		UserAccount account = new UserAccount();
		account.setId(999l);
		account.setUsername("john");
		account.setFirstname("john");
		account.setLastname("snow");
		account.setEmail("john@gmail.com");
		account.setPassword("john123");
		ResultActions action = this.mockMvc
				.perform(MockMvcRequestBuilders.post("/accounts").header("Authorization", this.adminToken)
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(account)));
		System.out.println(
				"adminShouldBeAbleToCreateUser: results: " + action.andReturn().getResponse().getContentAsString());
		action.andExpect(status().isOk()).andExpect(jsonPath("$.data").exists());
	}

	@Test
	public void adminShouldBeAbleToDeleteUser() throws Exception {
		String mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.delete("/accounts/999").header("Authorization", this.adminToken))
				.andExpect(status().isOk()).andExpect(jsonPath("$.data").exists()).andReturn().getResponse()
				.getContentAsString();
		System.out.println("adminShouldBeAbleToDeleteUser Result === " + mvcResult);
	}

	@Test
	public void adminShouldBeAbleToReadUser() throws Exception {
		ResultActions action = this.mockMvc.perform(
				MockMvcRequestBuilders.get("/accounts/1?platform=web").header("Authorization", this.adminToken));
		System.out.println(
				"adminShouldBeAbleToReadUser: results: " + action.andReturn().getResponse().getContentAsString());
		action.andExpect(status().isOk()).andExpect(jsonPath("$.data").exists());
	}

	@Test
	public void adminShouldBeAbleToRemoveRole() throws Exception {
		SysRole role = new SysRole();
		role.setId(2l);
		role.setRole("user");
		String mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.delete("/accounts/1/roles").header("Authorization", this.adminToken)
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(role)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.data").exists()).andReturn().getResponse()
				.getContentAsString();
		System.out.println("adminShouldBeAbleToRemoveRole Result === " + mvcResult);
	}

	@Test
	public void adminShouldBeAbleToAssignRole() throws Exception {
		SysRole role = new SysRole();
		role.setId(2l);
		role.setRole("user");
		String mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.post("/accounts/1/roles").header("Authorization", this.adminToken)
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(role)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.data").exists()).andReturn().getResponse()
				.getContentAsString();
		System.out.println("adminShouldBeAbleToAssignRole Result === " + mvcResult);
	}

	@Test
	public void adminShouldBeAbleToAddAddress() throws Exception {
		Address address = new Address();
		address.setId(1l);
		address.setType("home2");
		address.setCity("baiyin");
		String mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.post("/accounts/2/addresses").header("Authorization", this.adminToken)
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(address)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.data").exists()).andReturn().getResponse()
				.getContentAsString();
		System.out.println("adminShouldBeAbleToAddAddress Result === " + mvcResult);
	}

	@Test
	public void adminShouldBeAbleToRemoveAddress() throws Exception {
		Address address = new Address();
		address.setId(1l);
		address.setType("home2");
		address.setCity("baiyin");
		String mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.delete("/accounts/2/addresses").header("Authorization", this.adminToken)
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(address)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.data").exists())
				.andReturn().getResponse().getContentAsString();
		System.out.println("adminShouldBeAbleToRemoveAddress Result === " + mvcResult);
	}

	@Test
	public void userShouldBeAbleToReadOwnProfile() throws Exception {
		ResultActions action = this.mockMvc.perform(
				MockMvcRequestBuilders.get("/accounts/2?platform=mobile").header("Authorization", this.userToken));
		System.out.println(
				"adminShouldBeAbleToCreateUser: results: " + action.andReturn().getResponse().getContentAsString());
		action.andExpect(status().isOk()).andExpect(jsonPath("$.data").exists());
	}

	@Test
	public void userShouldNotBeAbleToReadOtherUsersProfile() throws Exception {
		String mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get("/accounts/1?platform=mobile").header("Authorization",
						this.userToken))
				.andExpect(status().isOk()).andExpect(jsonPath("$.data").doesNotExist()).andReturn().getResponse()
				.getContentAsString();
		System.out.println("userShouldNotBeAbleToReadOtherUsersProfile Result === " + mvcResult);
	}

	@Test
	public void userShouldNotBeAbleToDeleteOtherUsersProfile() throws Exception {
		String mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.delete("/accounts/1?platform=web").header("Authorization",
						this.userToken))
				.andExpect(status().isOk()).andExpect(jsonPath("$.data").doesNotExist()).andReturn().getResponse()
				.getContentAsString();
		System.out.println("userShouldNotBeAbleToDeleteOtherUsersProfile Result === " + mvcResult);
	}

	private String fetchJWTToken(MultiValueMap<String, String> params) {
		String response = null;
		try {
			response = mockMvc.perform(MockMvcRequestBuilders.post("/authentications").params(params)).andReturn()
					.getResponse().getContentAsString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JsonPath.parse(response).read("$.data").toString();
	}

}