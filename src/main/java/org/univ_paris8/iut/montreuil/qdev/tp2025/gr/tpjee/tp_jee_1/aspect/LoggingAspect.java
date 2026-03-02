package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private static final List<String> SENSITIVE_PARAM_NAMES = Arrays.asList(
            "password", "pwd", "token", "secret", "credentials", "jwt");

    @Around("execution(* org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.*.*(..))")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        String className = signature.getDeclaringType().getSimpleName();

        // Log start
        // log.info("[{}] {}() started", className, methodName);

        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();
        String safeArgs = formatArgs(paramNames, args);

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;

            log.info("[{}] {}({}) - {}ms | OK", className, methodName, safeArgs, executionTime);
            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - start;
            log.error("[{}] {}({}) - {}ms | ERROR: {}", className, methodName, safeArgs, executionTime,
                    e.getClass().getSimpleName() + " - " + e.getMessage());
            throw e;
        }
    }

    // Optional: Safe args formatter if needed to print passed arguments safely
    // (e.g. at request start)
    private String formatArgs(String[] paramNames, Object[] args) {
        if (paramNames == null || args == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0)
                sb.append(", ");
            String paramName = paramNames[i] != null ? paramNames[i].toLowerCase() : "arg" + i;
            if (SENSITIVE_PARAM_NAMES.stream().anyMatch(paramName::contains)) {
                sb.append("********");
            } else {
                Object arg = args[i];
                if (arg != null && !arg.getClass().getName().startsWith("java.lang") && !arg.getClass().isPrimitive()) {
                    sb.append(arg.getClass().getSimpleName()); // Avoids deep toString/lazy loading issues
                } else {
                    sb.append(arg);
                }
            }
        }
        return sb.toString();
    }
}
