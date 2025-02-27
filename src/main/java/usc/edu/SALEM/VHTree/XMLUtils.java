package usc.edu.SALEM.VHTree;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gatech.xpert.dom.DomNode;
import usc.edu.SALEM.SALEM;
import org.w3c.dom.*;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;

public class XMLUtils {

    private static final String rootTag = "";
    static String ANDROID_NS = "http://schemas.android.com/apk/res/android";
//    static List<String> verboseAttrList = Arrays.asList("checked", "enabled", "focused", "focusable", "scrollable",
//            "index", "NAF", "selected", "long-clickable", "checkable", "clickable", "content-desc", "focusable",
//            "password", "scrollable"); // the attrs that are can removed in dynamic layout xml files
//    static List<String> optionalAttrList = Arrays.asList("text", "resource-id");
//    static List<String> ignoreAttrList = Arrays.asList("text", "bounds", "checked", "enabled", "focused", "focusable",
//            "scrollable", "index", "NAF", "selected"); // the attrs that are ignore when comparing XML equivalence
    public static Node<DomNode> root;  //Ali
    private static XMLUtils instance;
    private static String filePath = "";
//    private static boolean calculateTouchTargetsNumbers;
//    private static int numberOfNonTouchTargets = 0;
//    private static int numberOfTouchTargets = 0;


