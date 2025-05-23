package edu.byteme.security;

import java.io.IOException;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom authentication success handler that redirects to different URLs
 * based on user roles
 */
public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthenticationSuccessHandler defaultHandler = new SavedRequestAwareAuthenticationSuccessHandler();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        System.out.println("DEBUG: RoleBasedAuthenticationSuccessHandler.onAuthenticationSuccess called");
        
        // Get user authorities/roles
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        System.out.println("DEBUG: User roles: " + roles);
        
        // Redirect based on role
        if (roles.contains("ROLE_ADMIN")) {
            System.out.println("DEBUG: Redirecting ADMIN user to admin dashboard");
            response.sendRedirect("/admin");
        } else {
            System.out.println("DEBUG: Redirecting regular user to menu page");
            response.sendRedirect("/");
        }
    }
}