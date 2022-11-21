package org.example.graph.parser.strategy.impl.xml;

import org.example.graph.parser.strategy.impl.xml.exception.XmlGraphParserStrategyException;

import javax.xml.namespace.QName;

public enum XmlTag {
    GRAPH("graph"),
    VERTEX("vertex"),
    EDGE("edge");

    private final String name;
    XmlTag(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public static XmlTag from(QName name) throws XmlGraphParserStrategyException {
        return switch (name.getLocalPart()) {
            case "graph" -> GRAPH;
            case "vertex" -> VERTEX;
            case "edge" -> EDGE;
            default -> throw new XmlGraphParserStrategyException("Unknown tag name");
        };
    }
}
