package simple_banking_app.simple_banking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import simple_banking_app.simple_banking.dto.requests.LoginRequest;
import simple_banking_app.simple_banking.dto.requests.SignupRequest;
import simple_banking_app.simple_banking.entity.Account;
import simple_banking_app.simple_banking.security.JwtUtil;
import simple_banking_app.simple_banking.service.AccountService;

@RestController
@RequestMapping("/api")
public class AuthController {

  @Autowired
  private AccountService accountService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtUtil jwtUtil;

  /**
   * Log the user in.
   * 
   * @param loginRequest
   * @return
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
    try {
      // Authenticate using AuthenticationManager.
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

      // After successful authentication, retrieve the user's information.
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();

      // Generate access token.
      String token = jwtUtil.generateToken(userDetails.getUsername());

      // Generate refresh token.
      String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

      // Generate cookie for the access token.
      Cookie accessTokenCookie = new Cookie("web-token", token);
      accessTokenCookie.setHttpOnly(true);
      accessTokenCookie.setSecure(false);
      accessTokenCookie.setPath("/");
      accessTokenCookie.setMaxAge(1 * 60);

      // Generate cookie for the refresh token.
      Cookie refreshTokenCookie = new Cookie("refresh-token", refreshToken);
      refreshTokenCookie.setHttpOnly(true);
      refreshTokenCookie.setSecure(false);
      refreshTokenCookie.setPath("/");
      refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);

      response.addCookie(accessTokenCookie);
      response.addCookie(refreshTokenCookie);

      return ResponseEntity.ok("Login successful");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Create new account.
   * 
   * @param signupRequest
   * @return
   */
  @PostMapping("/signup")
  public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
    try {
      Account account = accountService.registerAccount(
          signupRequest.getUsername(),
          signupRequest.getPassword(),
          signupRequest.getDeposit());

      return ResponseEntity.ok(account);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Refresh the access token by the refresh token.
   * 
   * @param request
   * @param response
   * @return
   */
  @GetMapping("/refresh-token")
  public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
    try {
      String refreshToken = null;
      String username = null;

      Cookie[] cookies = request.getCookies();
      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if (cookie.getName().equals("refresh-token")) {
            refreshToken = cookie.getValue();
            break;
          }
        }
      }

      if (refreshToken != null) {
        username = jwtUtil.extractUsername(refreshToken);
      }

      if (refreshToken != null && jwtUtil.validateToken(refreshToken, username)) {
        String newAccessToken = jwtUtil.generateToken(username);

        Cookie accessTokenCookie = new Cookie("web-token", newAccessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(1 * 60);

        response.addCookie(accessTokenCookie);
        return ResponseEntity.ok("Access token refreshed successfully");
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
      }

    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

}
