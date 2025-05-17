package com.sismo.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Enumeration;

@Component
public class SessionDebugFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(SessionDebugFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String uri = request.getRequestURI();
        String method = request.getMethod();
        
        // Verificar si la sesión existe antes de la ejecución del filtro
        HttpSession sessionBefore = request.getSession(false); // No crear una nueva si no existe
        String sessionIdBefore = sessionBefore != null ? sessionBefore.getId() : "no-session";
        
        // Log de solicitud entrante
        logger.debug("REQUEST: {} {} (SessionID: {})", method, uri, sessionIdBefore);
        
        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
        
        // Verificar nuevamente la sesión después de la ejecución del filtro
        HttpSession sessionAfter = request.getSession(false);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        logger.debug("---- Debugging Session After {} {} ----", method, uri);
        if (sessionAfter != null) {
            logger.debug("Session ID: {}", sessionAfter.getId());
            logger.debug("Session Creation Time: {}", new java.util.Date(sessionAfter.getCreationTime()));
            logger.debug("Session Last Accessed Time: {}", new java.util.Date(sessionAfter.getLastAccessedTime()));
            logger.debug("Session Max Inactive Interval: {} seconds", sessionAfter.getMaxInactiveInterval());
            
            // Mostrar atributos de sesión relacionados con la seguridad
            Enumeration<String> attributeNames = sessionAfter.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String name = attributeNames.nextElement();
                if (name.contains("SPRING_SECURITY")) {
                    logger.debug("Session Attribute: {} = {}", name, sessionAfter.getAttribute(name));
                }
            }
        } else {
            logger.debug("No session found after request processing.");
        }
        
        // Imprimir SecurityContext
        logger.debug("SecurityContext after request:");
        if (auth != null) {
            logger.debug(" - Principal: {}", auth.getName());
            logger.debug(" - Authenticated: {}", auth.isAuthenticated());
            logger.debug(" - Authorities: {}", auth.getAuthorities());
        } else {
            logger.debug(" - No authentication found");
        }
        
        // Imprimir cookies en la respuesta
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            logger.debug("Response Cookies:");
            for (Cookie cookie : cookies) {
                logger.debug(" - {}: {} (Path: {}, Secure: {}, HttpOnly: {}, MaxAge: {})", 
                          cookie.getName(), cookie.getValue(), cookie.getPath(), 
                          cookie.getSecure(), cookie.isHttpOnly(), cookie.getMaxAge());
            }
        } else {
            logger.debug("No cookies in response");
        }
        
        logger.debug("---------------------------");
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Opcional: No filtrar recursos estáticos
        return path.contains("/css/") || path.contains("/js/") || path.contains("/images/");
    }
}