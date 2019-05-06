package com.xingyu.service.account;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.core.MediaType;

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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.jayway.jsonpath.JsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableWebMvc
public class UMSAccountServiceTests {
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
	public void adminShouldBeAbleToListUsers() throws Exception {
		this.mockMvc.perform(get("/accounts").header("Authorization", "Bearer " + this.userToken))
				.andExpect(status().isOk());
	}

	@Test
	public void adminShouldBeAbleToCreateUser() throws Exception {
		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("username", "John");
		params.add("password", "John123");
		params.add("email", "John@gmail.com");
		params.add("firstname", "John");
		params.add("lastname", "Snow");
		params.add("dob", "1888-2-2");
		ResultActions action = this.mockMvc
				.perform(MockMvcRequestBuilders.post("/accounts").header("Authorization", this.adminToken)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED).params(params));
		System.out.println(action.andReturn().getResponse().getContentAsString());
	}

	@Test
	public void adminShouldBeAbleToReadUser() throws Exception {
		String mvcResult = mockMvc
				.perform(
						MockMvcRequestBuilders.get("/accounts/1?platform=web").header("Authorization", this.adminToken))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		System.out.println("Result === " + mvcResult);
	}

	@Test
	public void adminShouldBeAbleToAssignRole() throws Exception {
		String mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/2/roles"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		System.out.println("Result === " + mvcResult);
	}

	@Test
	public void adminShouldBeAbleToRemoveRole() throws Exception {
		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("role", "user");
		params.add("id", "2");
		String mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.post("/accounts/2/roles").header("Authorization", this.adminToken)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED).params(params))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		System.out.println("Result === " + mvcResult);
	}

	@Test
	public void adminShouldBeAbleToAddAddress() throws Exception {
		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("type", "home2");
		params.add("state", "VIC");
		params.add("city", "Brunswick");
		params.add("postcode", "3012");
		String mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.post("/accounts/1/addresses").header("Authorization", this.adminToken)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED).params(params))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		System.out.println("Result === " + mvcResult);
	}

	@Test
	public void adminShouldBeAbleToRemoveAddress() throws Exception {
		String mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/accounts/1/addresses"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		System.out.println("Result === " + mvcResult);
	}

	@Test
	public void adminShouldBeAbleToDeleteUser() throws Exception {
		String mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/accounts/2")).andReturn().getResponse()
				.getContentAsString();
		System.out.println("Result === " + mvcResult);
	}

	@Test
	public void userShouldBeAbleToReadOwnProfile() throws Exception {
		this.mockMvc.perform(get("/accounts/2?platform=web").header("Authorization", this.userToken))
				.andExpect(status().isOk());
	}

	@Test
	public void userShouldBeAbleToDeleteOwnProfile() throws Exception {
		this.mockMvc.perform(delete("/accounts/2").header("Authorization", this.userToken)).andExpect(status().isOk());
	}

	@Test
	public void userShouldNotBeAbleToListUsers() throws Exception {
		this.mockMvc.perform(get("/accounts").header("Authorization", this.userToken))
				.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	public void userShouldNotBeAbleToReadOtherUsersProfile() throws Exception {
		this.mockMvc.perform(get("/accounts/1").header("Authorization", this.userToken))
				.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	public void userShouldNotBeAbleToDeleteOtherUsersProfile() throws Exception {
		this.mockMvc.perform(delete("/accounts/1").header("Authorization", this.userToken))
				.andExpect(jsonPath("$.data").doesNotExist());
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

	private String buildUrlEncodedFormEntity(String... params) {
		if ((params.length % 2) > 0) {
			throw new IllegalArgumentException("Need to give an even number of parameters");
		}
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < params.length; i += 2) {
			if (i > 0) {
				result.append('&');
			}
			try {
				result.append(URLEncoder.encode(params[i], StandardCharsets.UTF_8.name())).append('=')
						.append(URLEncoder.encode(params[i + 1], StandardCharsets.UTF_8.name()));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		return result.toString();
	}

}