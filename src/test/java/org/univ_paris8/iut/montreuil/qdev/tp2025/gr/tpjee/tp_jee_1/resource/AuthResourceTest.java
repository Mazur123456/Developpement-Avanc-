package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.LoginDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.TokenDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.AuthService;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthResourceTest {

    @InjectMocks
    private AuthResource authResource;

    @Mock
    private AuthService authService;

    // Need to update AuthResource to allow injection or use constructor injection
    // Similar problem as AuthFilter

    @BeforeEach
    void setUp() {
        authResource = new AuthResource(authService);
    }

    @Test
    void testLoginSuccess() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("user");
        loginDTO.setPassword("password");

        when(authService.login("user", "password")).thenReturn("mock-token");

        Response response = authResource.login(loginDTO);

        assertEquals(200, response.getStatus());
        TokenDTO tokenDTO = (TokenDTO) response.getEntity();
        assertNotNull(tokenDTO);
        assertEquals("mock-token", tokenDTO.getToken());
    }

    @Test
    void testLoginFailure() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("user");
        loginDTO.setPassword("wrong");

        when(authService.login("user", "wrong")).thenReturn(null);

        Response response = authResource.login(loginDTO);

        assertEquals(401, response.getStatus());
    }
}
