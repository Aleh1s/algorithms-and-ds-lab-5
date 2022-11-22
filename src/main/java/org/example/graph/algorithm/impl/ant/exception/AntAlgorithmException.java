package org.example.graph.algorithm.impl.ant.exception;

import org.example.graph.algorithm.exception.RouteAlgorithmException;

public class AntAlgorithmException extends RouteAlgorithmException {
    public AntAlgorithmException() {
    }

    public AntAlgorithmException(String message) {
        super(message);
    }

    public AntAlgorithmException(String message, Throwable cause) {
        super(message, cause);
    }

    public AntAlgorithmException(Throwable cause) {
        super(cause);
    }
}
