package org.textsim.exception;

/**
 * A unChecked Exception happen in the (pre-)process.
 * <p>
 * This custom Exception is used to encapsulate the possible Exception which may occur in the (pre-)process.
 * </p>
 *
 * @author <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class ProcessRuntimeException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construct a new {@code ProcessRuntimeException} with {@code null} as detail information.
     * The cause is not initialized, and may subsequently be initialized by a call to {@link #initCause}.
     */
    public ProcessRuntimeException() {

        super();

    }
    
    /**
     * Construct a new {@code ProcessRuntimeException} with specified detail information.
     * The cause is not initialized, and may subsequently be initialized by a call to {@link #initCause}.
     *
     * @param  message  the detail message. The detail message is saved for later retrieval by the {@link #getMessage}
     *                  method.
     */
    public ProcessRuntimeException(String message) {

        super(message);

    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i> automatically incorporated in this runtime
     * exception's detail message.
     * </p>
     *
     * @param  message  The detail message. The detail message is saved for later retrieval by the {@link #getMessage}
     *                  method.
     * @param  cause    the cause (which is saved for later retrieval by the {@link #getMessage} method). (A {@code null}
     *                  value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ProcessRuntimeException(String message, Throwable cause) {

        super(message, cause);

    }
    
    /**
     * Constructs a new runtime exception with the specified cause and a detail message of
     * {@code (cause==null ? null : cause.toString())} (which typically contains the class and detail message of
     * {@code cause}).
     * This constructor is useful for runtime exceptions that are little more than wrappers for other throwables.
     *
     * @param  cause  the cause (which is saved for later retrieval by the {@link #getCause()} method). (A {@code null}
     *                value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ProcessRuntimeException(Throwable cause) {

        super(cause);

    }

}
