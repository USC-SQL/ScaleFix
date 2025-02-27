/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uiautomator;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
//import main.main;

import javax.xml.parsers.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UiHierarchyXmlLoader {

    private UiNode mRootNode;
    private List<Rectangle> mNafNodes;
    private List<BasicTreeNode> mNodeList;
    private boolean found = false;
    private UiNode found_node = null;

    public UiNode getmRootNode() {
        return mRootNode;
    }

    public UiHierarchyXmlLoader() {
    }

    /**
     * Uses a SAX parseparseXmlStaticr to process XML dump
     *
     * @param xmlPath
     * @return
     */
    public BasicTreeNode parseXml(String xmlPath) {
        mRootNode = null;
        mNafNodes = new ArrayList<Rectangle>();
        mNodeList = new ArrayList<BasicTreeNode>();
        // standard boilerplate to get a SAX parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = null;
        try {
            parser = factory.newSAXParser();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        }
        // handler class for SAX parser to receiver standard parsing events:
        // e.g. on reading "<foo>", startElement is called, on reading "</foo>",
        // endElement is called
        DefaultHandler handler = new DefaultHandler() {
            UiNode mParentNode;
            UiNode mWorkingNode;

            @Override
            public void startElement(String uri, String localName, String qName,
                                     Attributes attributes) throws SAXException {
                boolean nodeCreated = false;
                // starting an element implies that the element that has not yet been closed
                // will be the parent of the element that is being started here
                mParentNode = mWorkingNode;
                /*if ("hierarchy".equals(qName)) {
                    int rotation = 0;
                    for (int i = 0; i < attributes.getLength(); i++) {
                        if ("rotation".equals(attributes.getQName(i))) {
                            try {
                                rotation = Integer.parseInt(attributes.getValue(i));
                            } catch (NumberFormatException nfe) {
                                // do nothing
                            }
                        }
                    }
                    mWorkingNode = new RootWindowNode(attributes.getValue("windowName"), rotation);
                    nodeCreated = true;
                } else */
                if ("node".equals(qName)) {
                    UiNode tmpNode = new UiNode();
                    for (int i = 0; i < attributes.getLength(); i++) {
                        tmpNode.addAtrribute(attributes.getQName(i), attributes.getValue(i));
                    }
                    mWorkingNode = tmpNode;
                    nodeCreated = true;
                    // check if current node is NAF
                    String naf = tmpNode.getAttribute("NAF");
                    if ("true".equals(naf)) {
                        mNafNodes.add(new Rectangle(tmpNode.x, tmpNode.y,
                                tmpNode.width, tmpNode.height));
                    }
                }
                // nodeCreated will be false if the element started is neither
                // "hierarchy" nor "node"
                if (nodeCreated) {
                    if (mRootNode == null) {
                        // this will only happen once
                        mRootNode = mWorkingNode;
                    }
                    if (mParentNode != null) {
                        mParentNode.addChild(mWorkingNode);
                        mNodeList.add(mWorkingNode);
                    }
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                //mParentNode should never be null here in a well formed XML
                if (mParentNode != null) {
                    // closing an element implies that we are back to working on
                    // the parent node of the element just closed, i.e. continue to
                    // parse more child nodes
                    mWorkingNode = mParentNode;
                    mParentNode = (UiNode) mParentNode.getParent();
                }
            }
        };
        try {
            parser.parse(new File(xmlPath), handler);
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return mRootNode;
    }


    public BasicTreeNode parseXmlStatic(String xmlPath) {
        mRootNode = null;
        mNafNodes = new ArrayList<Rectangle>();
        mNodeList = new ArrayList<BasicTreeNode>();
        // standard boilerplate to get a SAX parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = null;
        try {
            parser = factory.newSAXParser();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        }
        // handler class for SAX parser to receiver standard parsing events:
        // e.g. on reading "<foo>", startElement is called, on reading "</foo>",
        // endElement is called
        DefaultHandler handler = new DefaultHandler() {
            UiNode mParentNode;
            UiNode mWorkingNode;

            @Override
            public void startElement(String uri, String localName, String qName,
                                     Attributes attributes) throws SAXException {
                boolean nodeCreated = false;
                //main.logger.info("    mParentNode = mWorkingNode");
                // starting an element implies that the element that has not yet been closed
                // will be the parent of the element that is being started here
                mParentNode = mWorkingNode;
                /*if ("hierarchy".equals(qName)) {
                    int rotation = 0;
                    for (int i = 0; i < attributes.getLength(); i++) {
                        if ("rotation".equals(attributes.getQName(i))) {
                            try {
                                rotation = Integer.parseInt(attributes.getValue(i));
                            } catch (NumberFormatException nfe) {
                                // do nothing
                            }
                        }
                    }
                    mWorkingNode = new RootWindowNode(attributes.getValue("windowName"), rotation);
                    nodeCreated = true;
//                } else */
                //if ("node".equals(qName)) {
                UiNode tmpNode = new UiNode();
                if (qName != null) {
                    tmpNode.addAtrribute("class", qName);
                }
                for (int i = 0; i < attributes.getLength(); i++) {
                    tmpNode.addAtrribute(attributes.getQName(i), attributes.getValue(i));
                }
                mWorkingNode = tmpNode;
                nodeCreated = true;
                // check if current node is NAF
                String naf = tmpNode.getAttribute("NAF");
                if ("true".equals(naf)) {
                    mNafNodes.add(new Rectangle(tmpNode.x, tmpNode.y,
                            tmpNode.width, tmpNode.height));
                }
                // }
                // nodeCreated will be false if the element started is neither
                // "hierarchy" nor "node"
                if (nodeCreated) {
                    if (mRootNode == null) {
                        // this will only happen once
                        mRootNode = mWorkingNode;
                    }
                    if (mParentNode != null) {
                        mParentNode.addChild(mWorkingNode);
                        mNodeList.add(mWorkingNode);
                    }
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                //mParentNode should never be null here in a well formed XML
                if (mParentNode != null) {
                    // closing an element implies that we are back to working on
                    // the parent node of the element just closed, i.e. continue to
                    // parse more child nodes
                    mWorkingNode = mParentNode;
                    mParentNode = (UiNode) mParentNode.getParent();
                }
            }
        };
        try {
            parser.parse(new File(xmlPath), handler);
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return mRootNode;
    }

    /**
     * Returns the list of "Not Accessibility Friendly" nodes found during parsing.
     * <p>
     * Call this function after parsing
     *
     * @return
     */
    public List<Rectangle> getNafNodes() {
        return Collections.unmodifiableList(mNafNodes);
    }

    public List<BasicTreeNode> getAllNodes() {
        return mNodeList;
    }

//    public void printTreeHierarchy(BasicTreeNode node, String indentation) {
//        BasicTreeNode[] nodes = node.getChildren();
//        indentation += " ";
//        for (int index = 0; index < nodes.length; index++) {
//            System.out.println(indentation + nodes[index]);
//            printTreeHierarchy(nodes[index], indentation);
//            UiNode temp = (UiNode) nodes[index];
//            if (temp.getAttribute("resource-id").equalsIgnoreCase("com.bytecode.aiodownloader:id/action_bar")) {
//                main.logger.info("TEST");
//            }
//
//
////        	for(String att: nodes[index].getAttributesArray()){
////        	    if(att.equalsIgnoreCase("resource-id")){
////
////                }
////            }
//        }
//    }


    public void printTreeHierarchy2(BasicTreeNode node, String indentation) {
        BasicTreeNode[] nodes = node.getChildren();
        indentation += " ";
        for (int index = 0; index < nodes.length; index++) {
            UiNode temp = (UiNode) nodes[index];
//            System.out.println( temp.getClass().toString());
            System.out.println(indentation + temp);
            printTreeHierarchy2(nodes[index], indentation);
            //  UiNode temp =(UiNode)nodes[index];
//            if(temp.getAttribute("resource-id").equalsIgnoreCase("com.bytecode.aiodownloader:id/action_bar")){
//                main.logger.info("TEST");
//            }


//        	for(String att: nodes[index].getAttributesArray()){
//        	    if(att.equalsIgnoreCase("resource-id")){
//
//                }
//            }
        }
    }

    public UiNode get_node_by_id(UiNode node, String id, String classWidget) {
        found = false;
        found_node = null;

        boolean result = compareIds(node, id, classWidget);
        search_for_node(node, id, classWidget);
        return found_node;
    }

    public void search_for_node(UiNode node, String id, String classWidget) {
        //  System.out.println("Node is :" + APPLayout.getID(node));
        if (found) {
            return;
        }
        BasicTreeNode[] nodes = node.getChildren();
        String id_key;

        for (int index = 0; index < nodes.length; index++) {
            if (found) {
                return;
            }
            UiNode temp = (UiNode) nodes[index];
            boolean result = compareIds(temp, id, classWidget);
            //  System.out.println("Node id: "+ node_id + " id: "+ id);
            if (result) {
//                    main.logger.info("found id match");
//                    found =true;
//                    found_node=temp;
                return;

            }

            search_for_node(temp, id, classWidget);
        }
        return;
    }


    public boolean compareIds(UiNode temp, String id, String classWidget) {
        String id_key;
        //   System.out.println("Temp Node id: "+ APPLayout.getID(temp));
        //main.logger.info("Nodes"+ nodes[index].toString());
        //main.logger.info("getAtt: "+ temp.getAttribute("android:id"));
//        System.out.println("TEMP NODE: "+temp);
        if (temp.getAttribute("resource-id") != null) {
            id_key = "resource-id";
        } else if (temp.getAttribute("android:id") != null) {
            id_key = "android:id";
        } else {
            id_key = null;
        }

        if (id_key != null) {
//            if (temp.getAttribute(id_key).equalsIgnoreCase("btn_background")) {
//                main.logger.info("btn_background FOUND");
////            System.exit(0);
//            }
            String node_id = temp.getAttribute(id_key);
            int ind = node_id.lastIndexOf('/');
            node_id = node_id.substring(ind + 1);


            ind = id.lastIndexOf('/');
            id = id.substring(ind + 1);

            if (node_id.trim().equalsIgnoreCase(id.trim())) {
                if (classWidget != null) {
                    String classTemp = temp.getAttribute("class");
                    classTemp = classTemp.substring(classTemp.lastIndexOf(".") + 1);
                    //ToDO: This is not accurate especially if it is using special class
                    if (classWidget.trim().toLowerCase().contains(classTemp.trim().toLowerCase())) {
                        found = true;
                        found_node = temp;
                        return true;
                    }
                } else {

                    found = true;
                    found_node = temp;
                    return true;
                }
            }


        }
        return false;
    }
}
