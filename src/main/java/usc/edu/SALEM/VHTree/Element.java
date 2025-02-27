package usc.edu.SALEM.VHTree;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
public class Element {
    private String xpath;
    private String cssProperty;
    private String value;
    private Node<DomNode> node;
    private boolean isParentIncrease;



    public Element(String xpath, String cssProperty, String value, boolean isParentIncrease, Node<DomNode> domNode) {
        super();
        this.xpath = xpath;
        this.cssProperty = cssProperty;
        this.value = value;
        setParentIncrease(isParentIncrease);
        setNode(domNode);
    }

    public Node<DomNode> getNode() {
        return node;
    }

    public void setNode(Node<DomNode> node) {
        this.node = node;
    }

    public boolean isParentIncrease() {
        return isParentIncrease;
    }

    public void setParentIncrease(boolean parentIncrease) {
        isParentIncrease = parentIncrease;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getCssProperty() {
        return cssProperty;
    }

    public void setCssProperty(String cssProperty) {
        this.cssProperty = cssProperty;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cssProperty == null) ? 0 : cssProperty.hashCode());
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
        Element other = (Element) obj;
        if (cssProperty == null) {
            if (other.cssProperty != null)
                return false;
        } else if (!cssProperty.equals(other.cssProperty))
            return false;
        if (xpath == null) {
            return other.xpath == null;
        } else return xpath.equals(other.xpath);
    }

    @Override
    public String toString() {
        return "<" + xpath + ", " + cssProperty + ", " + value + ">";
    }
}