    // For dynamic results, append color information to the views.
    // case 1: 1 xml layout has 1 corresponding screenshot
    // case 2: several xml layouts are identical but their screenshots are different
//    public static void appendColorInformation (String xmlFile, Set<String> pngSet) {
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        DocumentBuilder db;
//        String xmlFolderPath = xmlFile.substring(0, xmlFile.lastIndexOf(File.separator) + 1);
//
//        try {
//            db = dbf.newDocumentBuilder();
//            Document doc = db.parse(xmlFile);
//            Element root = doc.getDocumentElement();
//            Queue<Element> queue = new LinkedList<>();
//
//            for (String png : pngSet) {
//                // current png file
//                String screenFilePath = xmlFolderPath + png;
//                queue.offer(root);
//                // BFS traverse
//                while (!queue.isEmpty()) {
//                    Element node = queue.poll();
//                    String nodeName = node.getNodeName();
//                    if ("#text".equals(nodeName) || "#comment".equals(nodeName)) {
//                        continue;
//                    }
//                    // remove verbose attributes
//                    for (String attr : verboseAttrList) {
//                        if (node.hasAttribute(attr)) {
//                            node.removeAttribute(attr);
//                        }
//                    }
//                    for (String attr : optionalAttrList) {
//                        if(node.getAttribute(attr).equals("")) {
//                            node.removeAttribute(attr);
//                        }
//                    }
//
//                    String nodeBounds = node.getAttribute("bounds");
//                    List<String> childBounds = Lists.newArrayList();
//                    NodeList children = node.getChildNodes();
//                    // add children
//                    for (int i = 0; i < children.getLength(); ++i) {
//                        Element childNode = (Element) children.item(i);
//                        childBounds.add(childNode.getAttributes().getNamedItem("bounds").getTextContent());
//                        queue.offer(childNode);
//                    }
//                    if (nodeName.contains("ImageView") || nodeName.contains("MapView"))
//                        continue;
//                    System.out.println(nodeBounds + "\t" + childBounds);
//                    String bgColor = edu.usc.sql.merge.ColorPicker.getEffectBgFromScreenshot(screenFilePath, nodeBounds, childBounds);
//                    String oldBgColor = node.getAttribute("background");
//                    String newColorValue;
//                    if (!bgColor.equals("") && !oldBgColor.contains(bgColor)) {
//                        newColorValue = oldBgColor.equals("") ? bgColor : oldBgColor + "|" + bgColor;
//                        node.setAttribute("background", newColorValue);
//                    }
//                    String textColor = edu.usc.sql.merge.ColorPicker.getTextColorBasedOnColorDistance(node.getNodeName());
//                    String oldTextColor = node.getAttribute("textColor");
//                    if (!textColor.equals("") && !oldTextColor.contains(textColor)) {
//                        newColorValue = oldTextColor.equals("") ? textColor : oldBgColor + "|" + textColor;
//                        node.setAttribute("textColor", newColorValue);
//                    }
//                }
//            }
//
//            // write the content into xml file
//            TransformerFactory transformerFactory = TransformerFactory.newInstance();
//            Transformer transformer = transformerFactory.newTransformer();
//            DOMSource source = new DOMSource(doc);
////            String newFileName = file.substring(file.indexOf("_") + 1);
////            while (newFileName.contains("layout")) {
////                newFileName = newFileName.substring(newFileName.indexOf("_") + 1);
////            }
////            String newFile = folderPath + File.separator + newFileName;
//            StreamResult result = new StreamResult(new File(xmlFile));
//            transformer.transform(source, result);
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (TransformerConfigurationException e) {
//            e.printStackTrace();
//        } catch (TransformerException e) {
//            e.printStackTrace();
//        }
//
//    }


    private XMLUtils(String filePath) {
        XMLUtils.filePath = filePath;
        XMLUtils.root = null;
    }

    public static XMLUtils getInstance(String filePath) {
        if (instance == null || !XMLUtils.filePath.equalsIgnoreCase(filePath)) {
            instance = new XMLUtils(filePath);
//            numberOfTouchTargets = 0;
//            numberOfNonTouchTargets = 0;
            if (SALEM.DYNAMIC_LAYOUT_APPROACH.equalsIgnoreCase("sasha")) {
                readXML_S2(filePath);
            } else {
                readXML_T(filePath);
            }
        }
        return instance;
    }

    public static Node<DomNode> getRoot() {
        if (root == null || !XMLUtils.filePath.equalsIgnoreCase(filePath)) {
            instance = new XMLUtils(filePath);
//            numberOfTouchTargets = 0;
//            numberOfNonTouchTargets = 0;
            readXML_T(filePath);
        }
        return root;
    }

    public static DomNode buildTree(String xmlFile) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        DomNode rootNode = null;

        try {
            db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlFile);
            org.w3c.dom.Element root = doc.getDocumentElement();
            Queue<org.w3c.dom.Node> queue = new LinkedList<>();
            queue.offer(root);

            Map<org.w3c.dom.Node, DomNode> xml2dom = Maps.newHashMap();
            // BFS traverse
            while (!queue.isEmpty()) {
                org.w3c.dom.Node node = queue.poll();
                String nodeName = node.getNodeName();
                if ("#text".equals(nodeName) || "#comment".equals(nodeName)) {
                    continue;
                }
                DomNode domNode = new DomNode(node, getXPath(node));
                String nodeId = extractId(node);
//                domNode.setAttr("index",node.get);
                if (nodeId != null && !nodeId.isEmpty()) {
                    domNode.setId(nodeId);
                }
                org.w3c.dom.Element parent = null;
                if (!node.equals(root)) {
                    parent = (org.w3c.dom.Element) node.getParentNode();
                }
                if (parent != null) {
                    DomNode parentDomNode = xml2dom.get(parent);
                    parentDomNode.addChild(domNode);
                } else { // record rootDomNode
                    rootNode = domNode;
                }
                xml2dom.put(node, domNode);
                NodeList children = node.getChildNodes();
                for (int i = 0; i < children.getLength(); ++i) {
                    queue.add(children.item(i));
                }
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return rootNode;
        }
        //    return rootNode;
    }

    public static Node<DomNode> readCrawledXML_T(String file) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Node<DomNode> rootNode = null;
        // DomNode rootNode = null;

