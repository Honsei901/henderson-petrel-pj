package simple_banking_app.simple_banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import simple_banking_app.simple_banking.entity.Account;
import simple_banking_app.simple_banking.repository.AccountRepository;
import simple_banking_app.simple_banking.security.JwtUtil;

@Service
public class AuthService {
  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * Check the password to login.
   * 
   * @param username
   * @param password
   * @return
   * @throws Exception
   */
  public Cookie[] authenticate(String username, String password) throws Exception {
    // Authenticate using AuthenticationManager.
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password));

    // After successful authentication, retrieve the user's information.
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    // Generate access token.
    String token = jwtUtil.generateToken(userDetails.getUsername());

    // Generate refresh token.
    String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

    Cookie[] cookies = new Cookie[2];

    // Generate cookie for the access token.
    Cookie accessTokenCookie = new Cookie("web-token", token);
    accessTokenCookie.setHttpOnly(true);
    accessTokenCookie.setSecure(false);
    accessTokenCookie.setPath("/");
    accessTokenCookie.setMaxAge(1 * 60);
    cookies[0] = accessTokenCookie;

    // Generate cookie for the refresh token.
    Cookie refreshTokenCookie = new Cookie("refresh-token", refreshToken);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(false);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
    cookies[1] = refreshTokenCookie;

    return cookies;
  }

  /**
   * Register new account.
   * 
   * @param username
   * @param password
   * @param deposit
   * @return
   * @throws Exception
   */
  public Account registerAccount(String username, String password, Double deposit) throws Exception {
    if (accountRepository.existsByUsername(username)) {
      throw new Exception("Username already exists");
    }

    String encodedPassword = passwordEncoder.encode(password);

    Account account = new Account(username, encodedPassword, deposit);

    return accountRepository.save(account);
  }

  /**
   * Generate a new access token by the refresh token.
   * 
   * @param request
   * @return
   * @throws Exception
   */
  public Cookie generateAccessToken(HttpServletRequest request) throws Exception {
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

      return accessTokenCookie;
    } else {
      throw new Exception("Invalid refresh token");
    }
  }
}
