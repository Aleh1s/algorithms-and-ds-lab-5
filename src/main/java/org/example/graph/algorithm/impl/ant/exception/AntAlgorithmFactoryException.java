package org.example.graph.algorithm.impl.ant.exception;

import org.example.graph.algorithm.exception.RouteAlgorithmFactoryException;

public class AntAlgorithmFactoryException extends RouteAlgorithmFactoryException {
    public AntAlgorithmFactoryException() {
    }

    public AntAlgorithmFactoryException(String message) {
        super(message);
    }

    public AntAlgorithmFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public AntAlgorithmFactoryException(Throwable cause) {
        super(cause);
    }
}
