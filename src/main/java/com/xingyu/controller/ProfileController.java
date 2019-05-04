package com.xingyu.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xingyu.config.BaseResponse;
import com.xingyu.service.UserProfileService;

import io.swagger.annotations.Api;

@Api(value = "Profile Management", protocols = "http")
@RestController
public class ProfileController {

    private UserProfileService userProfileService;

    @Autowired
    public void setService(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }
    
    @GetMapping("/401")
    public BaseResponse<String> unauth() {
        return BaseResponse.failWithCodeAndMsg(401, "unauthorized");
    }
    /**
     * list all users' profile
     * @return
     */
    @GetMapping("/profiles")
    @RequiresPermissions("userProfile:list")
    public BaseResponse<String> listProfiles(){
        return BaseResponse.successWithData("aaaaa");
    }
    
    
//    
//    /**
//     * 用户查询.
//     * @return
//     */
////    @RequiresRoles("admin")
////    @RequiresPermissions("userInfo:list")//权限管理;
//    @GetMapping("/profiles")
//    @RequiresPermissions(logical = Logical.AND, value = {"userInfo:list", "userProfile:list"})
//    public BaseResponse<String> getProfile(){
//        return BaseResponse.successWithData("aaaaa");
//    }
//    
//    /**
//     * 用户查询.
//     * @return
//     */
////    @RequiresRoles("admin")
////    @RequiresPermissions("userInfo:list")//权限管理;
//    @GetMapping("/profiles")
//    @RequiresAuthentication
//    public BaseResponse<String> createProfile(){
//        return BaseResponse.successWithData("aaaaa");
//    }
//
//    /**
//     * 用户添加;
//     * @return
//     */
//    @RequestMapping("/profiles")
//    @RequiresPermissions("userInfo:create")//权限管理;
//    public String userInfoAdd(){
//        return "userInfoAdd";
//    }
//
//    /**
//     * 用户删除;
//     * @return
//     */
//    @RequestMapping("/profiles")
//    @RequiresPermissions("userInfo:del")//权限管理;
//    public String userDel(){
//        return "userInfoDel";
//    }

}
