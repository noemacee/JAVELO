package ch.epfl.javelo.routing;


import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 * This class takes care of the creation of a GPX document, describing our itinerary
 *
 * @author NoeMace 341504
 * @author Mathis Richard 346419
 */
public final class GpxGenerator {

    private GpxGenerator() {
    }

    /**
     * Creates a GPX of a route with its profile
     *
     * @param route   : the wanted route to change into GPX
     * @param profile : profile of the route
     * @return a GPX Document
     */

    public static Document createGpx(Route route, ElevationProfile profile) {

        double position = 0;

        Iterator<Edge> itr = route.edges().iterator();

        Document doc = newDocument();

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element rte = doc.createElement("rte");
        root.appendChild(rte);


        for (PointCh p : route.points()) {

            Element rtept = doc.createElement("rtept");
            Element ele = doc.createElement("ele");

            rtept.setAttribute("lat", Double.toString(Math.toDegrees(p.lat())));
            rtept.setAttribute("lon", Double.toString(Math.toDegrees(p.lon())));

            ele.setTextContent(Double.toString(profile.elevationAt(position)));

            while (itr.hasNext()) {
                position += itr.next().length();
            }

            rtept.appendChild(ele);
            rte.appendChild(rtept);
        }
        return doc;
    }

    /**
     * Writes the GPX in a File
     *
     * @param folder  : name of the file
     * @param route   : route we want written in GPX
     * @param profile : profile of the route
     * @throws Error in case of an input or output error
     */
    public static void writeGPX(String folder, Route route, ElevationProfile profile) throws Error {

        Document doc = createGpx(route, profile);

        try {
            Writer w = new FileWriter(folder);
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));

        } catch (TransformerException | IOException ioE) {
            throw new Error(ioE);
        }

    }

    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e);
        }
    }
}
