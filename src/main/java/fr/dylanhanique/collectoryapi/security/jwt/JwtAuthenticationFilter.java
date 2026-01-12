package fr.dylanhanique.collectoryapi.security.jwt;

import fr.dylanhanique.collectoryapi.model.User;
import fr.dylanhanique.collectoryapi.service.UserService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserService userService
    ) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();

        if (path.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                        "timestamp": "%s",
                        "status": 401,
                        "error": "Unauthorized",
                        "message": "Missing or invalid Authorization header",
                        "path": "%s"
                }
                """.formatted(LocalDateTime.now(), request.getRequestURI()));
            return;
        }

        final String jwt = authHeader.substring(7);
        String email;

        try {
            email = jwtService.extractEmail(jwt);
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                        "timestamp": "%s",
                        "status": 401,
                        "error": "Unauthorized",
                        "message": "Invalid JWT",
                        "path": "%s"
                }
                """.formatted(LocalDateTime.now(), request.getRequestURI()));
            return;
        }

        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();

        if (email != null && existingAuth == null) {
            User user = userService.findByEmail(email);

            if (!jwtService.isTokenValid(jwt, user)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("""
                {
                        "timestamp": "%s",
                        "status": 401,
                        "error": "Unauthorized",
                        "message": "Invalid JWT",
                        "path": "%s"
                }
                """.formatted(LocalDateTime.now(), request.getRequestURI()));

                return;
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}