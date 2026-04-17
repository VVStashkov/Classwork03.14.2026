package ru.kpfu.itis.group400.stashkov.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {

    @InjectMocks
    private CustomAuthenticationEntryPoint entryPoint;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @Test
    void commence_whenUriStartsWithNotes_shouldRedirectToNotesPublic() throws Exception {
        when(request.getRequestURI()).thenReturn("/notes/something");
        when(request.getContextPath()).thenReturn("");

        entryPoint.commence(request, response, authException);

        verify(response).sendRedirect("/notes/public");
    }

    @Test
    void commence_whenUriDoesNotStartWithNotes_shouldRedirectToLogin() throws Exception {
        when(request.getRequestURI()).thenReturn("/admin");
        when(request.getContextPath()).thenReturn("");

        entryPoint.commence(request, response, authException);

        verify(response).sendRedirect("/login");
    }

    @Test
    void commence_withContextPath_shouldIncludeContextPath() throws Exception {
        when(request.getRequestURI()).thenReturn("/notes/1");
        when(request.getContextPath()).thenReturn("/myapp");

        entryPoint.commence(request, response, authException);

        verify(response).sendRedirect("/myapp/notes/public");
    }
}