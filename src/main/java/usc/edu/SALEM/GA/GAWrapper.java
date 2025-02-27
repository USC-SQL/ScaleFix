package usc.edu.SALEM.GA;

import java.util.*;

//import GA.GAChromosome;
//import GA.GAGene;
import gatech.xpert.dom.DomNode;
import usc.edu.SALEM.fitness.TIssue;
import usc.edu.SALEM.Constants;
//import mfix.Util;
//import mfix.WebDriverSingleton;
import usc.edu.SALEM.VHTree.DependentNode;
import usc.edu.SALEM.VHTree.SegmentRelationGraph;
import usc.edu.SALEM.fitness.AccessibilityScannerResults;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.VHTree.DependencyGraph;
//import mfix.domTree.HtmlDomTree;
//import usc.edu.SALEM.VHTree.Node;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import usc.edu.SALEM.fitness.FitnessFunction;
//import mfix.fitness.GoogleAPIResults;
import usc.edu.SALEM.VHTree.XMLUtils;
import usc.edu.SALEM.segmentation.InterSegmentEdge;
import usc.edu.SALEM.segmentation.Segment;
import usc.edu.SALEM.segmentation.SegmentModel;
import usc.edu.SALEM.util.Util;


public class GAWrapper {
    private final AccessibilityScannerResults acr;

    public GAWrapper(AccessibilityScannerResults acr) {
        this.acr = acr;
    }




