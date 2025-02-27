package usc.edu.OwlEye.UIModels;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import usc.edu.OwlEye.ElementsProperties.*;
public class WRGNode extends GraphNode {


    private String xpath;
    private String property;
    private Node<DomNode> xmlNode;
    private double ratio;
    private double numerator; //
    private double denominator; //
    private double parentTotal; // total weight for parent
    private String parentXpath;
    private Property propertyObj; // either normal or vertical or horizontal weight





    public WRGNode(String xpath, String property, double ratio, double numerator, double parentTotal) {
        super(xpath);
        this.xpath = xpath;
        this.property = property;
        this.ratio = ratio;
        this.numerator = numerator;
        this.denominator = parentTotal;
        this.parentTotal = parentTotal;
        parentXpath=null;
        propertyObj=null;

    }

    public WRGNode(String xpath, Node<DomNode> xmlNode, String property, double ratio, double numerator, double parentTotal, String parentXpath) {
        super(xpath);
        this.property = property;
        this.ratio = ratio;
        this.numerator = numerator;
        this.denominator = denominator;
        this.parentTotal = parentTotal;
        this.xmlNode = xmlNode;
        this.parentXpath = parentXpath;
        propertyObj=null;

    }
    public WRGNode(String xpath, Node<DomNode> xmlNode, String property, double ratio, double numerator, double parentTotal, String parentXpath, Property propertyObj) {
        super(xpath);
        this.property = property;
        this.ratio = ratio;
        this.numerator = numerator;
        this.denominator = denominator;
        this.parentTotal = parentTotal;
        this.xmlNode = xmlNode;
        this.parentXpath = parentXpath;
        this.propertyObj=propertyObj;

    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }



    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public double getNumerator() {
        return numerator;
    }

    public void setNumerator(double numerator) {
        this.numerator = numerator;
    }

    public double getDenominator() {
        return denominator;
    }

    public void setDenominator(double denominator) {
        this.denominator = denominator;
    }

    public double getParentTotal() {
        return parentTotal;
    }

    public void setParentTotal(double parentTotal) {
        this.parentTotal = parentTotal;
    }


    public Node<DomNode> getXmlNode() {
        return xmlNode;
    }

    public void setXmlNode(Node<DomNode> xmlNode) {
        this.xmlNode = xmlNode;
    }

    public String getParentXpath() {
        return parentXpath;
    }
    public void setParentXpath(String parentXpath) {
        this.parentXpath = parentXpath;
    }

    public Property getPropertyObj() {
        return propertyObj;
    }
    public void setPropertyObj(Property propertyObj) {
        this.propertyObj = propertyObj;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((property == null) ? 0 : property.hashCode());
        long temp;
        temp = Double.doubleToLongBits(ratio);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        WRGNode other = (WRGNode) obj;
        if (property == null) {
            if (other.property != null)
                return false;
        } else if (!property.equals(other.property))
            return false;
        if (Double.doubleToLongBits(ratio) != Double.doubleToLongBits(other.ratio))
            return false;
        if (xpath == null) {
            if (other.xpath != null)
                return false;
        } else if (!xpath.equals(other.xpath))
            return false;
        return true;
    }



    @Override
    public String toString() {
        String returnValue = "Node   = (Xpath = " + this.xpath + ")\n";

        return returnValue;
    }
}
