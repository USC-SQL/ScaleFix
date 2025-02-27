package usc.edu.OwlEye.VH;

import com.google.common.collect.Lists;
import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import usc.edu.OwlEye.VHTree.XMLUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UI2 {

    String UITitle;
    public AndroidNode root;
    XMLUtils XMLTree;
    String xmlFilePath;
    String pngFilePath;



    public UI2(String filePath) {
        filePath = filePath;
        root = null;
        readXML_T(filePath); //  if (SALEM.DYNAMIC_LAYOUT_APPROACH.equalsIgnoreCase("sasha")) {
    }



    private Node<DomNode> readXML_T(String file) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Node<DomNode> rootNode = null;
        // DomNode rootNode = null;
        Document doc = null;

        try {

            db = dbf.newDocumentBuilder();
            try (InputStream stream = new FileInputStream(file)) {
                doc = db.parse(stream);
            }
//            Document doc = db.parse(file);
            Element root = doc.getDocumentElement();
//            rootTag = root.getTagName();
            List<Node> nodeList = new ArrayList<>();

            traverseDFS(root, nodeList, null);

            String screenFilePath = file.replace("xml", "png");
            File screenFile = new File(screenFilePath);
            if (screenFile.exists()) { // if dynamic XML output
                // Add effective background color from screenshot
                for (Node<DomNode> node : nodeList) {
                    //     edu.usc.sql.merge.ColorPicker.reset();
                    List<String> childBounds = Lists.newArrayList();
                    if (node.getChildren() != null) {
                        for (Node<DomNode> ch : node.getChildren()) {
                            childBounds.add(ch.getData().getAttr("bounds"));
                        }
                    }
                    String bounds = node.getData().getAttr("bounds");

                }
            }
            rootNode = nodeList.get(0);

        } catch (ParserConfigurationException pce) {
            System.err.println(pce);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        root = new AndroidNode( rootNode);
        //  rootNode;
        return rootNode;
    }

    static void traverseDFS(org.w3c.dom.Node node, List<Node> nodeList, Node parent) {
        String nodeName = node.getNodeName();
        if ("#text".equals(nodeName) || "#comment".equals(nodeName)) {
            return;
        }

        DomNode domNode = new DomNode(node, getXPath(node));
        String nodeId = extractId(node);
        if (nodeId != null && !nodeId.isEmpty()) {
            domNode.setId(nodeId);
        }
        // Ali add rectangle similar to HtmlDom
        Rectangle coord = getNodeCoordinates(domNode);
//        if(coord ==null){
//        }
        domNode.setCoord(coord);
        Node<DomNode> newNodeT = null;
        if (parent != null) {
            newNodeT = new Node<DomNode>(parent, domNode);
//            parent.setParent();
//            parent.addChild(domNode);
        } else { // if parent is null (root?)
            newNodeT = new Node<DomNode>(null, domNode);
        }
//        if (calculateTouchTargetsNumbers) {  // if we need to count TT and non TT views
//            if (Util.isElementClickable(domNode)) {
//                numberOfTouchTargets++;
//            } else {
//                numberOfNonTouchTargets++;
//            }
//        }
        nodeList.add(newNodeT);

        if (node.hasChildNodes()) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                org.w3c.dom.Node newNode = children.item(i);
                traverseDFS(newNode, nodeList, newNodeT);
            }
        }
    }
    //Ali similar to HtmlDomTree
    public static Rectangle getNodeCoordinates(DomNode node) {
        String bounds = node.getAttr("bounds");
        if (bounds == null) {
            return null; // hierarchy
        }
        bounds = bounds.substring(1, bounds.length() - 1).replace("][", ",");
        String[] coord = bounds.split(",");
//        Rectangle coord = new Rectangle();
        int[] coords = {Integer.parseInt(coord[0]), Integer.parseInt(coord[1]), Integer.parseInt(coord[2]), Integer.parseInt(coord[3])};
        Rectangle rect = new Rectangle(coords[0], coords[1], (coords[2] - coords[0]), (coords[3] - coords[1]));
        return rect;
    }
    static String getXPath(org.w3c.dom.Node node) {

        // This case assumes rootTag appears only once in the XML
//        if (node.getNodeName().equals(rootTag)) {
//            return "/" + rootTag + "[1]";
//        }

        ArrayList<String> paths = new ArrayList<>();
        for (; !(node instanceof Document); node = node.getParentNode()) {
            int index = 0;
            String nodeId = extractId(node);
            // Use id directly
//            if (nodeId != null) {
//                nodeId = nodeId.substring(nodeId.lastIndexOf("/") + 1);
//                if (!nodeId.isEmpty()) {
//                    paths.add("/*[@id=\"" + nodeId + "\"]");
//                    break;
//                }
//            }

            for (org.w3c.dom.Node sibling = node.getPreviousSibling(); sibling != null; sibling = sibling.getPreviousSibling()) {
                if (sibling.getNodeName().equals(node.getNodeName())) {
                    ++index;
                }
            }
            String tagName = node.getNodeName();
            String pathIndex;
            if (nodeId != null) {
                nodeId = nodeId.substring(nodeId.lastIndexOf("/") + 1);
            } else {
                nodeId = "";
            }
            if (!nodeId.isEmpty()) {
                pathIndex = "[@id=\"" + nodeId + "\"]";
                //  paths.add("/*[@id=\"" + nodeId + "\"]");
                //break;
            } else {
                pathIndex = "[" + (index + 1) + "]";
            }
            paths.add(tagName + pathIndex);
        }


        String result = null;
        if (paths.size() > 0) {
            result = "/";
            for (int i = paths.size() - 1; i > 0; i--) {
                result = result + paths.get(i) + "/";
            }
            result = result + paths.get(0);
        }

        return result;
    }

    static String extractId(org.w3c.dom.Node node) {
        String nodeId = null;
        NamedNodeMap nnm = node.getAttributes();
        org.w3c.dom.Node idNode = nnm.getNamedItem("resource-id");
        if (idNode == null) {
            idNode = nnm.getNamedItem("android:id");
        }
        if (idNode != null) {
            nodeId = idNode.getTextContent();
            nodeId = nodeId.substring(nodeId.lastIndexOf("/") + 1);
        }
        return nodeId;
    }
}
