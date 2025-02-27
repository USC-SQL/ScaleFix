package usc.edu.SALEM.segmentation;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.DomUtils;
import usc.edu.SALEM.VHTree.XMLUtils;
import usc.edu.SALEM.util.Util;
//import usc.edu.SALEM.VHTree.Node;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import java.util.List;

public class SegmentModel_TT extends SegmentModel {


    private XMLUtils uiHierarchy; // The hierarchy of the activity


    public SegmentModel_TT() {
        super();
    }


    public void buildSegmentModel() {
        runSegmentation();

        // iterate over segments and create intrasegment model within the members
        long startTime = System.nanoTime();
        for (Segment s : segmentationObject.getSegments()) {
            s.calculateEdges();
            System.out.println("\nIntra-segment edges for segment S" + s.getId());
            System.out.println(s.getEdges());
        }
        // try_segment.setOriginalPageSegments(segmentationObject.getSegments());
        long endTime = System.nanoTime();
        System.out.println("Total intrasegment calculation time = " + Util.convertNanosecondsToSeconds((endTime - startTime)) + " sec");

        startTime = System.nanoTime();
        this.edges = calculateEdges(segmentationObject.getSegments());
        addEdgeLabels(edges);
        endTime = System.nanoTime();
        System.out.println("Total intersegment calculation time = " + Util.convertNanosecondsToSeconds((endTime - startTime)) + " sec");
    }


    public void runSegmentation() {
        long startTime = System.nanoTime();
        Node<DomNode> root = XMLUtils.getRoot();

        List<Node<DomNode>> leafElements = DomUtils.getLeaves(root);
        System.out.println("\nLeaves (size = " + leafElements.size() + "): ");
        for (Node<DomNode> leaf : leafElements) {
            System.out.println(leaf.getData().getxPath());
        }

        segmentationObject = new Segmentation();
        segmentationObject.performSegmentation(leafElements);

        System.out.println("\nSegment terminate threshold value = " + segmentationObject.getSegmentTerminateThreshold());
        System.out.println("Segments (size = " + segmentationObject.getSegments().size() + "): ");
        for (Segment seg : segmentationObject.getSegments()) {
            System.out.println(seg);
        }
        long endTime = System.nanoTime();
        System.out.println("Total Segmentation_TT time = " + Util.convertNanosecondsToSeconds((endTime - startTime)) + " sec");
    }


    public Segmentation getSegmentationObject() {
        return segmentationObject;
    }

}
