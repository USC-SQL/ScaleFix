package usc.edu.SALEM.util;

import gatech.xpert.dom.DomNode;
import org.tinylog.Logger;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.VH.UI;

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

import static usc.edu.OwlEye.util.Utils.findOriginalStaticLayoutFile;

public class StaticLayoutUtil {

    private String layoutFilePath;
    private Element root;
    private DocumentBuilderFactory dbf;
    private DocumentBuilder db;

    // December 2022
    public void addElementToLayout(gatech.xpert.dom.Node<DomNode> domNodeFound, String outputPath) {
//        // get the parent of the element to add
//        String parentXpath = xpath.substring(0, xpath.lastIndexOf("/"));
//        // get the parent element
//        Element parentElement = getElementByXpath(parentXpath);
//        // create the new element
        Document ownerDoc = root.getOwnerDocument();
        String effective_id = domNodeFound.getData().getAttr("effective-id");
        String id = domNodeFound.getData().getId();
        if (effective_id != null) {
            id = effective_id;
        }
//                    System.out.println("ID: " + id);
        if (id != null) {
            org.w3c.dom.Element e = this.searchForNode(this.getRoot(), id);

            //String prop = Constants.SIZE_SPACE_ATTRIBUTES.get(element.getCssProperty()); // right now I am using the same property for width and height
//            String prop = element.getCssProperty(); // right now I am using the same property for width and height
           boolean parentIsInDifferentFile = false;
            if (e != null) {
                Document owner=e.getOwnerDocument();

                //TODO: do we need to actually get the parent if we already got the minimum common ancestor? lets skip this for now
               // Node p = e.getParentNode();
                Node p=e;
                Element parentElement = null;


                if (p==null || p.getNodeType() != Node.ELEMENT_NODE) {
                     parentElement=getParentOfElement(e,id,outputPath); // this can be tricky if the parent is in a different file like weaver
                     owner=parentElement.getOwnerDocument();
                    parentIsInDifferentFile = true;
                }
                else{
                     parentElement = e;
                }

//                Element parentElement = (Element) e.getParentNode();

                System.out.println("Least common Ancestor ID: " + id + " || owner doc: " +owner.getDocumentURI()  );
                //create a new element
                org.w3c.dom.Element newScrollElement = owner.createElement("ScrollView");
                //Element newElement = owner.getOwnerDocument().createElement("ScrollView");
                newScrollElement.setAttribute("android:layout_width", "fill_parent");
                newScrollElement.setAttribute("android:layout_height", "fill_parent");
             //   newElement.setAttribute("android:fillViewport", "true");
                newScrollElement.setAttribute("android:orientation", "vertical");
                //e.appendChild(newScrollElement);
                //  e.set
                // create new Layout element
                org.w3c.dom.Element newLayoutElement = owner.createElement("LinearLayout");
                newLayoutElement.setAttribute("android:layout_width", "fill_parent");
                newLayoutElement.setAttribute("android:layout_height", "fill_parent");
                newLayoutElement.setAttribute("android:orientation", "vertical");
                newScrollElement.appendChild(newLayoutElement);
                if(!parentIsInDifferentFile){
                    // usual case
                    boolean newApproach = true;                // adding all the children of the parent to the new layout and then removing the children from the parent

                    if (!newApproach){
                        parentElement.replaceChild(newScrollElement, e);
                        newLayoutElement.appendChild(e);
                    }

                    // check if parentElement is the root of the document
                    if (parentElement == root) {
                        // get the index of the element in the root
                        int index = 0;
                        for (int i = 0; i < root.getChildNodes().getLength(); i++) {
                            Node child = root.getChildNodes().item(i);
                            if (child.getNodeType() == Node.ELEMENT_NODE) {
                                if (child == e) {
                                    index = i;
                                    break;
                                }
                            }
                        }
                        // remove the element from the root
                       // root.removeChild(e);
                        // add the new element to the root
                        for (int i = 0; i < parentElement.getChildNodes().getLength(); i++) {
                            Node child = parentElement.getChildNodes().item(i);
                            if (child.getNodeType() == Node.ELEMENT_NODE) {
                                Element childElement = (Element) child;
                                newLayoutElement.appendChild(childElement);
                            }
                        }
                        // remove all children from the parent element
                        while (parentElement.hasChildNodes()) {
                            parentElement.removeChild(parentElement.getFirstChild());
                        }
                        // add the newLayoutElement to the parentElement
                        parentElement.appendChild(newScrollElement);
                        //root.insertBefore(newScrollElement, e);

                        newApproach = false;
                    }
//                    if (newApproach) {
//                        for (int i = 0; i < parentElement.getChildNodes().getLength(); i++) {
//                            Node child = parentElement.getChildNodes().item(i);
//                            if (child.getNodeType() == Node.ELEMENT_NODE) {
//                                Element childElement = (Element) child;
//                                    newLayoutElement.appendChild(childElement);
//                            }
//                        }
//                    }
                }
                else{
                    // parent is in a different file like in weaver. what do we do? #TODO how to handle this case? cheack weaver
                    parentElement.appendChild(newScrollElement);
//                    newLayoutElement.appendChild(e);
                }


                // call write xml method
                this.writeStaticXML();

            }



        }

     //   Element newElement = ownerDoc.createElement("ScrollView");
     //   Element newElement = root.getOwnerDocument().createElement(type);
//        newElement.setAttribute("id", id);
//        newElement.setAttribute("source", source);
//        newElement.setAttribute("value", value);
//        // add the new element to the parent
//        parentElement.appendChild(newElement);
    }

