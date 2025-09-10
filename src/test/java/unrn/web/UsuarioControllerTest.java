package unrn.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import unrn.DTOs.NuevoUsuario;
import unrn.DTOs.UsuarioDto;
import unrn.model.Usuario;
import unrn.service.TwitterService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private TwitterService twitterService;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    void crearUsuario_ValidRequest_ReturnsCreated() {
        // Arrange
        NuevoUsuario request = new NuevoUsuario("testuser");
        doNothing().when(twitterService).crearUsuario(anyString());

        // Act
        ResponseEntity<?> response = usuarioController.crearUsuario(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(twitterService, times(1)).crearUsuario("testuser");
    }

    @Test
    void crearUsuario_ServiceThrowsException_ReturnsBadRequest() {
        // Arrange
        NuevoUsuario request = new NuevoUsuario("short");
        String errorMessage = "Nombre de usuario inválido";
        doThrow(new RuntimeException(errorMessage))
            .when(twitterService).crearUsuario(anyString());

        // Act
        ResponseEntity<?> response = usuarioController.crearUsuario(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void listarUsuarios_ReturnsListOfUsers() {
        // Arrange
        Usuario user1 = mock(Usuario.class);
        Usuario user2 = mock(Usuario.class);
        when(user1.getId()).thenReturn(1L);
        when(user1.obtenerUserName()).thenReturn("user1");
        when(user2.getId()).thenReturn(2L);
        when(user2.obtenerUserName()).thenReturn("user2");
        when(twitterService.listarUsuarios()).thenReturn(List.of(user1, user2));

        // Act
        ResponseEntity<?> response = usuarioController.listarUsuarios();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        
        @SuppressWarnings("unchecked")
        List<UsuarioDto> usuarios = (List<UsuarioDto>) response.getBody();
        assertEquals(2, usuarios.size());
        assertEquals(1L, usuarios.get(0).id());
        assertEquals("user1", usuarios.get(0).username());
        assertEquals(2L, usuarios.get(1).id());
        assertEquals("user2", usuarios.get(1).username());
    }

    @Test
    void buscarUsuario_UserExists_ReturnsUser() {
        // Arrange
        Usuario user = mock(Usuario.class);
        when(user.getId()).thenReturn(1L);
        when(user.obtenerUserName()).thenReturn("testuser");
        when(twitterService.buscarUsuarioPorUserName("testuser"))
            .thenReturn(Optional.of(user));

        // Act
        ResponseEntity<?> response = usuarioController.buscarUsuario("testuser");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof UsuarioDto);
        UsuarioDto dto = (UsuarioDto) response.getBody();
        assertEquals(1L, dto.id());
        assertEquals("testuser", dto.username());
    }

    @Test
    void buscarUsuario_UserNotExists_ReturnsNotFound() {
        // Arrange
        when(twitterService.buscarUsuarioPorUserName("nonexistent"))
            .thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = usuarioController.buscarUsuario("nonexistent");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void eliminarUsuario_ValidId_ReturnsNoContent() {
        // Arrange
        doNothing().when(twitterService).eliminarUsuario(1L);

        // Act
        ResponseEntity<?> response = usuarioController.eliminarUsuario(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(twitterService, times(1)).eliminarUsuario(1L);
    }

    @Test
    void eliminarUsuario_ServiceThrowsException_ReturnsBadRequest() {
        // Arrange
        String errorMessage = "Usuario no encontrado";
        doThrow(new RuntimeException(errorMessage))
            .when(twitterService).eliminarUsuario(anyLong());

        // Act
        ResponseEntity<?> response = usuarioController.eliminarUsuario(999L);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }
}
