package org.example.graph.algorithm.exporter.factory.impl;

import org.example.Constants;
import org.example.graph.algorithm.exporter.GraphExporter;
import org.example.graph.algorithm.exporter.factory.GraphExporterFactory;
import org.example.graph.algorithm.exporter.factory.exception.GraphExporterFactoryImplException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static org.example.Constants.OUTPUT_FILE_NAME;

public class GraphExporterFactoryImpl extends GraphExporterFactory {

    private final String fileName;

    public GraphExporterFactoryImpl() throws GraphExporterFactoryImplException {
        Properties props = new Properties();
        try {
            props.load(new FileReader(Constants.APP_PROPS_FILE_NAME));
            this.fileName = props.getProperty(OUTPUT_FILE_NAME);
        } catch (IOException e) {
            throw new GraphExporterFactoryImplException(e);
        }
    }

    @Override
    public GraphExporter newGraphExporter() {
        return new GraphExporter(fileName);
    }
}
