package com.sergiu.authenticationdemo.cache;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtInvalidationCache {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtInvalidationCache.class);
	
	@Value("${secretKey}")
	private String secretKey;
	
	private static final String BEARER = "Bearer ";

	//We assume the tokens are unique
	private Map<String, Long> blackListedTokens = new HashMap<>();
	
	public void blackList(String token) {
		String strippedToken = token.replace(BEARER, "");
		Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(strippedToken).getBody();
		Long expiryTime = ((Integer)claims.get("exp")).longValue();
		blackListedTokens.put(strippedToken, expiryTime);
	}
	
	public boolean isTokenBlackListed(String token) {
		return blackListedTokens.containsKey(token);
	}
	
	//of course delays should be higher
	@Scheduled(fixedDelay = 10000, initialDelay = 60000)
	private void removeExpiredTokens() {
		logger.info("Deleting expired tokens from blacklist");
		blackListedTokens.entrySet().removeIf(e -> Instant.now().isAfter(Instant.ofEpochMilli(e.getValue())));
	}
	
}
