package com.xingyu.controller;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xingyu.auth.JWTUtil;
import com.xingyu.config.BaseResponse;
import com.xingyu.model.UserAccount;
import com.xingyu.service.UserAccountService;

import io.swagger.annotations.Api;

@Api(value = "Authenticator", protocols = "http")
@RestController
public class AccountController {

    private UserAccountService userAccountService;

    @Autowired
    public void setService(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
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
     * @return
     */
    @GetMapping("/accounts")
//    @RequiresPermissions("userAccount:list")
    public BaseResponse<String> listAccounts(){
        return BaseResponse.successWithData("aaaaacount");
    }
    
    /**
     * list all users' account
     * @return
     */
    @PostMapping("/accounts")
//    @RequiresPermissions("userAccount:create")
    public BaseResponse<String> createAccount(){
        return BaseResponse.successWithData("aaaaacount");
    }
    
    
}