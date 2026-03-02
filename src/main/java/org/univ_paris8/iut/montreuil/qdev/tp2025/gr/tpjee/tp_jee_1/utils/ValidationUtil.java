package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Utilitaire de validation utilisant Bean Validation (JSR-380)
 */
public final class ValidationUtil {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    private ValidationUtil() {
        // Classe utilitaire, pas d'instanciation
    }

    /**
     * Valide un objet et retourne les erreurs sous forme de Map
     * 
     * @param object L'objet à valider
     * @return Map avec les noms de champs comme clés et les messages d'erreur comme
     *         valeurs
     */
    public static <T> Map<String, String> validate(T object) {
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        for (ConstraintViolation<T> violation : violations) {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(field, message);
        }

        return errors;
    }

    /**
     * Valide un objet et lance une exception si des erreurs sont trouvées
     * 
     * @param object L'objet à valider
     * @throws ValidationException si l'objet n'est pas valide
     */
    public static <T> void validateAndThrow(T object) throws ValidationException {
        Map<String, String> errors = validate(object);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    /**
     * Vérifie si un objet est valide
     * 
     * @param object L'objet à valider
     * @return true si l'objet est valide, false sinon
     */
    public static <T> boolean isValid(T object) {
        return validate(object).isEmpty();
    }

    /**
     * Exception personnalisée pour les erreurs de validation
     */
    public static class ValidationException extends RuntimeException {
        private final Map<String, String> errors;

        public ValidationException(Map<String, String> errors) {
            super("Erreur de validation");
            this.errors = errors;
        }

        public Map<String, String> getErrors() {
            return errors;
        }

        public String getFirstError() {
            return errors.values().stream().findFirst().orElse("Erreur de validation");
        }

        @Override
        public String getMessage() {
            StringBuilder sb = new StringBuilder("Erreurs de validation: ");
            errors.forEach((field, message) -> sb.append(field).append(": ").append(message).append("; "));
            return sb.toString();
        }
    }
}
