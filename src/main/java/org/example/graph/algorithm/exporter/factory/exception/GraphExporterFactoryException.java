package org.example.graph.algorithm.exporter.factory.exception;

public class GraphExporterFactoryException extends Exception {
    public GraphExporterFactoryException() {
    }

    public GraphExporterFactoryException(String message) {
        super(message);
    }

    public GraphExporterFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public GraphExporterFactoryException(Throwable cause) {
        super(cause);
    }
}
