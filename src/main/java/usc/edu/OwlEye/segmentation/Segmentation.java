package usc.edu.OwlEye.segmentation;

import DBSCAN.clustring.ElementWrapper;
import DBSCAN.clustring.ElementsClusterer;
import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import org.apache.commons.math3.ml.clustering.Cluster;
import usc.edu.SALEM.Constants;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.VHTree.XMLUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class Segmentation {
    private double segmentTerminateThreshold;

    private List<Segment> segments;
    private List<Node<DomNode>> leaves;
    private static Rectangle viewportRectangle;

    public List<Segment> getSegments() {
        return segments;
    }

    public List<Node<DomNode>> getLeaves() {
        return leaves;
    }

    public void setLeaves(List<Node<DomNode>> leaves) {
        this.leaves = leaves;
    }

    public static Rectangle getViewportRectangle() {
        return viewportRectangle;
    }

    public static void setViewportRectangle(Rectangle viewportRectangle) {
        Segmentation.viewportRectangle = viewportRectangle;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    public double getSegmentTerminateThreshold() {
        return segmentTerminateThreshold;
    }

    public void setSegmentTerminateThreshold(double segmentTerminateThreshold) {
        this.segmentTerminateThreshold = segmentTerminateThreshold;
    }

    public void calculateSegmentThresholdValue() {
        segmentTerminateThreshold = Constants.SEGMENT_TERMINATE_THRESHOLD;

        // strategy 1: average leaf depth / 2
        int avgLeafDepth = 0;
        for (Node<DomNode> leaf : leaves) {
            String[] xpathArray = leaf.getData().getxPath().split("/");
            avgLeafDepth = avgLeafDepth + (xpathArray.length - 1);
        }
        avgLeafDepth = avgLeafDepth / leaves.size();
        double newValue = avgLeafDepth / 2;
        segmentTerminateThreshold = Math.min(segmentTerminateThreshold, newValue);

        // strategy 2: max leaf depth / 2
//		int maxLeafDepth = Integer.MIN_VALUE;
//		for(domTree.Node<DomNode> leaf : leaves)
//		{
//			String xpathArray[] = leaf.getData().getxPath().split("/");
//			if((xpathArray.length-1) > maxLeafDepth)
//			{
//				maxLeafDepth = xpathArray.length-1;
//			}
//		}
//        segmentTerminateThreshold = maxLeafDepth / 2;
    }

    //    public static Rectangle getSegmentMBR(Segment seg) {
//        if (seg.getMembers().size() == 0) {
//            return seg.getMinimumBoundingRectangle();
//        }
//
//        int minX, minY, maxX, maxY;
//        minX = minY = Integer.MAX_VALUE;
//        maxX = maxY = Integer.MIN_VALUE;
//        //	HtmlDomTree domTree = HtmlDomTree.getInstance(main.try_segment.getFilepath()); Ali
//        //DomNode domTree=merge.XMLUtils.readXML((main.try_segment.getFilepath()));
//        DomNode domTree = XMLUtils.getRoot().getData();
//        for (String xpath : seg.getMembers()) {
//            //	HtmlElement leaf = domTree.searchHtmlDomTreeByXpath(xpath).getData(); Ali
////            merge.XMLUtils x = XMLUtils.getInstance(try_segment.getFilepath());
//
//            DomNode leaf = XMLUtils.searchHtmlDomTreeByXpath(xpath, domTree);
//
//            // check if leaf is a text node
//            //Ali
////			if (leaf.getTextCoords().size() > 0)
////			{
////				// iterate over all text bounding boxes
////				for (domTree.Rectangle r : leaf.getTextCoords())
////				{
////					minX = Math.min(r.x, minX);
////					minY = Math.min(r.y, minY);
////					maxX = Math.max(r.x + r.width, maxX);
////					maxY = Math.max(r.y + r.height, maxY);
////				}
////			}
////			else
//            //{
//            minX = Math.min(leaf.getCoord().x, minX);
//            minY = Math.min(leaf.getCoord().y, minY);
//            maxX = Math.max(leaf.getCoord().x + leaf.getCoord().width, maxX);
//            maxY = Math.max(leaf.getCoord().y + leaf.getCoord().height, maxY);
//            //}
//        }
//        Rectangle mbr = new Rectangle(minX, minY, (maxX - minX), (maxY - minY));
//        return mbr;
//    }
    public static Rectangle getSegmentMBR(Segment seg) {
        /*** After If DB-Scan Segmentation ***/
        if (seg.getMembers().size() == 0) {
            return seg.getMinimumBoundingRectangle();
        }

        int minX, minY, maxX, maxY;
        minX = minY = Integer.MAX_VALUE;
        maxX = maxY = Integer.MIN_VALUE;
        //	HtmlDomTree domTree = HtmlDomTree.getInstance(main.try_segment.getFilepath()); Ali
        //DomNode domTree=merge.XMLUtils.readXML((main.try_segment.getFilepath()));
        Node<DomNode> domTree = XMLUtils.getRoot();
        for (String xpath : seg.getMembers()) {
            //	HtmlElement leaf = domTree.searchHtmlDomTreeByXpath(xpath).getData(); Ali
//            merge.XMLUtils x = XMLUtils.getInstance(try_segment.getFilepath());

            Node<DomNode> leaf = XMLUtils.searchVHTreeByXpath(xpath, domTree);

            // check if leaf is a text node
            //Ali
//			if (leaf.getTextCoords().size() > 0)
//			{
//				// iterate over all text bounding boxes
//				for (domTree.Rectangle r : leaf.getTextCoords())
//				{
//					minX = Math.min(r.x, minX);
//					minY = Math.min(r.y, minY);
//					maxX = Math.max(r.x + r.width, maxX);
//					maxY = Math.max(r.y + r.height, maxY);
//				}
//			}
//			else
            //{
            minX = Math.min(leaf.getData().getCoord().x, minX);
            minY = Math.min(leaf.getData().getCoord().y, minY);
            maxX = Math.max(leaf.getData().getCoord().x + leaf.getData().getCoord().width, maxX);
            maxY = Math.max(leaf.getData().getCoord().y + leaf.getData().getCoord().height, maxY);
            //}
        }
        Rectangle mbr = new Rectangle(minX, minY, (maxX - minX), (maxY - minY));
        return mbr;
    }

    public void performSegmentation(List<Node<DomNode>> leaves) {
        this.leaves = leaves;
        segments = new ArrayList<Segment>();

        // set dynamic value for segment termination threshold
        calculateSegmentThresholdValue();

        // Assign each leaf to its own segment
        int count = 1;
        for (Node<DomNode> leaf : leaves) {


            List<String> segmentMembers = new ArrayList<>();
            // Ali test if we check for clickable will produce better results
            String tagName = leaf.getData().getTagName();
            String isclickabe = leaf.getData().getAttributes().get("clickable");
            int sizen = leaf.getParent().getChildren().size();
            String paenClickable = leaf.getParent().getData().getAttributes().get("clickable");
            if (leaf.getData().getAttributes().get("clickable").equalsIgnoreCase("false")) {
                if (leaf.getParent().getChildren().size() <= 1 && leaf.getParent().getData().getAttributes().get("clickable").equalsIgnoreCase("true")
                        && leaf.getData().getTagName().equalsIgnoreCase("ImageView")) {
                    // only one child and the parent is clickable lets keep that in segment already
                    segmentMembers.add(leaf.getParent().getData().getxPath());
                }
            }
            //end of ALi modification
            segmentMembers.add(leaf.getData().getxPath());
            Segment s = new Segment(count, segmentMembers, leaf.getData().getxPath());
            segments.add(s);
            count++;
        }

        while (true) {
            // Compute the cost C(x,y) of merging each adjacent pair (x,y) of segments
            double minCost = Double.MAX_VALUE;
            Segment xMin = null;
            Segment yMin = null;
            for (int i = 0, j = i + 1; i < segments.size() && j < segments.size(); i++, j++) {
                Segment x = segments.get(i);
                Segment y = segments.get(j);

                double costXY = getCost(x.getLowestCommonAncestor(), y.getLowestCommonAncestor());

                // Locate the x=x* and y=y* for which C(x,y) is minimal
                if (costXY < minCost) {
                    minCost = costXY;
                    xMin = x;
                    yMin = y;
                }
            }

            // If C(x,y) = ∞ for all x,y then end
            if (minCost == Double.MAX_VALUE) {
                break;
            }

            // Merge segments x* and y*
            mergeSegments(xMin, yMin);
        }

        // update segment ids in a sequential order
        int cnt = Constants.VIEWPORT_SEGMENT_ID + 1;
        for (Segment s : segments) {
            s.setId(cnt);
            cnt++;
        }


        // update segments with MBRs
        for (Segment s : segments) {
            s.setMinimumBoundingRectangle(getSegmentMBR(s));
        }

        // add a "ghost" segment for the viewport

        //Ali comment - no need for this Iguess
//		Segment segGhost = new Segment();
//		segGhost.setId(util.Constants.VIEWPORT_SEGMENT_ID);
//		segGhost.setMinimumBoundingRectangle(viewportRectangle);
//		segments.add(segGhost);

        // draw segments
        //String imageFilepath = getOutputFolderPath() + File.separatorChar + "index-segmentation-" + segmentTerminateThreshold + "-screenshot.png";
        //WebDriverSingleton.takeScreenshot(imageFilepath); Ali, I am adding the file from dynamic
        String imageFilepath = SALEM.getMergePath() + File.separator + SALEM.getCurrentActivityName() + ".png";
        try {
            drawSegments(imageFilepath);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    //    //Before changing DomNode
//    public void performSegmentation(List<DomNode> leaves) {
//        this.leaves = leaves;
//        segments = new ArrayList<Segment>();
//
//        // set dynamic value for segment termination threshold
//        calculateSegmentThresholdValue();
//
//        // Assign each leaf to its own segment
//        int count = 1;
//        for (DomNode leaf : leaves) {
//
//
//            List<String> segmentMembers = new ArrayList<>();
//            // Ali test if we check for clickable will produce better results
//            if (leaf.getAttributes().get("clickable") == "false") {
//                if (leaf.getParent().getChildren().size() <= 1 && leaf.getParent().getAttributes().get("clickable") == "true") {
//                    // only one child and the parent is clickable lets keep that in segment already
//                    segmentMembers.add(leaf.getParent().getxPath());
//                }
//            }
//            //end of ALi modification
//            segmentMembers.add(leaf.getxPath());
//            Segment s = new Segment(count, segmentMembers, leaf.getxPath());
//            segments.add(s);
//            count++;
//        }
//
//        while (true) {
//            // Compute the cost C(x,y) of merging each adjacent pair (x,y) of segments
//            double minCost = Double.MAX_VALUE;
//            Segment xMin = null;
//            Segment yMin = null;
//            for (int i = 0, j = i + 1; i < segments.size() && j < segments.size(); i++, j++) {
//                Segment x = segments.get(i);
//                Segment y = segments.get(j);
//
//                double costXY = getCost(x.getLowestCommonAncestor(), y.getLowestCommonAncestor());
//
//                // Locate the x=x* and y=y* for which C(x,y) is minimal
//                if (costXY < minCost) {
//                    minCost = costXY;
//                    xMin = x;
//                    yMin = y;
//                }
//            }
//
//            // If C(x,y) = ∞ for all x,y then end
//            if (minCost == Double.MAX_VALUE) {
//                break;
//            }
//
//            // Merge segments x* and y*
//            mergeSegments(xMin, yMin);
//        }
//
//        // update segment ids in a sequential order
//        int cnt = util.Constants.VIEWPORT_SEGMENT_ID + 1;
//        for (Segment s : segments) {
//            s.setId(cnt);
//            cnt++;
//        }
//
//        // update segments with MBRs
//        for (Segment s : segments) {
//            s.setMinimumBoundingRectangle(getSegmentMBR(s));
//        }
//
//        // add a "ghost" segment for the viewport
//
//        //Ali comment - no need for this Iguess
////		Segment segGhost = new Segment();
////		segGhost.setId(util.Constants.VIEWPORT_SEGMENT_ID);
////		segGhost.setMinimumBoundingRectangle(viewportRectangle);
////		segments.add(segGhost);
//
//        // draw segments
//        //String imageFilepath = getOutputFolderPath() + File.separatorChar + "index-segmentation-" + segmentTerminateThreshold + "-screenshot.png";
//        //WebDriverSingleton.takeScreenshot(imageFilepath); Ali, I am adding the file from dynamic
//        String imageFilepath = TTFIX.merged_layout_files + File.separator + TTFIX.current_activity_name + ".png";
//        try {
//            drawSegments(imageFilepath);
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//    }
    private void mergeSegments(Segment x, Segment y) {
        // copy member of segment y to segment x
        x.addMembers(y.getMembers());

        // update segment xpath of x
        String newXpath = getLowestCommonAncestor(x.getMembers());
        x.setLowestCommonAncestor(newXpath);

        // remove y from segments
        segments.remove(y);
    }

    public void performSegmentation() {
        /*** If DB-SCAN  approach ***/

        segments = new ArrayList<Segment>();
        ElementsClusterer clusterer = new ElementsClusterer(XMLUtils.getRoot());
        List<Cluster<ElementWrapper>> clusterResults = clusterer.perfomrClustering();

        ArrayList<ArrayList<DomNode>> domNodesClusters = new ArrayList<ArrayList<DomNode>>();
        List<String> segmentMembers;
        int count = 1;
        ArrayList<ArrayList<String>> clustersXpaths = clusterer.getClustringResultsXpaths();


        for (ArrayList<String> cluterXpaths : clustersXpaths
        ) {
            segmentMembers = new ArrayList<>();
            for (String xpath : cluterXpaths
            ) {
                segmentMembers.add(xpath);
            }
            String lowestCommonXpath = getLowestCommonAncestor(segmentMembers);

            Segment s = new Segment(count, segmentMembers, lowestCommonXpath);
            segments.add(s);
            count++;
        }
        for (Segment s : segments) {
            s.setMinimumBoundingRectangle(getSegmentMBR(s));
        }


    }

    private double getCost(String xpath1, String xpath2) {
        // DOM distance between two nodes
        String[] xpath1Array = xpath1.split("/");
        String[] xpath2Array = xpath2.split("/");
        int expectedLength = xpath1Array.length - 1;
        int actualLength = xpath2Array.length - 1;
        int distance;

        int matchingCount = 0;
        for (int i = 1; i < xpath1Array.length && i < xpath2Array.length; i++) {
            if (xpath1Array[i].equals(xpath2Array[i])) {
                matchingCount++;
            } else {
                break;
            }
        }

        distance = (actualLength - matchingCount) + (expectedLength - matchingCount);

        /***   if the element is webivew then ToDO: apply heuristic and also think about more heuristic***/
        Node<DomNode> node1 = XMLUtils.searchVHTreeByXpath(xpath1, XMLUtils.getRoot());
        Node<DomNode> node2 = XMLUtils.searchVHTreeByXpath(xpath2, XMLUtils.getRoot());
        String node1Class = node1.getData().getTagName().toLowerCase();
        String node2Class = node2.getData().getTagName().toLowerCase();
        if (node1Class.contains("webview") || node2Class.contains("webview")) {
            return Double.MAX_VALUE;
        }
        if (distance > segmentTerminateThreshold) {
            return Double.MAX_VALUE;    // infinity
        }

        return distance;
    }

    public String getLowestCommonAncestor(List<String> xpaths) {
        if (xpaths.size() == 0) {
            return "";
        }

        String lowestCommonSubstring = "";
        List<String> xpathsList = new ArrayList<>(xpaths);    // to allow get by index
        String[] xpath1Array = xpathsList.get(0).split("/");

        for (int i = 1; i < xpath1Array.length; i++)    // xpath1Array[0] = ""
        {
            for (int j = 1; j < xpathsList.size(); j++) {
                String[] xpath2Array = xpathsList.get(j).split("/");
                if (i >= xpath2Array.length) {
                    return lowestCommonSubstring;
                }

                if (!xpath1Array[i].equalsIgnoreCase(xpath2Array[i])) {
                    return lowestCommonSubstring;
                }
            }
            lowestCommonSubstring = lowestCommonSubstring + "/" + xpath1Array[i];
        }
        return lowestCommonSubstring;
    }

    public Map<String, List<Rectangle>> getSegmentRectangles() {
        Map<String, List<Rectangle>> segmentRectangles = new HashMap<String, List<Rectangle>>();

        for (Segment s : segments) {
            if (s.getId() == 0)
                continue;

            List<Rectangle> members = new ArrayList<>();
            List<Rectangle> membersTemp = new ArrayList<>();
            int minX, minY, maxX, maxY;
            minX = minY = Integer.MAX_VALUE;
            maxX = maxY = Integer.MIN_VALUE;
            for (String xpath : s.getMembers()) {
                for (Node<DomNode> leaf : leaves) {
                    if (leaf.getData().getxPath().equalsIgnoreCase(xpath)) {
                        membersTemp.add(new Rectangle(leaf.getData().getCoord().x, leaf.getData().getCoord().y, leaf.getData().getCoord().width, leaf.getData().getCoord().height));
                        minX = Math.min(leaf.getData().getCoord().x, minX);
                        minY = Math.min(leaf.getData().getCoord().y, minY);
                        maxX = Math.max(leaf.getData().getCoord().x + leaf.getData().getCoord().width, maxX);
                        maxY = Math.max(leaf.getData().getCoord().y + leaf.getData().getCoord().height, maxY);
                        break;
                    }
                }
            }
            Rectangle mbr = new Rectangle(minX, minY, (maxX - minX), (maxY - minY));
            if (s.getMembers().size() == 0) {
                mbr = s.getMinimumBoundingRectangle();
            }

            s.setMinimumBoundingRectangle(mbr);
            members.add(mbr);
            members.addAll(membersTemp);
            segmentRectangles.put("S" + s.getId(), members);
        }

        return segmentRectangles;
    }

    public Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16));
    }

    private Color getRandomColor(List<String> visitedColors) {
        Random rand = new Random();
        int cnt = 0;
        while (cnt < 50) {
            int r = rand.nextInt(255);
            int g = rand.nextInt(255);
            int b = rand.nextInt(255);
            Color randomColor = new Color(r, g, b);
            String color = String.format("#%02x%02x%02x", r, g, b);
            if (!visitedColors.contains(color)) {
                visitedColors.add(color);
                return randomColor;
            }
            cnt++;
        }
        return hex2Rgb(visitedColors.get(0));
    }

    public void drawSegments(String imagePath) throws IOException {
        Map<String, List<Rectangle>> segmentRectangles = getSegmentRectangles();

        List<String> visitedColors = new ArrayList<String>();

        // clusterRectangles first rect: outermost cluster rect
        // other rectangles: cluster elements

        BufferedImage bi = ImageIO.read(new File(imagePath));

        Random rand = new Random();
        for (String sId : segmentRectangles.keySet()) {
            List<Rectangle> rects = segmentRectangles.get(sId);
            if (rects.size() == 0)
                continue;

            Graphics graphics = bi.getGraphics();
            graphics.setColor(Color.RED);
            graphics.setFont(new Font("Arial Black", Font.BOLD, 22));

            Rectangle rect = rects.get(0);
            int x = rect.x + 10 + rand.nextInt(11);
            int y = rect.y + 10 + rand.nextInt(11);
            graphics.drawString(sId, x, y);

            Graphics2D g2D = (Graphics2D) graphics;
            Color color = getRandomColor(visitedColors);
            g2D.setColor(color);
            g2D.setStroke(new BasicStroke(3F));
            g2D.drawRect(rect.x, rect.y, rect.width, rect.height);

            if (rects.size() > 1) {
                // draw dashed rectangles around individual cluster elements
                float[] dash = {10.0f};
                g2D.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                int alpha = 50;
                Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
                g2D.setPaint(c);
                for (int m = 1; m < rects.size(); m++) {
                    Rectangle localRect = rects.get(m);
                    java.awt.Rectangle r = new java.awt.Rectangle(localRect.x, localRect.y, localRect.width, localRect.height);
                    g2D.fill(r);
                }
            }
        }
        ImageIO.write(bi, "png", new File(imagePath));
    }
}
