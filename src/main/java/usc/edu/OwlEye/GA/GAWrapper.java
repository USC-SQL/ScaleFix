package usc.edu.OwlEye.GA;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import org.tinylog.Logger;
import usc.edu.OwlEye.AUCII.Collision;
import usc.edu.OwlEye.AUCII.Cutoff;
import usc.edu.OwlEye.AUCII.Missing;
import usc.edu.OwlEye.ElementsProperties.*;
import usc.edu.OwlEye.GAChanges.GAChange;
import usc.edu.OwlEye.GAChanges.GAElementToChange;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.UIModels.WRGNode;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.fitness.AccessibilityScannerResults;
import usc.edu.SALEM.util.Util;

import java.util.*;


public class GAWrapper {
    private final AccessibilityScannerResults acr;

    public GAWrapper(AccessibilityScannerResults acr) {
        this.acr = acr;
    }


    public ArrayList<GAChromosome> extractPopulationBasedOnIssueType() {
        ArrayList<GAChromosome> initialPopulation = new ArrayList<GAChromosome>();


        //TODO: Handle segments later


        //1- iterate through the issue types and for each type iterate through the issues and create genes for them
        HashMap<String, HashMap<String, GAChange>> groupsOfChangesCutoff = new HashMap<String, HashMap<String, GAChange>>();
        HashMap<String, HashMap<String, GAChange>> groupsOfChangesCollision = new HashMap<String, HashMap<String, GAChange>>();
        HashMap<String, HashMap<String, GAChange>> groupsOfChangesMissing = new HashMap<String, HashMap<String, GAChange>>();
        for (String issueType : OwlConstants.GENERAL_ISSUES_TYPES) {

            //New approach
            boolean newApproach = true;

            switch (issueType) {
                case OwlConstants.CUT_OFF_ISSUE: {
                    HashMap<String, Cutoff> cutOffIssues = OwlEye.getOriginalCutoffIssues();
                    if (cutOffIssues.size() == 0) {
                        continue;
                    }
                    GADependencyAnalysisORG gaDependencyAnalysis = new GADependencyAnalysisORG();
                   groupsOfChangesCutoff = gaDependencyAnalysis.CutOffDependencyAnalysis(cutOffIssues);

                    HashMap<String, GAChromosome> generatedChromosomes = createChromosomesBasedOnGroupsOfChanges(groupsOfChangesCutoff);
//                    Logger.debug("generatedChromosomes size: " + generatedChromosomes.size());
//                    for (String key : generatedChromosomes.keySet()
//                    ) {
//                        initialPopulation.add(generatedChromosomes.get(key));
//                    }
//
//                    if (initialPopulation.size() == 0) {
//                        System.exit(1);
//
//                    }
                    break;
                }
                case OwlConstants.COLLISION_ISSUE: {
                    //chromosome.addGenes(extractTextColorGenes());


                    HashMap<String, Collision> collisionIssues = OwlEye.getOriginalCollisionIssues();
                    if (collisionIssues.size() == 0) {
                        continue;
                    }

                    GACollisionDPAnalysis dependencyAnalysis = new GACollisionDPAnalysis();
                    groupsOfChangesCollision = dependencyAnalysis.CollisionDependencyAnalysis(collisionIssues);
//                    HashMap<String, GAChromosome> generatedChromosomes = createChromosomesBasedOnGroupsOfChangesForCollision(groupsOfChangesCollision);
//                    Logger.debug("generatedChromosomes size: " + generatedChromosomes.size());
//                    for (String key : generatedChromosomes.keySet()
//                    ) {
//                        initialPopulation.add(generatedChromosomes.get(key));
//
//                    }
//
//                    if (initialPopulation.size() == 0) {
//                        System.exit(1);
//
//                    }
                    break;
                }
                case OwlConstants.MISSING_ISSUE:{
                    HashMap<String, Missing> missingIssues = OwlEye.getOriginalMissingIssues();
                    if (missingIssues.size() == 0) {
                        continue;
                    }
                    GAMissingDPAnalysis missingDependencyAnalysis = new GAMissingDPAnalysis();
                    groupsOfChangesMissing = missingDependencyAnalysis.MissingDependencyAnalysis(missingIssues);
//                    HashMap<String, GAChromosome> generatedChromosomes = createChromosomesBasedOnGroupsOfChangesForCollision(groupsOfChangesMissing);
//                    Logger.debug("generatedChromosomes size: " + generatedChromosomes.size());
//                    for (String key : generatedChromosomes.keySet()
//                    ) {
//                        initialPopulation.add(generatedChromosomes.get(key));
//
//                    }
//
//                    if (initialPopulation.size() == 0) {
//                        System.exit(1);
//
//                    }

                    break;
                }


            }
        }
//        HashMap<String, GAChromosome> generatedChromosomes = createChromosomesBasedOnGroupsOfChangesAllIssues(groupsOfChangesCutoff,groupsOfChangesCollision,
//                groupsOfChangesMissing);
        HashMap<String, GAChromosome> generatedChromosomes = createChromosomesBasedOnGroupsOfChangesAllIssuesUnified(groupsOfChangesCutoff,groupsOfChangesCollision,
                groupsOfChangesMissing);
        Logger.debug("generatedChromosomes size: " + generatedChromosomes.size());
        for (String key : generatedChromosomes.keySet()
        ) {
            initialPopulation.add(generatedChromosomes.get(key));
        }

        if (initialPopulation.size() == 0) {
            System.exit(1);

        }

        // FitnessFunction ff = new FitnessFunction();

        //    chromosome.setFitnessFunctionObj(ff);


        return initialPopulation;

    }

    private static GAChange generateMaxLinesChange(String propName, int currentMaxLine, int newMaxLine, GAElementToChange element, Property property) {

        String changeVal = newMaxLine + "";
        String changeType = OwlConstants.CHANGE_INCREASE;
        //changeType, changeVal,element,sizeProp);
        GAChange change3 = new GAChange(propName, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(propName), changeType, changeVal, element, property);
        return change3;
    }

