package usc.edu.OwlEye.UIModels;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;

import java.util.List;
import java.util.TreeMap;

public class VSRGNode extends GraphNode{
    private String xpath;
    private String clsName;
    private Node<DomNode> xmlNode;
    private String clusterId;



    public VSRGNode(String xpath,Node<DomNode> xmlNode, String clsName ,String clusterId) {
        super(xpath);
        this.xpath = xpath;

        this.xmlNode = xmlNode;
        this.clsName = clsName;
        this.clusterId = clusterId;
    }

    public VSRGNode(Node<DomNode> xmlNode,String clusterId) {
        super(xmlNode.getData().getxPath());
        this.xpath = xmlNode.getData().getxPath();

        this.xmlNode = xmlNode;
        String clsName = xmlNode.getData().getTagName();
        this.clsName = clsName;
        this.clusterId = clusterId;
    }

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

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clsName == null) ? 0 : clsName.hashCode());

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
        VSRGNode other = (VSRGNode) obj;
        if (clsName == null) {
            if (other.clsName != null)
                return false;
        } else if (!clsName.equals(other.clsName))
            return false;
        if (xpath == null) {
            if (other.xpath != null)
                return false;
        } else if (!xpath.equals(other.xpath))
            return false;
        return true;
    }

    public Node<DomNode> getXmlNode() {
        return xmlNode;
    }

    public void setXmlNode(Node<DomNode> xmlNode) {
        this.xmlNode = xmlNode;
    }

    @Override
    public String toString() {
        String returnValue = "Node   = (Xpath = " + this.xpath + ")\n";

        return returnValue;
    }
}
