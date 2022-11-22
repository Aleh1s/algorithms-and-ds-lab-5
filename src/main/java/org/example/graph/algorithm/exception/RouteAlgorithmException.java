package org.example.graph.algorithm.exception;

public class RouteAlgorithmException extends Exception {
    public RouteAlgorithmException() {
    }

    public RouteAlgorithmException(String message) {
        super(message);
    }

    public RouteAlgorithmException(String message, Throwable cause) {
        super(message, cause);
    }

    public RouteAlgorithmException(Throwable cause) {
        super(cause);
    }
}
