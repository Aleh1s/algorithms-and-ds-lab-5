package org.example.graph.parser.strategy.impl.xml.exception;

import org.example.graph.parser.strategy.exception.GraphParserStrategyException;

public class XmlGraphParserStrategyException extends GraphParserStrategyException {
    public XmlGraphParserStrategyException() {
    }

    public XmlGraphParserStrategyException(String message) {
        super(message);
    }

    public XmlGraphParserStrategyException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlGraphParserStrategyException(Throwable cause) {
        super(cause);
    }
}