        try {
            db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            org.w3c.dom.Element root = doc.getDocumentElement();
//            rootTag = root.getTagName();
            List<Node> nodeList = new ArrayList<>();
//            calculateTouchTargetsNumbers = false;  // it is for crawled layout to update the bounds so no need to recalculate TT numbers
            traverseDFS(root, nodeList, null);


//            if (readType.equalsIgnoreCase("new")) // reading layout first time so no need to recalculate  number of TT vies
//            {
//
//            }else if(readType.equalsIgnoreCase("update")){  need to calculate TT numbers again
//
//            }
            String screenFilePath = file.replace("xml", "png");
            File screenFile = new File(screenFilePath);
            if (screenFile.exists()) { // if dynamic XML output
                for (Node<DomNode> node : nodeList) {
                    List<String> childBounds = Lists.newArrayList();
                    if (node.getChildren() != null) {
                        for (Node<DomNode> ch : node.getChildren()) {
                            childBounds.add(ch.getData().getAttr("bounds"));
                        }
                    }

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

        return rootNode;
    }

    private static Node<DomNode> readXML_S2(String file) {
        root = readXML_T(file);
        if (root==null){
            return null;
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);
//        dbf.setValidating(true);
        dbf.setIgnoringComments(true);

        DocumentBuilder db;
        Node<DomNode> rootNode = null;
        // DomNode rootNode = null;
        Document doc = null;
        org.w3c.dom.Element newRoot = null;

        try {

            db = dbf.newDocumentBuilder();

            try (InputStream stream = new FileInputStream(file)) {
                doc = db.parse(stream);
//                doc.normalizeDocument();
                doc.normalize();

            }
//            Document doc = db.parse(file);

            org.w3c.dom.Element root = doc.getDocumentElement();
            int length = root.getChildNodes().getLength();
            for (int i = 0; i < length; i++) {
                org.w3c.dom.Node child = root.getChildNodes().item(i);
                String nodeName = child.getNodeName();

//                String nodeName= child.getAttributes().getNamedItem("title").getTextContent();
                if (nodeName.equalsIgnoreCase("#text") || nodeName.equalsIgnoreCase("#comment")) {
                    continue;
                }
                newRoot = (org.w3c.dom.Element) child;
                NamedNodeMap d = child.getAttributes();
//                String title= d.getNamedItem("title").getTextContent();
                String act = SALEM.getCurrentActivityName();

                int x = 0;

            }


        } catch (ParserConfigurationException pce) {
            System.err.println(pce);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        return root;
    }

    private static Node<DomNode> readXML_S(String file) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setValidating(true);
        dbf.setIgnoringComments(true);

        DocumentBuilder db;
        Node<DomNode> rootNode = null;
        // DomNode rootNode = null;
        Document doc = null;

        try {

            db = dbf.newDocumentBuilder();

            try (InputStream stream = new FileInputStream(file)) {
                doc = db.parse(stream);
//                doc.normalizeDocument();
                doc.normalize();

            }
//            Document doc = db.parse(file);
            org.w3c.dom.Node newRoot = null;

            org.w3c.dom.Element root = doc.getDocumentElement();
            int length = root.getChildNodes().getLength();
            for (int i = 0; i < length; i++) {
                org.w3c.dom.Node child = root.getChildNodes().item(i);
                String nodeName = child.getNodeName();

//                String nodeName= child.getAttributes().getNamedItem("title").getTextContent();
                if (nodeName.equalsIgnoreCase("#text") || nodeName.equalsIgnoreCase("#comment")) {
                    continue;
                }
                NamedNodeMap d = child.getAttributes();
                String title = d.getNamedItem("title").getTextContent();
                String act = SALEM.getCurrentActivityName();

                if (title.contains(SALEM.getCurrentActivityName())) {

                    while (true) {
                        if (newRoot != null && !newRoot.getNodeName().equalsIgnoreCase("#text")) {
                            String id = newRoot.getAttributes().getNamedItem("id").getTextContent();
                            if (id.equalsIgnoreCase("id/content")) {
                                break;
                            }
                        }
                        newRoot = child.getFirstChild();
                        if (newRoot.getNodeName().equalsIgnoreCase("#text")) {
                            newRoot = newRoot.getNextSibling();
                            continue;
                        }
                        newRoot = newRoot.getFirstChild();
                        //    newRoot=newRoot.getFirstChild();


                    }


                }
                int x = 0;

            }


        } catch (ParserConfigurationException pce) {
            System.err.println(pce);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // readXML_T(file);


        Node<DomNode> newRoot = null;
        for (Node<DomNode> node : root.getChildren()) {
            String xpath = node.getData().getxPath();
//            System.out.println(node);
            if (node.getData().getAttr("title").contains(SALEM.getCurrentActivityName())) {
//                System.out.println(node);
                newRoot = node;
                node = node.getChildren().get(0);
                node = node.getChildren().get(0);


                for (Node<DomNode> ch : node.getChildren()) {
                    String id = ch.getData().getAttr("id");
                    if (id.equalsIgnoreCase("id/content")) {
                        System.out.println(id);
                        newRoot = ch;
                    }
                }
            }
        }
        root = newRoot;

        return newRoot;
    }

    private static Node<DomNode> readXML_T(String file) {
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
            org.w3c.dom.Element root = doc.getDocumentElement();
//            rootTag = root.getTagName();
            List<Node> nodeList = new ArrayList<>();
//            if (numberOfTouchTargets == 0 && numberOfNonTouchTargets == 0) {
//                // reading layout for first time so we need to calculate  number of TT views
//
//                calculateTouchTargetsNumbers = true;
//            } else {
//                // just reading to update layout bounds so no
//                calculateTouchTargetsNumbers = false;
//            }
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
//                    System.out.println(bounds + "\t" + childBounds);
                    //       String bgColor = edu.usc.sql.merge.ColorPicker.getEffectBgFromScreenshot(screenFilePath, bounds, childBounds);
                    //     node.setAttr("background", bgColor);
                    //       String textColor = edu.usc.sql.merge.ColorPicker.getTextColorBasedOnColorDistance(node.getTagName());
                    //       if (!textColor.equals("")) {
                    //           node.setAttr("textColor", textColor);
                    //       }
                }
            }
            rootNode = nodeList.get(0);

        } catch (ParserConfigurationException pce) {
            System.err.println(pce);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            root = rootNode;
            //  rootNode;
            return rootNode;
        }
//        root = rootNode;
//        //  rootNode;
//        return rootNode;
    }


