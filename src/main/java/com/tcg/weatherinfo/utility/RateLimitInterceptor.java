package com.tcg.weatherinfo.utility;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

//RateLimitInterceptor.java
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
 private final RateLimiter rateLimiter;

 public RateLimitInterceptor(RateLimiter rateLimiter) {
     this.rateLimiter = rateLimiter;
 }

 @Override
 public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
     String apiKey = request.getHeader("X-API-Key");
     if (apiKey == null || apiKey.isEmpty()) {
         response.setStatus(HttpStatus.BAD_REQUEST.value());
         return false;
     }

     Bucket bucket = rateLimiter.resolveBucket(apiKey);
     if (!bucket.tryConsume(1)) {
         response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
         return false;
     }

     return true;
 }
}
