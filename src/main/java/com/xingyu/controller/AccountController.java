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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.xingyu.auth.JWTUtil;
import com.xingyu.config.BaseResponse;
import com.xingyu.model.Address;
import com.xingyu.model.SysRole;
import com.xingyu.model.UserAccount;
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

	@ApiOperation(value = "list all users", notes = "list all users", produces = "application/json", consumes = "application/json")
	@ApiResponses({ @ApiResponse(code = 100, message = "Data Exception") })
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

	@ApiOperation(value = "Create Account", notes = "Create Account")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "username", value = "username", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "password", value = "password", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "uid", value = "user account id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "dob", value = "date of birth", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "email", value = "email", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "firstname", value = "first name", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "lastname", value = "last name", required = false, dataType = "String", paramType = "query"), })
	@PostMapping("")
	@RequiresPermissions("userAccount:create")
	public BaseResponse<String> createAccount(UserAccount account) {
		userAccountService.saveAccount(account);
		return new BaseResponse<String>(201, "Create Success",
				"resource id: " + userAccountService.findByUsername(account.getUsername()).getId());
	}

	@ApiOperation(value = "Put update account", notes = "Put update account, could replace the entity")
	@ApiResponses({ @ApiResponse(code = 201, message = "Create Success"),
			@ApiResponse(code = 202, message = "Overwrite Success"), @ApiResponse(code = 401, message = "Unauthorised"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
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

	@ApiOperation(value = "Patch update Account", notes = "Patch update Account")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "username", value = "username", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "password", value = "password", required = true, dataType = "String", paramType = "query"), })
	@PatchMapping("/{id}")
	@RequiresPermissions("userAccount:update")
	public BaseResponse<String> patchAccount(@PathVariable Long id, @ApiIgnore UserAccount account) {
		UserAccount savedAccount = userAccountService.findById(id);
		savedAccount.setPassword(account.getPassword());
		savedAccount.setUsername(account.getUsername());
		savedAccount.setSalt(account.getSalt());
		savedAccount.setDob(account.getDob());
		savedAccount.setEmail(account.getEmail());
		savedAccount.setFirstname(account.getFirstname());
		savedAccount.setLastname(account.getLastname());
		userAccountService.saveAccount(account);
		return new BaseResponse<String>(202, "Overwrite Success", "resource id: " + id);
	}

	@ApiOperation(value = "delete a user", notes = "delete a user")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String", paramType = "query"), })
	@DeleteMapping("/{id}")
	@RequiresPermissions("userAccount:delete")
	public BaseResponse<String> deleteAccount(@PathVariable long id,
			@RequestHeader(value = "Authorization") String authorizationHeader) {
		UserAccount account = userAccountService.findById(id);
		String currentUsername = JWTUtil.getUsername(authorizationHeader);
		boolean isAdmin = false;
		for (SysRole role : userAccountService.findByUsername(currentUsername).getRoleList()) {
			if ("admin".equalsIgnoreCase(role.getRole())) {
				isAdmin = true;
			}
		}
		if (isAdmin || account.getUsername().equalsIgnoreCase(currentUsername)) {
			userAccountService.deleteAccount(id);
			return BaseResponse.successWithData("Deleted");
		} else {
			return BaseResponse.failWithCodeAndMsg(401,
					"Unauthorised: You dont have permission to delete other people's profile");
		}
	}

	@ApiOperation(value = "get a user account info", notes = "get a user account info")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String", paramType = "query"), })
	@GetMapping("/{id}")
	@RequiresPermissions("userAccount:read")
	public BaseResponse<String> readAccount(@PathVariable long id,
			@RequestHeader(value = "Authorization") String authorizationHeader) {

		UserAccount account = userAccountService.findById(id);
		String currentUsername = JWTUtil.getUsername(authorizationHeader);
		boolean isAdmin = false;
		for (SysRole role : userAccountService.findByUsername(currentUsername).getRoleList()) {
			if ("admin".equalsIgnoreCase(role.getRole())) {
				isAdmin = true;
			}
		}
		if (isAdmin || account.getUsername().equalsIgnoreCase(currentUsername)) {
			return BaseResponse.successWithData(gson.toJson(userAccountService.findById(id)));
		} else {
			return BaseResponse.failWithCodeAndMsg(401,
					"Unauthorised: You dont have permission to read other people's profile");
		}
	}

	/**
	 * role processes
	 */

	@ApiOperation(value = "Assigne role to user", notes = "Assigne role to user")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "role", value = "role name", required = false, dataType = "String", paramType = "query"), })
	@PostMapping("/{id}/roles")
	@RequiresPermissions("userAccount:update")
	public BaseResponse<String> assignAccountRole(@PathVariable long id, @ApiIgnore SysRole role) {
		UserAccount savedAccount = userAccountService.findById(id);
		savedAccount.getRoleList().add(role);
		userAccountService.saveAccount(savedAccount);
		return BaseResponse.successWithData("Assign Role Success");
	}

	@ApiOperation(value = "remove a role from user", notes = "remove a role from user")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "role", value = "role name", required = false, dataType = "String", paramType = "query"), })
	@DeleteMapping("/{id}/roles")
	@RequiresPermissions("userAccount:update")
	public BaseResponse<String> removeAccountRole(@PathVariable long id, @ApiIgnore SysRole role) {
		UserAccount savedAccount = userAccountService.findById(id);
		SysRole roleToBeRemoved = null;
		for (SysRole sysRole : savedAccount.getRoleList()) {
			if (sysRole.getId() == role.getId() || sysRole.getRole().equalsIgnoreCase(role.getRole())) {
				roleToBeRemoved = sysRole;
			}
		}
		savedAccount.getRoleList().remove(roleToBeRemoved);
		userAccountService.saveAccount(savedAccount);
		return BaseResponse.successWithData("Remove Role Success");
	}

	/**
	 * Address processes
	 */

	@ApiOperation(value = "Create Address", notes = "Create Address")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "type", value = "type", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "city", value = "city", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "postcode", value = "postcode", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "state", value = "state", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "street", value = "street", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "type", value = "type", required = false, dataType = "String", paramType = "query"), })
	@PostMapping("/{id}/addresses")
	@RequiresPermissions("userAccount:update")
	public BaseResponse<String> createProfileAddress(@PathVariable Long id, @ApiIgnore Address address) {
		UserAccount account = userAccountService.findById(id);
		account.getAddresses().add(address);
		userAccountService.saveAccount(account);
		return new BaseResponse<String>(201, "Create Success", null);
	}

	@ApiOperation(value = "Update Address", notes = "Update Address")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "type", value = "type", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "city", value = "city", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "postcode", value = "postcode", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "state", value = "state", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "street", value = "street", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "type", value = "type", required = false, dataType = "String", paramType = "query"), })
	@PatchMapping("/{id}/addresses")
	@RequiresPermissions("userAccount:update")
	public BaseResponse<String> updateProfileAddress(@PathVariable Long id, @ApiIgnore Address address) {
		UserAccount account = userAccountService.findById(id);
		for (Address savedAddress : account.getAddresses()) {
			if (savedAddress.getType().equalsIgnoreCase(address.getType())) {
				savedAddress.setCity(address.getCity());
				savedAddress.setPostcode(address.getPostcode());
				savedAddress.setState(address.getState());
				savedAddress.setStreet(address.getStreet());
				savedAddress.setType(address.getType());
			}
		}
		userAccountService.saveAccount(account);
		return new BaseResponse<String>(201, "Address Update Success", null);
	}

	@ApiOperation(value = "Delete Address", notes = "Delete Address")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "type", value = "address type", required = true, dataType = "String", paramType = "query"), })
	@DeleteMapping("/{id}/addresses")
	@RequiresPermissions("userAccount:update")
	public BaseResponse<String> removeProfileAddress(@PathVariable Long id, @ApiIgnore Address address) {
		UserAccount account = userAccountService.findById(id);
		Address addressToBeRemoved = null;
		for (Address savedAddress : account.getAddresses()) {
			if (savedAddress.getType().equalsIgnoreCase(address.getType())) {
				addressToBeRemoved = savedAddress;
			}
		}
		account.getAddresses().remove(addressToBeRemoved);
		userAccountService.saveAccount(account);
		return new BaseResponse<String>(201, "Address Update Success", null);
	}

}