    public static DomNode readXML(String file) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        DomNode rootNode = null;

        try {
            db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            org.w3c.dom.Element root = doc.getDocumentElement();
//            rootTag = root.getTagName();
            List<DomNode> nodeList = new ArrayList<>();
            traverseDFS0(root, nodeList, null);

            String screenFilePath = file.replace("xml", "png");
            File screenFile = new File(screenFilePath);
            if (screenFile.exists()) { // if dynamic XML output
                // Add effective background color from screenshot
                for (DomNode node : nodeList) {
                    //     edu.usc.sql.merge.ColorPicker.reset();
                    List<String> childBounds = Lists.newArrayList();
                    for (DomNode ch : node.getChildren()) {
                        childBounds.add(ch.getAttr("bounds"));
                    }
                    String bounds = node.getAttr("bounds");
//                    System.out.println(bounds + "\t" + childBounds);
                    //       String bgColor = edu.usc.sql.merge.ColorPicker.getEffectBgFromScreenshot(screenFilePath, bounds, childBounds);
                    //     node.setAttr("background", bgColor);
                    //       String textColor = edu.usc.sql.merge.ColorPicker.getTextColorBasedOnColorDistance(node.getTagName());
                    //       if (!textColor.equals("")) {
                    //           node.setAttr("textColor", textColor);
                    //       }
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
        root = new Node<DomNode>(null, rootNode);
        //  rootNode;
        return rootNode;
    }

    static void traverseDFS0(org.w3c.dom.Node node, List<DomNode> nodeList, DomNode parent) {
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
        if (parent != null) {
            parent.addChild(domNode);
        }
        nodeList.add(domNode);

        if (node.hasChildNodes()) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                org.w3c.dom.Node newNode = children.item(i);
                traverseDFS0(newNode, nodeList, domNode);
            }
        }
    }

//    public static int getNumberOfTouchTargets() {
//        return numberOfTouchTargets;
//    }
//
//    public static void setNumberOfTouchTargets(int numberOfTouchTargets) {
//        XMLUtils.numberOfTouchTargets = numberOfTouchTargets;
//    }

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

