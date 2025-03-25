package io.repliforce.RepliforceJsonValidator.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomAuthenticationFilterTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CustomAuthenticationFilter customAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager);
    }

    @Test
    void testAttemptAuthentication() {
        when(request.getParameter("username")).thenReturn("user");
        when(request.getParameter("password")).thenReturn("password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        Authentication result = customAuthenticationFilter.attemptAuthentication(request, response);

        assertNotNull(result);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testSuccessfulAuthentication() throws IOException, ServletException {
        User user = new User("user", "password", Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(user);

        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/login"));

        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);

        customAuthenticationFilter.successfulAuthentication(request, response, chain, authentication);

        verify(response, times(1)).setContentType("application/json");
        verify(response, times(1)).getOutputStream();
        verify(outputStream, times(1)).write(any(byte[].class), eq(0), anyInt());
    }

    @Test
    void testAttemptAuthenticationThrowsException() {
        when(request.getParameter("username")).thenReturn("user");
        when(request.getParameter("password")).thenReturn("password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new AuthenticationException("Authentication failed") {});

        assertThrows(AuthenticationException.class, () -> {
            customAuthenticationFilter.attemptAuthentication(request, response);
        });
    }
}