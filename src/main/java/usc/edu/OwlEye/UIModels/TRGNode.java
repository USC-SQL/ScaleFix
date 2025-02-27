package usc.edu.OwlEye.UIModels;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import usc.edu.OwlEye.ElementsProperties.Lines;
import usc.edu.OwlEye.ElementsProperties.MaxLine;
import usc.edu.OwlEye.ElementsProperties.Property;

import java.util.HashMap;

public class TRGNode {

    private String xpath;
    private String clsName;
    private Node<DomNode> xmlNode;
    private HashMap<String, Property> textAttributes ;// text Attibutes defined  (attribute name, attribute value)


//    public TRGNode(String xpath,Node<DomNode> xmlNode, String clsName ,HashMap<String, String> textAttributes) {
//        this.xpath = xpath;
//
//        this.xmlNode = xmlNode;
//        this.clsName = clsName;
//        this.textAttributes = textAttributes;
//    }

    public TRGNode(Node<DomNode> xmlNode,HashMap<String, String> textAttributes) {
        this.xpath = xmlNode.getData().getxPath();
        this.textAttributes = new HashMap<>();
        this.xmlNode = xmlNode;
        String clsName = xmlNode.getData().getTagName();
        this.clsName = clsName;
        for (String key : textAttributes.keySet()
        ) {
            switch (key) {
                case MaxLine.propertyName: {
                    MaxLine maxLine = new MaxLine();
                    maxLine.setCurrentVal(Integer.parseInt(textAttributes.get(key)));
                    this.textAttributes.put(key, maxLine);
                    break;
                }
                case Lines.propertyName: {
                    Lines lines = new Lines();
                    lines.setCurrentVal(Integer.parseInt(textAttributes.get(key)));
                    this.textAttributes.put(key, lines);
                    break;
                }
            }
        }
    }

    public TRGNode(Node<DomNode> xmlNode) {
        this.xpath = xmlNode.getData().getxPath();

        this.xmlNode = xmlNode;
        String clsName = xmlNode.getData().getTagName();
        this.clsName = clsName;
        this.textAttributes = new HashMap<String, Property>();
    }

//    public void addTextAttribute(String attributeName, String attributeValue){
//        this.textAttributes.put(attributeName, attributeValue);
//    }
    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getClsName() {
        return clsName;
    }

    public void setClsName(String clsName) {
        this.clsName = clsName;
    }

    public Node<DomNode> getXmlNode() {
        return xmlNode;
    }

    public void setXmlNode(Node<DomNode> xmlNode) {
        this.xmlNode = xmlNode;
    }

    public HashMap<String, Property> getTextAttributes() {
        return textAttributes;
    }

    public void setTextAttributes(HashMap<String, Property> textAttributes) {
        this.textAttributes = textAttributes;
    }
}
