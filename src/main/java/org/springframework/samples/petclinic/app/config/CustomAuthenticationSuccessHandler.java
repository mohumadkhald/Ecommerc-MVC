package org.springframework.samples.petclinic.app.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.app.users.Role;
import org.springframework.samples.petclinic.app.users.User;
import org.springframework.samples.petclinic.app.users.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email"); // assuming 'email' is the attribute representing the username/email
        String name = oauth2User.getAttribute("name");
//        String password = oauth2User.getAttribute("password");
        User existingUser = userService.findByEmail(email);
        if (existingUser == null) {
            // create a new user and save it to the database
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setRole(Role.USER);
            // Set a placeholder or generate a password since OAuth2 doesn't provide one
            newUser.setPassword("password"); // Example placeholder password
            userService.save(newUser);
        }

        // Redirect to the default success URL
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}