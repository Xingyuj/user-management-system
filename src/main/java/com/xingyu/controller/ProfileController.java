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
import com.xingyu.model.UserProfile;
import com.xingyu.model.UserProfile;
import com.xingyu.service.UserProfileService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@Api(value = "Profile Management", protocols = "http")
@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private UserProfileService userProfileService;
	private Gson gson;

    @Autowired
    public void setService(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }
    
    @GetMapping("/401")
    public BaseResponse<String> unauth() {
        return BaseResponse.failWithCodeAndMsg(401, "unauthorized");
    }

	@Autowired
	public void setGson(Gson gson) {
		GsonBuilder builder = new GsonBuilder().disableHtmlEscaping();
		builder.excludeFieldsWithoutExposeAnnotation();
		this.gson = builder.create();
	}

	@ApiOperation(
			value = "list all users' profile",
			notes = "list all users' profile",
			produces="application/json",
			consumes="application/json")
    @ApiResponses({
            @ApiResponse(code = 100, message = "Data Exception")
    })
	@GetMapping("")
	@RequiresPermissions("userProfile:list")
	public BaseResponse<String> listProfiles() {
		JsonElement element = null;
		try {
			element = gson.toJsonTree(this.userProfileService.findAll(), new TypeToken<ArrayList<UserProfile>>() {
			}.getType());
		} catch (Exception e) {
			e.printStackTrace();
			return new BaseResponse<String>(100, "Data Exception", null);
		}
		return BaseResponse.successWithData(element.getAsJsonArray().toString());
	}

	@ApiOperation(
			value = "Create Profile",
			notes = "Create Profile"
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "uid", value = "user account id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "dob", value = "date of birth", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "email", value = "email", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "firstname", value = "first name", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "lastname", value = "last name", required = false, dataType = "String", paramType = "query"),
	})
	@PostMapping("")
	@RequiresPermissions("userProfile:create")
	public BaseResponse<String> createProfile(UserProfile profile) {
		userProfileService.saveProfile(profile);
		return new BaseResponse<String>(201, "Create Success",null);
	}
	
	@ApiOperation(
			value = "Create Address",
			notes = "Create Address"
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "type", value = "type", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "city", value = "city", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "postcode", value = "postcode", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "state", value = "state", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "street", value = "street", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "type", value = "type", required = false, dataType = "String", paramType = "query"),
	})
	@PostMapping("/{id}/addresses")
	@RequiresPermissions("userProfile:create")
	public BaseResponse<String> createProfileAddress(@PathVariable Long id, @ApiIgnore Address address) {
		UserProfile profile = userProfileService.findById(id);
		profile.getAddresses().add(address);
		userProfileService.saveProfile(profile);
		return new BaseResponse<String>(201, "Create Success",null);
	}
	
	@ApiOperation(
			value = "Update Address",
			notes = "Update Address"
			)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "type", value = "type", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "city", value = "city", required = false, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "postcode", value = "postcode", required = false, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "state", value = "state", required = false, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "street", value = "street", required = false, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "type", value = "type", required = false, dataType = "String", paramType = "query"),
	})
	@PatchMapping("/{id}/addresses")
	@RequiresPermissions("userProfile:create")
	public BaseResponse<String> updateProfileAddress(@PathVariable Long id, @ApiIgnore Address address) {
		UserProfile profile = userProfileService.findById(id);
		for (Address savedAddress : profile.getAddresses()) {
			if(savedAddress.getType().equalsIgnoreCase(address.getType())) {
				savedAddress.setCity(address.getCity());
				savedAddress.setPostcode(address.getPostcode());
				savedAddress.setState(address.getState());
				savedAddress.setStreet(address.getStreet());
				savedAddress.setType(address.getType());
			}
		}
		userProfileService.saveProfile(profile);
		return new BaseResponse<String>(201, "Address Update Success",null);
	}
	
	@ApiOperation(
			value = "Delete Address",
			notes = "Delete Address"
			)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "type", value = "address type", required = true, dataType = "String", paramType = "query"),
	})
	@DeleteMapping("/{id}/addresses")
	@RequiresPermissions("userProfile:create")
	public BaseResponse<String> removeProfileAddress(@PathVariable Long id, @ApiIgnore Address address) {
		UserProfile profile = userProfileService.findById(id);
		Address addressToBeRemoved = null;
		for (Address savedAddress : profile.getAddresses()) {
			if(savedAddress.getType().equalsIgnoreCase(address.getType())) {
				addressToBeRemoved = savedAddress;
			}
		}
		profile.getAddresses().remove(addressToBeRemoved);
		userProfileService.saveProfile(profile);
		return new BaseResponse<String>(201, "Address Update Success",null);
	}

	@ApiOperation(
			value = "Put update profile",
			notes = "Put update profile, could replace the entity"
	)
	@ApiResponses({
			@ApiResponse(code = 201, message = "Create Success"),
			@ApiResponse(code = 202, message = "Overwrite Success"),
			@ApiResponse(code = 401, message = "Unauthorised"),
			@ApiResponse(code = 100, message = "Unable to save")
	})
	@PutMapping("/{id}")
	@RequiresPermissions("userProfile:update")
	public BaseResponse<String> putProfile(@PathVariable Long id, @ApiIgnore UserProfile profile) {
		UserProfile savedProfile = userProfileService.findById(id);
		try {
			if (savedProfile == null) {
				userProfileService.saveProfile(profile);
				return new BaseResponse<String>(201, "Create Success", "Cool!");
			} else {
				userProfileService.deleteProfile(id);
				userProfileService.saveProfile(profile);
				return new BaseResponse<String>(202, "Overwrite Success", "resource id: " + id);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new BaseResponse<String>(100, "Unable to save the profile", null);
		}
	}

	@ApiOperation(
			value = "Patch update Profile",
			notes = "Patch update Profile"
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "username", value = "username", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "password", value = "password", required = true, dataType = "String", paramType = "query"),
	})
	@PatchMapping("/{id}")
	@RequiresPermissions("userProfile:update")
	public BaseResponse<String> patchProfile(@PathVariable Long id, @ApiIgnore UserProfile profile) {
		UserProfile savedProfile = userProfileService.findById(id);
		savedProfile.setDob(profile.getDob());
		savedProfile.setEmail(profile.getEmail());
		savedProfile.setFirstname(profile.getFirstname());
		savedProfile.setLastname(profile.getLastname());
		userProfileService.saveProfile(profile);
		return new BaseResponse<String>(202, "Overwrite Success", "resource id: " + id);
	}

	@ApiOperation(
			value = "delete a profile",
			notes = "delete a profile"
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String", paramType = "query"),
	})
	@DeleteMapping("/{id}")
	@RequiresPermissions("userProfile:delete")
	public BaseResponse<String> deleteProfile(@PathVariable long id) {
		userProfileService.deleteProfile(id);
		return BaseResponse.successWithData("delete success");
	}

	@ApiOperation(
			value = "get a user profile info",
			notes = "get a user profile info"
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String", paramType = "query"),
	})
	@GetMapping("/{id}")
	@RequiresPermissions("userProfile:read")
	public BaseResponse<String> readProfile(@PathVariable long id) {
		System.out.println(gson.toJson(userProfileService.findById(id).toString()));
		return BaseResponse.successWithData(gson.toJson(userProfileService.findById(id)));
	}

}
