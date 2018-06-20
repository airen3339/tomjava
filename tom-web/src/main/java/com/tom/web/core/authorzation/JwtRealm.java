package com.tom.web.core.authorzation;

import com.tom.core.utils.JWTUtil;
import com.tom.model.Users;
import com.tom.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

public class JwtRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        String username = JWTUtil.getUsername(principalCollection.toString());
        Users user = userService.getUser(username);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        //simpleAuthorizationInfo.addRole(user.getRole());
        //Set<String> permission = new HashSet<>(Arrays.asList(user.getPermission().split(",")));
        //simpleAuthorizationInfo.addStringPermissions(permission);
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws
            AuthenticationException {
        if (!(authenticationToken instanceof JwtToken)) return null;
        String token = (String) authenticationToken.getCredentials();
        String jwtToken = ((JwtToken) authenticationToken).getJwtToken();
        String userName = JWTUtil.getUsername(token);
        if (userName == null) {
            throw new AuthenticationException("User didn't existed!");
        }
        Users userInfo = userService.getUser(userName);
        if (userInfo == null) {
            throw new AuthenticationException("User didn't existed!");
        }

        if (!JWTUtil.verify(token, userName, userInfo.getPassword())) {
            throw new AuthenticationException("Username or password error");
        }
        return new SimpleAuthenticationInfo("jwt:" + jwtToken, token, getName());
    }
}
