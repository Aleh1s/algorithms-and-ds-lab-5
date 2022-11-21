package org.example.graph.parser.strategy.exception;

public class GraphParserStrategyException extends Exception {
    public GraphParserStrategyException() {
    }

    public GraphParserStrategyException(String message) {
        super(message);
    }

    public GraphParserStrategyException(String message, Throwable cause) {
        super(message, cause);
    }

    public GraphParserStrategyException(Throwable cause) {
        super(cause);
    }
}