    static String extractId(DomNode node) {
        String nodeId = null;
        Map<String, String> nnm = node.getAttributes();
        String idNode = nnm.get("resource-id");
        if (idNode == null) {
            idNode = nnm.get("android:id");
        }
        if (idNode != null) {
//            nodeId = idNode.getTextContent();
            nodeId = idNode.substring(idNode.lastIndexOf("/") + 1);
        }
        return nodeId;
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
            //Ali OwlEye addition
            if (tagName.equalsIgnoreCase("node")) {
                tagName = node.getAttributes().getNamedItem("class").getTextContent();
            }
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

//    public static String getXPathInSameFile(Node node) {
//        String nodeId = extractId(node);
//        // This case assumes rootTag appears only once in the XML
////        if (node.getNodeName().equals(rootTag)) {
////            return "/" + rootTag + "[1]";
////        }
//        String nodeOrigin = node.getAttributes().getNamedItem("origin").getTextContent();
//        ArrayList<String> paths = new ArrayList<>();
//        while (true) {
//            int index = 0;
//
//            // Use id directly
//            if (nodeId != null) {
//                nodeId = nodeId.substring(nodeId.lastIndexOf("/") + 1);
//                if (!nodeId.isEmpty()) {
//                    paths.add("/*[@id=\"" + nodeId + "\"]");
//                    break;
//                }
//            }
//
//            for (Node sibling = node.getPreviousSibling(); sibling != null; sibling = sibling.getPreviousSibling()) {
//                if (sibling.getNodeName().equals(node.getNodeName()) && sibling.getAttributes().getNamedItem("origin").getTextContent().equals(nodeOrigin)) {
//                    ++index;
//                }
//            }
//            String tagName = node.getNodeName();
//            String pathIndex = "[" + (index + 1) + "]";
//            paths.add(tagName + pathIndex);
//            Node parent = node.getParentNode();
//            if (!(parent instanceof Document) && parent.getAttributes().getNamedItem("origin").getTextContent().equals(nodeOrigin)) {
//                node = parent;
//            } else {
//                break;
//            }
//        }
//
//        String result = null;
//        if (paths.size() > 0) {
//            result = "/";
//            for (int i = paths.size() - 1; i > 0; i--) {
//                result = result + paths.get(i) + "/";
//            }
//            result = result + paths.get(0);
//        }
//
//        return result;
//    }

    public static boolean extractActivityLayout(String file, String folderPath) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;

        try {
            db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            org.w3c.dom.Element root = doc.getDocumentElement(); // <hierarchy>
            //doc.getDocumentElement().normalize();
            // doc.getDocumentElement().normalize();
//            System.out.println("Root: " + root.getNodeName());

            NodeList nodeList = doc.getElementsByTagName("node");

            org.w3c.dom.Node contentFrameLayout = null;
            for (int i = 0; i < nodeList.getLength(); i++) {
                org.w3c.dom.Node node = nodeList.item(i);
                if (node.getAttributes().getNamedItem("class").getTextContent().equals("android.widget.FrameLayout")) {
                    String id = node.getAttributes().getNamedItem("resource-id").getTextContent();
                    if (id.equals("android:id/content")) {
                        contentFrameLayout = node;
                        break;
                    }
                }
            }

            if (contentFrameLayout == null) {
                System.out.println("Cannot find the Content FrameLayout! Failed to handle " + file);
                return false;
            }
            String borderBound = contentFrameLayout.getAttributes().getNamedItem("bounds").getTextContent();
//            System.out.println(borderBound);

            // Keep the root node, first remove the root's child nodes
            /*NodeList rootChildren = root.getChildNodes();
            for (int i = 0; i < rootChildren.getLength(); i++) {
                Node n = rootChildren.item(i);
                root.removeChild(n);
            }*/
            contentFrameLayout.normalize();
            NodeList contentChildren = contentFrameLayout.getChildNodes();
            int correct_child_index = 0;
            int correct_number_of_children = contentChildren.getLength();
            for (int i = 0; i < contentChildren.getLength(); i++) {
                org.w3c.dom.Node current = contentChildren.item(i);
                if ("#text".equals(current.getNodeName()) || "#comment".equals(current.getNodeName())) {
                    correct_number_of_children--;
                } else {
                    correct_child_index = i;
                }
            }
            // Attach the real content to the root node
            /*for (int i = 0; i < contentChildren.getLength(); i++) {
                Node n = contentChildren.item(i);

                root.appendChild(n);
                // replace the tag name from "node" to its className, and remove the "class" attribute
                changeNodeName(n, doc);
            }*/
            int size = contentChildren.getLength();
//            System.out.println("Content Childs: " + contentChildren.item(0));
            // if (contentChildren.getLength() == 1) {  // instead of getting the length directly I am looking for the correct no children
            if (correct_number_of_children == 1) {
                org.w3c.dom.Node n = contentChildren.item(correct_child_index);  // correct_child_index to account for #text #comment
                String rootId = n.getAttributes().getNamedItem("resource-id").getTextContent();
                //Oct 17 2022 to fix inifini app which is a dialog
//                if (rootId.contains(":id/parentPanel")) {
//                    // Capture a alertDialog
//                    return false;
//                }
                // Now the real content root is the new root node
                root.getParentNode().replaceChild(n, root);
                // replace the tag name from "node" to its className, and remove the "class" attribute
                changeNodeName(n, doc);
            }


            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            //    String newFileName = file.substring(file.indexOf("_") + 1);
            //Ali
            String newFileName = file;
//            while (newFileName.contains("layout")) {
//                newFileName = newFileName.substring(newFileName.indexOf("_") + 1);
//            }
            // special for org.vi_server.red_screen_2
//            newFileName = newFileName.substring(newFileName.indexOf("_") + 1);
//            newFileName = newFileName.substring(newFileName.indexOf("_") + 1);
            //String lastIndex = newFileName.substring(newFileName.lastIndexOf("/") + 1);
            String newFile = folderPath + newFileName.substring(newFileName.lastIndexOf("/") + 1);

//            String newFile = folderPath + newFileName.substring(newFileName.lastIndexOf("/")+1,newFileName.length());
            //String newFile = newFileName;

            StreamResult result = new StreamResult(new File(newFile));
            transformer.transform(source, result);

//            System.out.println("Done");

        } catch (ParserConfigurationException pce) {
            System.err.println(pce);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return true;
    }


    private static void changeNodeName(org.w3c.dom.Node node, Document doc) {
        if (node.getNodeName().equals("node")) {
            NamedNodeMap attrs = node.getAttributes();
            String className = attrs.getNamedItem("class").getTextContent();
            attrs.removeNamedItem("class");
            // omit the package name "android.widget."
            if (className.startsWith("android.widget."))
                className = className.substring(15);
            className = className.replace("$", ".");
            if (className.contains("WebView") || className.contains("AdView")) { //if it is webview or AdView, then just delete its children
                int numberofChildren = node.getChildNodes().getLength();
                for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                    node.removeChild(node.getChildNodes().item(i));

                }

            }

            if (className.contains("ListView")) {
                /*** If it is a list view we will check if
                 * (1)it has more than one child
                 * (2) if the children are of same class and same Id
                 * THAT means they are duplicate so we can trim them and use only one
                 */
                NodeList listChList = node.getChildNodes();
                boolean sameClass = true;
                boolean sameID = true;
                String previousClass = "";
                String listChildClassName = "";
                String previousID = "";
                String listChildID = "";
                int originalLength = listChList.getLength();
                for (int i = 0; i < originalLength; ++i) {
                    org.w3c.dom.Node child = listChList.item(i);

                    if (previousClass != "" && listChildClassName != "") {
                        if (!listChildClassName.equalsIgnoreCase(previousClass)) {
                            sameClass = false;
                        } else {
                            listChildClassName = child.getAttributes().getNamedItem("class").getTextContent();
                            previousClass = listChildClassName;

                        }
                    } else {
                        previousClass = listChildClassName;
                        listChildClassName = child.getAttributes().getNamedItem("class").getTextContent();
                    }
                    if (previousID != "" && listChildID != "") {
                        if (!listChildID.equalsIgnoreCase(previousID)) {
                            sameID = false;
                        } else {
                            listChildID = child.getAttributes().getNamedItem("resource-id").getTextContent();
                            previousID = listChildID;
//                            node.removeChild(child);


                        }
                    } else {
                        previousID = listChildID;
                        listChildID = child.getAttributes().getNamedItem("resource-id").getTextContent();
                    }

                }
                if (sameID && sameClass && listChildClassName != "" && listChildID != "") {
                    //That mean all children are identical so we just keep one
//                    Node firstChild = node.getFirstChild();
//                    Node clonedFirstChild=node.cloneNode(true);
//                    for (int i = 0; i < originalLength; i++) {
//                        if(i==0){
//                            continue; // k the first element
//                        }
//                        node.removeChild(node.getChildNodes().item(i));
//
//                    }
                    org.w3c.dom.Node fChild = node.getFirstChild();
                    while (fChild.getNextSibling() != null) {
                        node.removeChild(fChild.getNextSibling());

                    }
//                    for (int i = 0; i < node.getChildNodes().getLength(); i++) {
//
//                        node.removeChild(node.getChildNodes().item(i));
//
//                    }

                }

            }

            doc.renameNode(node, null, className);

            NodeList chList = node.getChildNodes();
            int newChildList = chList.getLength();
            for (int i = 0; i < chList.getLength(); ++i) {
                org.w3c.dom.Node child = chList.item(i);
                changeNodeName(child, doc);
            }
        }
    }

