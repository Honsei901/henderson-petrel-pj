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
  private JwtUtil jwtUtil;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private AuthenticationManager authenticationManager;

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
    Cookie accessTokenCookie = generateAccessTokenCookie(token);
    cookies[0] = accessTokenCookie;

    // Generate cookie for the refresh token.
    Cookie refreshTokenCookie = generateRefreshTokenCookie(refreshToken);
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
        if (cookie.getName().equals("refresh_id")) {
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

      Cookie accessTokenCookie = generateAccessTokenCookie(newAccessToken);
      return accessTokenCookie;
    } else {
      throw new Exception("Invalid refresh token");
    }
  }

  /**
   * 
   * @param token
   * @return
   * @throws Exception
   */
  public Account getAccountByToken(String token) throws Exception {
    String username = jwtUtil.extractUsername(token);
    Account account = accountRepository.findByUsername(username)
        .orElseThrow(() -> new Exception("Account doesn't exist"));
    return account;
  }

  /**
   * Generate a cookie for the access token.
   * 
   * @param token
   * @return
   */
  private Cookie generateAccessTokenCookie(String token) {
    Cookie accessTokenCookie = new Cookie("aid", token);
    accessTokenCookie.setHttpOnly(true);
    accessTokenCookie.setSecure(false);
    accessTokenCookie.setPath("/");
    accessTokenCookie.setMaxAge(15 * 60);
    return accessTokenCookie;
  }

  /**
   * Generate a cookie for the refresh token.
   * 
   * @param refreshToken
   * @return
   */
  private Cookie generateRefreshTokenCookie(String refreshToken) {
    Cookie refreshTokenCookie = new Cookie("refresh_id", refreshToken);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(false);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
    return refreshTokenCookie;
  }
}
