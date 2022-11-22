package org.example.graph.algorithm.exporter.factory;

import org.example.graph.algorithm.exporter.GraphExporter;
import org.example.graph.algorithm.exporter.factory.exception.GraphExporterFactoryException;
import org.example.graph.algorithm.exporter.factory.impl.GraphExporterFactoryImpl;

public abstract class GraphExporterFactory {

    public static GraphExporterFactory newInstance() throws GraphExporterFactoryException {
        return new GraphExporterFactoryImpl();
    }

    public abstract GraphExporter newGraphExporter();

}
