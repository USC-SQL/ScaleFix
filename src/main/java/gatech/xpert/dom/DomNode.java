package gatech.xpert.dom;

//import usc.edu.SALEM.VHTree.Rectangle;
import gatech.xpert.dom.visitors.DomVisitor;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.SALEM.util.Util;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DomNode implements generalNode<DomNode> {
    private String tagName, xPath, id;
    private Map<String, String> attributes;
    private int level;
    private List<DomNode> children;
    private DomNode parent;
    private Rectangle coord;
    private double area;
    private double areaInDP;

    private int[] bounds;
    private boolean matched;
    private boolean layout;
    private boolean clickable;
    private boolean visible;
    private boolean longClickable;
    private boolean scrollable;
    private String text;

    private boolean checkable;



    // adapt subtree matching
    private boolean isAssignedRoot;
    private static final Pattern BOUNDS_PATTERN = Pattern
            .compile("\\[-?(\\d+),-?(\\d+)\\]\\[-?(\\d+),-?(\\d+)\\]");
    public int x, y, width, height;

    // modified from the Xpert project: https://github.com/gatech/xpert
    public DomNode() {
        children = new ArrayList<>();

        attributes = new HashMap<>();

    }

    public DomNode(Node node, String xPath) {
        this.setTagName(node.getNodeName());
        this.setxPath(xPath);
        children = new ArrayList<>();

        attributes = new HashMap<>();
        NamedNodeMap nodeMap = node.getAttributes();
        for (int j = 0; j < nodeMap.getLength(); j++) {
            String attr = nodeMap.item(j).getNodeName();
            String val = nodeMap.item(j).getNodeValue();
            attributes.put(attr, val);
            String b = attributes.get("bounds");
            if (b != null) {
                updateBounds(b);
            }
            this.setClickable(Util.isElementClickable(this));
            this.setVisible(Util.isElementVisible(this));
            this.setLongClickable(Util.isElementLongClickable(this));
            this.setScrollable(Util.isElementScrollable(this));
            this.setCheckable(Util.isElementCheckable(this));
            this.setLayout(Util.isElementLayout(this));
            this.text = computeText(this);
            calculateArea();
        }
    }
    public String computeText(DomNode targetNode) {
        String text = "";
        if (targetNode.getAttributes().get("text") != null && !targetNode.getAttributes().get("text").isEmpty()) {
            text = targetNode.getAttributes().get("text") .toString();
        }


        return text;
    }
    public int[] getBounds() {
        return bounds;
    }

    public void setBounds(int[] bounds) {
        this.bounds = bounds;
    }

    public void setLayout(boolean layout) {
        this.layout = layout;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public DomNode(String tagName) {
        setTagName(tagName);
        children = new ArrayList<>();
        attributes = new HashMap<>();
    }

    //  Accept Visitors
    public void accept(DomVisitor visitor) {
        visitor.visit(this);

        for (DomNode child : this.children) {
            child.accept(visitor);
        }
    }

    // Getter & Setter
    public boolean isLongClickable() {
        return longClickable;
    }

    public void setLongClickable(boolean longClickable) {
        this.longClickable = longClickable;
    }

    public boolean isScrollable() {
        return scrollable;
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    public boolean isCheckable() {
        return checkable;
    }

    public void setCheckable(boolean checkable) {
        this.checkable = checkable;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getxPath() {
        return xPath;
    }
    public String getText(){
        return text;
    }

    public void setxPath(String xPath) {
        this.xPath = xPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {

        this.attributes = attributes;

    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<DomNode> getChildren() {
        return children;
    }


    public void setChildren(List<DomNode> children) {
        this.children = children;
    }

    public DomNode getParent() {
        return parent;
    }

    public void setParent(DomNode parent) {
        this.parent = parent;
    }


    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public boolean isAssignedRoot() {
        return isAssignedRoot;
    }

    public void setAssignedRoot(boolean assignedRoot) {
        isAssignedRoot = assignedRoot;
    }

    // Utility methods

    public void addChild(DomNode node) {
        node.setParent(this);
        this.children.add(node);
    }

    public String attr(String key) {
        return attributes.get(key);
    }


    public DomNode getChild(int index) {
        return children.get(index);
    }

    public boolean containsAttr(String key) {
        return attributes.containsKey(key);
    }

    public void setAttr(String attr, String value) {
        attributes.put(attr, value);
    }

    public String getAttr(String attr) {
        return attributes.get(attr);
    }

    public Rectangle getCoord() {
        return coord;
    }

    public int[] getCoords() {
        // here return array insead of rectangle
//        return coord;
        int[] rectangleArray = {x, y, width, height};
        return rectangleArray;
    }

    public void setCoord(Rectangle coord) {
        this.coord = coord;
    }

    public DomNode copy() {
//        private String tagName, xPath, id;
//        private Map<String, String> attributes;
//        private int level;
//        private List<DomNode> children;
//        private DomNode parent;
//        private Rectangle coord;
//        private boolean matched;
//        // adapt subtree matching
//        private boolean isAssignedRoot;
        DomNode copy = new DomNode();
        copy.xPath = xPath;
        copy.tagName = tagName;
        copy.id = id;
        copy.coord = new Rectangle(coord);
//        copy.textCoords = new ArrayList<Rectangle>(textCoords);
//        copy.isVisible = isVisible;
//        if(textContent != null)
//        {
//            copy.textContent = new ArrayList<String>(textContent);
//        }
        if (attributes != null) {
            copy.attributes = new HashMap<>(attributes);
        }
//        if(cssMap != null)
//        {
//            copy.cssMap = new HashMap<>(cssMap);
//        }
        if (children != null) {
            copy.children = new ArrayList<>(children);
        }
        return copy;
    }
//    public void setCoord(){
//        int[] coords = { data.getInt(0), data.getInt(1), data.getInt(2), data.getInt(3) };
//        Rectangle rect = new Rectangle(coords[0], coords[1], (coords[2] - coords[0]), (coords[3] - coords[1]));
//    }
    //Ali: Copied from UiNode Class

    public void updateBounds(String bounds) {
        Matcher m = BOUNDS_PATTERN.matcher(bounds);
        if (m.matches()) {
            x = Integer.parseInt(m.group(1));
            y = Integer.parseInt(m.group(2));
            width = Integer.parseInt(m.group(3)) - x;
            height = Integer.parseInt(m.group(4)) - y;
            // Update the rectangle that we add following mfix
            this.getAttributes().put("bounds", bounds);
            setBounds(new int[]{x, y, width + x, height + y});
            updateRectangle(bounds);
        } else {
            throw new RuntimeException("Invalid bounds: " + bounds);
        }
    }

    private void updateRectangle(String bounds) {
        Rectangle coord = getNodeCoordinates(this);
        this.setCoord(coord);
    }

    //Ali similar to HtmlDomTree
    private static Rectangle getNodeCoordinates(DomNode node) {
        String bounds = node.getAttr("bounds");
        if (bounds == null) {
            return null; // hierarchy
        }
        bounds = bounds.substring(1, bounds.length() - 1).replace("][", ",");
        String[] coord = bounds.split(",");
//        Rectangle coord = new Rectangle();
        int[] coords = {Integer.parseInt(coord[0]), Integer.parseInt(coord[1]), Integer.parseInt(coord[2]), Integer.parseInt(coord[3])};
        Rectangle rect = new Rectangle(coords[0], coords[1], (coords[2] - coords[0]), (coords[3] - coords[1]));
        return rect;
    }


    public Map<String, String> getExplicitCSS() {
        // To use with ifix clusting
        // I may need to filter these and only choose subset of them that we really want to use in measuring the distance
        return this.getAttributes();
    }
    public void calculateArea() {
            area=(width * height);
            areaInDP=area / OwlConstants.PHONE_DENSITY;
    }


    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getAreaInDP() {
        return areaInDP;
    }

    public void setAreaInDP(double areaInDP) {
        this.areaInDP = areaInDP;
    }

    public boolean isLayout() {
        return layout;
    }

}
