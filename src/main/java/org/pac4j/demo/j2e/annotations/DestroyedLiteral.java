package org.pac4j.demo.j2e.annotations;


import javax.enterprise.util.AnnotationLiteral;


/**
 * Literal for the {@link Destroyed} annotation.
 *
 * @author Phillip Ross
 */
public class DestroyedLiteral extends AnnotationLiteral<Destroyed> implements Destroyed {

    public static final Destroyed INSTANCE = new DestroyedLiteral();

    private static final long serialVersionUID = 5492433717936370502L;

}
