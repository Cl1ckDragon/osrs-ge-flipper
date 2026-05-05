package com.osrsflip.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthRateLimitInterceptorTest {

    private AuthRateLimitInterceptor interceptor;
    private HttpServletRequest  request;
    private HttpServletResponse response;
    private PrintWriter         writer;

    @BeforeEach
    void setUp() throws Exception {
        interceptor = new AuthRateLimitInterceptor();
        request     = mock(HttpServletRequest.class);
        response    = mock(HttpServletResponse.class);
        writer      = new PrintWriter(new StringWriter());

        when(response.getWriter()).thenReturn(writer);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("1.2.3.4");
    }

    @Test
    void login_allows5AttemptsBeforeBlocking() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        for (int i = 0; i < 5; i++) {
            assertThat(interceptor.preHandle(request, response, null))
                    .as("attempt %d should pass", i + 1)
                    .isTrue();
        }

        assertThat(interceptor.preHandle(request, response, null))
                .as("6th attempt should be blocked")
                .isFalse();

        verify(response).setStatus(429);
    }

    @Test
    void register_allows3AttemptsBeforeBlocking() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/auth/register");

        for (int i = 0; i < 3; i++) {
            assertThat(interceptor.preHandle(request, response, null))
                    .as("attempt %d should pass", i + 1)
                    .isTrue();
        }

        assertThat(interceptor.preHandle(request, response, null))
                .as("4th attempt should be blocked")
                .isFalse();

        verify(response).setStatus(429);
    }

    @Test
    void differentIps_getIndependentBuckets() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        // Exhaust bucket for 1.2.3.4
        when(request.getRemoteAddr()).thenReturn("1.2.3.4");
        for (int i = 0; i < 5; i++) interceptor.preHandle(request, response, null);
        assertThat(interceptor.preHandle(request, response, null)).isFalse();

        // Different IP should still have a full bucket
        when(request.getRemoteAddr()).thenReturn("9.9.9.9");
        assertThat(interceptor.preHandle(request, response, null)).isTrue();
    }

    @Test
    void xForwardedFor_usedAsClientIp() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/auth/login");
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.5, 10.0.0.1");

        // Exhaust bucket for the forwarded IP
        for (int i = 0; i < 5; i++) interceptor.preHandle(request, response, null);
        assertThat(interceptor.preHandle(request, response, null)).isFalse();

        // Direct IP (different) should be unaffected
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        assertThat(interceptor.preHandle(request, response, null)).isTrue();
    }

    @Test
    void nonAuthPath_isNotRateLimited() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/prices");

        // Should pass indefinitely — no bucket applied
        for (int i = 0; i < 20; i++) {
            assertThat(interceptor.preHandle(request, response, null)).isTrue();
        }
        verify(response, never()).setStatus(429);
    }
}
