package simple_banking_app.simple_banking.security;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String accessToken = null;
    String username = null;

    String path = request.getRequestURI();
    if (path.equals("/api/login")
        || path.equals("/api/signup")
        || path.equals("/api/refresh-token")) {
      filterChain.doFilter(request, response);
      return;
    }

    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals("web-token")) {
          accessToken = cookie.getValue();
          break;
        }
      }
    }

    if (accessToken != null) {
      username = jwtUtil.extractUsername(accessToken);
    }

    // Set authentication information in the security context.
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      if (jwtUtil.validateToken(accessToken, username)) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
            null, new ArrayList<>());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }
    }

    filterChain.doFilter(request, response);
  }

}
