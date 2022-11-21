package org.example.graph.algorithm.exception;

public class RouteAlgorithmFactoryException extends Exception {
    public RouteAlgorithmFactoryException() {
    }

    public RouteAlgorithmFactoryException(String message) {
        super(message);
    }

    public RouteAlgorithmFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RouteAlgorithmFactoryException(Throwable cause) {
        super(cause);
    }
}
