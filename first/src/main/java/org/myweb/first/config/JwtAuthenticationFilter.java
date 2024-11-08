package org.myweb.first.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.myweb.first.common.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
            logger.debug("Authorization Header: {}", bearerToken);

            String jwt = getJwtFromRequest(request);
            logger.debug("Extracted JWT Token: {}", jwt);

            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                String userId = jwtUtil.extractUserId(jwt);
                Set<String> roles = jwtUtil.extractRoles(jwt);

                logger.debug("Extracted UserId: {}", userId);
                logger.debug("Extracted Roles: {}", roles);

                if (StringUtils.hasText(userId) && roles != null && !roles.isEmpty()) {
                    List<GrantedAuthority> authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // ROLE_ 접두사 추가
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userId, null, authorities);

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("User authenticated: {}", userId);
                } else {
                    logger.warn("JWT Token does not contain required claims");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token does not contain required claims");
                    return;
                }
            } else {
                logger.debug("JWT Token is missing or invalid");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token is missing or invalid");
                return;
            }
        } catch (ExpiredJwtException ex) {
            logger.error("JWT Token has expired", ex);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token has expired");
            return;
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT Token", ex);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unsupported JWT Token");
            return;
        } catch (MalformedJwtException ex) {
            logger.error("Malformed JWT Token", ex);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Malformed JWT Token");
            return;
        } catch (IllegalArgumentException ex) {
            logger.error("JWT Token compact of handler are invalid", ex);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
            return;
        } catch (Exception e) {
            logger.error("Could not set user authentication in security context", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
