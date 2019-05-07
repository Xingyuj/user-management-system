package com.xingyu.controller;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.MediaType;

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

import com.xingyu.auth.JWTUtil;
import com.xingyu.config.BaseResponse;
import com.xingyu.model.Address;
import com.xingyu.model.SysRole;
import com.xingyu.model.UserAccount;
import com.xingyu.service.UserAccountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ExampleProperty;
import springfox.documentation.annotations.ApiIgnore;

@Api(value = "Authenticator", protocols = "http")
@RestController
@RequestMapping("/accounts")
public class AccountController {

	private UserAccountService userAccountService;

	@Autowired
	public void setUserAccountService(UserAccountService userAccountService) {
		this.userAccountService = userAccountService;
	}

	@ApiOperation(value = "list all users", notes = "list all users")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "platform", value = "platform", required = false, dataType = "String", paramType = "query"), })
	@ApiResponses({ @ApiResponse(code = 100, message = "Data Exception") })
	@GetMapping("")
	@RequiresPermissions("userAccount:list")
	public BaseResponse<List<UserAccount>> listAccounts(@RequestParam(required = false) String platform) {
		try {
			List<UserAccount> accounts = userAccountService.findAll();
			if ("mobile".equalsIgnoreCase(platform)) {
				for (UserAccount userAccount : accounts) {
					userAccount.getAddresses().clear();
					userAccount.getRoleList().clear();
				}
			}
			return BaseResponse.successWithData(accounts);
		} catch (Exception e) {
			return BaseResponse.failWithCodeAndMsg(401, e.getMessage());
		}

	}

	@ApiOperation(value = "get a user account info", notes = "get a user account info")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "platform", value = "platform", required = false, dataType = "String", paramType = "query"), })
	@GetMapping("/{id}")
	@RequiresPermissions("userAccount:read")
	public BaseResponse<UserAccount> readAccount(@PathVariable long id, @RequestParam(required = false) String platform,
			@RequestHeader(value = "Authorization") String authorizationHeader) {
		try {
			UserAccount account = userAccountService.findById(id);
			String username = JWTUtil.getUsername(authorizationHeader);
			if (account == null) {
				return BaseResponse.failWithCodeAndMsg(100, "Unable to find account, check your ID input.");
			}
			if (account.getRoleList().stream().filter(it -> it.getRole().equals("admin")).findFirst().isPresent()
					|| account.getUsername().equalsIgnoreCase(username)) {
				if ("mobile".equalsIgnoreCase(platform)) {
					account.getAddresses().clear();
					account.getRoleList().clear();
				}
				return BaseResponse.successWithData(account);
			} else {
				return BaseResponse.failWithCodeAndMsg(401,
						"Unauthorised: You dont have permission to read other people's profile");
			}
		} catch (Exception e) {
			return BaseResponse.failWithCodeAndMsg(401, e.getMessage());
		}

	}

	@ApiOperation(value = "Create Account", notes = "Create Account")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "body", value = "create new user json body", required = true, dataType = "string", paramType = "body", examples = @io.swagger.annotations.Example(value = {
					@ExampleProperty(mediaType = "application/json", value = "{username:'username', password:'password', dob:'dob', email:'email',firstname:'firstname',lastname:'lastname'}") })) })
	@PostMapping("")
	@RequiresPermissions("userAccount:create")
	public BaseResponse<UserAccount> createAccount(UserAccount account) {
		try {
			userAccountService.saveAccount(account);
		} catch (Exception e) {
			e.printStackTrace();
			return BaseResponse.failWithCodeAndMsg(100, e.getMessage());
		}
		return new BaseResponse<UserAccount>(201, "Create Success", account);
	}

	@ApiOperation(value = "Put update account", notes = "Put update account, could replace the entity")
	@ApiResponses({ @ApiResponse(code = 201, message = "Create Success"),
			@ApiResponse(code = 202, message = "Overwrite Success"), @ApiResponse(code = 401, message = "Unauthorised"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "body", value = "update user json body", required = true, dataType = "string", paramType = "body", examples = @io.swagger.annotations.Example(value = {
					@ExampleProperty(mediaType = MediaType.APPLICATION_FORM_URLENCODED, value = "{username:'username', password:'password', dob:'dob', email:'email',firstname:'firstname',lastname:'lastname'}") })) })
	@PutMapping("/{id}")
	@RequiresPermissions("userAccount:update")
	public BaseResponse<UserAccount> putAccount(@PathVariable Long id, @ApiIgnore UserAccount account) {
		UserAccount savedAccount = userAccountService.findById(id);
		try {
			if (savedAccount == null) {
				account.setId(id);
				userAccountService.saveAccount(account);
				return new BaseResponse<UserAccount>(201, "Create Success", account);
			} else {
				userAccountService.saveAccount(account);
				return new BaseResponse<UserAccount>(202, "Overwrite Success", account);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return BaseResponse.failWithCodeAndMsg(100, e.getMessage());
		}
	}

	@ApiOperation(value = "Patch update Account", notes = "Patch update Account")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "body", value = "update user json body", required = true, dataType = "string", paramType = "body", examples = @io.swagger.annotations.Example(value = {
					@ExampleProperty(mediaType = MediaType.APPLICATION_FORM_URLENCODED, value = "{username:'username', password:'password', dob:'dob', email:'email',firstname:'firstname',lastname:'lastname'}") })) })
	@PatchMapping("/{id}")
	@RequiresPermissions("userAccount:update")
	public BaseResponse<UserAccount> patchAccount(@PathVariable Long id, @ApiIgnore UserAccount account) {
		try {
			UserAccount savedAccount = userAccountService.findById(id);
			Optional.ofNullable(account.getPassword()).ifPresent(savedAccount::setPassword);
			Optional.ofNullable(account.getUsername()).ifPresent(savedAccount::setUsername);
			Optional.ofNullable(account.getSalt()).ifPresent(savedAccount::setSalt);
			Optional.ofNullable(account.getDob()).ifPresent(savedAccount::setDob);
			Optional.ofNullable(account.getEmail()).ifPresent(savedAccount::setEmail);
			Optional.ofNullable(account.getFirstname()).ifPresent(savedAccount::setFirstname);
			Optional.ofNullable(account.getLastname()).ifPresent(savedAccount::setLastname);
			userAccountService.saveAccount(account);
			return new BaseResponse<UserAccount>(202, "Overwrite Success", account);
		} catch (Exception e) {
			return BaseResponse.failWithCodeAndMsg(100, e.getMessage());
		}
	}

	@ApiOperation(value = "delete a user", notes = "delete a user")
	@DeleteMapping("/{id}")
	@RequiresPermissions("userAccount:delete")
	public BaseResponse<String> deleteAccount(@PathVariable long id,
			@RequestHeader(value = "Authorization") String authorizationHeader) {
		try {
			UserAccount account = userAccountService.findById(id);
			String username = JWTUtil.getUsername(authorizationHeader);
			if (account == null) {
				return BaseResponse.failWithCodeAndMsg(100, "No such an account can be deleted, Check your ID input.");
			}
			if (userAccountService.findByUsername(username).getRoleList().stream()
					.filter(it -> it.getRole().equals("admin")).findFirst().isPresent()
					|| account.getUsername().equalsIgnoreCase(username)) {
				userAccountService.deleteAccount(id);
				return BaseResponse.successWithData("resource:" + id + " deleted");
			} else {
				return BaseResponse.failWithCodeAndMsg(401,
						"Unauthorised: You dont have permission to delete other people's profile");
			}
		} catch (Exception e) {
			return BaseResponse.failWithCodeAndMsg(100, e.getMessage());
		}
	}

	/**
	 * role processes
	 */
	@ApiOperation(value = "Assigne role to user", notes = "Assigne role to user")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "body", value = "update json body", required = true, dataType = "string", paramType = "body", examples = @io.swagger.annotations.Example(value = {
					@ExampleProperty(mediaType = MediaType.APPLICATION_FORM_URLENCODED, value = "{id:'id', role:'role'") })) })
	@PostMapping("/{id}/roles")
	@RequiresPermissions("userAccount:update")
	public BaseResponse<UserAccount> assignAccountRole(@PathVariable long id, @ApiIgnore SysRole role) {
		try {
			UserAccount savedAccount = userAccountService.findById(id);
			savedAccount.getRoleList().add(role);
			userAccountService.saveAccount(savedAccount);
			return BaseResponse.successWithData(savedAccount);
		} catch (Exception e) {
			return BaseResponse.failWithCodeAndMsg(100, e.getMessage());
		}
	}

	@ApiOperation(value = "remove a role from user", notes = "remove a role from user")
	@DeleteMapping("/{id}/roles")
	@RequiresPermissions("userAccount:update")
	public BaseResponse<UserAccount> removeAccountRole(@PathVariable long id, @ApiIgnore SysRole role) {
		try {
			UserAccount savedAccount = userAccountService.findById(id);
			savedAccount.getRoleList()
					.removeIf(it -> it.getRole().equalsIgnoreCase(role.getRole()) || it.getId() == role.getId());
			userAccountService.saveAccount(savedAccount);
			return BaseResponse.successWithData(savedAccount);
		} catch (Exception e) {
			return BaseResponse.failWithCodeAndMsg(100, e.getMessage());
		}
	}

	/**
	 * Address processes
	 */
	@ApiOperation(value = "Create Address", notes = "Create Address")
	@ApiImplicitParam(name = "body", value = "update json body", required = true, dataType = "string", paramType = "body", examples = @io.swagger.annotations.Example(value = {
			@ExampleProperty(mediaType = MediaType.APPLICATION_FORM_URLENCODED, value = "{type:'type', city:'city', postcode:'postcode', state:'state', street:'street'") }))
	@PostMapping("/{id}/addresses")
	@RequiresPermissions("userAccount:update")
	public BaseResponse<UserAccount> createProfileAddress(@PathVariable Long id, @ApiIgnore Address address) {
		try {
			UserAccount account = userAccountService.findById(id);
			account.getAddresses().add(address);
			userAccountService.saveAccount(account);
			return new BaseResponse<UserAccount>(201, "Create Success", account);
		} catch (Exception e) {
			return BaseResponse.failWithCodeAndMsg(100, e.getMessage());
		}
	}

	@ApiOperation(value = "Update Address", notes = "Update Address")
	@ApiImplicitParam(name = "body", value = "update json body", required = true, dataType = "string", paramType = "body", examples = @io.swagger.annotations.Example(value = {
			@ExampleProperty(mediaType = MediaType.APPLICATION_FORM_URLENCODED, value = "{type:'type', city:'city', postcode:'postcode', state:'state', street:'street'") }))
	@PatchMapping("/{id}/addresses")
	@RequiresPermissions("userAccount:update")
	public BaseResponse<UserAccount> updateProfileAddress(@PathVariable Long id, @ApiIgnore Address address) {
		try {
			UserAccount account = userAccountService.findById(id);
			for (Address savedAddress : account.getAddresses()) {
				if(savedAddress.getId().equals(address.getId())) {
					Optional.ofNullable(address.getCity()).ifPresent(savedAddress::setCity);
					Optional.ofNullable(address.getPostcode()).ifPresent(savedAddress::setPostcode);
					Optional.ofNullable(address.getState()).ifPresent(savedAddress::setState);
					Optional.ofNullable(address.getStreet()).ifPresent(savedAddress::setStreet);
					Optional.ofNullable(address.getType()).ifPresent(savedAddress::setType);
				}
			}
			userAccountService.saveAccount(account);
			return new BaseResponse<UserAccount>(202, "Overwrite Success", account);
		} catch (Exception e) {
			return BaseResponse.failWithCodeAndMsg(100, e.getMessage());
		}
	}

	@ApiOperation(value = "Delete Address", notes = "Delete Address")
	@DeleteMapping("/{id}/addresses")
	@RequiresPermissions("userAccount:update")
	public BaseResponse<UserAccount> removeProfileAddress(@PathVariable Long id, @ApiIgnore Address address) {
		try {
			UserAccount account = userAccountService.findById(id);
			if(account == null) {
				return BaseResponse.failWithCodeAndMsg(100, "No such an address to be deleted");
			}
			account.getAddresses().removeIf(it -> it.getId().equals(address.getId()));
			userAccountService.saveAccount(account);
			return new BaseResponse<UserAccount>(203, "Remove Success", account);
		} catch (Exception e) {
			return BaseResponse.failWithCodeAndMsg(100, e.getMessage());
		}
	}

}