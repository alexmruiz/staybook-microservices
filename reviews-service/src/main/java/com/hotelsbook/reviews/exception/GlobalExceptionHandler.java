package com.hotelsbook.reviews.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Captura excepciones de TODOS los controladores
 * GlobalExceptionHandler
 */
@RestControllerAdvice 
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Captura errores cuando NO se encuentra un recurso (RuntimeException de "no encontrado")
    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntimeException(RuntimeException ex) {
        log.warn("Recurso no encontrado o error de negocio: {}", ex.getMessage());

        // Devuelve HTTP 404 NOT FOUND con los detalles del error
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Captura errores de validación de Bean Validation (@Valid en el DTO)
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Error de validación en la petición recibida");

        // Devuelve HTTP 400 BAD REQUEST
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, 
            "Los datos enviados en la petición no son válidos"
        );
    }

    // Captura cualquier otro error no controlado (ej. fallo de BD)
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Error no esperado en el servidor: ", ex); // Registra el stacktrace completo

        // Devuelve HTTP 500 INTERNAL SERVER ERROR
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "Ha ocurrido un error interno en el servidor"
        );
    }
}