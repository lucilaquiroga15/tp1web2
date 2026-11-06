
package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - Recurso inexistente
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail handleNoEncontrado(
            RecursoNoEncontradoException ex) {

        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    // 409 - Conflicto
    @ExceptionHandler(ConflictoException.class)
    public ProblemDetail handleConflicto(
            ConflictoException ex) {

        ProblemDetail problema =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.CONFLICT,
                        ex.getMessage()
                );

        problema.setTitle("Conflicto");
        return problema;
    }

    // 502 - Servicio externo
    @ExceptionHandler(ServicioExternoException.class)
    public ProblemDetail handleServicioExterno(
            ServicioExternoException ex) {

        ProblemDetail problema =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_GATEWAY,
                        ex.getMessage()
                );

        problema.setTitle("Falla al consumir un servicio externo");
        return problema;
    }

    // 400 - Error de validación
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidacion(
            MethodArgumentNotValidException ex) {

        Map<String, String> errores = new LinkedHashMap<>();

        for (FieldError error :
                ex.getBindingResult().getFieldErrors()) {

            errores.put(
                    error.getField(),
                    error.getDefaultMessage()
            );
        }

        ProblemDetail problema =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        "Uno o más campos no son válidos"
                );

        problema.setTitle("Error de validación");
        problema.setProperty("errores", errores);

        return problema;
    }

    // 401 - Usuario no autenticado
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleNoAutenticado(
            AuthenticationException ex) {

        ProblemDetail problema =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario no autenticado"
                );

        problema.setTitle("No autorizado");
        return problema;
    }

    // 403 - Usuario sin permisos
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccesoDenegado(
            AccessDeniedException ex) {

        ProblemDetail problema =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.FORBIDDEN,
                        ex.getMessage()
                );

        problema.setTitle("Acceso denegado");
        return problema;
    }

    // 500 - Errores inesperados
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenerico(Exception ex) {

        ProblemDetail problema =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Ocurrió un error inesperado"
                );

        problema.setTitle("Error interno");
        return problema;
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
public ProblemDetail handleJsonInvalido(
        HttpMessageNotReadableException ex) {

    ProblemDetail problema = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "El cuerpo de la solicitud contiene un JSON invalido o mal codificado"
    );

    problema.setTitle("Solicitud invalida");
    return problema;
}
}