    private static void handleWeightForMainSize(GAElementToChange elementToChange, String propName, String staticVal,
                                                String dynamicVal, HashMap<String, GAChange> weightChanges) {
        // get the weight assigned to the element and its sibilings

        Node<DomNode> node = elementToChange.getNode();

        // 1- is it a normal main weight (ie android:layout_weight) or a weight in a constraint layout (ie app:layout_constraintHorizontal_weight)
        String weightVal = Utils.getValueFromElement(node.getData(), Weight.propertyName); // we do TO_FuLL_ATTRIBUTES_MAPPING mapping in the method
        if (weightVal != null) {
            // it is a normal weight
            Logger.debug("It is a normal weight");
        } else {
            // check if it is a constraint layout weight
            if (propName.equalsIgnoreCase(Height.propertyName)) {
                // check vertical weight
                String verticalWeightVal = Utils.getValueFromElement(node.getData(), ConstraintVerticalWeight.propertyName); // we do TO_FuLL_ATTRIBUTES_MAPPING mapping in the method
                if (verticalWeightVal != null) {
                    // it is a constraint layout weight
                    ConstraintVerticalWeight constraintVerticalWeight = new ConstraintVerticalWeight();
                    constraintVerticalWeight.setCurrentDynamicVal(verticalWeightVal);
                    constraintVerticalWeight.setCurrentStaticVal(verticalWeightVal);
                    Logger.debug("It is a vertical constraint layout weight");
                    addWeightChanges(elementToChange, constraintVerticalWeight, verticalWeightVal, weightChanges);
                    Logger.debug("Done adding weight changes, size of weight group is " + weightChanges.size());
                }
            } else if (propName.equalsIgnoreCase(Width.propertyName)) {
                String horizontalWeightVal = Utils.getValueFromElement(node.getData(), ConstraintHorizontalWeight.propertyName); // we do TO_FuLL_ATTRIBUTES_MAPPING mapping in the method
                if (horizontalWeightVal != null) {
                    // it is a constraint layout weight
                    Logger.debug("It is a horizontal constraint layout weight");
                }
            } else {
                Logger.debug("NO WEIGHT FOUND");
            }
        }


    }

    private static void addWeightChanges(GAElementToChange elementToChange, Property weightProp, String verticalWeightVal,
                                         HashMap<String, GAChange> weightGroup) {
        // in this method we handle how to change the weight of the problematic element and its siblings
        // basically create one change for the problematic element and one change for each of its siblings with weight

        String weightPropName = weightProp.getPropertyName();
        Double currentProblematicWeight = Double.parseDouble(verticalWeightVal);
        HashMap<String, Double> siblingsWeights = new HashMap<>();
        Node<DomNode> problematicElementNode = elementToChange.getNode();
        List<Node<DomNode>> siblings = problematicElementNode.getParent().getChildren();
//        for (Node<DomNode> sibling : siblings) {
//            if (sibling.getData().getId().equalsIgnoreCase(problematicElementNode.getData().getId())){ // we only need the siblings
//                continue;
//            }
//            String siblingWeightVal= Utils.getValueFromElement(sibling.getData(), weightPropName); // we do TO_FuLL_ATTRIBUTES_MAPPING mapping in the method
//            if (siblingWeightVal!=null){
//                // it is a constraint layout weight
//                siblingsWeights.put(sibling.getData().getId(),Double.parseDouble(siblingWeightVal));
//            }
//        }
        // now we have the current weight of the problematic element and the weights of its siblings
        // we need to calculate the new weights for the problematic element and its siblings
        // if there is imageView we decrease them more
//        GAChange change = new GAChange(weightProp.propertyName, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(weightProp.propertyName),
//                OwlConstants.Increase, "0.1");
        Double newProblematicWeight = Math.ceil(currentProblematicWeight * 1.5);
        GAChange problematicChange = new GAChange(weightPropName, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(weightPropName),
                OwlConstants.CHANGE_INCREASE, newProblematicWeight + "", elementToChange, weightProp);
        weightGroup.put(elementToChange.getNode().getData().getId(), problematicChange);
        elementToChange.addChange(weightPropName, problematicChange);
        for (Node<DomNode> sibling : siblings) {
            if (sibling.getData().getId().equalsIgnoreCase(problematicElementNode.getData().getId())) { // we only need the siblings
                continue;
            }
            String siblingWeightVal = Utils.getValueFromElement(sibling.getData(), weightPropName); // we do TO_FuLL_ATTRIBUTES_MAPPING mapping in the method
            if (siblingWeightVal != null) {

                // it is a constraint layout weight
                siblingsWeights.put(sibling.getData().getId(), Double.parseDouble(siblingWeightVal));
                //create a change for the sibling
                double percentage = 0.75;
                if (sibling.getData().getTagName().equalsIgnoreCase("ImageView")) {
                    percentage = 0.6;
                }
                Double newSiblingWeight = Math.ceil(Double.parseDouble(siblingWeightVal) * percentage);

                GAElementToChange siblingElementToChange = new GAElementToChange(sibling, "problematic",
                        OwlConstants.CHANGE_DECREASE, weightPropName); // usually we know it is increase fo the problematic element

                GAChange siblingChange = new GAChange(weightPropName, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(weightPropName),
                        OwlConstants.CHANGE_DECREASE, newSiblingWeight + "", siblingElementToChange, weightProp);
                siblingElementToChange.addChange(weightPropName, siblingChange);
                weightGroup.put(sibling.getData().getId(), siblingChange);
            }

//            String siblingWeightVal= Utils.getValueFromElement(sibling.getData(), weightPropName); // we do TO_FuLL_ATTRIBUTES_MAPPING mapping in the method
//            if (siblingWeightVal!=null){
//                // it is a constraint layout weight
//                siblingsWeights.put(sibling.getData().getId(),Double.parseDouble(siblingWeightVal));
//            }
        }


    }


    private static GAChange addMinChange2(GAElementToChange elementToChange, String propName, String dynamicVal) {
        String changeVal;
        String changeType;
        GAChange minChange = null;
        Property minProp = new MinHeight();
        String minProperty = MinHeight.propertyName;
        if (propName.equalsIgnoreCase(Width.propertyName)) {
            minProperty = MinWidth.propertyName;
            minProp = new MinWidth();
        }
        propName = minProperty;

        String miPropVal = Utils.getValueFromElement(elementToChange.getNode().getData(), minProperty);
        boolean minPropSet = false;
        if (miPropVal != null) {
            List<Double> y = Util.getNumbersFromString(miPropVal);
            if (y.size() > 0) { // val exists
                if (y.get(0) > 0) {
                    minPropSet = true;
                    Double numericalMinProp = y.get(0);
                    double n = numericalMinProp + 20;
                    changeVal = n + "";

                    changeType = OwlConstants.CHANGE_INCREASE;

                    minChange = new GAChange(propName, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(propName), changeType, changeVal, elementToChange, minProp);
                    // elementToChange.addChange(propName, change);
                }
            }
        }

        if (!minPropSet) {
            //it does not exist so we add it = dynamic height + 10
            double n = Double.parseDouble(dynamicVal) + 20;
            changeVal = n + "";

            changeType = OwlConstants.CHANGE_INCREASE;
            minChange = new GAChange(propName, OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(propName), changeType, changeVal, elementToChange, null);
        }
        return minChange;

    }