    private Element getParentOfElement(Element e, String id, String outputPath) {
        Node p = e.getParentNode();
        String x = p.getNodeName();
        NamedNodeMap ss = p.getAttributes();

        if (p!=null && p.getNodeType() == Node.ELEMENT_NODE) {

            Element parentElement = (Element) p;
            return parentElement;
        }
        else {
            // lets check if the parent is in a different file
            // (1) get the VH of the original element
            UI orgUI = OwlEye.getOriginalDefaultUI();
            gatech.xpert.dom.Node<DomNode> domNodeFound = orgUI.getXMLTree().searchByID_T(id, null); // Place holder for now i need to continue to use xpath
            if(domNodeFound==null){
                Logger.debug("ERROR: Could not find the  least common ancestor for Scroll View");
                return null;
            }
            // (2) get the parent of the original element
            gatech.xpert.dom.Node<DomNode> parent = domNodeFound.getParent();
            if(parent==null){
                Logger.debug("ERROR: Could not find the parent of least common ancestor for Scroll View");
                return null;
            }
            // (3) get the id of the parent
            String parentID = parent.getData().getId();
            if(parentID==null || parentID.isEmpty()){
                Logger.debug("ERROR: Could not find the id of the parent of least common ancestor for Scroll View");
                return null;
            }
            String originFIle=parent.getData().getAttributes().get("origin");
            StaticLayoutUtil originLayout= findOriginalStaticLayoutFile(outputPath, originFIle);

            org.w3c.dom.Element eParent = originLayout.searchForNode(originLayout.getRoot(), parentID);
            return eParent;
        }

    }

    public Element readStaticXML(File file) {
        dbf = DocumentBuilderFactory.newInstance();
        db = null;

        try {
            db = dbf.newDocumentBuilder();
            Document dom = db.parse(file.getAbsoluteFile());
            Element docEle = dom.getDocumentElement();
            root = docEle;
            layoutFilePath = file.getAbsolutePath();

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

    public Element searchForNode(Element root, String dynamicID) {
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

    public Element searchForNode_old(Element root, String dynamicID) {
        Element found = findStaticNodeByID(dynamicID, root);
        if (found != null) {
            return found;
        }
        NodeList nl = root.getChildNodes();
        int length = nl.getLength();
        for (int i = 0; i < length; i++) {
            if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) nl.item(i);
                Element el1 = findStaticNodeByID(dynamicID, el);
                if (el1 != null) return el1;

            }
        }
        return null;
    }


    //    @org.jetbrains.annotations.Nullable
    private Element findStaticNodeByID(String dynamicID, Element el) {
        String static_id = el.getAttribute("android:id");
        if (static_id != null) {
            static_id = static_id.substring(static_id.lastIndexOf("/") + 1);
            if (dynamicID.equalsIgnoreCase(static_id)) {
                return el;
            }
        }
        return null;
    }

    public void writeStaticXML() {

        try {

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(root);
//            String newFile = pathToFolder +File.separator+
//                    file.substring(file.indexOf("_") + 1);

            if (OwlEye.debugCompile) {
                System.out.println("XML written to " + layoutFilePath);
            }
            StreamResult result = new StreamResult(new File(layoutFilePath));
            transformer.transform(source, result);

//        } catch (ParserConfigurationException pce) {
//            System.err.println(pce);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }


    public Element getRoot() {

        return root;
    }

    public void setRoot(Element root) {
        this.root = root;
    }

    public DocumentBuilderFactory getDbf() {
        return dbf;
    }

    public void setDbf(DocumentBuilderFactory dbf) {
        this.dbf = dbf;
    }

    public DocumentBuilder getDb() {
        return db;
    }

    public void setDb(DocumentBuilder db) {
        this.db = db;
    }
}

