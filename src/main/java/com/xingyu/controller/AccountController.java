package com.xingyu.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.xingyu.auth.JWTUtil;
import com.xingyu.config.BaseResponse;
import com.xingyu.model.SysRole;
import com.xingyu.model.UserAccount;
import com.xingyu.service.UserAccountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@Api(value = "Authenticator", protocols = "http")
@RestController
@RequestMapping("/accounts")
public class AccountController {

	private UserAccountService userAccountService;
	private Gson gson;

	@Autowired
	public void setUserAccountService(UserAccountService userAccountService) {
		this.userAccountService = userAccountService;
	}

	@Autowired
	public void setGson(Gson gson) {
		GsonBuilder builder = new GsonBuilder().disableHtmlEscaping();
		builder.excludeFieldsWithoutExposeAnnotation();
		this.gson = builder.create();
	}
	
    @ApiOperation(value = "Authentication", notes = "get JWT token from authentication service")
	@PostMapping("/login")
	public BaseResponse<String> login(@RequestParam("username") String username,
			@RequestParam("password") String password) {
		UserAccount userInfo = userAccountService.findByUsername(username);
		if (userInfo.getPassword().equals(password)) {
			return BaseResponse.successWithData(JWTUtil.sign(username, password));
		} else {
			throw new UnauthorizedException();
		}
	}

	@ApiOperation(
			value = "list all users",
			notes = "list all users",
			produces="application/json",
			consumes="application/json")
    @ApiResponses({
            @ApiResponse(code = 100, message = "Data Exception")
    })
	@GetMapping("")
	@RequiresPermissions("userAccount:list")
	public BaseResponse<String> listAccounts() {
		JsonElement element = null;
		try {
			element = gson.toJsonTree(this.userAccountService.findAll(), new TypeToken<ArrayList<UserAccount>>() {
			}.getType());
		} catch (Exception e) {
			e.printStackTrace();
			return new BaseResponse<String>(100, "Data Exception", null);
		}
		return BaseResponse.successWithData(element.getAsJsonArray().toString());
	}

	@ApiOperation(
			value = "Create Account",
			notes = "Create Account"
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "username", value = "username", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "password", value = "password", required = true, dataType = "String", paramType = "query"),
	})
	@PostMapping("")
	@RequiresPermissions("userAccount:create")
	public BaseResponse<String> createAccount(UserAccount account) {
		userAccountService.saveAccount(account);
		return new BaseResponse<String>(201, "Create Success",
				"resource id: " + userAccountService.findByUsername(account.getUsername()).getId());
	}

	@ApiOperation(
			value = "Put update account",
			notes = "Put update account, could replace the entity"
	)
	@ApiResponses({
			@ApiResponse(code = 201, message = "Create Success"),
			@ApiResponse(code = 202, message = "Overwrite Success"),
			@ApiResponse(code = 401, message = "Unauthorised"),
			@ApiResponse(code = 500, message = "Internal Server Error")
	})
	@PutMapping("/{id}")
	@RequiresPermissions("userAccount:update")
	public BaseResponse<String> putAccount(@PathVariable Long id, @ApiIgnore UserAccount account) {
		UserAccount savedAccount = userAccountService.findById(id);
		try {
			if (savedAccount == null) {
				userAccountService.saveAccount(account);
				return new BaseResponse<String>(201, "Create Success",
						"resource id: " + userAccountService.findByUsername(account.getUsername()).getId());
			} else {
				userAccountService.deleteAccount(id);
				userAccountService.saveAccount(account);
				return new BaseResponse<String>(202, "Overwrite Success", "resource id: " + id);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new BaseResponse<String>(-1, "Unable to save the account", null);
		}
	}

	@ApiOperation(
			value = "Patch update Account",
			notes = "Patch update Account"
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "username", value = "username", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "password", value = "password", required = true, dataType = "String", paramType = "query"),
	})
	@PatchMapping("/{id}")
	@RequiresPermissions("userAccount:update")
	public BaseResponse<String> patchAccount(@PathVariable Long id, @ApiIgnore UserAccount account) {
		UserAccount savedAccount = userAccountService.findById(id);
		savedAccount.setPassword(account.getPassword());
		savedAccount.setUsername(account.getUsername());
		savedAccount.setSalt(account.getSalt());
		userAccountService.saveAccount(account);
		return new BaseResponse<String>(202, "Overwrite Success", "resource id: " + id);
	}

	@ApiOperation(
			value = "Assigne role to user",
			notes = "Assigne role to user"
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "role", value = "role name", required = false, dataType = "String", paramType = "query"),
	})
	@PostMapping("/{id}/roles")
	@RequiresPermissions("userAccount:update")
	public BaseResponse<String> assignAccountRole(@PathVariable long id, @ApiIgnore SysRole role) {
		UserAccount savedAccount = userAccountService.findById(id);
		savedAccount.getRoleList().add(role);
		userAccountService.saveAccount(savedAccount);
		return BaseResponse.successWithData("Assign Role Success");
	}

	@ApiOperation(
			value = "remove a role from user",
			notes = "remove a role from user"
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "role", value = "role name", required = false, dataType = "String", paramType = "query"),
	})
	@DeleteMapping("/{id}/roles")
	@RequiresPermissions("userAccount:update")
	public BaseResponse<String> removeAccountRole(@PathVariable long id, @ApiIgnore SysRole role) {
		UserAccount savedAccount = userAccountService.findById(id);
		SysRole roleToBeRemoved = null;
		for (SysRole sysRole : savedAccount.getRoleList()) {
			if (sysRole.getId() == role.getId()
					|| sysRole.getRole().equalsIgnoreCase(role.getRole())) {
				roleToBeRemoved = sysRole;
			}
		}
		savedAccount.getRoleList().remove(roleToBeRemoved);
		userAccountService.saveAccount(savedAccount);
		return BaseResponse.successWithData("Remove Role Success");
	}

	@ApiOperation(
			value = "delete a user",
			notes = "delete a user"
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String", paramType = "query"),
	})
	@DeleteMapping("/{id}")
	@RequiresPermissions("userAccount:delete")
	public BaseResponse<String> deleteAccount(@PathVariable long id) {
		userAccountService.deleteAccount(id);
		return BaseResponse.successWithData("delete success");
	}

	@ApiOperation(
			value = "get a user account info",
			notes = "get a user account info"
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String", paramType = "query"),
	})
	@GetMapping("/{id}")
	@RequiresPermissions("userAccount:read")
	public BaseResponse<String> readAccount(@PathVariable long id) {
		System.out.println(gson.toJson(userAccountService.findById(id).toString()));
		return BaseResponse.successWithData(gson.toJson(userAccountService.findById(id)));
	}

}