    private void findImpactedElementsBasedWRG(Cutoff cutoff, HashMap<String, GAElementToChange> wrgElementsToChange, Node<DomNode> problematicElement, String prop) {
        // get the WRG
        TreeMap<String, List<WRGNode>> propWRG = OwlEye.getOriginalDefaultUIWRG().getDependentNodesMap().get(prop);
        String problematicElementID = problematicElement.getData().getId();
        String parentXpath = problematicElement.getParent().getData().getxPath();
        List<WRGNode> parentNodeWRG = propWRG.descendingMap().get(parentXpath);
        if (parentNodeWRG == null) {
            return;
        }
        double parentTotalWeight;
        for (WRGNode wrgNode : parentNodeWRG) {
            parentTotalWeight = wrgNode.getParentTotal();
            if (wrgNode.getXmlNode().getData().getId().equals(problematicElementID)) {
                Logger.debug("Found the problematic element in the WRG || INCREASE WEIGHT");
            } else {
                Logger.debug("Found other element in the WRG || DECREASE WEIGHT");
            }

//            GAElementToChange elementToChangeGA = new GAElementToChange(elementToChange, "wrg", OwlConstants.CHANGE_INCREASE, prop);
//            wrgElementsToChange.put(elementToChange.getData().getId(), elementToChangeGA);
//
        }


    }


    private Node<DomNode> checkIfThereIsAlongScrollableTextView(ArrayList<Node<DomNode>> problematicNodes) {
        for (Node<DomNode> node : problematicNodes
        ) {
            String className = node.getData().getTagName();
            String id = node.getData().getId();
            if (className.equals("TextView")) {
                String scrollbars = Utils.getValueFromElement(node.getData(), "scrollbars");
                if (scrollbars != null) {
                    // there is a scroll so we do not need to worry about cuttoff but maybe add lines
                    Logger.debug("There is a scrollable text view");
                    return node;
                }


            }
        }
        return null;
    }


