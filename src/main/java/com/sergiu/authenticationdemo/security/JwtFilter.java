package com.sergiu.authenticationdemo.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sergiu.authenticationdemo.cache.JwtInvalidationCache;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtFilter extends OncePerRequestFilter {
	
	Logger logger = LoggerFactory.getLogger(JwtFilter.class);

	private final String HEADER = "Authorization";
	private final String BEARER = "Bearer ";

	@Value("${secretKey}")
	private String secretKey;

	private JwtInvalidationCache invalidationCache;

	public JwtFilter(JwtInvalidationCache invalidationCache) {
		this.invalidationCache = invalidationCache;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		try {
			if (checkJWTToken(request, response)) {
				Claims claims = validateToken(request);
				if (claims.get("authorities") != null) {
					setAuthenticatedUserOnContext(claims);
				} else {
					logger.info("User has no rights on the token!");
					SecurityContextHolder.clearContext();
				}
			} else {
				logger.info("JWT token check failed!");
				SecurityContextHolder.clearContext();
			}
			chain.doFilter(request, response);
		} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			return;
		}
	}

	private Claims validateToken(HttpServletRequest request) {
		// TODO check if token was blacklisted
		String jwtToken = request.getHeader(HEADER).replace(BEARER, "");
		return Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
	}

	private void setAuthenticatedUserOnContext(Claims claims) {
		@SuppressWarnings("unchecked")
		List<String> authorities = (List) claims.get("authorities");

		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
				authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
		SecurityContextHolder.getContext().setAuthentication(auth);

	}

	private boolean checkJWTToken(HttpServletRequest request, HttpServletResponse res) {
		String authenticationHeader = request.getHeader(HEADER);
		if (authenticationHeader == null || !authenticationHeader.startsWith(BEARER)) {
			logger.info("No token on request header!");
			return false;
		}
		String strippedToken = request.getHeader(HEADER).replace(BEARER, "");
		if (invalidationCache.isTokenBlackListed(strippedToken)) {
			logger.info("Token is blacklisted: " + strippedToken);
			return false;
		}
		return true;
	}

}
