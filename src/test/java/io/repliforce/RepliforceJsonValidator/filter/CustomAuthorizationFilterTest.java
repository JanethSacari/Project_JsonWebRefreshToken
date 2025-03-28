package io.repliforce.RepliforceJsonValidator.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class CustomAuthorizationFilterTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    private DecodedJWT decodedJWT;

    @InjectMocks
    private CustomAuthorizationFilter customAuthorizationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void doFilterInternalWithoutAuthorizationHeader() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/somepath");
        when(request.getHeader("Authorization")).thenReturn(null);

        customAuthorizationFilter.doFilterInternal(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternalWithLoginPath() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/login");

        customAuthorizationFilter.doFilterInternal(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternalWithTokenRefreshPath() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/token/refresh");

        customAuthorizationFilter.doFilterInternal(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
    }
}