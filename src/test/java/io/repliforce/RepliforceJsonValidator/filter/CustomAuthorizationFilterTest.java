package io.repliforce.RepliforceJsonValidator.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomAuthorizationFilterTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_bypassesForLoginPath() throws Exception {
        CustomAuthorizationFilter filter = new CustomAuthorizationFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getServletPath()).thenReturn("/api/login");

        filter.doFilterInternal(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_missingAuthorizationHeader_callsChain() throws Exception {
        CustomAuthorizationFilter filter = new CustomAuthorizationFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getServletPath()).thenReturn("/api/secure");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_malformedAuthorizationHeader_callsChain() throws Exception {
        CustomAuthorizationFilter filter = new CustomAuthorizationFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getServletPath()).thenReturn("/api/secure");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Token abc");

        filter.doFilterInternal(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_invalidToken_returnsForbiddenWithJsonError() throws Exception {
        CustomAuthorizationFilter filter = new CustomAuthorizationFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getServletPath()).thenReturn("/api/secure");
        // create a token signed with a different secret so verification fails
        Algorithm badAlg = Algorithm.HMAC256("badsecret".getBytes());
        String badToken = JWT.create().withSubject("someone").sign(badAlg);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + badToken);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream(baos));

        filter.doFilterInternal(request, response, chain);

        verify(response).setStatus(403);
        verify(response).setHeader(eq("error"), anyString());
        String body = baos.toString();
        assertTrue(body.contains("error_message"));
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_validToken_setsSecurityContext_andCallsChain() throws Exception {
        CustomAuthorizationFilter filter = new CustomAuthorizationFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getServletPath()).thenReturn("/api/secure");
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        String token = JWT.create()
                .withSubject("john")
                .withArrayClaim("roles", new String[]{"ROLE_USER"})
                .sign(algorithm);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);

        filter.doFilterInternal(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("john", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    // Helper ServletOutputStream that delegates to a ByteArrayOutputStream
    static class DelegatingServletOutputStream extends ServletOutputStream {
        private final ByteArrayOutputStream outputStream;

        DelegatingServletOutputStream(ByteArrayOutputStream baos) {
            this.outputStream = baos;
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(javax.servlet.WriteListener writeListener) {
            // no-op
        }
    }
}

