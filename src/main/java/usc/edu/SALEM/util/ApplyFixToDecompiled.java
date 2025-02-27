package usc.edu.SALEM.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class ApplyFixToDecompiled {

    static Element root;
    static DocumentBuilderFactory dbf;
    static DocumentBuilder db;

    public static Element readStaticXML(File file) {
        dbf = DocumentBuilderFactory.newInstance();
        db = null;

        try {
            db = dbf.newDocumentBuilder();
            Document dom = db.parse(file.getAbsoluteFile());
            Element docEle = dom.getDocumentElement();
            root = docEle;
            return docEle;
        } catch (ParserConfigurationException pce) {
            System.err.println(pce);
        } catch (SAXException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Element searchForNode(Element root, String dynamicID, boolean searchRootOnly) {
        if (searchRootOnly) {
            Element found = findStaticNodeByID(dynamicID, root);
            // We only search for the root because searchRootOnly
            return found;
        }
        Queue<Element> q = new LinkedList<Element>();
        q.add(root);

//        // process descendants of the root in a bread first fashion
        while (!q.isEmpty()) {
            Element currElement = q.remove();
            Element found = findStaticNodeByID(dynamicID, currElement);
            if (found != null) {
                return found;
            }


            if (currElement.getChildNodes() != null) {
                int length = currElement.getChildNodes().getLength();
                for (int i = 0; i < length; i++) {
                    if (currElement.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element child = (Element) currElement.getChildNodes().item(i);
                        q.add(child);
                    }
                }
            }
        }
        return null;
    }

    private static Element findStaticNodeByID(String dynamicID, Element el) {
        String static_id = el.getAttribute("android:id");
        if (static_id != null) {
            static_id = static_id.substring(static_id.lastIndexOf("/") + 1);
            if (dynamicID.equalsIgnoreCase(static_id)) {
                return el;
            }
        }
        return null;
    }


    public static void writestaticXML(Element staticFileRoot, String newFile) {

        try {

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(staticFileRoot);
//            String newFile = pathToFolder +File.separator+
//                    file.substring(file.indexOf("_") + 1);

            System.out.println("XML written to " + newFile);
            StreamResult result = new StreamResult(new File(newFile));
            transformer.transform(source, result);

//        } catch (ParserConfigurationException pce) {
//            System.err.println(pce);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }


}
