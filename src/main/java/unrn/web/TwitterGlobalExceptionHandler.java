package unrn.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import unrn.DTOs.ErrorResponse;

//El término advise viene de AOP
//Un advice es un bloque de código que se ejecuta antes y/o despues de otro bloque código.
@RestControllerAdvice
public class TwitterGlobalExceptionHandler {

    //las que lanza SpringMVC cuando el Body o cualquier otro parametro requerido no viene
    //las que lanza mi modelo o servicio
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeExceptions(RuntimeException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponse> handleSpringMVCParams(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                "Parámetros inválidos");
        return ResponseEntity.badRequest().body(error);
    }

    //cualquier otra
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException() {
        ErrorResponse error = new ErrorResponse(
                "Algo salió mal... contacte a bla bla");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}