package it.lisik.springutils;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Annotation for enabling Revocation Endpoint
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(RevokeTokenEndpoint.class)
public @interface EnableRevocationEndpoint {
}