    private HashMap<String, GAChromosome> createChromosomesBasedOnGroupsOfChangesForCollision(
            HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {

        /*
        Iterate through the groups of changes and create a chromosome for each group
         */

        HashMap<String, GAChromosome> listOfChromosomes = new HashMap<>();
        for (String groupKey : groupsOfChanges.keySet()
        ) {

            HashMap<String, GAChange> group = groupsOfChanges.get(groupKey);
            GAChromosome chromosome = new GAChromosome();
            for (String changeKey : group.keySet()
            ) {
                GAChange change = group.get(changeKey);
                Node<DomNode> currNode = change.getElement().getNode();
                GAGene g = createNewGene(currNode, OwlConstants.COLLISION_ISSUE, change);
                g.setGAChange(change);
                g.setChangeType(change.getChangeType());
                chromosome.addGene(g);
            }
            if (chromosome.getGenes().size() > 0) { // only add the chromosome if it has genes
                listOfChromosomes.put(groupKey, chromosome);
            }


        }


        return listOfChromosomes;
    }
    private HashMap<String, GAChromosome> createChromosomesBasedOnGroupsOfChangesAllIssuesUnified(HashMap<String, HashMap<String, GAChange>> groupsOfChangesCutoff
            , HashMap<String, HashMap<String, GAChange>> groupsOfChangesCollision
            , HashMap<String, HashMap<String, GAChange>> groupsOfChangesMissing) {

        /*
        Iterate through the groups of changes and create a chromosome for each group
         */



        HashMap<String, HashMap<String, GAChange>> combinedGroupOfChanges = new LinkedHashMap<>();
        combinedGroupOfChanges.put("group1",new HashMap<>());
        combinedGroupOfChanges.put("group2",new HashMap<>());
        combinedGroupOfChanges.put("group3",new HashMap<>());
        combinedGroupOfChanges.put("group4",new HashMap<>());
        combinedGroupOfChanges.put("group5",new HashMap<>());
        combinedGroupOfChanges.put("group6",new HashMap<>());
        combinedGroupOfChanges.put("group7",new HashMap<>());
        // new orginal just  7 groups
        combinedGroupOfChanges.put("group8",new HashMap<>());
        combinedGroupOfChanges.put("group9",new HashMap<>());
        combinedGroupOfChanges.put("group10",new HashMap<>());
        combinedGroupOfChanges.put("group11",new HashMap<>());
        combinedGroupOfChanges.put("group12",new HashMap<>());
        combinedGroupOfChanges.put("group13",new HashMap<>());
        combinedGroupOfChanges.put("group14",new HashMap<>());
        combinedGroupOfChanges.put("group15",new HashMap<>());
        combinedGroupOfChanges.put("group16",new HashMap<>());
        combinedGroupOfChanges.put("group17",new HashMap<>());
        combinedGroupOfChanges.put("group18",new HashMap<>());
        combinedGroupOfChanges.put("group19",new HashMap<>());
        combinedGroupOfChanges.put("group20",new HashMap<>());
        combinedGroupOfChanges.put("group21",new HashMap<>());
        combinedGroupOfChanges.put("group22",new HashMap<>());
        combinedGroupOfChanges.put("group23",new HashMap<>());
        combinedGroupOfChanges.put("group24",new HashMap<>());
        combinedGroupOfChanges.put("groupScrollView",new HashMap<>());

        for (String groupKey : combinedGroupOfChanges.keySet()
        ) {
            if(groupsOfChangesCutoff.containsKey(groupKey)){
                combinedGroupOfChanges.get(groupKey).putAll(groupsOfChangesCutoff.get(groupKey));
            }
            if(groupsOfChangesCollision.containsKey(groupKey)){
                combinedGroupOfChanges.get(groupKey).putAll(groupsOfChangesCollision.get(groupKey));
            }
            if(groupsOfChangesMissing.containsKey(groupKey)){
                combinedGroupOfChanges.get(groupKey).putAll(groupsOfChangesMissing.get(groupKey));
            }
        }

        HashMap<String, GAChromosome> listOfChromosomes = new HashMap<>();
        HashMap<String, String> uniqueGenes = new HashMap<>();

        // Identify unique genes based on the combination of element ID and attribute name
        // iterate through key value pairs of the combinedGroupOfChanges
        for (String groupKey : combinedGroupOfChanges.keySet()) {
       // for (HashMap<String, GAChange> group : combinedGroupOfChanges.values()) {
            // key string in the HashMap<String, GAChange> group
            HashMap<String, GAChange> group = combinedGroupOfChanges.get(groupKey);
            if(groupKey.equals("groupScrollView") && group.size()>0){
                GAGene scrollGene=createScrollViewGeneMissing(combinedGroupOfChanges.get(groupKey));
                System.out.println("scrollGene "+scrollGene);
                String geneKey = scrollGene.getSegmentIssueId()+"#"+OwlConstants.CHANGE_ADD_NEW_ELEMENT;
                if (!uniqueGenes.containsKey(geneKey)) {
                    uniqueGenes.put(geneKey, geneKey);
                }
                continue;
            }
            for (GAChange change : group.values()) {

                Node<DomNode> currNode = change.getElement().getNode();
                String id = currNode.getData().getId();
                if (change.getAttName()==null || change.getAttName().equals("")){
                    continue;
                }
                String geneKey = id + "#" + change.getAttName();

                if (!uniqueGenes.containsKey(geneKey)) {
                    uniqueGenes.put(geneKey, geneKey);
                }
            }
        }

        // Create chromosomes with the unified number of genes
        for (String groupKey : combinedGroupOfChanges.keySet()) {
            if(groupKey.equals("groupScrollView")){
                Logger.debug("it is a scroll view group");
                // take the whole group to  createScrollViewGeneMissing method
                GAGene scrollGene=createScrollViewGeneMissing(combinedGroupOfChanges.get(groupKey));
                if (scrollGene != null) {
                    GAChromosome chromosome = new GAChromosome();
                    chromosome.addGene(scrollGene);
                    listOfChromosomes.put(groupKey, chromosome);
                }
                continue;
            }
            HashMap<String, GAChange> group = combinedGroupOfChanges.get(groupKey);
            GAChromosome chromosome = new GAChromosome();

            for (String geneKey : uniqueGenes.keySet()) {
                String[] geneKeyParts = geneKey.split("#");
                String elementId = geneKeyParts[0];
                String attributeName = geneKeyParts[1];

                //GAChange change = group.get(elementId + "#" + attributeName);
                GAChange change = findChange(group, elementId, attributeName);

                Node<DomNode> currNode = change != null ? change.getElement().getNode() : null;
                String geneValue = change != null ? change.getValue() : OwlConstants.CHANGE_SKIP;
                String issueToFix=change != null ? change.getIssueToFix() : OwlConstants.CHANGE_SKIP;
               // GAGene g = createNewGeneUnified(currNode, issueToFix, change);
                GAGene g = createNewGeneUnified(currNode, issueToFix, attributeName, geneValue, elementId);

                g.setGAChange(change);
                g.setChangeType(change != null ? change.getChangeType() : OwlConstants.CHANGE_SKIP);
                chromosome.addGene(g);
            }

        if (chromosome.getGenes().size() > 0 && !allGenesAreSkip(chromosome)) { // only add the chromosome if it has genes
            listOfChromosomes.put(groupKey, chromosome);
        }

//            listOfChromosomes.put(groupKey, chromosome);
        }

//        combinedGroupOfChanges.putAll(groupsOfChangesCutoff);
//        combinedGroupOfChanges.putAll(groupsOfChangesCollision);
//        combinedGroupOfChanges.putAll(groupsOfChangesMissing);



        return listOfChromosomes;
    }
    private boolean allGenesAreSkip(GAChromosome chromosome) {
        for (GAGene gene : chromosome.getGenes()) {
            if (!OwlConstants.CHANGE_SKIP.equals(gene.getValue())) {
                return false;
            }
        }
        return true;
    }
    private GAChange findChange(HashMap<String, GAChange> group, String elementId, String attributeName) {
        for (GAChange change : group.values()) {
            String att = change.getAttName();
            String changeID = change.getElement().getNode().getData().getId();
            if (changeID.equals(elementId) && att.equals(attributeName)) {
                return change;
            }
        }
        return null;
    }
    private HashMap<String, GAChromosome> createChromosomesBasedOnGroupsOfChangesAllIssues(HashMap<String, HashMap<String, GAChange>> groupsOfChangesCutoff
            , HashMap<String, HashMap<String, GAChange>> groupsOfChangesCollision
            , HashMap<String, HashMap<String, GAChange>> groupsOfChangesMissing) {

        /*
        Iterate through the groups of changes and create a chromosome for each group
         */



        HashMap<String, HashMap<String, GAChange>> combinedGroupOfChanges = new LinkedHashMap<>();
        combinedGroupOfChanges.put("group1",new HashMap<>());
        combinedGroupOfChanges.put("group2",new HashMap<>());
        combinedGroupOfChanges.put("group3",new HashMap<>());
        combinedGroupOfChanges.put("group4",new HashMap<>());
        combinedGroupOfChanges.put("group5",new HashMap<>());
        combinedGroupOfChanges.put("group6",new HashMap<>());
        combinedGroupOfChanges.put("group7",new HashMap<>());
        // new orginal just  7 groups
        combinedGroupOfChanges.put("group8",new HashMap<>());
        combinedGroupOfChanges.put("group9",new HashMap<>());
        combinedGroupOfChanges.put("group10",new HashMap<>());
        combinedGroupOfChanges.put("group11",new HashMap<>());
        combinedGroupOfChanges.put("group12",new HashMap<>());
        combinedGroupOfChanges.put("group13",new HashMap<>());
        combinedGroupOfChanges.put("group14",new HashMap<>());
        combinedGroupOfChanges.put("group15",new HashMap<>());
        combinedGroupOfChanges.put("group16",new HashMap<>());
        combinedGroupOfChanges.put("group17",new HashMap<>());
        combinedGroupOfChanges.put("group18",new HashMap<>());
        combinedGroupOfChanges.put("group19",new HashMap<>());
        combinedGroupOfChanges.put("group20",new HashMap<>());
        combinedGroupOfChanges.put("group21",new HashMap<>());
        combinedGroupOfChanges.put("group22",new HashMap<>());
        combinedGroupOfChanges.put("group23",new HashMap<>());
        combinedGroupOfChanges.put("group24",new HashMap<>());
        combinedGroupOfChanges.put("groupScrollView",new HashMap<>());

        for (String groupKey : combinedGroupOfChanges.keySet()
             ) {
            if(groupsOfChangesCutoff.containsKey(groupKey)){
                combinedGroupOfChanges.get(groupKey).putAll(groupsOfChangesCutoff.get(groupKey));
            }
            if(groupsOfChangesCollision.containsKey(groupKey)){
                combinedGroupOfChanges.get(groupKey).putAll(groupsOfChangesCollision.get(groupKey));
            }
            if(groupsOfChangesMissing.containsKey(groupKey)){
                combinedGroupOfChanges.get(groupKey).putAll(groupsOfChangesMissing.get(groupKey));
            }
        }

//        combinedGroupOfChanges.putAll(groupsOfChangesCutoff);
//        combinedGroupOfChanges.putAll(groupsOfChangesCollision);
//        combinedGroupOfChanges.putAll(groupsOfChangesMissing);

        HashMap<String, GAChromosome> listOfChromosomes = new HashMap<>();
        for (String groupKey : combinedGroupOfChanges.keySet()
        ) {
            if(groupKey.equals("groupScrollView")){
                Logger.debug("it is a scroll view group");
                // take the whole group to  createScrollViewGeneMissing method
                GAGene scrollGene=createScrollViewGeneMissing(combinedGroupOfChanges.get(groupKey));
                if (scrollGene != null) {
                    GAChromosome chromosome = new GAChromosome();
                    chromosome.addGene(scrollGene);
                    listOfChromosomes.put(groupKey, chromosome);
                }
                continue;
            }
            HashMap<String, GAChange> group = combinedGroupOfChanges.get(groupKey);
            GAChromosome chromosome = new GAChromosome();
            for (String changeKey : group.keySet()
            ) {

                GAChange change = group.get(changeKey);
                Node<DomNode> currNode = change.getElement().getNode();
                String issueToFix=change.getIssueToFix();
                GAGene g = createNewGene(currNode, issueToFix, change);
                g.setGAChange(change);
                g.setChangeType(change.getChangeType());
                chromosome.addGene(g);
            }
            if (chromosome.getGenes().size() > 0) { // only add the chromosome if it has genes
                listOfChromosomes.put(groupKey, chromosome);
            }


        }


        return listOfChromosomes;
    }
    private HashMap<String, GAChromosome> createChromosomesBasedOnGroupsOfChangesUnified(HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {

        HashMap<String, GAChromosome> listOfChromosomes = new HashMap<>();
        HashMap<String, String> uniqueGenes = new HashMap<>();

        // Identify unique genes based on the combination of element ID and attribute name
        for (HashMap<String, GAChange> group : groupsOfChanges.values()) {
            for (GAChange change : group.values()) {
                Node<DomNode> currNode = change.getElement().getNode();
                String id = currNode.getData().getId();
                String geneKey = id + "#" + change.getAttName();

                if (!uniqueGenes.containsKey(geneKey)) {
                    uniqueGenes.put(geneKey, geneKey);
                }
            }
        }

        // Create chromosomes with the unified number of genes
        for (String groupKey : groupsOfChanges.keySet()) {
            HashMap<String, GAChange> group = groupsOfChanges.get(groupKey);
            GAChromosome chromosome = new GAChromosome();

            for (String geneKey : uniqueGenes.keySet()) {
                String[] geneKeyParts = geneKey.split("#");
                String elementId = geneKeyParts[0];
                String attributeName = geneKeyParts[1];

                GAChange change = group.get(elementId + "#" + attributeName);
                Node<DomNode> currNode = change != null ? change.getElement().getNode() : null;
                String geneValue = change != null ? change.getValue() : "Skip";

                GAGene g = createNewGene(currNode, OwlConstants.CUT_OFF_ISSUE, change);
                g.setGAChange(change);
                g.setChangeType(change != null ? change.getChangeType() : null);
                chromosome.addGene(g);
            }

            listOfChromosomes.put(groupKey, chromosome);
        }

        return listOfChromosomes;
    }
    private HashMap<String, GAChromosome> createChromosomesBasedOnGroupsOfChanges(HashMap<String, HashMap<String, GAChange>> groupsOfChanges) {

        /*
        Iterate through the groups of changes and create a chromosome for each group
         */

        HashMap<String, GAChromosome> listOfChromosomes = new HashMap<>();
        for (String groupKey : groupsOfChanges.keySet()
        ) {

            HashMap<String, GAChange> group = groupsOfChanges.get(groupKey);
            GAChromosome chromosome = new GAChromosome();
            for (String changeKey : group.keySet()
            ) {
                GAChange change = group.get(changeKey);
                Node<DomNode> currNode = change.getElement().getNode();
                GAGene g = createNewGene(currNode, OwlConstants.CUT_OFF_ISSUE, change);
                g.setGAChange(change);
                g.setChangeType(change.getChangeType());
                chromosome.addGene(g);
            }
            if (chromosome.getGenes().size() > 0) { // only add the chromosome if it has genes
                listOfChromosomes.put(groupKey, chromosome);
            }


        }


        return listOfChromosomes;
    }


    private GAChange createChangeSizeTypeGroup(GAElementToChange element, Property sizeProp, HashMap<String, GAElementToChange> problematicElements) {
        // get the size attribute set type: fixed or constraints (ie wrap_content or match_parent)
        GAChange change = null;
        String staticVal = sizeProp.getCurrentStaticVal();
        if (Utils.isValueNumerical(staticVal)) {
            Double numericalStaticVal = null;
            List<Double> x = Util.getNumbersFromString(staticVal);
            if (x.size() > 0) {  // it is fixed so we should do wrap_content
                numericalStaticVal = x.get(0);
                String changeVal;
                String changeType;
                if (numericalStaticVal > 0) { // it is set as fixed value in the app so we change it to wrap_content
                    changeVal = "-2";
                    changeType = OwlConstants.CHANGE_INCREASE;
                    change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);
                } else if (numericalStaticVal == -2) { // it is set as wrap_content or match_parent so we change it to fixed value
                    String dynamicVal = sizeProp.getCurrentDynamicVal();
                    if (dynamicVal != null) {
                        List<Double> y = Util.getNumbersFromString(dynamicVal);
                        if (y.size() > 0) {
                            Double numericalDynamicVal = y.get(0);
                            changeVal = String.valueOf(numericalDynamicVal + 20);
                            changeType = OwlConstants.CHANGE_INCREASE;
                            change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);
                        }
                    }
                }


            }

        } else { // it is an exceptional  case that I will handle later

        }

        return change;
    }

