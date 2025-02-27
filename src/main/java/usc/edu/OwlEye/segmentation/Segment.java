package usc.edu.OwlEye.segmentation;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.VHTree.XMLUtils;
import usc.edu.SALEM.fitness.SpatialRelationships;

import java.util.ArrayList;
import java.util.List;

public class Segment {
    private int id;
    private List<String> members;
    private String lowestCommonAncestor;
    private Rectangle minimumBoundingRectangle;
    private boolean isGhostSegment;


    private String sizeIssueType;
    private List<IntraSegmentEdge> edges;    // sparse complete graph -> stores only edges that have labels

    public Segment() {
        members = new ArrayList<>();
        lowestCommonAncestor = "";
        minimumBoundingRectangle = new Rectangle();
        isGhostSegment = false;
    }

    public Segment(int id, List<String> members, String lowestCommonAncestor) {
        setId(id);
        this.members = members;
        this.lowestCommonAncestor = lowestCommonAncestor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;

//		if(id == Constants.VIEWPORT_SEGMENT_ID)
//		{
//			isGhostSegment = true;
//		}
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getLowestCommonAncestor() {
        return lowestCommonAncestor;
    }

    public void setLowestCommonAncestor(String lowestCommonAncestor) {
        this.lowestCommonAncestor = lowestCommonAncestor;
    }

    public Rectangle getMinimumBoundingRectangle() {
        return minimumBoundingRectangle;
    }

    public void setMinimumBoundingRectangle(Rectangle minimumBoundingRectangle) {
        this.minimumBoundingRectangle = minimumBoundingRectangle;
    }

    public void addMember(String member) {
        members.add(member);
    }

    public void removeMember(String xpath) {
        members.remove(xpath);
    }

    public void addMembers(List<String> newMembers) {
        members.addAll(newMembers);
    }

    public boolean isGhostSegment() {
        return isGhostSegment;
    }

    public void setGhostSegment(boolean isGhostSegment) {
        this.isGhostSegment = isGhostSegment;
    }

    public List<IntraSegmentEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<IntraSegmentEdge> edges) {
        this.edges = edges;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
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
        Segment other = (Segment) obj;
        return id == other.id;
    }

    public Segment copy() {
        Segment copySegment = new Segment();
        copySegment.id = id;
        copySegment.members = new ArrayList<>(members);
        copySegment.lowestCommonAncestor = lowestCommonAncestor;
        copySegment.minimumBoundingRectangle = new Rectangle(minimumBoundingRectangle);
        copySegment.edges = new ArrayList<IntraSegmentEdge>();
        for (IntraSegmentEdge e : this.edges) {
            copySegment.edges.add(e.copy());
        }
        return copySegment;
    }

    public void calculateEdges() {
        // for members (element pairs) add an edge if it contains labels and add to intra-segment model => sparse complete graph
        edges = new ArrayList<>();
//        XMLUtils instance = XMLUtils.getInstance(TTFIX.merged_layout_files);

        for (int i = 0; i < members.size(); i++) {
            for (int j = i + 1; j < members.size(); j++) {
                Node<DomNode> member1 = XMLUtils.searchVHTreeByXpath(members.get(i), XMLUtils.getRoot());
                Node<DomNode> member2 = XMLUtils.searchVHTreeByXpath(members.get(j), XMLUtils.getRoot());
                // before iFix
//
                List<EdgeLabel> labels = calculateEdgeLabels(member1, member2);
                if (labels.size() > 0) {
                    IntraSegmentEdge e = new IntraSegmentEdge(member1, member2);
                    e.setLabels(labels);
                    edges.add(e);
                }
            }
        }
        //addEdgeLabels();
    }


    public String getSizeIssueType() {
        return sizeIssueType;
    }

    public void setSizeIssueType(String sizeIssueType) {
        this.sizeIssueType = sizeIssueType;
    }




