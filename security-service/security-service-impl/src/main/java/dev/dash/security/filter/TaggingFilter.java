package dev.dash.security.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import dev.dash.security.AuditLogicService;
import dev.dash.security.util.JwtUtil;

@Component
public class TaggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String corIdStr = request.getHeader(JwtUtil.X_CORRELATION_ID);
        UUID corId = null;

        if (corIdStr != null && corIdStr.length() > 5) {
            corId = UUID.fromString(corIdStr);
        } else {
            corId = UUID.randomUUID();
        }

        //response.setHeader("Access-Control-Allow-Headers", JwtUtil.X_CORRELATION_ID+", Access-Control-Allow-Headers, Origin, Accept, X-Requested-With, " +
        //"Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");

        corIdStr = corId.toString();
        MDC.put(AuditLogicService.CORRELATION_ID, corIdStr);
        response.setHeader(JwtUtil.X_CORRELATION_ID, corIdStr);
        filterChain.doFilter(request, response);
    }
    
}
