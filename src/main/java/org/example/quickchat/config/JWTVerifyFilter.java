package org.example.quickchat.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.quickchat.dto.response.config.FilterResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTVerifyFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    public JWTVerifyFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) throws ServletException {
        String uri = req.getRequestURI();
        return !uri.startsWith("/api/chat");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {
        String[] authorization = req.getHeader("Authorization").split(" ");
        String authType = authorization[0];
        String token = authorization[1];

        FilterResponse fr = new FilterResponse(false);

        if (token == null || token.isEmpty() || !authType.equals("Bearer")) {
            fr.setMessage("Token is empty");
            resp.getWriter().println(objectMapper.writeValueAsString(fr));
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        DecodedJWT jwt;
        try {
            jwt = JWT.require(Algorithm.HMAC256("f891c04f488d6c66cd4a5e4d9c8c0615"))
                    .withIssuer("quickchat").build().verify(token);
        } catch (Exception e) {
            e.printStackTrace();
            fr.setMessage("Invalid Token");
            resp.getWriter().println(objectMapper.writeValueAsString(fr));
            return;
        }
        String sub = jwt.getSubject();
        req.setAttribute("tokenId", Long.valueOf(sub));
        filterChain.doFilter(req, resp);
    }
}
