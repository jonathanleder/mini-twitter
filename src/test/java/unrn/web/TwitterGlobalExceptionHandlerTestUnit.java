package unrn.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import unrn.DTOs.ErrorResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TwitterGlobalExceptionHandlerTestUnit {

    @InjectMocks
    private TwitterGlobalExceptionHandler exceptionHandler;

    @Test
    void handleRuntimeException_ReturnsBadRequest() {
        // Arrange
        String errorMessage = "Test error message";
        RuntimeException ex = new RuntimeException(errorMessage);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleRuntimeExceptions(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().message());
    }

    @Test
    void handleHttpMessageNotReadableException_ReturnsBadRequest() {
        // Arrange
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleSpringMVCParams(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Parámetros inválidos", response.getBody().message());
    }

    @Test
    void handleMissingServletRequestParameterException_ReturnsBadRequest() {
        // Arrange
        MissingServletRequestParameterException ex = mock(MissingServletRequestParameterException.class);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleSpringMVCParams(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Parámetros inválidos", response.getBody().message());
    }

    @Test
    void handleGenericException_ReturnsInternalServerError() {
        // Arrange
        Exception ex = new Exception("Unexpected error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleException();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().message().contains("Algo salió mal"));
    }

    @Test
    void errorResponse_ConstructorAndGetters_WorkCorrectly() {
        // Arrange
        String testMessage = "Test error message";

        // Act
        ErrorResponse errorResponse = new ErrorResponse(testMessage);

        // Assert
        assertNotNull(errorResponse);
        assertEquals(testMessage, errorResponse.message());
    }
}