    public static void dump(DomNode root, String xmlPath) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            Document doc = factory.newDocumentBuilder().newDocument();
            dumpTree(root, null, doc);
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.setOutputProperty(OutputKeys.METHOD, "xml");
            tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            if (doc != null) {
                DOMSource domSource = new DOMSource(doc);
                StreamResult sr = new StreamResult(new File(xmlPath));
                tf.transform(domSource, sr);
            }
        } catch (TransformerException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    static void dumpTree(DomNode node, org.w3c.dom.Element parent, Document doc) {
        Element e = doc.createElement(node.getTagName());
        Map<String, String> attrs = node.getAttributes();
        for (Map.Entry<String, String> entry : attrs.entrySet()) {
            e.setAttribute(entry.getKey(), entry.getValue());
        }

        if (parent == null) {
            e.setAttribute("xmlns:android", ANDROID_NS);
            doc.appendChild(e);
        } else {
            parent.appendChild(e);
        }

        for (DomNode ch : node.getChildren()) {
            dumpTree(ch, e, doc);
        }
    }


    public static Node<DomNode> searchByID_T(String id, String className) {
        Queue<Node<DomNode>> q = new LinkedList<>();
        q.add(getRoot());

        while (!q.isEmpty()) {
            Node<DomNode> node = q.remove();
            String node_id = XMLUtils.extractId(node.getData());
            if (node_id != null && node_id != "") {  // Ali node does not have an id
                if (compareIds(id, node.getData(), className)) {
                    return node;
                }
            }

            if (node.getChildren() != null) {
                for (Node<DomNode> child : node.getChildren()) {
                    q.add(child);
                }
            }
        }
        return null;
    }

    public static boolean compareIds(String issueID, DomNode node, String classWidget) {

//            if (temp.getAttribute(id_key).equalsIgnoreCase("btn_background")) {
//                main.logger.info("btn_background FOUND");
////            System.exit(0);
//            }
        String node_id = XMLUtils.extractId(node);
//            int ind = node_id.lastIndexOf('/');
//            node_id = node_id.substring(ind + 1, node_id.length());


//            int ind = issueID.lastIndexOf('/');
        issueID = issueID.substring(issueID.lastIndexOf("/") + 1);

//        issueID = issueID.substring(ind + 1, issueID.length());

        if (issueID.trim().equalsIgnoreCase(node_id.trim())) {
            if (classWidget != null) {
                String classTemp = node.getAttr("class");
                if (classTemp == null) {
                    classTemp = node.getTagName();
                }
                classTemp = classTemp.substring(classTemp.lastIndexOf(".") + 1);
                //ToDO: This is not accurate especially if it is using special class
                //                        found = true;
                //                        found_node = temp;
                return classWidget.trim().toLowerCase().contains(classTemp.trim().toLowerCase());
            } else {
//                    main.logger.info("found id match");
//                    found = true;
//                    found_node = temp;
                return true;
            }
        }


        return false;
    }



    //Ali copied from mf'S HTMLDomTree class
    public static DomNode searchVHTreeByXpath(String xpath, DomNode root) {
        Queue<DomNode> q = new LinkedList<DomNode>();
        q.add(root);

        while (!q.isEmpty()) {
            DomNode node = q.remove();
            if (node.getxPath().equalsIgnoreCase(xpath)) {
                return node;
            }
            if (node.getChildren() != null) {
                for (DomNode child : node.getChildren()) {
                    q.add(child);
                }
            }
        }
        return null;
    }

    //Ali copied from mf"S HTMLDomTree class
    public static Node<DomNode> searchVHTreeByXpath(String xpath, Node<DomNode> root) {
        Queue<Node<DomNode>> q = new LinkedList<Node<DomNode>>();
        q.add(root);

        while (!q.isEmpty()) {
            Node<DomNode> node = q.remove();
            if (node.getData().getxPath().equalsIgnoreCase(xpath)) {
                return node;
            }
            if (node.getChildren() != null) {
                for (Node<DomNode> child : node.getChildren()) {
                    q.add(child);
                }
            }
        }
        return null;
    }

    public static void resetInstance() {
        instance = null;
        filePath = "";
        root = null;
//        numberOfTouchTargets = 0;
//        numberOfNonTouchTargets = 0;

    }

    public static ArrayList<Node<DomNode>> getAllTextNodes(Node<DomNode> rootNode){
        ArrayList<Node<DomNode>> textNodes = new ArrayList<>();
        Queue<Node<DomNode>> q = new LinkedList<>();
        q.add(rootNode);

        while(!q.isEmpty()){
            Node<DomNode> node = q.remove();
            node.getData().getAttributes();
            //System.out.println("Attributes: "+ node.getData().getAttributes());
            //System.out.println("text:::"+ node.getData().getAttributes().get("text"));
            if(node.getData().getAttributes().get("text") != null && node.getData().getAttributes().get("text").length() > 0){
                textNodes.add(node);
            }
            if(node.getChildren() != null){
                for(Node<DomNode> child : node.getChildren()){
                    q.add(child);
                }
            }
        }
        return textNodes;
    }


}