    public List<EdgeLabel> calculateEdgeLabels(Node<DomNode> e1, Node<DomNode> e2) {
        List<EdgeLabel> labels = new ArrayList<EdgeLabel>();
        SpatialRelationships sr = new SpatialRelationships();

        // check intersection
        //Rectangle r1 = e1.getCoord();
        //Rectangle r2 = e2.getCoord();

        List<Rectangle> e1Rects = new ArrayList<>();
        List<Rectangle> e2Rects = new ArrayList<>();

        // compare text content individually
        //Ali
//		if(e1.getTextCoords().size() > 0)
//		{
//			e1Rects.addAll(e1.getTextCoords());
//		}
//		else
//		{
        e1Rects.add(e1.getData().getCoord());
//		}

//		if(e2.getTextCoords().size() > 0)
//		{
//			e2Rects.addAll(e2.getTextCoords());
//		}
//		else
//		{
        e2Rects.add(e2.getData().getCoord());
        //	}


		/*if(e1.getTextCoords().size() > 0)
		{
			int minX = r1.x;
			int minY = r1.y;
			int maxX = r1.x + r1.width;
			int maxY = r1.y + r1.height;

			int minX = Integer.MAX_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE;
			int maxY = Integer.MIN_VALUE;

			for(Rectangle r : e1.getTextCoords())
			{
				minX = Math.min(r.x, minX);
				minY = Math.min(r.y, minY);
				maxX = Math.max(r.x + r.width, maxX);
				maxY = Math.max(r.y + r.height, maxY);
			}
			Rectangle textMBR = new Rectangle(minX, minY, (maxX - minX), (maxY - minY));
			r1 = new Rectangle(textMBR);
		}

		if(e2.getTextCoords().size() > 0)
		{
			int minX = r2.x;
			int minY = r2.y;
			int maxX = r2.x + r2.width;
			int maxY = r2.y + r2.height;

			int minX = Integer.MAX_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE;
			int maxY = Integer.MIN_VALUE;

			for(Rectangle r : e2.getTextCoords())
			{
				minX = Math.min(r.x, minX);
				minY = Math.min(r.y, minY);
				maxX = Math.max(r.x + r.width, maxX);
				maxY = Math.max(r.y + r.height, maxY);
			}
			Rectangle textMBR = new Rectangle(minX, minY, (maxX - minX), (maxY - minY));
			r2 = new Rectangle(textMBR);
		}*/

        for (Rectangle r1 : e1Rects) {
            for (Rectangle r2 : e2Rects) {
                if (sr.isIntersection(r1, r2)) {
                    labels.add(EdgeLabel.INTERSECTION);

                }
                //Ali
                else if (sr.isDirectionBelow(r1, r2)) {
                    labels.add(EdgeLabel.BELOW);
                } else if (sr.isDirectionAbove(r1, r2)) {
                    labels.add(EdgeLabel.ABOVE);
                } else if (sr.isDirectionRight(r1, r2)) {
                    labels.add(EdgeLabel.RIGHT);
                } else if (sr.isDirectionLeft(r1, r2)) {
                    labels.add(EdgeLabel.LEFT);
                }
            }
            //}
        }

        return labels;
    }



    public Segment getUpdatedIntraSegmentEdges() {
        /*** After Ifix segmentation ***/
        XMLUtils domTree = XMLUtils.getInstance(SALEM.getCurrentActivityMergedFilePath());

        // copy segment
        Segment segmentUpdated = this.copy();

        // update members with new MBRs
        for (IntraSegmentEdge e : segmentUpdated.getEdges()) {
            Node<DomNode> e1 = XMLUtils.searchVHTreeByXpath(e.getE1().getData().getxPath(), XMLUtils.getRoot());
            e.setE1(e1);
            Node<DomNode> e2 = XMLUtils.searchVHTreeByXpath(e.getE2().getData().getxPath(), XMLUtils.getRoot());

            //	HtmlElement e2 = domTree.searchHtmlDomTreeByXpath(e.getE2().getXpath()).getData();
            e.setE2(e2);
        }

        // recalculate edges
        segmentUpdated.calculateEdges();

        return segmentUpdated;
    }

    public List<IntraSegmentEdge> compareToSegment(Segment otherSegment) {
        List<IntraSegmentEdge> violatedEdges = new ArrayList<>();

        // compare the edge labels, and report violations
        List<IntraSegmentEdge> otherSegmentEdges = otherSegment.edges;
        for (IntraSegmentEdge e : this.edges) {
            // check if same edge present in other segments
            if (otherSegmentEdges.contains(e)) {
                IntraSegmentEdge otherE = null;
                for (IntraSegmentEdge e2 : otherSegmentEdges) {
                    if (e.equals(e2)) {
                        otherE = e2.copy();

                        // remove processed e2 from other segment edges
                        otherSegmentEdges.remove(e2);
                        break;
                    }
                }

                List<EdgeLabel> e1Labels = e.getLabels();
                List<EdgeLabel> e2Labels = otherE.getLabels();

                // remove all elements in e2Labels from e1Labels => set difference
                List<EdgeLabel> intersectionList = new ArrayList<EdgeLabel>(e1Labels);
                intersectionList.retainAll(e2Labels);
                e1Labels.removeAll(intersectionList);
                e2Labels.removeAll(intersectionList);

                List<EdgeLabel> violatedLabels = new ArrayList<>();
                violatedLabels.addAll(e1Labels);
                violatedLabels.addAll(e2Labels);

                if (violatedLabels.size() > 0) {
                    violatedEdges.add(new IntraSegmentEdge(e.getE1().copy(), e.getE2().copy(), violatedLabels));
                }
            }
            // add e directly to violated edges
            else {
                violatedEdges.add(e.copy());
            }
        }

        // add to violated edges any remaining edges from other edges
        violatedEdges.addAll(otherSegmentEdges);

        return violatedEdges;
    }

    @Override
    public String toString() {
        return "<" + id + ", "
                + "members (size = " + members.size() + "): {" + members + "}, "
                + "LCA = " + lowestCommonAncestor + ", "
                + "MBR = " + minimumBoundingRectangle
                + "Intrasegment model = " + edges
                + ">" + (isGhostSegment ? " => ghost segment for viewport" : "");
    }

    public boolean isNodeInSegment(String xpath) {
        // find and return the element of the segment by Xpath or return null if the xpath is not part of the segment
        for (String memberXpath : this.getMembers()
        ) {
            if (memberXpath.equalsIgnoreCase(xpath)) {
                return true;
            }
        }

        return false;
    }

}
