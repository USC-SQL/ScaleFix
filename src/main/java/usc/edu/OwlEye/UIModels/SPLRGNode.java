package usc.edu.OwlEye.UIModels;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;

import java.util.HashMap;

public class SPLRGNode extends GraphNode {
    private String xpath;
    private String id;
    private String property;
    private Node<DomNode> xmlNode;
//    private double ratio;
    private propertyValue propValue;
//    private double numerator;
//    private double denominator;

    public SPLRGNode(String xpath,String id, String property, propertyValue propVal) {
        super(xpath);
        this.xpath = xpath;
        this.id=id;
        this.property = property;
        this.propValue = propVal;
//        this.ratio = ratio;
//        this.numerator = numerator;
//        this.denominator = denominator;
        this.xmlNode = null;
    }
    public SPLRGNode(String xpath,  String id, Node<DomNode> xmlNode, String property,  propertyValue propVal) {
        super(xpath);
        this.xpath = xpath;
        this.id=id;
        this.property = property;
        this.propValue = propVal;

//        this.numerator = numerator;
//        this.denominator = denominator;
        this.xmlNode = xmlNode;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
//    public double getRatio() {
//        return ratio;
//    }
//
//    public void setRatio(double ratio) {
//        this.ratio = ratio;
//    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

//    public double getNumerator() {
//        return numerator;
//    }
//
//    public void setNumerator(double numerator) {
//        this.numerator = numerator;
//    }
//
//    public double getDenominator() {
//        return denominator;
//    }
//
//    public void setDenominator(double denominator) {
//        this.denominator = denominator;
//    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((property == null) ? 0 : property.hashCode());
        long temp;
       // temp = Double.doubleToLongBits(ratio);
        //result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((xpath == null) ? 0 : xpath.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SPLRGNode other = (SPLRGNode) obj;
        if (property == null) {
            if (other.property != null)
                return false;
        } else if (!property.equals(other.property))
            return false;
//        if (Double.doubleToLongBits(ratio) != Double.doubleToLongBits(other.ratio))
//            return false;
        if (xpath == null) {
            if (other.xpath != null)
                return false;
        } else if (!xpath.equals(other.xpath))
            return false;
        return true;
    }

    public propertyValue getPropValue() {
        return propValue;
    }

    public void setPropValue(propertyValue propValue) {
        this.propValue = propValue;
    }

    public Node<DomNode> getXmlNode() {
        return xmlNode;
    }

    public void setXmlNode(Node<DomNode> xmlNode) {
        this.xmlNode = xmlNode;
    }

    @Override
    public String toString() {
        return "SPLRGNode [id=" + id + ", property=" + property +  ", propValue=" + propValue + "]";
//        String returnValue = "Node   = (Xpath = " + this.xpath + ")\n";

//        return returnValue;
    }
}