    private GAChange createDoNotChangeSizeTypeGroup(GAElementToChange element, Property sizeProp,
                                                    HashMap<String, GAElementToChange> problematicElements,
                                                    HashMap<String, GAChange> weightChanges) {
        GAChange change = null;
        String staticVal = sizeProp.getCurrentStaticVal();
        if (Utils.isValueNumerical(staticVal)) {  //we are replacing wrap_content and match_parent with -2 and -1 in getValueFromElement method so basically we are always going to get numerical unless execptional cases
            Double numericalStaticVal = null;
            List<Double> x = Util.getNumbersFromString(staticVal);
            if (x.size() > 0) {
                numericalStaticVal = x.get(0);
                String changeVal;
                String changeType;
                if (numericalStaticVal > 0) { // it is set as fixed value in the app
                    double n = numericalStaticVal + 20;
                    changeVal = n + "";
                    changeType = OwlConstants.CHANGE_INCREASE;
                    change = new GAChange(sizeProp.getPropertyName(), OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(sizeProp.getPropertyName()), changeType, changeVal, element, sizeProp);

                } else if (numericalStaticVal == 0) { // it is set with weight  ToDo: How to handle this case? maybe we look for other type of changes not the layout_height
                    handleWeightForMainSize(element, sizeProp.getPropertyName(), staticVal, sizeProp.getCurrentDynamicVal(), weightChanges);

                } else if (numericalStaticVal == -1) { // it is set with match_parent
                    // Should we go with min_height so we get dynamic height and add 10  as min_height and then leave the change to a parent in the SRG

                    change = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal());

                } else if (numericalStaticVal == -2) { // it is set with wrap_content
                    // since it is wrap_content we should go with min_hgight
//                                   changeVal = numericalStaticVal + 10 + "";
//                                   changeType = OwlConstants.Increase;

                    change = addMinChange2(element, sizeProp.getPropertyName(), sizeProp.getCurrentDynamicVal());
                }
            }


        } else { // it is an exceptional  case that I will handle later

        }

