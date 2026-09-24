package com.hotelsbook.services.com_hotelsbook_services.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Captura excepciones de TODOS los controladores
 * GlobalExceptionHandler
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Captura errores cuando NO se encuentra un recurso (RuntimeException de "no
    // encontrado")
    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleReviewNotFound(EntityNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
    }

    /**
     * Captura errores de validación de Bean Validation (@Valid en el DTO)
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Error de validación en la petición recibida");

        // Devuelve HTTP 400 BAD REQUEST
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Los datos enviados en la petición no son válidos");
    }

    // Captura errores cuando un path variable no puede convertirse al tipo esperado (ej. "{id")
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String detail = String.format("El parámetro '%s' tiene un valor inválido: %s", ex.getName(), ex.getValue());
        log.warn("MethodArgumentTypeMismatch: {} = {}", ex.getName(), ex.getValue());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    }

    // Captura cualquier otro error no controlado (ej. fallo de BD)
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Error no esperado en el servidor: ", ex); // Registra el stacktrace completo

        // Devuelve HTTP 500 INTERNAL SERVER ERROR
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ha ocurrido un error interno en el servidor");
    }
}
