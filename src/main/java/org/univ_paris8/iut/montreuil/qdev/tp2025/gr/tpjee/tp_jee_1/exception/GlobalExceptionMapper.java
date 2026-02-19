package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.ValidationUtil;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestionnaire global des exceptions JAX-RS.
 * Transforme les exceptions Java en réponses HTTP JSON standardisées.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {

        // 1. Exceptions JAX-RS (ex: 404 Not Found natif, 403 Forbidden...)
        // On les laisse passer telles quelles ou on les formate en JSON
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        }

        // 2. Accès interdit (ownership) → 403
        if (exception instanceof SecurityException) {
            return buildResponse(Response.Status.FORBIDDEN, exception.getMessage());
        }

        // 3. Conflit de concurrence (@Version) → 409
        if (exception instanceof javax.persistence.OptimisticLockException) {
            return buildResponse(Response.Status.CONFLICT,
                    "Conflit de concurrence : la ressource a été modifiée par un autre utilisateur");
        }

        // 4. Entité non trouvée -> 404
        if (exception instanceof EntityNotFoundException) {
            return buildResponse(Response.Status.NOT_FOUND, exception.getMessage());
        }

        // 5. Validation manuelle (ValidationUtil) -> 400
        if (exception instanceof ValidationUtil.ValidationException) {
            ValidationUtil.ValidationException ve = (ValidationUtil.ValidationException) exception;
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ve.getErrors())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // 6. Validation automatique (@Valid / Bean Validation) -> 400
        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) exception;
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
                String path = violation.getPropertyPath().toString();
                // On garde seulement le nom du champ (dernier élément du path)
                String field = path.substring(path.lastIndexOf('.') + 1);
                errors.put(field, violation.getMessage());
            }
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errors)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // 7. État invalide (ex: transition DRAFT -> DRAFT) -> 409 Conflict
        if (exception instanceof InvalidStateException) {
            return buildResponse(Response.Status.CONFLICT, exception.getMessage());
        }

        // 8. Autres erreurs métier -> 400 Bad Request
        if (exception instanceof BusinessException) {
            return buildResponse(Response.Status.BAD_REQUEST, exception.getMessage());
        }

        // 9. Erreurs inattendues (NullPointer, SQL, etc.) -> 500 Internal Server Error
        logger.error("Erreur interne non interceptée", exception);
        return buildResponse(Response.Status.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue");
    }

    private Response buildResponse(Response.Status status, String message) {
        Map<String, String> body = new HashMap<>();
        body.put("error", message);
        return Response.status(status)
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
