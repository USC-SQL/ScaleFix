package gatech.xpert.dom;

import com.google.common.collect.Lists;
//import usc.edu.SALEM.VHTree.Node;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DomUtils {

    // Calculates the DOM Match-Index Metric from the WebDiff paper (Ref: roychoudhary10icsm)
    public static float calculateMatchIndexBeforeIfix(gatech.xpert.dom.DomNode x, gatech.xpert.dom.DomNode y) {
        float XPATH = 1.0f;
        String xPath1 = processXpath(x.getxPath());
        String xPath2 = processXpath(y.getxPath());

        LevenshteinDistance ld = new LevenshteinDistance();
        float xPathSim = 1 - ld.apply(xPath1, xPath2)
                / (float) Math.max(xPath1.length(), xPath2.length());
        float matchIndex = XPATH * xPathSim;
        return matchIndex;
    }

    public static float calculateMatchIndex(Node<DomNode> x, Node<DomNode> y) {
        float XPATH = 1.0f;
        String xPath1 = processXpath(x.getData().getxPath());
        String xPath2 = processXpath(y.getData().getxPath());

        LevenshteinDistance ld = new LevenshteinDistance();
        float xPathSim = 1 - ld.apply(xPath1, xPath2)
                / (float) Math.max(xPath1.length(), xPath2.length());
        float matchIndex = XPATH * xPathSim;
        return matchIndex;
    }

    public static float calculateMatchIndex(DomNode x, DomNode y) {
        float XPATH = 1.0f;
        String xPath1 = processXpath(x.getxPath());
        String xPath2 = processXpath(y.getxPath());

        LevenshteinDistance ld = new LevenshteinDistance();
        float xPathSim = 1 - ld.apply(xPath1, xPath2)
                / (float) Math.max(xPath1.length(), xPath2.length());
        float matchIndex = XPATH * xPathSim;
        return matchIndex;
    }

    private static String processXpath(String xPath) {
//		System.out.println(xPath);
        xPath = xPath.replace("//", "/");
        // some heuristics to make sure some class to match with "View" in dynamic XMLs
        xPath = xPath.replace("android.support.design.widget.CoordinatorLayout", "android.view.View");
        xPath = xPath.replace("android.support.constraint.ConstraintLayout", "android.view.View");
        xPath = xPath.replace("android.support.v7.widget.CardView", "FrameLayout");
        xPath = xPath.replace("android.support.design.widget.FloatingActionButton", "ImageButton");
        xPath = xPath.replace("android.support.design.widget.AppBarLayout", "LinearLayout");
        xPath = xPath.replace("android.support.design.widget.NavigationView", "FrameLayout");
        xPath = xPath.replace("android.support.v4.widget.SwipeRefreshLayout", "android.view.ViewGroup");
        return xPath;
    }

    public static String getXPath(gatech.xpert.dom.DomNode node) {
        String nodeId = node.getId();

        //Ali modify Xpath to keep full path with ID
//        if (nodeId != null) {
//            return "//*[@id=\"" + nodeId + "\"]";
//        }
        String nodeTag = node.getTagName();
        if (node.getParent() == null) {
            if (nodeId != null) {
                return "/" + nodeTag + "[@id=\"" + nodeId + "\"]";
            } else {
                return "/" + nodeTag + "[1]";
            }
        }

        ArrayList<String> paths = new ArrayList<>();
        for (; node != null; node = node.getParent()) {
            int index = 0;

            gatech.xpert.dom.DomNode parent = node.getParent();
            String tagName = node.getTagName();
            if (parent != null) {
                List<gatech.xpert.dom.DomNode> sameGen = parent.getChildren();
                for (int i = 0; i < sameGen.size(); i++) {
                    gatech.xpert.dom.DomNode curr = sameGen.get(i);
                    if (curr.equals(node)) {
                        break;
                    }

                    if (curr.getTagName().equals(tagName)) {
                        index++;
                    }
                }
            }
            String pathIndex;
            if (nodeId != null) {
//                 tagName +"[@id=\"" + nodeId + "\"]";}
                pathIndex = "[" + "[@id=\"" + nodeId + "\"]" + "]";
            } else {
                pathIndex = "[" + (index + 1) + "]";
            }

            paths.add(tagName + pathIndex);
        }

        String result = null;
        if (paths.size() > 0) {
            result = "/";
            for (int i = paths.size() - 1; i > 0; i--) {
                result = result + paths.get(i) + "/";
            }
            result = result + paths.get(0);
        }

        return result;
    }

    public static int getDisplayArea(gatech.xpert.dom.DomNode node) {
        if (node.getAttr("bounds") == null) {
            return 0;
        }
        List<gatech.xpert.dom.DomNode> children = node.getChildren();
        String bounds = node.getAttr("bounds");
        List<String> childBounds = Lists.newArrayList();
        children.forEach(c -> {
            if (c.getAttr("bounds") != null)
                childBounds.add(c.getAttr("bounds"));
        });
        return getDisplayArea(bounds, childBounds);
    }

    public static int getDisplayArea(String bounds, List<String> childBounds) {
        bounds = bounds.substring(1, bounds.length() - 1).replace("][", ",");
        String[] coord = bounds.split(",");
        int parentArea = (Integer.valueOf(coord[2]) - Integer.valueOf(coord[0])) *
                (Integer.valueOf(coord[3]) - Integer.valueOf(coord[1]));
        for (String bound : childBounds) {
            String inB = bound.substring(1, bound.length() - 1).replace("][", ",");
            coord = inB.split(",");
            parentArea -= (Integer.valueOf(coord[2]) - Integer.valueOf(coord[0])) *
                    (Integer.valueOf(coord[3]) - Integer.valueOf(coord[1]));
        }
        return parentArea;
    }

    public static boolean isNaiveMatch(Node<gatech.xpert.dom.DomNode> x, Node<gatech.xpert.dom.DomNode> y) {
        String xPath1 = processXpath(x.getData().getxPath());
        String xPath2 = processXpath(y.getData().getxPath());
        return xPath1.equals(xPath2);
    }

    public static boolean isNaiveMatch(gatech.xpert.dom.DomNode x, gatech.xpert.dom.DomNode y) {
        String xPath1 = processXpath(x.getxPath());
        String xPath2 = processXpath(y.getxPath());
        return xPath1.equals(xPath2);
    }

    public static List<Node<DomNode>> getLeaves(Node<DomNode> root) {
        List<Node<DomNode>> leafElements = new ArrayList<>();

        // depth first traversal: preorder
        Stack<Node<DomNode>> stack = new Stack<Node<DomNode>>();
        stack.add(root);

        while (!stack.isEmpty()) {
            Node<DomNode> node = stack.pop();
            //   DomNode e = node.getData();
            Node<DomNode> e = node;
            if (node.getChildren() != null) {
                if (node.getChildren().size() > 0) {
                    for (Node<DomNode> child : node.getChildren()) {
                        stack.add(child);
                    }
                }

                // check if the element is a text node (the parent element might not necessarily be a leaf node)
//              // Ali temp
//                if(e.isVisible() && e.getTextCoords().size() > 0)
//                {
                //     leafElements.add(e);
                // }
            }
            //Ali temp
            else {
                // general leaf nodes
//                if(e.isVisible())
//                {
//                    // further filtration criteria: consider element as not visible if it has no text, image, or background-image (e.g. it is a spacer div)
//                    if(e.getTextCoords().size() > 0 ||
//                            e.getTagName().equalsIgnoreCase("img") ||
//                            e.getCssMap().containsKey("background-image") ||
//                            e.getTagName().equalsIgnoreCase("input") ||
//                            e.getTagName().equalsIgnoreCase("button"))
                //     {
                leafElements.add(e);
//                    }
//                }
            }
        }
        return leafElements;
    }

    public static List<DomNode> getLeaves(DomNode root) {
        List<DomNode> leafElements = new ArrayList<DomNode>();

        // depth first traversal: preorder
        Stack<DomNode> stack = new Stack<DomNode>();
        stack.add(root);

        while (!stack.isEmpty()) {
            DomNode node = stack.pop();
            //   DomNode e = node.getData();
            DomNode e = node;

            if (node.getChildren().size() > 0) {
                for (DomNode child : node.getChildren()) {
                    stack.add(child);
                }

                // check if the element is a text node (the parent element might not necessarily be a leaf node)
//              // Ali temp
//                if(e.isVisible() && e.getTextCoords().size() > 0)
//                {
                //     leafElements.add(e);
                // }
            }
            //Ali temp
            else {
                // general leaf nodes
//                if(e.isVisible())
//                {
//                    // further filtration criteria: consider element as not visible if it has no text, image, or background-image (e.g. it is a spacer div)
//                    if(e.getTextCoords().size() > 0 ||
//                            e.getTagName().equalsIgnoreCase("img") ||
//                            e.getCssMap().containsKey("background-image") ||
//                            e.getTagName().equalsIgnoreCase("input") ||
//                            e.getTagName().equalsIgnoreCase("button"))
                //     {
                leafElements.add(e);
//                    }
//                }
            }
        }
        return leafElements;
    }
}
