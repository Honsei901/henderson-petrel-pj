package simple_banking_app.simple_banking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
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
   * ログイン処理
   * 
   * @param loginRequest
   * @return
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
    try {
      // AuthenticationManagerを使用して認証
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

      // 認証成功後、ユーザー情報を取得
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();

      // JWTを生成
      String token = jwtUtil.generateToken(userDetails.getUsername());

      // クッキーを作成
      Cookie cookie = new Cookie("web-token", token);
      cookie.setHttpOnly(true);
      cookie.setSecure(false);
      cookie.setPath("/");
      cookie.setMaxAge(1 * 60);

      response.addCookie(cookie);
      return ResponseEntity.ok("Login successful");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * 新規登録処理
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

}
