package usc.edu.OwlEye.GAChanges;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import usc.edu.OwlEye.ElementsProperties.Property;
import usc.edu.OwlEye.UIModels.GraphNode;

import java.util.ArrayList;
import java.util.HashMap;

public class GAElementToChange {
private String xpath;
private String id;
private Node<DomNode> node;
private GraphNode graphNode; // this is the graph for SRG, TRG, etc (null if this change is not derived from a graph)

private String source;  // how it was added to the lsit of changes (i.e., problematic element, from SRG, from VSRG, from layout.)
private String typeOfChange; // type of change (i.e., add, remove, increase, decrease.)

private HashMap<String, ArrayList<String>> propertiesToChange; // String: the property to change, ArrayList<String>: the values to change it to (initial suggested values)private HashMap<String, ArrayList<String>> propertiesToChange; // String: the property to change, ArrayList<String>: the values to change it to (initial suggested values)
 private HashMap<String, Property> propertiesToChangeObject; // String: the property to change, ArrayList<String>: the values to change it to (initial suggested values)
private HashMap<String,GAChange> listOfChanges; // String: the property to change, String: the value to change it to (final value)

    private String   directionFocus; // either height or width based on the cutoff type
    public String generalPropertyToChange; //for collision such as size, layout_direction, scrollableTextView
    private double heightRatio; // for SRG and VSRG
private double widthRatio; // for SRG and VSRG
    public GAElementToChange(String xpath, String id, Node<DomNode> node, String source, String typeOfChange) {
        this.xpath = xpath;
        this.id = id;
        this.node = node;
        this.source = source;
        this.typeOfChange = typeOfChange;

    }
    public GAElementToChange(Node<DomNode> node, String source, String typeOfChange, String directionFocus) {
        this.xpath = node.getData().getxPath();
        this.id = node.getData().getId();
        this.node = node;
        this.source = source;
        this.typeOfChange = typeOfChange;
        this.directionFocus = directionFocus;
    }
    public GAElementToChange(Node<DomNode> node, String source, String typeOfChange, String directionFocus,String generalPropertyToChange) {
        this.xpath = node.getData().getxPath();
        this.id = node.getData().getId();
        this.node = node;
        this.source = source;
        this.typeOfChange = typeOfChange;
        this.directionFocus = directionFocus;
        this.generalPropertyToChange = generalPropertyToChange;
    }

    public GAElementToChange(Node<DomNode> node, String source, String typeOfChange, String directionFocus,GraphNode graphNode) {
        this.xpath = node.getData().getxPath();
        this.id = node.getData().getId();
        this.node = node;
        this.source = source;
        this.typeOfChange = typeOfChange;
        this.directionFocus = directionFocus;
        this.graphNode = graphNode;
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

    public Node<DomNode> getNode() {
        return node;
    }

    public void setNode(Node<DomNode> node) {
        this.node = node;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTypeOfChange() {
        return typeOfChange;
    }
    public void setTypeOfChange(String typeOfChange) {
        this.typeOfChange = typeOfChange;
    }



    public HashMap<String, ArrayList<String>> getPropertiesToChange() {
        return propertiesToChange;
    }

    public void setPropertiesToChange(HashMap<String, ArrayList<String>> propertiesToChange) {
        this.propertiesToChange = propertiesToChange;
    }

    public void addPropertyToChange(String property, ArrayList<String> values) {
        if (this.propertiesToChange == null) {
            this.propertiesToChange = new HashMap<String, ArrayList<String>>();
        }
        this.propertiesToChange.put(property, values);
    }
    public void addPropertyToChange(String property, String value) {
        if (this.propertiesToChange == null) {
            this.propertiesToChange = new HashMap<String, ArrayList<String>>();
        }

        if (this.propertiesToChange.containsKey(property)) {
            this.propertiesToChange.get(property).add(value);
        } else {
            ArrayList<String> values = new ArrayList<String>();
            values.add(value);
            this.propertiesToChange.put(property, values);
        }
      
    }

    public GraphNode getGraphNode() {
        return graphNode;
    }

    public void setGraphNode(GraphNode graphNode) {
        this.graphNode = graphNode;
    }

    public HashMap<String, Property> getPropertiesToChangeObject() {
        return propertiesToChangeObject;
    }

    public void setPropertiesToChangeObject(HashMap<String, Property> propertiesToChangeObject) {
        this.propertiesToChangeObject = propertiesToChangeObject;
    }

    public HashMap<String, GAChange> getListOfChanges() {
        return listOfChanges;
    }

    public void setListOfChanges(HashMap<String, GAChange> listOfChanges) {
        this.listOfChanges = listOfChanges;
    }

    public void addChange(String property, GAChange change) {
        if (this.listOfChanges == null) {
            this.listOfChanges = new HashMap<String, GAChange>();
        }
        this.listOfChanges.put(property, change);
    }

//    public void addChangeRatioForRelatedElements(double ratio) {
//
//
//    }

    public void setHeightRatio(double heightRatio) {
        // we use that for SRG elements for now but we could also use it VSRG elements
        this.heightRatio = heightRatio;
    }

    public void setWidthRatio(double widthRatio) {
        // we use that for SRG elements for now but we could also use it VSRG elements
        this.widthRatio = widthRatio;
    }

    public String getDirectionFocus() {
        return directionFocus;
    }

    public void setDirectionFocus(String directionFocus) {
        this.directionFocus = directionFocus;
    }
}
