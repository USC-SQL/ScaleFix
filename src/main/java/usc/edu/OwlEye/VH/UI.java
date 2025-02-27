package usc.edu.OwlEye.VH;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import usc.edu.OwlEye.VHTree.XMLUtils;
import usc.edu.OwlEye.util.Utils;

import java.util.LinkedList;
import java.util.Queue;

/*
I created this class to enclose the XMLUtil class that I was using to manage Vh that is in VHTree
 */
public class UI {
    String UITitle;
    XMLUtils XMLTree;
    String xmlFilePath;
    String pngFilePath;
    int noOfElements;
     double totalArea;
     double maxArea;
     double height;
        double width;
     int noOfLeaveNodes;

    public UI(String UITitle, XMLUtils XMLTree, String xmlFilePath, String pngFilePath) {
        this.UITitle = UITitle;
        this.XMLTree = XMLTree;
        this.xmlFilePath = xmlFilePath;
        this.pngFilePath = pngFilePath;
    }

    public XMLUtils getXMLTree() {
        return XMLTree;
    }

    public void setXMLTree(XMLUtils XMLTree) {
        this.XMLTree = XMLTree;
    }

    public String getXmlFilePath() {
        return xmlFilePath;
    }

    public void setXmlFilePath(String xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
    }

    public String getPngFilePath() {
        return pngFilePath;
    }

    public void setPngFilePath(String pngFilePath) {
        this.pngFilePath = pngFilePath;
    }

    public String getUITitle() {
        return UITitle;
    }

    public void setUITitle(String UITitle) {
        this.UITitle = UITitle;
    }

    public void calculateMaxAreaForAllElements(){
        double area=0;
        Queue<Node<DomNode>> q = new LinkedList<>();
        q.add(XMLTree.getRoot());

        while (!q.isEmpty()) {
            Node<DomNode> node = q.remove();
            area+=node.getData().getAreaInDP();
            if (node.getChildren() != null) {
                for (Node<DomNode> child : node.getChildren()) {
                    q.add(child);
                }
            }
        }
           this.maxArea=area;
    }

    public void calculateNoOfLeaveNodes(){
        double area=0;
        int noOfLeaveNodes=0;
        Queue<Node<DomNode>> q = new LinkedList<>();
        q.add(XMLTree.getRoot());

        while (!q.isEmpty()) {
            Node<DomNode> node = q.remove();
            if (Utils.isLeaveNode(node)){
                area+=node.getData().getAreaInDP();;
                height+=node.getData().height;
                width+=node.getData().width;
                noOfLeaveNodes++;
            }
            if (node.getChildren() != null) {
                for (Node<DomNode> child : node.getChildren()) {
                    q.add(child);
                }
            }
        }
        this.noOfLeaveNodes=noOfLeaveNodes;
        this.totalArea=area;
    }

    public double getTotalArea() {
        return totalArea;
    }
    public int getNoOfLeaveNodes() {
        return noOfLeaveNodes;
    }
    public double getMaxArea() {
        return maxArea;
    }
    public double getHeight() {
        return height;
    }
    public double getWidth() {
        return width;
    }
}
