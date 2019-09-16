package org.kurento.tutorial.groupcall.permissions.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Collection;


public class JwtTokenProvider {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_ID = "Token_";
    private static final String AUTHORITIES = "authorities";
    private static final String EMPTY = "";
    @Value("${jwt.token.secret}")
    private String secret;
    private final JwtUserDetailsService userDetailsService;

    public JwtTokenProvider(JwtUserDetailsService jwtUserDetailsService) {
        this.userDetailsService = jwtUserDetailsService;
    }

    @PostConstruct
    private void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(String userName, Collection<String> roles) {
        Claims claims = Jwts.claims().setSubject(userName);
        claims.put(AUTHORITIES, roles);
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    Authentication getAuthentication(String token) {
        String username = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, EMPTY, userDetails.getAuthorities());
    }

    boolean validateToken(String token) {
        if (Strings.isBlank(token)) {
            return false;
        }
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    String resolveToken(HttpServletRequest servletRequest) {
        String header = servletRequest.getHeader(AUTHORIZATION_HEADER);
        if (Strings.isBlank(header)) {
            return EMPTY;
        }
        if (!header.startsWith(TOKEN_ID)) {
            return EMPTY;
        }
        return header.substring(TOKEN_ID.length());
    }
}