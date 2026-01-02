package io.repliforce.RepliforceJsonValidator.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.repliforce.RepliforceJsonValidator.domain.Role;
import io.repliforce.RepliforceJsonValidator.domain.User;
import io.repliforce.RepliforceJsonValidator.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserResourceRefreshTokenTest {

    private UserService userService;
    private UserResource userResource;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userResource = new UserResource(userService);
    }

    @Test
    void testRefreshToken_missingAuthorization_throwsRuntimeException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getHeader(org.springframework.http.HttpHeaders.AUTHORIZATION)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> userResource.refreshToken(request, response));
    }

    @Test
    void testRefreshToken_validRefreshToken_writesNewAccessToken() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/token/refresh"));

        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        String refresh = JWT.create().withSubject("carol").sign(algorithm);
        when(request.getHeader(org.springframework.http.HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + refresh);

        User u = new User();
        u.setUsername("carol");
        Role r = new Role();
        r.setRolename("ROLE_Z");
        u.setRoles(Collections.singletonList(r));
        when(userService.getUser("carol")).thenReturn(u);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream(baos));

        userResource.refreshToken(request, response);

        String body = baos.toString();
        assertTrue(body.contains("access_token"));
        assertTrue(body.contains("refresh_token"));

        // verify access token subject
        String access = extractJsonValue(body, "access_token");
        assertNotNull(access);
        assertEquals("carol", JWT.require(algorithm).build().verify(access).getSubject());
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

    // naive JSON extraction to avoid adding libs
    private String extractJsonValue(String json, String key) {
        int idx = json.indexOf(key);
        if(idx == -1) return null;
        int colon = json.indexOf(':', idx);
        int firstQuote = json.indexOf('"', colon);
        int secondQuote = json.indexOf('"', firstQuote + 1);
        return json.substring(firstQuote + 1, secondQuote);
    }
}
