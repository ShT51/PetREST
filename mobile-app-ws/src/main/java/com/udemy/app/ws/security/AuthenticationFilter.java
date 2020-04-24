package com.udemy.app.ws.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udemy.app.ws.SpringApplicationContext;
import com.udemy.app.ws.service.UserService;
import com.udemy.app.ws.service.impl.UserServiceImpl;
import com.udemy.app.ws.shared.dto.UserDto;
import com.udemy.app.ws.ui.model.request.UserLoginRequestModel;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * This method will be trigger when server receive a request to authenticate user.
     * The JSON payload from request body will be used to create the {@link UserLoginRequestModel}.
     * Then {@link AuthenticationManager} authenticate user and find him in DB
     * by email using {@method loadUserByUsername} from {@link UserServiceImpl}
     *
     * @param req - http request to authenticate user
     * @return Authentication object into {@method (un)successfulAuthentication}
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            UserLoginRequestModel creds = new ObjectMapper()
                    .readValue(req.getInputStream(), UserLoginRequestModel.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>())
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Once authentication was successful this method will be trigger by Spring
     * This method returns the Web Token {@literal token}, which will be included to the
     * response header, as a result of successful authentication.
     * It will be used in Authorization flow.
     * Every time when application needs to communicate with protected recourse (URL) it has to
     * include this Web Token to the request header.
     *
     * @param auth - {@method attemptAuthentication} return value.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        String userName = ((UserPrincipals)auth.getPrincipal()).getUsername();
        // Generate Web Token
        String token = Jwts.builder()
                // set the User name
                .setSubject(userName)
                // set the token's expiration time
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                // set the crypt algorithm and secret key
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                // convert the token to a string
                .compact();
        // add header [key: Authorization, value: Web Token]
        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        // We get the UserServiceImpl Bean from our implementation of ApplicationContext (SpringApplicationContext)
        UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl", UserService.class);
        UserDto userDto = userService.getUser(userName);
        // add header [key: UserID, value: publicId]
        res.addHeader("UserID", userDto.getUserId());
    }
}
