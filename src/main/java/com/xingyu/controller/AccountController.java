package com.xingyu.controller;

import java.util.ArrayList;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

	/**
	 * list all users' account
	 * 
	 * @return
	 */
	@GetMapping("")
	@RequiresPermissions("userAccount:list")
	public BaseResponse<String> listAccounts() {
		JsonElement element = null;
		try {
			element = gson.toJsonTree(this.userAccountService.findAll(), new TypeToken<ArrayList<UserAccount>>() {
			}.getType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BaseResponse.successWithData(element.getAsJsonArray().toString());
	}

	@PostMapping("")
	@RequiresPermissions("userAccount:create")
	public BaseResponse<String> createAccount(UserAccount account) {
		userAccountService.saveAccount(account);
		return new BaseResponse<String>(201, "Create Success",
				"resource id: " + userAccountService.findByUsername(account.getUsername()).getId());
	}

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

	@PatchMapping("/{id}")
	@RequiresPermissions("userAccount:update")
	public BaseResponse<String> patAccount(@PathVariable Long id, @ApiIgnore UserAccount account) {
		UserAccount savedAccount = userAccountService.findById(id);
		savedAccount.setPassword(account.getPassword());
		savedAccount.setUsername(account.getUsername());
		savedAccount.setSalt(account.getSalt());
		userAccountService.saveAccount(account);
		return new BaseResponse<String>(202, "Overwrite Success", "resource id: " + id);
	}

	@PostMapping("/{id}/roles")
	@RequiresPermissions("userAccount:update")
	public BaseResponse<String> assignAccountRole(@PathVariable long id, @ApiIgnore SysRole role) {
		UserAccount savedAccount = userAccountService.findById(id);
		savedAccount.getRoleList().add(role);
		userAccountService.saveAccount(savedAccount);
		return BaseResponse.successWithData("Assign Role Success");
	}

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

	@DeleteMapping("/{id}")
	@RequiresPermissions("userAccount:delete")
	public BaseResponse<String> deleteAccount(@PathVariable long id) {
		userAccountService.deleteAccount(id);
		return BaseResponse.successWithData("delete success");
	}

	@GetMapping("/{id}")
	@RequiresPermissions("userAccount:read")
	public BaseResponse<String> readAccount(@PathVariable long id) {
		System.out.println(gson.toJson(userAccountService.findById(id).toString()));
		return BaseResponse.successWithData(gson.toJson(userAccountService.findById(id)));
	}

}