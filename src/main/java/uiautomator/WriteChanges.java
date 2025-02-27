//package uiautomator;
//
////import main.main;
//
//import org.w3c.dom.*;
////import mfix.util.APPLayout;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import java.util.ArrayList;
//
//public class WriteChanges {
//    public ArrayList<Modification> genes;
//    public DocumentBuilderFactory dbf;
//    public DocumentBuilder db;
//
//    public Document doc;
//    private ArrayList<Modification> genes2;
//
////    public void writeXMLFile(String path,String fileName, ArrayList<Modification> genes){ //, Modification[] genes
////            this.genes=genes;
////        readXML(path,fileName);
////
////
////}
//
//    private void write(Node root, String indentation) {
//        NodeList nodes = root.getChildNodes();
//        System.out.println("Inside Write");
//        indentation += " ";
//        for (int index = 0; index < nodes.getLength(); index++) {
//            System.out.println("Ali" + indentation + nodes.item(index).getAttributes().getLength());
//            write(nodes.item(index), indentation);
////                UiNode temp = (UiNode) nodes[index];
////                if (temp.getAttribute("resource-id").equalsIgnoreCase("com.bytecode.aiodownloader:id/action_bar")) {
////                    main.logger.info("TEST");
////                }
//
//
//        }
//    }
//
////    public Node readXML(String path, String file) {
////         dbf = DocumentBuilderFactory.newInstance();
////         db = null;
////        Node rootNode = null;
////
////        try {
////            db = dbf.newDocumentBuilder();
////             doc = db.parse(path + File.separator+file);
////            Element root = doc.getDocumentElement(); // <hierarchy>
////
//////            rootTag = root.getTagName();
//////            System.out.println("rootTag1   1 "+ rootTag + "parent "+ root.getParentNode().getNodeValue());
////            List<Node> nodeList = new ArrayList<>();
////            System.out.println("File: "+ path + File.separator+file);
////            System.out.println("origianl root list "+ root.getChildNodes().getLength());
////            System.out.println("The root: "+ root.getNodeName());
////            System.out.println("The root: "+ root.getFirstChild().getLocalName());
////            if (doc.hasChildNodes()) {
////
////                printNote(doc.getChildNodes());
////
////            }
////
////
////            //        // write the content into xml file
////        TransformerFactory transformerFactory = TransformerFactory.newInstance();
////        Transformer transformer = transformerFactory.newTransformer();
////        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
////        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
////        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
////        DOMSource source = new DOMSource(doc);
//////            String newFile = pathToFolder +File.separator+
//////                    file.substring(file.indexOf("_") + 1);
////        String newFile = path+File.separator+
////                "activity_main.xml";
////        System.out.println("XML written to "+ newFile);
////        StreamResult result = new StreamResult(new File(newFile));
////        transformer.transform(source, result);
////
////        } catch (ParserConfigurationException pce) {
////            System.err.println(pce);
////        } catch (SAXException e) {
////            e.printStackTrace();
////        } catch (IOException e) {
////            e.printStackTrace();
////        } catch (TransformerConfigurationException e) {
////            e.printStackTrace();
////        } catch (TransformerException e) {
////            e.printStackTrace();
////        }
////
////        return rootNode;
////    }
//
////    private  void printNote(NodeList nodeList) {
////
////        for (int count = 0; count < nodeList.getLength(); count++) {
////
////            Node tempNode = nodeList.item(count);
////
////            // make sure it's element node.
////            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
////
////                // get node name and value
////                System.out.println("\nNode Name =" + tempNode.getNodeName() + " [OPEN]");
////                System.out.println("Node Value =" + tempNode.getTextContent());
////
////                if (tempNode.hasAttributes()) {
////
////                    // get attributes names and values
////                    NamedNodeMap nodeMap = tempNode.getAttributes();
////                    if(tempNode.getAttributes().getNamedItem("android:id")!=null){
////                        if(tempNode.getAttributes().getNamedItem("fff")!=null){
////
////                        }
////                       boolean isAdded= addAttributes2(tempNode);
////                        System.out.println("IT HAS AN ID");
//////                        Attr attribute = doc.createAttribute("test");
//////                        attribute.setNodeValue("True");
//////                        tempNode.getAttributes().setNamedItem(attribute);
////                        if(isAdded) {
////                            main.logger.info("android:minHeight " + tempNode.getNodeName() + "Value: " + tempNode.getAttributes().getNamedItem("android:minHeight").toString());
////                        }
////                    }
////                    for (int i = 0; i < nodeMap.getLength(); i++) {
////
////                        Node node = nodeMap.item(i);
////                        System.out.println("attr name : " + node.getNodeName());
////                        System.out.println("attr value : " + node.getNodeValue());
////
////                    }
////
////                }
////
////                if (tempNode.hasChildNodes()) {
////
////                    // loop again if has child nodes
////                    printNote(tempNode.getChildNodes());
////
////                }
////
////                System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");
////
////            }
////
////        }
////
////    }
//
////    private boolean addAttributes(Node tempNode) {
////        for (int j = 0; j < genes.size(); j++) {
////            String id = genes.get(j).getId();
////            main.logger.info("genes of " + j + " " + genes.get(j).getId());
////            main.logger.info("TempNode ID:  "+ tempNode.getAttributes().getNamedItem("android:id"));
////            if (tempNode.getAttributes().getNamedItem("android:id").getTextContent().contains(id)) {
////                for (Attributes att : genes.get(j).getModifications()) {
////                    if (att.isNew()) {   // it is new attribute so just add it to the list of attributes
////                        Attr attribute = doc.createAttribute(att.getAttribute());
////                        attribute.setNodeValue(att.getValue());
////                        main.logger.info("Ali Att Value:  "+ att.getAttribute());
////                        tempNode.getAttributes().setNamedItem(attribute);
////                        main.logger.info("Attribute added "+ tempNode.getNodeName());
////                        return true;
////                                }
////
////                            }
////                        }
////                    }
////        return false;
////    }
//
//
////    void traverseDFS(Node node, List<Node> nodeList, Node parent) {
////        System.out.println("Begin traverse: Node: "+ node.getNodeName() + " nodeList size: "+ nodeList.size());
////        if(parent!=null){
////            System.out.println(" parent "+ parent.getNodeName());
////        }
////        String nodeName = node.getNodeName();
////        if ("#text".equals(nodeName) || "#comment".equals(nodeName)) {
////            return;
////        }
////
////        if((node).getAttributes().getNamedItem("android:id")!=null){
////            main.logger.info("genes len: "+genes.size());
////            main.logger.info("genes are: "+genes);
////                for (int j = 0; j < genes.size(); j++) {
////                    String id = genes.get(j).getId();
////                    main.logger.info("genes of "+j+" "+genes.get(j).getId());
////                        if (node.getAttributes().getNamedItem("android:id").getTextContent().contains(id)) {
////                            for (Attributes att : genes.get(j).getModifications()) {
////                                if (att.isNew()) {   // it is new attribute so just add it to the list of attributes
////                                    Attr attribute = doc.createAttribute(att.getAttribute());
////                                    attribute.setNodeValue(att.getValue());
////                                    node.getAttributes().setNamedItem(attribute);
////                                }
////
////                            }
////                        }
////
////                }
////        }
////        if((node).getAttributes().getNamedItem("android:id")!=null)
////        System.out.println("NODE IS: " + node.getNodeName() + "att " +(node).getAttributes().getNamedItem("android:id"));
////        if (parent != null) {
////            parent.appendChild(node);
////
////        }
////        nodeList.add(node);
////
////        if (node.hasChildNodes()) {
////            NodeList children = node.getChildNodes();
////            for (int i = 0; i < children.getLength(); ++i) {
////                Node newNode = children.item(i);
////                traverseDFS(newNode, nodeList, node);
////            }
////        }
////    }
////
////    public void writeChanges(String activityName, ArrayList<Modification> modificationsToWrite) {
////        this.genes2=modificationsToWrite;
////        String path = APPLayout.staticFolder+File.separator+activityName;
////        writeChangesToFile(modificationsToWrite,path);
////    }
//
////    private void writeChangesToFile(ArrayList<Modification> modificationsToWrite, String path) {
////
////        dbf = DocumentBuilderFactory.newInstance();
////        db = null;
////        Node rootNode = null;
////
////        try {
////            db = dbf.newDocumentBuilder();
////            doc = db.parse(path);
////            Element root = doc.getDocumentElement(); // <hierarchy>
////
////            List<Node> nodeList = new ArrayList<>();
////            if (doc.hasChildNodes()) {
////                perfromWrite(doc.getChildNodes());
////            }
////
////
////            //        // write the content into xml file
////            TransformerFactory transformerFactory = TransformerFactory.newInstance();
////            Transformer transformer = transformerFactory.newTransformer();
////            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
////            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
////            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
////            DOMSource source = new DOMSource(doc);
//////            String newFile = pathToFolder +File.separator+
//////                    file.substring(file.indexOf("_") + 1);
////            String newFile = path;
////            System.out.println("XML written to "+ newFile);
////            StreamResult result = new StreamResult(new File(newFile));
////            transformer.transform(source, result);
////
////        } catch (ParserConfigurationException pce) {
////            System.err.println(pce);
////        } catch (SAXException e) {
////            e.printStackTrace();
////        } catch (IOException e) {
////            e.printStackTrace();
////        } catch (TransformerConfigurationException e) {
////            e.printStackTrace();
////        } catch (TransformerException e) {
////            e.printStackTrace();
////        }
////
////
////    }
//
////    private void perfromWrite(NodeList nodeList) {
////
////        for (int count = 0; count < nodeList.getLength(); count++) {
////
////            Node tempNode = nodeList.item(count);
////
////            // make sure it's element node.
////            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
////
////                // get node name and value
////                System.out.println("\nNode Name =" + tempNode.getNodeName() + " [OPEN]");
////                System.out.println("Node Value =" + tempNode.getTextContent());
////
////                if (tempNode.hasAttributes()) {
////
////                    // get attributes names and values
////                    NamedNodeMap nodeMap = tempNode.getAttributes();
////                    if(tempNode.getAttributes().getNamedItem("android:id")!=null){
//////                        if(tempNode.getAttributes().getNamedItem("fff")!=null){
//////
//////                        }
//////                        modificationsToWrite.get(tempNode.getAttributes().getNamedItem("android:id"))
////                        boolean isAdded= addAttributes2(tempNode);
////                        System.out.println("IT HAS AN ID");
//////                        Attr attribute = doc.createAttribute("test");
//////                        attribute.setNodeValue("True");
//////                        tempNode.getAttributes().setNamedItem(attribute);
//////                        if(isAdded) {
//////                            main.logger.info("android:minHeight " + tempNode.getNodeName() + "Value: " + tempNode.getAttributes().getNamedItem("android:minHeight").toString());
//////                        }
////                    }
////                    for (int i = 0; i < nodeMap.getLength(); i++) {
////
////                        Node node = nodeMap.item(i);
////                        System.out.println("attr name : " + node.getNodeName());
////                        System.out.println("attr value : " + node.getNodeValue());
////
////                    }
////
////                }
////
////                if (tempNode.hasChildNodes()) {
////
////                    // loop again if has child nodes
////                    perfromWrite(tempNode.getChildNodes());
////
////                }
////
////                System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");
////
////            }
////
////        }
////
////    }
//
////    private boolean addAttributes2(Node tempNode) {
////        for (int i = 0; i <genes2.size() ; i++) {
////            String id = genes2.get(i).getId();
////            main.logger.info("genes of " + i + " " + genes2.get(i).getId());
////            main.logger.info("TempNode ID:  "+ tempNode.getAttributes().getNamedItem("android:id"));
////            if (tempNode.getAttributes().getNamedItem("android:id").getTextContent().contains(id)) {
////                for (Attributes att : genes2.get(i).getModifications()) {
////                    if (att.isNew()) {   // it is new attribute so just add it to the list of attributes
////                        Attr attribute = doc.createAttribute(att.getAttribute());
////                        attribute.setNodeValue(att.getValue());
////
////                        tempNode.getAttributes().setNamedItem(attribute);
////                        main.logger.info("Ali Att Value:  "+ att.getAttribute());
////                        main.logger.info("Attribute added "+ tempNode.getNodeName());
////                        return true;
////                    } else{
////                       String  isFound= tempNode.getAttributes().getNamedItem(att.getAttribute()).getTextContent();
////                        main.logger.info("Ali Att Existing Value:  "+ att.getAttribute());
////                        main.logger.info("Existing Attribute removed and re added "+ tempNode.getNodeName());
////                        main.logger.info("Ali Att Existing Value:  "+ att.getAttribute());
////                      //  doc.getAttributes().removeNamedItem(att.getAttribute());
////                        Attr attribute = doc.createAttribute(att.getAttribute());
////                        attribute.setNodeValue(att.getValue());
////                        tempNode.getAttributes().setNamedItem(attribute);
////
////                        return true;
////                    }
////                }
////            }
////        }
//////        for (int j = 0; j < genes.size(); j++) {
//////            String id = genes.get(j).getId();
//////            main.logger.info("genes of " + j + " " + genes.get(j).getId());
//////            main.logger.info("TempNode ID:  "+ tempNode.getAttributes().getNamedItem("android:id"));
//////            if (tempNode.getAttributes().getNamedItem("android:id").getTextContent().contains(id)) {
//////                for (Attributes att : genes.get(j).getModifications()) {
//////                    if (att.isNew()) {   // it is new attribute so just add it to the list of attributes
//////                        Attr attribute = doc.createAttribute(att.getAttribute());
//////                        attribute.setNodeValue(att.getValue());
//////                        main.logger.info("Ali Att Value:  "+ att.getAttribute());
//////                        tempNode.getAttributes().setNamedItem(attribute);
//////                        main.logger.info("Attribute added "+ tempNode.getNodeName());
//////                        return true;
//////                    }
//////
//////                }
//////            }
//////        }
////        return false;
////    }
//}
