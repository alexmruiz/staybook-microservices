package com.staybook.booking.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404 NOT FOUND - Recurso de reserva no encontrado
    @ExceptionHandler(BookingNotFoundException.class)
    public ProblemDetail handleBookingNotFound(BookingNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 404 NOT FOUND - Disponibilidad de habitación no encontrada
    @ExceptionHandler(RoomAvailabilityNotFoundException.class)
    public ProblemDetail handleRoomAvailabilityNotFound(RoomAvailabilityNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 409 CONFLICT - Habitación no disponible
    @ExceptionHandler(RoomNotAvailableException.class)
    public ProblemDetail handleRoomNotAvailable(RoomNotAvailableException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    // 409 CONFLICT - Estado de reserva inválido para la operación
    @ExceptionHandler(InvalidBookingStateException.class)
    public ProblemDetail handleInvalidBookingState(InvalidBookingStateException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    // 400 BAD REQUEST - Rango de fechas no válido
    @ExceptionHandler(InvalidDateRangeException.class)
    public ProblemDetail handleInvalidDateRange(InvalidDateRangeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 400 BAD REQUEST - Bean Validation (@Valid en DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Error de validación en la petición recibida");
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Los datos enviados en la petición no son válidos"
        );
    }

    // 500 INTERNAL SERVER ERROR - Errores no controlados
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Error no esperado en el servidor: ", ex);
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ha ocurrido un error interno en el servidor"
        );
    }
}