        return change;
    }

    private GAGene createScrollViewGeneMissing(HashMap<String, GAChange> MissingElementsChangesGroup) {
      // Iterate through the problematic elements in MissingElementsChanges that represent all missing and find the least common parent
        ArrayList<String> problematicElementsXPaths = new ArrayList<>();
        for (String elementId : MissingElementsChangesGroup.keySet()) {

            GAChange change = MissingElementsChangesGroup.get(elementId);
            GAElementToChange problematicElement = change.getElement();
            Node<DomNode> problematicElementNode = problematicElement.getNode();

            if (problematicElementNode == null) { // should not be null since we already found it in the dependency analysis
                continue;
            }
            String xpath = problematicElement.getXpath();
            if (xpath == null) {
                continue;
            }
            problematicElementsXPaths.add(xpath);

        }

        String leastCommonParentXpath = Utils.getLowestCommonAncestor(problematicElementsXPaths);
        Logger.trace("leastCommonParent: " + leastCommonParentXpath);
        if (leastCommonParentXpath == null || leastCommonParentXpath.equals("")) {
            return null;
        }
        usc.edu.OwlEye.VHTree.XMLUtils xmlTree = OwlEye.getOriginalDefaultUI().getXMLTree();
        Node<DomNode> leastCommonParentNode = xmlTree.searchVHTreeByXpath(leastCommonParentXpath);
        if (leastCommonParentNode == null) {
            return null;
        }
        String leastCommonParentID = leastCommonParentNode.getData().getId();
        Logger.trace("leastCommonParentID: " + leastCommonParentID);

        GAGene gene = createNewElementGeneMissing(leastCommonParentNode);

        // now we have the least common parent, we need to create the scroll view and the linear layout above it
        return gene;


    }
    private Node<DomNode> createScrollViewGene(HashMap<String, Cutoff> cutOffIssues, HashMap<String, HashMap<String, HashMap<String, GAElementToChange>>> issuesToElements,
                                               HashMap<String, GAChromosome> generatedChromosomes) {
        // Right now I will add them as at the level that is equal to the least common parent of all probelmatic elements of the horizontal cuttoff issues..
        // ToDo: I will need to revise and update this later
        usc.edu.OwlEye.VHTree.XMLUtils xmlTree = OwlEye.getOriginalDefaultUI().getXMLTree();
        // first get the least common parent of all problematic elements
        ArrayList<String> problematicElements = new ArrayList<>();
        for (String issue : cutOffIssues.keySet()) {
            if (cutOffIssues.get(issue).getIssueType().equals(OwlConstants.HORIZONTAL_ISSUE)) {
                Cutoff cutoff = cutOffIssues.get(issue);
                String problematicElement = cutoff.getProblematicElement();

                //domNodeFound = XMLUtils.searchVHTreeByXpath(element.getXpath(), root);
                Node<DomNode> domNodeFound = xmlTree.searchByID_T(problematicElement, null);
                Logger.trace("problematicElement: " + problematicElement);
                String xpath = domNodeFound.getData().getxPath();
                problematicElements.add(xpath);
            }
        }
        String leastCommonParentXpath = Utils.getLowestCommonAncestor(problematicElements);
        Logger.trace("leastCommonParent: " + leastCommonParentXpath);
        if (leastCommonParentXpath == null || leastCommonParentXpath.equals("")) {
            return null;
        }
        Node<DomNode> leastCommonParentNode = xmlTree.searchVHTreeByXpath(leastCommonParentXpath);
        if (leastCommonParentNode == null) {
            return null;
        }
        String leastCommonParentID = leastCommonParentNode.getData().getId();
        Logger.trace("leastCommonParentID: " + leastCommonParentID);

        createAnElementFromScratch(leastCommonParentNode, generatedChromosomes);
        // now we have the least common parent, we need to create the scroll view and the linear layout above it
        return leastCommonParentNode;


    }

    public void createAnElementFromScratch(Node<DomNode> leastCommonParentNode, HashMap<String, GAChromosome> generatedChromosomes) {
        GAGene gene = createNewElementGene(leastCommonParentNode);
        GAChromosome chromosome = new GAChromosome();
        chromosome.addGene(gene);
        generatedChromosomes.put(gene.getSegmentID(), chromosome);

    }

    private boolean doWeNeedToAddScrollView(HashMap<String, Cutoff> cutOffIssues, HashMap<String, HashMap<String, HashMap<String, GAElementToChange>>> issuesToElements) {
        // this method will analyze the issues and decide if we need to add a scroll view or not
        if (cutOffIssues.size() <= 1) {  // for now if one issue no need to add scroll view
            return false;
        }
        int numberOfHeightIssues = 0;
        int numberOfWidthIssues = 0;
        for (String issue : cutOffIssues.keySet()
        ) {
            Cutoff cutoff = cutOffIssues.get(issue);
            String issueType = cutoff.getIssueType();
            if (issueType.equals(OwlConstants.HORIZONTAL_ISSUE)) {
                numberOfHeightIssues++;
            } else if (issueType.equals(OwlConstants.VERTICAL_ISSUE)) {
                numberOfWidthIssues++;
            }
        }

        if (numberOfHeightIssues > 3) {
            return true;
        }

        return false;
    }

    private HashMap<String, GAChromosome> createChromosomesBasedOnFixSet(HashMap<String, HashMap<String, HashMap<String, GAElementToChange>>> issuesToElements
            , ArrayList<GAChromosome> initialPopulation) {
        int chromosomeId = 1;
        HashMap<String, GAChromosome> listOfChromosomes = new HashMap<>();
        // 1- extracting all the problematic elements
        HashMap<String, GAElementToChange> problematic = new HashMap<>();
        for (String issue : issuesToElements.keySet()
        ) {
            HashMap<String, GAElementToChange> problematicForIssue = issuesToElements.get(issue).get("problematic");
            problematic.putAll(problematicForIssue);
        }
        // 2- extracting all the SRG elements
        HashMap<String, GAElementToChange> srg = new HashMap<>();
        for (String issue : issuesToElements.keySet()
        ) {
            HashMap<String, GAElementToChange> srgForIssue = issuesToElements.get(issue).get("srg");
            srg.putAll(srgForIssue);
        }
        // 3- extracting all vsrg elements
        HashMap<String, GAElementToChange> vsrg = new HashMap<>();
        for (String issue : issuesToElements.keySet()
        ) {
            HashMap<String, GAElementToChange> vsrgForIssue = issuesToElements.get(issue).get("vsrg");
            vsrg.putAll(vsrgForIssue);
        }

        // 4- extracting all padding elements
        HashMap<String, GAElementToChange> padding = new HashMap<>();
        for (String issue : issuesToElements.keySet()
        ) {
            HashMap<String, GAElementToChange> paddingForIssue = issuesToElements.get(issue).get("padding");
            padding.putAll(paddingForIssue);
        }

        // 5- extracting all margin elements
        HashMap<String, GAElementToChange> margin = new HashMap<>();
        for (String issue : issuesToElements.keySet()
        ) {
            HashMap<String, GAElementToChange> marginForIssue = issuesToElements.get(issue).get("margin");
            margin.putAll(marginForIssue);
        }

        boolean includeSRG = true;
        boolean includeVSrg = true;
        boolean includePadding = false;
        boolean includeMargin = true;

        ArrayList<GAGene> problematicGenes = new ArrayList<>();
        ArrayList<GAGene> srgGenes = new ArrayList<>();
        ArrayList<GAGene> vsrgGenes = new ArrayList<>();
        ArrayList<GAGene> paddingGenes = new ArrayList<>();
        ArrayList<GAGene> marginsGenes = new ArrayList<>();


        /* - all problematic elements changes should be together in one chromosome. However we should make sure that the changes are not conflicting with each other.
         *  - SRG elements should be once alone in their own chromosome and once with the problematic elements in one chromosome.
         *  - VSRG elements should once be off (not added) and once added with the problematic elements in one chromosome.
         *  ** So we basically we have 6 chromsomes:
         *   1- problematic elements alone
         *  2- SRG elements alone
         *  3- Problematic elements + SRG elements
         * 4- Problematic elements + VSRG elements
         * 5- Problematic elements + SRG elements + VSRG elements
         */

        // 1- problematic elements genes alone
        for (String elementId : problematic.keySet()) {
            GAElementToChange elementToChange = problematic.get(elementId);
            HashMap<String, GAChange> listOfChange = elementToChange.getListOfChanges();
            if (listOfChange == null) {
                continue;
            }
            for (String changeId : listOfChange.keySet()
            ) {
                Node<DomNode> currNode = elementToChange.getNode();
                GAChange change = listOfChange.get(changeId);
                GAGene g = createNewGene(currNode, OwlConstants.CUT_OFF_ISSUE, change);
                problematicGenes.add(g);
                // GAGene gene = new GAGene(changeId, OwlConstants.CUT_OFF_ISSUE, cutoff);
            }
        }
        //  GAChromosome chromosome = new GAChromosome(problematicGenes);
        //listOfChromosomes.put(String.valueOf(chromosomeId++), chromosome);


        // 2- SRG genes elements alone
        if (includeSRG) {
            for (String elementId : srg.keySet()) {
                GAElementToChange elementToChange = srg.get(elementId);
                HashMap<String, GAChange> listOfChange = elementToChange.getListOfChanges();
                if (listOfChange == null) {
                    continue;
                }
                for (String changeId : listOfChange.keySet()
                ) {
                    Node<DomNode> currNode = elementToChange.getNode();
                    GAChange change = listOfChange.get(changeId);
                    //     GAGene g = new GAGene(currNode.getData().getxPath(), OwlConstants.CUT_OFF_ISSUE, change.getAttName(), change.getValue());
                    GAGene g = createNewGene(currNode, OwlConstants.CUT_OFF_ISSUE, change);
                    srgGenes.add(g);
                    // GAGene gene = new GAGene(changeId, OwlConstants.CUT_OFF_ISSUE, cutoff);
                }
            }


        }// if includeSRG

        // 3- VSRG genes elements alone
        if (includeVSrg) {
            for (String elementId : vsrg.keySet()) {
                GAElementToChange elementToChange = vsrg.get(elementId);
                HashMap<String, GAChange> listOfChange = elementToChange.getListOfChanges();
                if (listOfChange == null) {
                    continue;
                }
                for (String changeId : listOfChange.keySet()
                ) {
                    Node<DomNode> currNode = elementToChange.getNode();
                    GAChange change = listOfChange.get(changeId);
                    //     GAGene g = new GAGene(currNode.getData().getxPath(), OwlConstants.CUT_OFF_ISSUE, change.getAttName(), change.getValue());
                    GAGene g = createNewGene(currNode, OwlConstants.CUT_OFF_ISSUE, change);
                    vsrgGenes.add(g);
                    // GAGene gene = new GAGene(changeId, OwlConstants.CUT_OFF_ISSUE, cutoff);
                }
            }
        }// if includeVSrg

        // 4- padding genes elements alone
        if (includePadding) {
            for (String elementId : padding.keySet()) {
                GAElementToChange elementToChange = padding.get(elementId);
                HashMap<String, GAChange> listOfChange = elementToChange.getListOfChanges();
                if (listOfChange == null) {
                    continue;
                }
                for (String changeId : listOfChange.keySet()
                ) {
                    Node<DomNode> currNode = elementToChange.getNode();
                    GAChange change = listOfChange.get(changeId);
                    //     GAGene g = new GAGene(currNode.getData().getxPath(), OwlConstants.CUT_OFF_ISSUE, change.getAttName(), change.getValue());
                    GAGene g = createNewGene(currNode, OwlConstants.CUT_OFF_ISSUE, change);
                    paddingGenes.add(g);
                    // GAGene gene = new GAGene(changeId, OwlConstants.CUT_OFF_ISSUE, cutoff);
                }
            }
        }// if includePadding

        // 5- margins genes elements alone
        if (includeMargin) {
            for (String elementId : margin.keySet()) {
                GAElementToChange elementToChange = margin.get(elementId);
                HashMap<String, GAChange> listOfChange = elementToChange.getListOfChanges();
                if (listOfChange == null) {
                    continue;
                }
                for (String changeId : listOfChange.keySet()
                ) {
                    Node<DomNode> currNode = elementToChange.getNode();
                    GAChange change = listOfChange.get(changeId);
                    //     GAGene g = new GAGene(currNode.getData().getxPath(), OwlConstants.CUT_OFF_ISSUE, change.getAttName(), change.getValue());
                    GAGene g = createNewGene(currNode, OwlConstants.CUT_OFF_ISSUE, change);
                    marginsGenes.add(g);
                    // GAGene gene = new GAGene(changeId, OwlConstants.CUT_OFF_ISSUE, cutoff);
                }
            }
        }// if includeMargin

        // -- chromosome-1 for problematic elements only
        GAChromosome chromosome1 = new GAChromosome(problematicGenes);
        listOfChromosomes.put(String.valueOf(chromosomeId++), chromosome1);

        if (srgGenes.size() > 0) {
            // -- chromosome-2 for problematic elements + SRG elements
            ArrayList<GAGene> problematicSRGGenes = new ArrayList<>();
            problematicSRGGenes.addAll(problematicGenes);
            problematicSRGGenes.addAll(srgGenes);
            GAChromosome chromosome2 = new GAChromosome(problematicSRGGenes);
            listOfChromosomes.put(String.valueOf(chromosomeId++), chromosome2);

            GAChromosome chromosome3 = new GAChromosome(srgGenes);
            listOfChromosomes.put(String.valueOf(chromosomeId++), chromosome3);
        }

        if (vsrgGenes.size() > 0) {
            // -- chromosome for problematic elements + VSRG elements
            ArrayList<GAGene> problematicVSRGGenes = new ArrayList<>();
            problematicVSRGGenes.addAll(problematicGenes);
            problematicVSRGGenes.addAll(vsrgGenes);
            GAChromosome chromosome4 = new GAChromosome(problematicVSRGGenes);
            listOfChromosomes.put(String.valueOf(chromosomeId++), chromosome4);


            // -- chromosome for problematic elements + SRG elements + VSRG elements
            if (srgGenes.size() > 0) {
                ArrayList<GAGene> problematicSRGVSRGGenes = new ArrayList<>();
                problematicSRGVSRGGenes.addAll(problematicGenes);
                problematicSRGVSRGGenes.addAll(srgGenes);
                problematicSRGVSRGGenes.addAll(vsrgGenes);
                GAChromosome chromosome5 = new GAChromosome(problematicSRGVSRGGenes);
                listOfChromosomes.put(String.valueOf(chromosomeId++), chromosome5);
            }
        }

        if (paddingGenes.size() > 0) {
            // -- chromosome for problematic elements + padding elements
            ArrayList<GAGene> problematicPaddingGenes = new ArrayList<>();
            problematicPaddingGenes.addAll(problematicGenes);

            problematicPaddingGenes.addAll(paddingGenes);
            GAChromosome chromosome6 = new GAChromosome(problematicPaddingGenes);
            listOfChromosomes.put(String.valueOf(chromosomeId++), chromosome6);
        }

        if (marginsGenes.size() > 0) {
            // -- chromosome for problematic elements + margins elements + SRG elements
            ArrayList<GAGene> problematicMarginsSRGGenes = new ArrayList<>();
            problematicMarginsSRGGenes.addAll(problematicGenes);
            problematicMarginsSRGGenes.addAll(srgGenes);
            problematicMarginsSRGGenes.addAll(marginsGenes);
            GAChromosome chromosome7 = new GAChromosome(problematicMarginsSRGGenes);
            listOfChromosomes.put(String.valueOf(chromosomeId++), chromosome7);
        }
        return listOfChromosomes;

    }

    private GAGene createNewGene(Node<DomNode> currNode, String issue, GAChange change) {
        String xpath = currNode.getData().getxPath();
        String id = currNode.getData().getId();
        String segmentId = null; // for now I am not considering that
        if (id != null) {

            segmentId = id + "#" + change.getAttName() + "#" + change.getValue();
        }

        GAGene g = new GAGene(currNode.getData().getxPath(), OwlConstants.CUT_OFF_ISSUE, change.getAttName(), change.getValue(), segmentId);
        return g;
    }
    private GAGene createNewGeneUnified(Node<DomNode> currNode, String issue, String attName, String value, String id) {
        String xpath = currNode != null ? currNode.getData().getxPath() : null;
        String segmentId = id != null ? id + "#" + attName + "#" + value : null;

        GAGene g = new GAGene(xpath, issue, attName, value, segmentId);
        return g;
//        String xpath = currNode.getData().getxPath();
//        String id = currNode.getData().getId();
//        String segmentId = null; // for now I am not considering that
//        if (id != null) {
//
//            segmentId = id + "#" + change.getAttName() + "#" + change.getValue();
//        }
//
//        GAGene g = new GAGene(currNode.getData().getxPath(), OwlConstants.CUT_OFF_ISSUE, change.getAttName(), change.getValue(), segmentId);
//        return g;
    }
    private GAGene createNewElementGeneMissing(Node<DomNode> currNode) {
        String xpath = currNode.getData().getxPath();
        String id = currNode.getData().getId();
        String segmentId = null; // for now I am not considering that
        if (id != null) {

            segmentId = id + "#ScrollView";
        }
        GAGene g = new GAGene(currNode.getData().getxPath(), OwlConstants.MISSING_ISSUE, OwlConstants.CHANGE_ADD_NEW_ELEMENT, OwlConstants.CHANGE_ADD_NEW_ELEMENT, segmentId);
        g.setChangeType(OwlConstants.CHANGE_ADD_NEW_ELEMENT);
        return g;
    }
    private GAGene createNewElementGene(Node<DomNode> currNode) {
        String xpath = currNode.getData().getxPath();
        String id = currNode.getData().getId();
        String segmentId = null; // for now I am not considering that
        if (id != null) {

            segmentId = id + "#ScrollView";
        }
        GAGene g = new GAGene(currNode.getData().getxPath(), OwlConstants.CUT_OFF_ISSUE, "NewElement", "NewElement", segmentId);
        g.setChangeType(OwlConstants.CHANGE_ADD_NEW_ELEMENT);
        return g;
    }


}
