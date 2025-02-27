package DBSCAN.clustring;
//import edu.usc.gwali.Gwali;

import usc.edu.SALEM.VHTree.XMLUtils;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
//import Util;
import gatech.xpert.dom.DomNode;
//import usc.edu.SALEM.VHTree.Node;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import java.util.*;
import java.util.List;


public class ElementsClusterer {
    public static Map<String, ElementWrapper> xpathToElementMap = new HashMap<>();
    private static List<ElementWrapper> wrappedElems;
    // DBSCAN attributes
    private final double EPS = 0.5; // Ali: set empirically, plz change to best generate best clustring
    private final int MIN_POINTS = 0; // Ali: I do not want to have a min
    // normalize the values using mean and stdDev (standardize),
    // so it is has a mean of 0 and a standard deviation of 1.
    // if this is set to false, the values will be only scaled to [0-1] only and not standardized
    private final boolean standardize = false;
    private List<Cluster<ElementWrapper>> clusterResults;
    private DBSCANClusterer<ElementWrapper> clusterer;


    //constructor to cluster elements in an ArrayList (used by ifix)
    public ElementsClusterer(Node<DomNode> elementsForClustering) {

        ArrayList<DomNode> visibleNodes = new ArrayList<DomNode>();
        //only cluster visible elements

        Queue<Node<DomNode>> q = new LinkedList<>();
        q.add(XMLUtils.getRoot());

        while (!q.isEmpty()) {
            Node<DomNode> node = q.remove();
            visibleNodes.add(node.getData());


            if (node.getChildren() != null) {
                for (Node<DomNode> child : node.getChildren()) {
                    q.add(child);
                }
            }
        }

        prepareElementsForClustering(visibleNodes);
    }

    private void prepareElementsForClustering(List<DomNode> elementsForClustering) {
        wrappedElems = new ArrayList<ElementWrapper>();
        for (DomNode elm : elementsForClustering) {
            ElementWrapper elmWraped = new ElementWrapper(elm);
            wrappedElems.add(elmWraped);
            xpathToElementMap.put(elm.getxPath(), elmWraped); // Ali: I copied that map here
        }
        clusterer = new DBSCANClusterer<ElementWrapper>(EPS, MIN_POINTS, new WebElmDistanceMeasure(wrappedElems));
    }


    public List<Cluster<ElementWrapper>> getClusterResults() {
        return clusterResults;
    }


    public List<Cluster<ElementWrapper>> perfomrClustering() {


        List<Cluster<ElementWrapper>> clusterResults = clusterer.cluster(wrappedElems);
        this.clusterResults = clusterResults;
        return clusterResults;
    }



    public void printClusterResults() {
        int i = 0;
        for (Cluster<ElementWrapper> cluster : clusterResults) {
            i++;
            System.out.println("Cluster " + i + ":");
            for (ElementWrapper element : cluster.getPoints()) {
                System.out.println("\t" + element.getDomNode().getxPath());
            }

        }
    }

    public ArrayList<ArrayList<DomNode>> getClustringResultsDomNodes() {
        ArrayList<ArrayList<DomNode>> domNodesClusters = new ArrayList<ArrayList<DomNode>>();
        for (Cluster<ElementWrapper> cluster : clusterResults) {
            ArrayList<DomNode> DomNodesCluster = new ArrayList<DomNode>();
            for (ElementWrapper elmWrapper : cluster.getPoints()) {
                DomNodesCluster.add(elmWrapper.getDomNode());
            }
            domNodesClusters.add(DomNodesCluster);
        }
        return domNodesClusters;
    }


    public ArrayList<ArrayList<String>> getClustringResultsXpaths() {
        ArrayList<ArrayList<String>> xPathsClusters = new ArrayList<ArrayList<String>>();
        for (Cluster<ElementWrapper> cluster : clusterResults) {
            ArrayList<String> xPathsCluster = getXpathCluster(cluster);
            xPathsClusters.add(xPathsCluster);
        }
        return xPathsClusters;
    }

    private ArrayList<String> getXpathCluster(Cluster<ElementWrapper> cluster) {
        ArrayList<String> Xpathscluster = new ArrayList<String>(cluster.getPoints().size());
        for (ElementWrapper element : cluster.getPoints()) {
            String xpath = element.getDomNode().getxPath();
            Xpathscluster.add(xpath);
        }
        return Xpathscluster;
    }
//	public void drawSegments(String screenshot) throws IOException {
//		Map<String, List<Rectangle>> segmentRectangles = getSegmentRectangles();
//
//		List<String> visitedColors = new ArrayList<String>();
//
//		// clusterRectangles first rect: outermost cluster rect
//		// other rectangles: cluster elements
//
//		BufferedImage bi = ImageIO.read(new File(screenshot));
//
//		Random rand = new Random();
//		for (String sId : segmentRectangles.keySet()) {
//			List<Rectangle> rects = segmentRectangles.get(sId);
//			if (rects.size() == 0)
//				continue;
//
//			Graphics graphics = bi.getGraphics();
//			graphics.setColor(Color.RED);
//			graphics.setFont(new Font("Arial Black", Font.BOLD, 22));
//
//			Rectangle rect = rects.get(0);
//			int x = rect.x + 10 + rand.nextInt(11);
//			int y = rect.y + 10 + rand.nextInt(11);
//			graphics.drawString(sId, x, y);
//
//			Graphics2D g2D = (Graphics2D) graphics;
//			Color color = getRandomColor(visitedColors);
//			g2D.setColor(color);
//			g2D.setStroke(new BasicStroke(3F));
//			g2D.drawRect(rect.x, rect.y, rect.width, rect.height);
//
//			if (rects.size() > 1) {
//				// draw dashed rectangles around individual cluster elements
//				float[] dash = {10.0f};
//				g2D.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
//				int alpha = 50;
//				Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
//				g2D.setPaint(c);
//				for (int m = 1; m < rects.size(); m++) {
//					Rectangle localRect = rects.get(m);
//					java.awt.Rectangle r = new java.awt.Rectangle(localRect.x, localRect.y, localRect.width, localRect.height);
//					g2D.fill(r);
//				}
//			}
//		}
//		ImageIO.write(bi, "png", new File(imagePath));
//	}
}
