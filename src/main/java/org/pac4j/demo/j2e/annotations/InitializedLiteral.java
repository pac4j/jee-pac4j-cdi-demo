package org.pac4j.demo.j2e.annotations;


import javax.enterprise.util.AnnotationLiteral;


/**
 * Literal for the {@link Initialized} annotation.
 *
 * @author Phillip Ross
 */
public class InitializedLiteral extends AnnotationLiteral<Initialized> implements Initialized {

    public static final Initialized INSTANCE = new InitializedLiteral();

    private static final long serialVersionUID = 1300374454019932582L;

}
