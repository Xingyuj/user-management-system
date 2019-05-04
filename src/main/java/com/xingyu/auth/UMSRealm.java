package com.xingyu.auth;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xingyu.model.SysPermission;
import com.xingyu.model.SysRole;
import com.xingyu.model.UserAccount;
import com.xingyu.service.UserAccountService;

@Service
public class UMSRealm extends AuthorizingRealm {
	
    @Autowired
    public void setUserService(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    private UserAccountService userAccountService;
    
    /**
     * 大坑！，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }
    
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    	String username = JWTUtil.getUsername(principals.toString());
        UserAccount userAccount = userAccountService.findByUsername(username);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        for(SysRole role:userAccount.getRoleList()){
        	simpleAuthorizationInfo.addRole(role.getRole());
            for(SysPermission p:role.getPermissions()){
            	simpleAuthorizationInfo.addStringPermission(p.getPermission());
            }
        }
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken)
            throws AuthenticationException {
        String credential = (String) authToken.getCredentials();
        String username = JWTUtil.getUsername(credential);
        if (username == null) {
            throw new AuthenticationException("token invalid");
        }

        UserAccount userAccount = userAccountService.findByUsername(username);
        if (userAccount == null) {
            throw new AuthenticationException("User didn't existed!");
        }

        if (! JWTUtil.verify(credential, username, userAccount.getPassword())) {
            throw new AuthenticationException("Username or password error");
        }

        return new SimpleAuthenticationInfo(credential, credential, this.getName());
    }
}