    public GAChromosome extractGenesBasedOnIssueTypeSegmentRelationGraph() {
        GAChromosome chromosome = new GAChromosome();

        // get set of segments
        Set<Segment> segments = new HashSet<Segment>();
        SegmentModel x = SALEM.getOriginalUISegmentModel();
        for (InterSegmentEdge e : SALEM.getOriginalUISegmentModel().getEdges()) {
            segments.add(e.getSegment1());
            segments.add(e.getSegment2());
        }
        if (segments.size() == 0) { // In case there us only one segment
            segments.add(SALEM.getOriginalUISegmentModel().getSegmentationObject().getSegments().get(0));

        }
        // get the initial list of issues for the current activity to identify the problematic segments
        HashMap<String, Set<TIssue>> c = this.acr.getListOfIssues();
        Set<TIssue> acitvityIssues = c.get(SALEM.getCurrentActivityName());
        Set<String> problematicSegments = new HashSet<String>();
        for (TIssue issue : acitvityIssues
        ) {
            String id = issue.getWidgetID();
            String issueClass = issue.getClassName();
            Node<DomNode> dd = XMLUtils.searchByID_T(id, issueClass);
            if (dd != null) {
                String xpath = dd.getData().getxPath();
                if (xpath != null && xpath != "") {
                    int segmentId = Util.elementToSegmenets(xpath, segments);
                    if (segmentId > -1) {
                        problematicSegments.add(String.valueOf(segmentId));
                    }
                }
            }
        }
        SALEM.setProblematicSegments(problematicSegments);


        String segmentIssueId = "";
//        TOUCH_SPACE_PROBLEM --> "TouchTargetSpace"
//        TOUCH_SIZE_PROBLEM-->"TouchTargetSize"
        /*** Trying to create the dependancy graph for the whole activity at once ***/

        // Create dependancy for height then widith
        for (String issue : Constants.SIZE_SUB_ISSUES_TYPES) {  //SIZE_SUB_ISSUES_TYPES= TOUCH_TARGET_HEIGHT_ISSUE  and TOUCH_TARGET_WIDTH_ISSUE
            DependencyGraph dg = new DependencyGraph(0);
            dg.createDependencyGraph(XMLUtils.getRoot(), issue);
            if (dg.getDependentNodesMap().size() > 0) {
                segmentIssueId = Util.getSegmentIssueId(0, issue);
                SALEM.getSegmentToDG().put(segmentIssueId, dg);
            }
        }
        HashMap<String, HashMap<String, List<Double>>> parents = new HashMap<>();
        parents.put("height", new HashMap<>());
        parents.put("width", new HashMap<>());


//        for (Segment seg : segments) {
//            if (seg.isGhostSegment()) { // Ali: Not applicable for Touch target
//                continue;
//            }
//            if (!problematicSegments.contains(String.valueOf(seg.getId()))) {  // Not a segment that contains issue
//                continue;
//            }
//            for (String xpath :
//                    seg.getMembers() ) {
//
//            }
//    }

        for (String issue : Constants.GENERAL_ISSUES_TYPES_SEGMENT_TRY) {
            //Ali a command to avoid parsing issues that are not actual issues
//            if (issue.equalsIgnoreCase(Constants.TOUCH_TARGET_SPACE_ISSUE)) {
//                continue;
//            }

            double impactScore = acr.getRuleImpactScore(issue);
            if (impactScore > 0.0) {


                // create dependency graph for each segment based on the issue types legibility and tap targets
                for (Segment seg : segments) {
                    if (seg.isGhostSegment()) { // Ali: Not applicable for Touch target
                        continue;
                    }
                    Set<String> segProblematic = SALEM.getProblematicSegments();
                    if (!problematicSegments.contains(String.valueOf(seg.getId()))) {  // Not a segment that contains issue
                        continue;
                    }


                    Node<DomNode> segmentRoot = XMLUtils.searchVHTreeByXpath(seg.getLowestCommonAncestor(), XMLUtils.getRoot());

                    /** For Spacing we do the following
                     * 1- for each segment: create spacing depenendy graph
                     * 2- find smallest space between any two TT and call this space as smallest ( object that has space and two elements attached?)
                     * 3- create segment graph between them and do the same as segmetn for size
                     *     3.1- increase space for the smallest relation then increase the rest with same relation
                     *     3.2-
                     */

                    if (issue.equalsIgnoreCase(Constants.TOUCH_TARGET_SPACE_ISSUE)) {
                        //  continue; // disable spacing
                    }
                    //Create sg for spacing
                    if (issue.equalsIgnoreCase(Constants.TOUCH_TARGET_SPACE_ISSUE)) {
                        DependencyGraph dgSpacging = new DependencyGraph(seg.getId());
                        dgSpacging.createDependencyGraph(XMLUtils.getRoot(), issue);
                        if (dgSpacging.getDependentNodesMap().size() > 0) {
                            segmentIssueId = Util.getSegmentIssueId(seg.getId(), issue);
                            SALEM.getSegmentToDG().put(segmentIssueId, dgSpacging);
                        }
                    }
                    /*** SUN DEC 20, trying SegmentRelationGraph   ***/
                    SegmentRelationGraph sg = new SegmentRelationGraph(seg.getId());
                    String[] smallestTouchTargetsXpath = sg.createSegmentRelationGraph(segmentRoot, issue); // I made this method return the xpaths of touch targets with smallest height and width so we can include them in the gene
                    Node<DomNode> ROOT = XMLUtils.getRoot();
                    // create a gene only if dependency graph exists
                    if (sg.getDependentNodesMap().size() > 0) {
                        segmentIssueId = Util.getSegmentIssueId(seg.getId(), issue);
                        SALEM.getSegmentToSG().put(segmentIssueId, sg);
                        System.out.println("\nDependency graph for " + segmentIssueId);
                        System.out.println(sg);
                        ArrayList touchTargetsXpaths;
                        /** create gene based the issues type if it is TT issues( height, width or both **/
                        String segIsssueType = seg.getSizeIssueType();
                        String issueTOCSS = issue;
                        if (issue.equalsIgnoreCase(Constants.TOUCH_TARGET_SIZE_ISSUE)) {

                            //    public static final String TOUCH_TARGET_HEIGHT_ISSUE = "TouchTargetHeightIssue";
                            //    public static final String TOUCH_TARGET_WIDTH_ISSUE = "TouchTargetWidthIssue";
                            //    public static final String TOUCH_TARGET_BOTH_ISSUE = "TouchTargetBothIssue";
                            issueTOCSS = segIsssueType;  // to s=get height if is heightissue or width if width issue or height and widht if bothissue
                            if (Constants.PARENTS_INCREASE_APPROACH.equalsIgnoreCase("ratio")) {


                                if (segIsssueType.equalsIgnoreCase("TouchTargetHeightIssue")) {
                                    DependencyGraph dg = SALEM.getSegmentToDG().get("S0_TouchTargetHeightIssue");

                                    HashMap<String, List<Double>> heightParents = parents.get("height");
                                    for (String s : seg.getMembers()
                                    ) {
                                        List<DependentNode> depentNodes = dg.getDependentNodesMap().get(s + "#height");
                                        if (depentNodes != null)
                                            for (DependentNode d : depentNodes
                                            ) {
                                                if (!d.getXpath().equalsIgnoreCase(s)) {  // Not relation to same element, dependant

                                                    if (!heightParents.containsKey(d.getXpath())) {
                                                        heightParents.put(d.getXpath(), new ArrayList<Double>());
                                                    }
                                                    heightParents.get(d.getXpath()).add(d.getRatio());
                                                }

                                            }
                                    }
                                }
                                else if (segIsssueType.equalsIgnoreCase("TouchTargetWidthIssue")) {
                                    DependencyGraph dg = SALEM.getSegmentToDG().get("S0_TouchTargetWidthIssue");

                                    HashMap<String, List<Double>> widthParents = parents.get("width");
                                    for (String s : seg.getMembers()
                                    ) {
                                        List<DependentNode> depentNodes = dg.getDependentNodesMap().get(s + "#width");
                                        if (depentNodes != null)
                                            for (DependentNode d : depentNodes
                                            ) {
                                                if (!d.getXpath().equalsIgnoreCase(s)) {  // Not relation to same element, dependant

                                                    if (!widthParents.containsKey(d.getXpath())) {
                                                        widthParents.put(d.getXpath(), new ArrayList<Double>());
                                                    }
                                                    widthParents.get(d.getXpath()).add(d.getRatio());
                                                }

                                            }
                                    }
                                }
                            }
                        }
                        Set<String> cssProperties = getApplicableSizeProperties(issueTOCSS);
                        for (String property : cssProperties) {
                            System.out.println("issue: " + issue);
                            Map<String, HashMap<String, String>> xx = Constants.ACCESSIBILITY_SUGGESTED_VALUES;
                            String value = Constants.ACCESSIBILITY_SUGGESTED_VALUES.get(issueTOCSS).get(property);
                            touchTargetsXpaths = new ArrayList();


                            GAGene gene = new GAGene();

                            if (issue.equalsIgnoreCase(Constants.TOUCH_TARGET_SIZE_ISSUE)) {
                                touchTargetsXpaths.add(smallestTouchTargetsXpath[0]);   // what to do if it is space?
                                touchTargetsXpaths.add(smallestTouchTargetsXpath[1]);
                            }
                            gene.setXpaths(touchTargetsXpaths);
                            gene.setCssProperty(property);
                            gene.setValue(value);
                            gene.setOriginalValue(value);
                            gene.setIssueType(issue);
                            gene.setImpactScore(impactScore);
                            gene.setSegmentIssueId(segmentIssueId);

                            chromosome.addGene(gene);
                        }


                    }
                }
            }
        }
        // add parents gene
        if (Constants.PARENTS_INCREASE_APPROACH.equalsIgnoreCase("ratio")) {
            for (String prop : parents.keySet()
            ) {
                HashMap<String, List<Double>> heightParent = parents.get(prop);
                for (String element : heightParent.keySet()
                ) {
                    ArrayList<String> arrXpath = new ArrayList<>();
                    arrXpath.add(element);// only contains one element but kept it as array to match other gene implementation
                    double maxRatio = Collections.max(heightParent.get(element));
                    GAGene gene = new GAGene();
                    gene.setXpaths(arrXpath);
                    gene.setCssProperty(prop);
                    gene.setValue(String.valueOf(maxRatio));
                    gene.setOriginalValue(String.valueOf(maxRatio));
                    gene.setIssueType("PARENT_INCREASE");
                    gene.setImpactScore(1);
                    gene.setSegmentIssueId("0");

                    chromosome.addGene(gene);
                }
            }
//        if (issue.equalsIgnoreCase(Constants.TOUCH_TARGET_SIZE_ISSUE)) {
//            HashMap<String,List<Double>> heightParent=parents.get("height");
//            for (String element:heightParent.keySet()
//            ) {
//                ArrayList<String> arrXpath= new ArrayList<>();
//                arrXpath.add(element);
//                double maxRatio= Collections.max(heightParent.get(element));
//                GAGene gene = new GAGene();
//                gene.setXpaths(arrXpath);
//                gene.setCssProperty("height");
//                gene.setValue(String.valueOf(maxRatio));
//                gene.setOriginalValue("");
//                gene.setIssueType("PARENT_INCREASE");
//                gene.setImpactScore(1);
//                gene.setSegmentIssueId("0");
//
//                chromosome.addGene(gene);
//            }
//
//
//        }
        }
        //add textSIze gene
        if (SALEM.HandleTextField) {


        if (Constants.TEXT_SIZE_APPROACH.equalsIgnoreCase("ratio")) {

            // general TT genes
            ArrayList<String> arrXpath = new ArrayList<>();
            arrXpath.add(XMLUtils.getRoot().getData().getxPath());// only contains one element but kept it as array to match other gene implementation
            GAGene gene = new GAGene();
            gene.setXpaths(arrXpath);
            gene.setCssProperty("textSize");
            gene.setValue(String.valueOf(0.1));
            gene.setOriginalValue(String.valueOf(0.0));
            gene.setIssueType("TEXT_INCREASE");
            gene.setImpactScore(0.5);
            gene.setSegmentIssueId("0");
            chromosome.addGene(gene);

        }
            // add specific text view inside Listview
//             arrXpath = new ArrayList<>();
//            arrXpath.add(XMLUtils.getRoot().getData().getxPath());// only contains one element but kept it as array to match other gene implementation
//             gene = new GAGene();
//            gene.setXpaths(arrXpath);
//            gene.setCssProperty("height");
//            gene.setValue(String.valueOf(0.5));
//            gene.setOriginalValue(String.valueOf(0.5));
//            gene.setIssueType("ListView_TEXT_INCREASE");
//            gene.setImpactScore(0.5);
//            gene.setSegmentIssueId("0");
//            chromosome.addGene(gene);

        }

        FitnessFunction ff = new FitnessFunction();
        //ff.setUsabilityScore(gar.getUsabilityScoreAPIObj().getUsabilityScore());

        //Ali: Since we do not know the scores yet, we set them to the max
        //TODO: What should empty accessibility and aestheticScore be? should size and space be different?
        ff.setA11yHeuristicsScore(Double.MAX_VALUE);
        ff.setAestheticScore(0.0);
        ff.setFitnessScore(ff.getA11yHeuristicsScore());
        chromosome.setFitnessFunctionObj(ff);

        // sort root causes in descending order by "rule impact" score in the Usability Score API
        //Collections.sort(rootCauseList.getRootCauses());

        return chromosome;
    }



    public Set<String> getApplicableSizeProperties(String problemType) {

        /* Height, Width, Both, or Space | Stored in the GENERAL_ISSUES_TYPES Constants
         * So we will just get the properties attached of the issue*/
        /* there are two Map constants: basic and full, basic contains only height width, and margin. Ful contains min_width, padding and more   | Right now I am choosing the basic only , TODO: think about choosing the full */
        Map<String, Map> x = Constants.ISSUES_TO_BASIC_ATTRIBUTES_MAP;
        System.out.println("keys:");
        Map<String, String> propertiesMap = Constants.ISSUES_TO_BASIC_ATTRIBUTES_MAP.get(problemType);
        Set<String> cssProperties = new HashSet<>();

        for (String key : propertiesMap.keySet()
        ) {
            // I am using the keys so we keep
            cssProperties.add(key);
        }

        return cssProperties;
    }


}
