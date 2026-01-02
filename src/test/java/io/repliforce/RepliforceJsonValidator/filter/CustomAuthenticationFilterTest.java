package io.repliforce.RepliforceJsonValidator.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomAuthenticationFilterTest {

    @Test
    void testAttemptAuthentication_readsParametersAndDelegates() {
        AuthenticationManager authManager = mock(AuthenticationManager.class);
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter(authManager);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("username")).thenReturn("bob");
        when(request.getParameter("password")).thenReturn("pwd");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("bob", "pwd");
        when(authManager.authenticate(any())).thenReturn(token);

        Authentication result = filter.attemptAuthentication(request, response);
        assertNotNull(result);
    }

    @Test
    void testSuccessfulAuthentication_writesTokensToResponseJson() throws Exception {
        AuthenticationManager authManager = mock(AuthenticationManager.class);
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter(authManager);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/login"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream(baos));

        User principal = new User("alice", "pwd", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        filter.successfulAuthentication(request, response, chain, authentication);

        String body = baos.toString();
        assertTrue(body.contains("access_token"));
        assertTrue(body.contains("refresh_token"));

        String access = extractJsonValue(body, "access_token");
        assertNotNull(access);
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        assertEquals("alice", JWT.require(algorithm).build().verify(access).getSubject());
    }

    private String extractJsonValue(String json, String key) {
        int idx = json.indexOf(key);
        if(idx == -1) return null;
        int colon = json.indexOf(':', idx);
        int firstQuote = json.indexOf('"', colon);
        int secondQuote = json.indexOf('"', firstQuote + 1);
        return json.substring(firstQuote + 1, secondQuote);
    }

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
