package org.example.graph.parser.strategy.impl.xml;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import org.example.graph.Edge;
import org.example.graph.Vertex;
import org.example.graph.parser.strategy.GraphParserStrategy;
import org.example.graph.parser.strategy.impl.xml.exception.XmlGraphParserStrategyException;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static org.example.Constants.XSD_FILE_NAME;
import static org.example.Utils.parseIntValueFrom;

public class XmlGraphParserStrategy extends GraphParserStrategy {

    private final XMLInputFactory factory;
    private final List<Vertex> twoAdjacentVertices;
    private int distance;
    private MutableValueGraph<Vertex, Edge> valueGraph;

    public XmlGraphParserStrategy(String fileName) {
        super(fileName);
        factory = XMLInputFactory.newInstance();
        twoAdjacentVertices = new ArrayList<>(2);
    }

    @Override
    public void parse() throws XmlGraphParserStrategyException {
        validate(fileName);
        build(fileName);
    }

    private void build(String fileName) throws XmlGraphParserStrategyException {
        try {
            XMLEventReader reader = factory.createXMLEventReader(new FileReader(fileName));
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    handleStartElement(event.asStartElement());
                } else if (event.isEndElement()) {
                    handleEndElement(event.asEndElement());
                }
            }
        } catch (XMLStreamException | FileNotFoundException e) {
            throw new XmlGraphParserStrategyException(e);
        }
    }

    private void handleStartElement(StartElement startElement) throws XmlGraphParserStrategyException {
        XmlTag tag = XmlTag.from(startElement.getName());
        switch (tag) {
            case GRAPH -> this.valueGraph = ValueGraphBuilder
                    .undirected().allowsSelfLoops(false).build();
            case EDGE -> {
                twoAdjacentVertices.clear();
                distance = getEdgeValue(startElement);
            }
            case VERTEX -> twoAdjacentVertices.add(createVertex(startElement));
        }
    }

    private int getEdgeValue(StartElement startElement) {
        Attribute attribute = startElement.getAttributeByName(QName.valueOf("value"));
        return parseIntValueFrom(attribute);
    }

    private void handleEndElement(EndElement endElement) throws XmlGraphParserStrategyException {
        XmlTag tag = XmlTag.from(endElement.getName());
        if (tag == XmlTag.EDGE)
            valueGraph.putEdgeValue(
                    twoAdjacentVertices.get(0), twoAdjacentVertices.get(1), Edge.valueOf(distance));
    }

    private Vertex createVertex(StartElement startElement) {
        Attribute attribute = startElement.getAttributeByName(QName.valueOf("id"));
        return Vertex.valueOf(parseIntValueFrom(attribute));
    }

    private void validate(String fileName) throws XmlGraphParserStrategyException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(new File(XSD_FILE_NAME));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(fileName));
        } catch (SAXException | IOException e) {
            throw new XmlGraphParserStrategyException(e);
        }
    }

    @Override
    public MutableValueGraph<Vertex, Edge> getValueGraph() {
        return valueGraph;
    }
}
