package com.xingyu.controller;

import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xingyu.auth.JWTUtil;
import com.xingyu.config.BaseResponse;
import com.xingyu.model.UserAccount;
import com.xingyu.service.UserAccountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Authenticator", protocols = "http")
@RestController
public class AuthenticationController {

	private UserAccountService userAccountService;

	@Autowired
	public void setUserAccountService(UserAccountService userAccountService) {
		this.userAccountService = userAccountService;
	}

	@ApiOperation(value = "Authentication", notes = "get JWT token from authentication service")
	@PostMapping("/authentications")
	public BaseResponse<String> login(@RequestParam("username") String username,
			@RequestParam("password") String password) {
		UserAccount userInfo = userAccountService.findByUsername(username);
		if (userInfo.getPassword().equals(password)) {
			return BaseResponse.successWithData(JWTUtil.sign(username, password));
		} else {
			throw new UnauthorizedException();
		}
	}
	
}