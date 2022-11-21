package org.example;

import javax.xml.stream.events.Attribute;

public class Utils {

    public static int parseIntValueFrom(Attribute attribute) {
        return Integer.parseInt(attribute.getValue());
    }

}
