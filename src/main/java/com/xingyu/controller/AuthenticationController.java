package com.xingyu.controller;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.xingyu.auth.JWTUtil;
import com.xingyu.config.BaseError;
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
		if (userInfo != null && userInfo.getPassword().equals(password)) {
			return BaseResponse.successWithData(JWTUtil.sign(username, password));
		} else {
			throw new AuthenticationException();
		}
	}

	@ExceptionHandler(RuntimeException.class)
	public BaseResponse<BaseError> handle(RuntimeException ex) {
		System.out.println("controller local exception handling @ExceptionHandler");
		BaseError error = new BaseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
		return new BaseResponse<BaseError>(500, "Internal Server Error", error);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public BaseResponse<BaseError> handle(UnauthorizedException ex) {
		System.out.println("controller local exception handling @ExceptionHandler");
		BaseError error = new BaseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
		return new BaseResponse<BaseError>(402, "Unauthorised! You dont have permission", error);
	}

	@ExceptionHandler(AuthenticationException.class)
	public BaseResponse<BaseError> handle(AuthenticationException ex) {
		System.out.println("controller local exception handling @ExceptionHandler");
		BaseError error = new BaseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
		return new BaseResponse<BaseError>(401, "Authentication failed!", error);
	}

	@ExceptionHandler(UnsupportedTokenException.class)
	public BaseResponse<BaseError> handle(UnsupportedTokenException ex) {
		System.out.println("controller local exception handling @ExceptionHandler");
		BaseError error = new BaseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
		return new BaseResponse<BaseError>(401, "Authentication failed!", error);
	}

	@GetMapping("/invalid_token")
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public BaseResponse<String> unauthorized() {
		return new BaseResponse<String>(401, "Unauthorized! You may have an invalid or expired token", null);
	}

}