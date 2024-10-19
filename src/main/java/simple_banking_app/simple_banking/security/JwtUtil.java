package simple_banking_app.simple_banking.security;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
  private final String SECRET_KEY = "my-256-bit-secret-your-256-bit-secret";

  private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

  /**
   * Generate access token.
   * 
   * @param username
   * @return
   */
  public String generateToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 1))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Generate refresh token.
   * 
   * @param username
   * @return
   */
  public String generateRefreshToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Check the validity of the token.
   * 
   * @param token
   * @param username
   * @return
   */
  public boolean validateToken(String token, String username) {
    String extractedUsername = extractUsername(token);
    return (extractedUsername.equals(username) && !isTokenExpired(token));
  }

  /**
   * Validate the JWT token and retrieve the username.
   * 
   * @param token
   * @return
   */
  public String extractUsername(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();

  }

  /**
   * Check the token's expiration date.
   * 
   * @param token
   * @return
   */
  public boolean isTokenExpired(String token) {
    Date expiration = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getExpiration();
    return expiration.before(new Date());
  }
}
