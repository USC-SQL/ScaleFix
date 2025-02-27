package usc.edu.OwlEye.GA;

import org.tinylog.Logger;
import usc.edu.OwlEye.GAChanges.GAChange;
import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.fitness.FitnessFunction;
import usc.edu.OwlEye.util.AndroidEmulatorAccess;
import usc.edu.OwlEye.util.PrepareAppRunner;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.Constants;
import usc.edu.SALEM.SALEM;
//import usc.edu.SALEM.fitness.FitnessFunction;
//import usc.edu.SALEM.util.PrepareAppRunner;
import usc.edu.SALEM.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GASearch {


    private final GAChromosome initialChromosome;
    private List<GAChromosome> paretoFront;
    private List<GAChromosome> initialPopulation;
    private List<GAChromosome> currentPopulation;
    private List<GAChromosome> nextGeneration;
    private static int generationCount;

    public GASearch(GAChromosome chromosome) {
        this.initialChromosome = chromosome;
        this.currentPopulation = new ArrayList<GAChromosome>();
        this.nextGeneration = new ArrayList<GAChromosome>();
        generationCount = 0;
    }
    public GASearch(ArrayList<GAChromosome> population) {
        initialChromosome= population.get(0);
        this.initialPopulation = new ArrayList<>();
        for (GAChromosome chromosome: population) {
             GAChromosome temp = chromosome.copy();
             initialPopulation.add(temp);
        }
       // this.initialPopulation = population;
        this.currentPopulation = population;
        this.nextGeneration = new ArrayList<GAChromosome>();
        generationCount = 0;
    }


    public static int getGenerationCount() {
        return generationCount;
    }

    public static void setGenerationCount(int generationCount) {
        GASearch.generationCount = generationCount;
    }


    private void initialize3(int populationSize) {
        // initialize first chromosome with all genes as suggested values from Google

        HashMap<String,Set<String>> usedValues= new HashMap<>(); // to store used values for each gene
        for (GAChromosome chromosome: initialPopulation) {
            // Going through all of the genes we created in the impact analysis and flag their origin
            chromosome.addOriginToTrace("ImpactAnalysis");

        }
        // iterate through the initial population and  copy and make other chromosomes
        // 1- if value is numerical, we apply a gaussian distribution
        // 2- if value is not numerical, we apply the different possible values.
        // 3- we may also need to add genes that basically say "do not apply change to the property in the gene"

        for (int i = 1; i < populationSize; i++) {
            for (GAChromosome chromosome: initialPopulation) {
                GAChromosome temp = chromosome.copy();
                for (GAGene g : chromosome.getGenes()) {


                    GAGene newGene = g.copy();
                    String valType = newGene.getValueType();
                    String changeType = newGene.getChangeType();
                    if (changeType.equals(OwlConstants.CHANGE_ADD_NEW_ELEMENT)) {
                        continue;
                    }
                    // getting the new value of the gene
                    if (!g.getChangeType().equals(OwlConstants.CHANGE_SKIP)) { // if it is a skip gene, we keep it the same

                    String newValue = determineNewValueForGene(valType, newGene, usedValues);
                    if (newValue != null && !newValue.equals("")) {  // if it is not null and it is not returned as empty , we set the new value for the gene; otherwise, we keep the old value

                        newGene.setValue(newValue); // no need to add  unit, I am adding that later when getting elements to change

                    }
                }

                    temp.replaceGene(g, newGene);
                }
                temp.addOriginToTrace("initialization");
                currentPopulation.add(temp);
            }


            }
        }

    private String determineNewValueForGene(String valType, GAGene newGene, HashMap<String, Set<String>> usedValues) {
        // (1) determine the type of the gene value

        /*
            dp: numericalDP ; no. numerical number from 1 to 10; sCon: size constraint "match_parent" & "wrap_content";
    boolean: true & false; id: id of other element ; element: add new element; remove: remove property from the element
         */

        // add the initial value of the gene to the used values
        addGeneUsedValue(newGene, usedValues);
        Set<String> geneUsedValues = usedValues.get(newGene.getSegmentIssueId());


        double newVal= Double.MIN_VALUE;


//        String stringNewVal= "";
        String property=newGene.getCssProperty();
        String changeType=newGene.getChangeType();
        GAChange gaChange= newGene.getGAChange();
        String currentVal=newGene.getValue();
      //  double newVal= currentVal;
        String stringNewVal= currentVal;
//        if(Utils.isValueNumerical(currentVal)){
//            newVal= Double.parseDouble(currentVal);
//        }
//        Double currentValNumerical=Util.getNumbersFromString(currentVal).get(0);
        switch (valType) {
            case OwlConstants.SCON_VALUE:                 // // wrap_content or match_parent. In that case maybe we can iterate through the different possible values (so match_parent and wrap_content and value)


            {

               Logger.trace("SCon value");

                // if min is defined then increase change the value of the min???
                boolean skipThisFeb8= true;
                if(skipThisFeb8){
                    return stringNewVal;
                }
                List<Double> inputs = Utils.generateGaussianInputsForDP(property,gaChange, changeType, currentVal);
                newVal = Util.getGaussianValue(inputs.get(0), inputs.get(1), inputs.get(2));
                stringNewVal = String.valueOf(newVal);
                geneUsedValues.add(stringNewVal);
                break;
            }
            case OwlConstants.NUMERICAL_DP_VALUE: // numerical dp value (e.g. 10dp) in this case, we need to apply a gaussian distribution and the min and max values will be based on the prop type
                {
               Logger.trace("numerical dp value");
                // determine if wrap_content or match_parent has been used (we focus on wrap_content first)

//                if (!geneUsedValues.contains("wrap_content")) {
//                    stringNewVal="wrap_content";
//                    geneUsedValues.add(stringNewVal);
//                }
//                else {
                    List<Double> inputs = Utils.generateGaussianInputsForDP(property,gaChange, changeType, currentVal);
                    newVal = Util.getGaussianValue(inputs.get(0), inputs.get(1), inputs.get(2));
                    stringNewVal = String.valueOf(newVal);
                    geneUsedValues.add(stringNewVal);
              //  }

                break;
            }
            case OwlConstants.NUMERICAL_INT_VALUE: // numerical value from 1 to 10 (no unit) such as android:layout_weight or android:lines
            {
                //weight should be here
               Logger.trace("numerical int value");
                List<Double> inputs = Utils.generateGaussianInputsForNumerical(property,gaChange, changeType, currentVal);
                newVal = Util.getGaussianValue(inputs.get(0), inputs.get(1), inputs.get(2));
                stringNewVal = String.valueOf(newVal);
                geneUsedValues.add(stringNewVal);
                break;
            }

            case OwlConstants.BOOLEAN_VALUE:  // in this case, we need to flip the value (true to false, false to true)
            {
               Logger.trace("boolean value");

                break;
            }

            case OwlConstants.ID_VALUE:// id to another element (what to do then? maybe just remove the gene?)
            {
               Logger.trace("id value");
               // return the same
                stringNewVal = currentVal;
                geneUsedValues.add(stringNewVal);
                break;
            }
            default:
               Logger.trace("default");
                stringNewVal = currentVal;
                geneUsedValues.add(stringNewVal);
                break;
        }
        if (stringNewVal=="") {
            return null;
        }
        else {
            return stringNewVal;
        }
//        return String.valueOf(newVal);
    }

    private static Set<String> addGeneUsedValue(GAGene newGene, HashMap<String, Set<String>> usedValues) {
        String geneName= newGene.getSegmentIssueId();
        Set<String> values;
        if (usedValues.containsKey(geneName)){
            values= usedValues.get(geneName);
            values.add(newGene.getValue());
        }
        else{
             values= new HashSet<>();
            values.add(newGene.getValue());
            usedValues.put(geneName,values);
        }

        return values;
    }


    private void initialize2(int populationSize) {
        // initialize first chromosome with all genes as suggested values from Google


        for (GAChromosome chromosome: currentPopulation) {
            // Going through all of the genes we created in the impact analysis and flag their origin
            chromosome.addOriginToTrace("ImpactAnalysis");

        }




//  TODO: Add more genes to the chromosome using the process in the original initilize methdod
    }

    private void initialize(int populationSize) {
        // initialize first chromosome with all genes as suggested values from Google
        GAChromosome c = initialChromosome.copy();
        for (GAGene g : initialChromosome.getGenes()) {
            GAGene newGene = g.copy();
            newGene.setValue(Constants.ACCESSIBILITY_SUGGESTED_VALUES.get(g.getIssueType()).get(g.getCssProperty()));
            c.replaceGene(g, newGene);
        }
        c.addOriginToTrace("initialization");
        currentPopulation.add(c);

        // initialize other chromosomes in the population
        for (int i = 1; i < populationSize; i++) {
            GAChromosome temp = initialChromosome.copy();
            for (GAGene g : initialChromosome.getGenes()) {

                GAGene newGene = g.copy();
                List<Double> inputs = Util.generateGaussianInputs(g.getIssueType());
                double val = Util.getGaussianValue(inputs.get(0), inputs.get(1), inputs.get(2));
                newGene.setValue(String.valueOf(val)); // no need to add  unit, I am adding that later when getting elements to change
//                newGene.setValue(val + "dp");

                temp.replaceGene(g, newGene);
            }
            temp.addOriginToTrace("initialization");
            currentPopulation.add(temp);
        }
    }

    public void sortPopulationByFitnessScore(List<GAChromosome> population) {
        // descending order of fitness score
        Collections.sort(population);
    }

    private GAChromosome selectParent() {
        // roulette wheel selection
        double populationSum = 0;
        List<GAChromosome> tempPopulation = new ArrayList<GAChromosome>(currentPopulation);

        Collections.shuffle(tempPopulation);

        for (GAChromosome c : tempPopulation) {
            populationSum = populationSum + c.getFitnessFunctionObj().getFitnessScore();
        }

        double r = Math.random();
        double sum = 0.0;
        for (GAChromosome c : tempPopulation) {
            double probability = c.getFitnessFunctionObj().getFitnessScore() / populationSum;
            sum = sum + probability; // probability -> as the fitness function
            // is maximizing
            if (r < sum) {
                return c;
            }
        }
        return currentPopulation.get(0);
    }


    private List<GAChromosome> getBestSolutionWithoutScrollView() {
        List<GAChromosome> tempPopulation = new ArrayList<GAChromosome>(currentPopulation);
        sortPopulationByFitnessScore(tempPopulation);
        boolean hasScrollView=false;
        for (GAChromosome c : tempPopulation) { // check if there is a chromosome with a scrollview
             boolean res=containsScrollViewGene(c);
                if (res){
                    hasScrollView=true;
                    break;
                }
        }
        if (!hasScrollView){
            return null;
        }

        Collections.sort(tempPopulation);
        GAChromosome bestChromosomeScrollView = null;
        GAChromosome bestChromosomeNoScrollView = null;
        for (GAChromosome c : tempPopulation) {
            if (containsScrollViewGene(c)) {
                if(c.getGenes().size()>1){
                Logger.debug("getBestSolutionWithoutScrollView - but chromosome has more than one gene");
                        continue;
                   }
                if (bestChromosomeScrollView == null) {
                    bestChromosomeScrollView = c;
                } else {
                    if (c.getFitnessFunctionObj().getFitnessScore() <bestChromosomeScrollView.getFitnessFunctionObj().getFitnessScore()) {
                        bestChromosomeScrollView = c;
                    }
                }
//                bestChromosomeScrollView = c;
               // break;
            }else{
                if (bestChromosomeNoScrollView == null) {
                    bestChromosomeNoScrollView = c;
                } else {
                    if (c.getFitnessFunctionObj().getFitnessScore() <bestChromosomeNoScrollView.getFitnessFunctionObj().getFitnessScore()) {
                        bestChromosomeNoScrollView = c;
                    }
                }
//                bestChromosomeNoScrollView = c;
                //break;
            }

        }
        List<GAChromosome> chromosomes = new ArrayList<>();
        if(bestChromosomeScrollView==null || bestChromosomeNoScrollView==null){
            return null;
        }
        chromosomes.add(bestChromosomeScrollView);
        chromosomes.add(bestChromosomeNoScrollView);

        return chromosomes;
    }

    private boolean containsScrollViewGene(GAChromosome c) {

        // iterate through all genes and check if they are scrollview genes
        for (GAGene g : c.getGenes()) {
            if (g.getChangeType().equals(OwlConstants.CHANGE_ADD_NEW_ELEMENT)) {

                return true;
            }
        }
        return false;
    }
    private int findIndexOfScrollViewGeneIndex(GAChromosome chromosome) {
        int index=0;
        if(chromosome==null){
            return -1;
        }
        for (GAGene g : chromosome.getGenes()) {
            if (g.getCssProperty().equalsIgnoreCase("ScrollView")) {
               return index;
            }
            index++;
        }
        return -1;
    }
    private GAChromosome crossoverScrollView() {
        List<GAChromosome> bestChromosomes = getBestSolutionWithoutScrollView();
        if (bestChromosomes==null || bestChromosomes.size()<2){
            return null;
        }
        int geneIndex=findIndexOfScrollViewGeneIndex(bestChromosomes.get(1));
        System.out.println("bestChromosomes size: "+bestChromosomes.size());
        // now replace the gene at index "geneIndex" in bestChromosomes.get(1) with the gene at index 0 in bestChromosomes.get(1)
        if(geneIndex==-1){
            return null;
        }
        GAGene geneToReplace=bestChromosomes.get(1).getGenes().get(geneIndex);
        GAGene geneToReplaceWith=bestChromosomes.get(0).getGenes().get(0);
        bestChromosomes.get(1).replaceGene(geneToReplace, geneToReplaceWith);

        return bestChromosomes.get(1);
    }



    private List<GAChromosome> crossoverGPT() {
        List<GAChromosome> offspring = new ArrayList<>();
        double crossoverRate=0.3;
        int numberOfCrossovers = (int) Math.round(currentPopulation.size() * OwlConstants.CROSSOVER_RATE2);
        Logger.debug("Number of crossovers to be performed = " + numberOfCrossovers);
        for (int i = 0; i < numberOfCrossovers; i += 2) {
            GAChromosome parent1 = selectParent();
            GAChromosome parent2 = selectParent();

            int lastIndex = parent1.getGenes().size();
            if (lastIndex > parent2.getGenes().size()) {
                lastIndex = parent2.getGenes().size();
            }
            GAChromosome child1 = new GAChromosome();
            GAChromosome child2 = new GAChromosome();

            for (int j = 0; j < lastIndex; j++) {
                GAGene gene1 = parent1.getGenes().get(j);
                GAGene gene2 = parent2.getGenes().get(j);
               double randomRate= Math.random();
                if (randomRate < crossoverRate) {
                    child1.getGenes().add(gene1.copy());
                    child2.getGenes().add(gene2.copy());
                } else {
                    child1.getGenes().add(gene2.copy());
                    child2.getGenes().add(gene1.copy());
                }
            }

            child1.addOriginToTrace("crossover_gen_GP" + generationCount);
            child2.addOriginToTrace("crossover_gen_GP" + generationCount);

            offspring.add(child1);
            offspring.add(child2);
        }

        return offspring;
    }
    private List<GAChromosome> crossover() {
        List<GAChromosome> nextGenerationCrossover = new ArrayList<>();

        int numberOfCrossovers = (int) Math.round(currentPopulation.size() * OwlConstants.CROSSOVER_RATE);
       Logger.debug("Number of crossovers to be performed = " + numberOfCrossovers);
        for (int n = 0; n < numberOfCrossovers; n++) {
           Logger.debug("Crossover no. " + (n + 1));
            GAChromosome parent1 = selectParent();
            GAChromosome parent2 = selectParent();

            // uniform crossover
            int numberOfGenesToProcess = Util.getRandomIntValueInRange(1, parent1.getGenes().size() + 1);
            Set<Integer> geneIndicesToProcess = new HashSet<Integer>();
            int lastIndex = parent1.getGenes().size();
            if (lastIndex > parent2.getGenes().size()) {
                lastIndex = parent2.getGenes().size();
            }
            for (int i = 0; i < numberOfGenesToProcess; i++) {
                int randomGeneIndex = Util.getRandomIntValueInRange(0, lastIndex - 1);
                geneIndicesToProcess.add(randomGeneIndex);
            }
            // System.out.println("Parent 1 = " + parent1);
            // System.out.println("Parent 2 = " + parent2);
            // System.out.println("Gene indices to process = " +
            // geneIndicesToProcess);

            // add four children generated by uniform crossover to next

            // generation
            List<GAChromosome> children1 = createChildUniformCrossover(parent1, parent2, geneIndicesToProcess);
            for (GAChromosome child1 : children1) {
                child1.addOriginToTrace("crossover_gen" + generationCount);
                nextGenerationCrossover.add(child1);
            }


//			List<GAChromosome> children2 = extractGenesBasedOnIssueTypeSegmentRelationGraphChildUniformCrossover(parent2, parent1, geneIndicesToProcess);
//			for (GAChromosome child2 : children2) {
//				computeFitnessScore(child2);
//				nextGeneration.add(child2);
//			}

/*			List<GAChromosome> children2 = createChildUniformCrossover(parent2, parent1, geneIndicesToProcess);
			for (GAChromosome child2 : children2) {
				nextGenerationCrossover.add(child2);
			}
*/
        }
        return nextGenerationCrossover;
    }


    private List<GAChromosome> createChildUniformCrossover(GAChromosome parent1, GAChromosome parent2,
                                                           Set<Integer> geneIndicesToProcess) {
        List<GAChromosome> children = new ArrayList<GAChromosome>();

        GAChromosome child1 = parent1.copy();
        GAChromosome child2 = parent2.copy();
        for (int i : geneIndicesToProcess) {

            GAGene oldGene = child1.getGene(i);
            GAGene oldGene2 = child2.getGene(i);
            double alpha = Util.getRandomDoubleValueInRange(0.0, 1.0);
            double value1=Double.MIN_VALUE;
            double value2=Double.MIN_VALUE;
            String parent1Val=parent1.getGene(i).getValue();
            String parent2Val=parent2.getGene(i).getValue();

            if(parent1Val.equalsIgnoreCase(OwlConstants.CHANGE_SKIP) && !parent2Val.equalsIgnoreCase(OwlConstants.CHANGE_SKIP)){
                parent1Val=parent2Val;
            }
            if(parent2Val.equalsIgnoreCase(OwlConstants.CHANGE_SKIP) && !parent1Val.equalsIgnoreCase(OwlConstants.CHANGE_SKIP)){
                parent2Val=parent1Val;
            }
            if(Utils.isValueNumerical(parent1Val)){
//                 value1 = Util.getNumbersFromString(parent1.getGene(i).getValue()).get(0);
                    value1 = Util.getNumbersFromString(parent1Val).get(0);
            }
            if(Utils.isValueNumerical(parent2Val)){
                 //value2 = Util.getNumbersFromString(parent2.getGene(i).getValue()).get(0);
                value2 = Util.getNumbersFromString(parent2Val).get(0);
            }
//            double value1 = Util.getNumbersFromString(parent1.getGene(i).getValue()).get(0);
//            String unit = Util.getUnitFromStringValue(parent1.getGene(i).getValue());
//            double value2 = Util.getNumbersFromString(parent2.getGene(i).getValue()).get(0);

            // Old approach - not working
//            GAGene newGene1 = new GAGene(oldGene.getXpaths(), oldGene.getCssProperty(), oldGene.getIssueType(),
//                    Util.getWeightedAverage(value1, value2, alpha) + unit, oldGene.getSegmentIssueId());
//            newGene1.setOriginalValue(oldGene.getOriginalValue());
//            GAGene newGene2 = new GAGene(oldGene2.getXpaths(), oldGene2.getCssProperty(), oldGene2.getIssueType(),
//                    Util.getWeightedAverage(value2, value1, alpha) + unit, oldGene2.getSegmentIssueId());
//            newGene2.setOriginalValue(oldGene2.getOriginalValue());

            // New approach - working
            GAGene newGene1 = oldGene.copy();
            GAGene newGene2 = oldGene2.copy();
            if(value1!=Double.MIN_VALUE && value2!=Double.MIN_VALUE) {
                newGene1.setValue(Util.getWeightedAverage(value1, value2, alpha) + "");

                newGene2.setValue(Util.getWeightedAverage(value2, value1, alpha) + "");
            }else{
               // if (parent1.getGene(i).getValue().equals(parent2.getGene(i).getValue()
               // change the value of the gene without swiping
//                newGene1.setValue(parent1.getGene(i).getValue());
//                newGene2.setValue(parent2.getGene(i).getValue());
                newGene1.setValue(parent1Val);
                newGene2.setValue(parent2Val);
            }

            child1.replaceGene(oldGene, newGene1);
            child2.replaceGene(oldGene2, newGene2);
        }
        children.add(child1);
        children.add(child2);
        return children;
    }

    private List<GAChromosome> mutation2() {
        List<GAChromosome> nextGenerationMutation = new ArrayList<>();
      //  int numberOfChromosomesToMutate = (int) Math.round(currentPopulation.size() * OwlConstants.MUTATION_RATE); original
        int numberOfChromosomesToMutate = (int) Math.round(OwlConstants.NUM_GENERATIONS * OwlConstants.MUTATION_RATE); // March25

        Logger.debug("Number of chromosomes to mutate = " + numberOfChromosomesToMutate);
        for (int i = 0; i < numberOfChromosomesToMutate; i++) {
            int randomChromosomeIndex = Util.getRandomIntValueInRange(0, currentPopulation.size());
            GAChromosome mutatedChromosome = currentPopulation.get(randomChromosomeIndex).copy();

            int numberOfGenesToMutate = Util.getRandomIntValueInRange(1, mutatedChromosome.getGenes().size() + 1);
            for (int j = 0; j < numberOfGenesToMutate; j++) {
                int randomGeneIndex = Util.getRandomIntValueInRange(0, mutatedChromosome.getGenes().size());
                GAGene geneToMutate = mutatedChromosome.getGene(randomGeneIndex);
                GAGene mutatedGene = geneToMutate.copy();
                // TODO Guassian
                // uniform mutation with a random value chosen between [-30, 30]
                String val = null;
                if(!Utils.isValueNumerical(mutatedGene.getValue())) {
                    // keep the value the same if it is not numerical
                    val = mutatedGene.getValue();
                    // maybe once off and one on
                }else { // it is numerical so we can mutate it with a random value
                    // ToDo:  we need to use normal distribution to generate the value based on the change type dp or numerical so on. See Utils.generateGaussianInputs method
                    double dVal;

                    double currentGeneValue = Util.getNumbersFromString(geneToMutate.getValue()).get(0);
                   val= mutateGene(mutatedGene, currentGeneValue);

//                            val = Util.getNumbersFromString(geneToMutate.getValue()).get(0) +
//                                    Util.getRandomIntValueInRange(1, 30) + "";
//                        }
//                        val = Util.getNumbersFromString(geneToMutate.getValue()).get(0) +
//                                Util.getRandomIntValueInRange(1, 30) + "";
//                    }
//                    val = Util.getNumbersFromString(geneToMutate.getValue()).get(0) +
//                            Util.getRandomIntValueInRange(-30, 30) + "";
                }


                mutatedGene.setValue(val);
                mutatedChromosome.replaceGene(geneToMutate, mutatedGene);
            }
            mutatedChromosome.addOriginToTrace("mutation_gen" + generationCount);
            nextGenerationMutation.add(mutatedChromosome);
        }
        return nextGenerationMutation;
    }

    private String mutateGene(GAGene mutatedGene, double currentGeneValue) {
        // generate value based on the change type. If it is increase or decrease then we need to generate a value based on the current value
        String changeType = mutatedGene.getChangeType();
        double amountOfChange= currentGeneValue;
        int minVal = 0;
        int maxVal = 0;
       String val = currentGeneValue +
                "";
        String valType=mutatedGene.getValueType();
        boolean keepTheSameValue = false;
        switch (valType){
            case OwlConstants.NUMERICAL_DP_VALUE:
            {
                if (currentGeneValue<=10){
                    minVal = 30;
                    maxVal = 150;
                }else {
                    minVal = 10;
                    maxVal = 50;
                }
                break;
            }
            case OwlConstants.NUMERICAL_INT_VALUE:
            {
//                minVal = 1;
//                maxVal = 10;
                minVal = 30;
                maxVal = 120;
                break;
            }
            case OwlConstants.SCON_VALUE:
            {
               keepTheSameValue = true;
                break;
            }
            default:
            {
                minVal = 1;
                maxVal = 5;
                break;
            }
        }
        if(!keepTheSameValue){
            amountOfChange= Math.ceil(Util.getRandomIntValueInRange(minVal, maxVal)* currentGeneValue/100);

        switch (changeType) {
            case OwlConstants.CHANGE_INCREASE: {

                Logger.debug("Increasing value for mutation by " + amountOfChange);
                break;
            }
            case OwlConstants.CHANGE_DECREASE: {
               // int ContineuToDecrease=getRandomOneOrZero(0.3); // 50% chance to continue to decrease the value
            //    if(ContineuToDecrease==1){ // if not then try to increase the value
                amountOfChange = amountOfChange * -1;
             //   }


                Logger.debug("Decreasing value for mutation by " + amountOfChange);
                break;
            }
        }
            double dVal =currentGeneValue +
                    amountOfChange ;
            if (dVal < 0) {
                dVal = 0;
            }
            val = dVal + "";

        }
      return val;
    }

    private List<GAChromosome> mutation() {
        List<GAChromosome> nextGenerationMutation = new ArrayList<>();
        int numberOfChromosomesToMutate = (int) Math.round(currentPopulation.size() * Constants.MUTATION_RATE);
       Logger.debug("Number of chromosomes to mutate = " + numberOfChromosomesToMutate);
        for (int i = 0; i < numberOfChromosomesToMutate; i++) {
            int randomChromosomeIndex = Util.getRandomIntValueInRange(0, currentPopulation.size());
            GAChromosome mutatedChromosome = currentPopulation.get(randomChromosomeIndex).copy();

            int numberOfGenesToMutate = Util.getRandomIntValueInRange(1, mutatedChromosome.getGenes().size() + 1);
            for (int j = 0; j < numberOfGenesToMutate; j++) {
                int randomGeneIndex = Util.getRandomIntValueInRange(0, mutatedChromosome.getGenes().size());
                GAGene geneToMutate = mutatedChromosome.getGene(randomGeneIndex);
                GAGene mutatedGene = geneToMutate.copy();
                // TODO Guassian
                // uniform mutation with a random value chosen between [-30, 30]
                String val = null;
                if(!Utils.isValueNumerical(mutatedGene.getValue())) {
                // keep the value the same if it is not numerical
                 val = mutatedGene.getValue();
                 // maybe once off and one on
                }else {
                    // ToDo:  we need to use normal distribution to generate the value based on the change type dp or numerical so on. See Utils.generateGaussianInputs method
                    double dVal;
                    double percentage=1; // same value
                    double currentGeneValue = Util.getNumbersFromString(geneToMutate.getValue()).get(0);
                    if(geneToMutate.getChangeType().equalsIgnoreCase(OwlConstants.CHANGE_INCREASE)){
//                        double percentage=1; // same value
//                        double currentGeneValue = Util.getNumbersFromString(geneToMutate.getValue()).get(0);
                        if(geneToMutate.getValueType().equalsIgnoreCase(OwlConstants.NUMERICAL_DP_VALUE)){
                            double amountOfChange= Math.ceil(Util.getRandomIntValueInRange(10, 50)* currentGeneValue/100);
                            val = currentGeneValue +
                                    amountOfChange+"";
                        } else if (geneToMutate.getValueType().equalsIgnoreCase(OwlConstants.NUMERICAL_INT_VALUE)) {
                            double amountOfChange= Math.ceil(Util.getRandomIntValueInRange(10, 50)* currentGeneValue/100);

                             dVal = currentGeneValue +
                                    amountOfChange;
                            if (dVal < 0) {
                                dVal = 0;
                            }
                            val = dVal + "";
                        }  else {
                            if (val == "-2" || val == "-1") {
                                // keep the value the same
                            } else {
                                double amountOfChange= Math.ceil(Util.getRandomIntValueInRange(1, 10)* currentGeneValue/100);

                                 dVal = currentGeneValue +
                                        amountOfChange;
                                if (dVal < 0) {
                                    dVal = 0;
                                }
                                val = dVal + "";
                            }
                        }
                        }

                         else if(geneToMutate.getChangeType().equalsIgnoreCase(OwlConstants.CHANGE_DECREASE)) {
                         dVal = Util.getNumbersFromString(geneToMutate.getValue()).get(0);

                        if (geneToMutate.getValueType().equalsIgnoreCase(OwlConstants.NUMERICAL_DP_VALUE)) {
                            double amountOfChange= Math.ceil(Util.getRandomIntValueInRange(1, 50)* currentGeneValue/100);
                            amountOfChange = amountOfChange * -1;
                            dVal = currentGeneValue +
                                    amountOfChange;

                            if (dVal < 0) {
                                dVal = 0;
                            }
                            val = dVal + "";
                        } else if (geneToMutate.getValueType().equalsIgnoreCase(OwlConstants.NUMERICAL_INT_VALUE)) {
                            double amountOfChange= Math.ceil(Util.getRandomIntValueInRange(1, 50)* currentGeneValue/100);
                            amountOfChange = amountOfChange * -1;
                            dVal = currentGeneValue +
                                    amountOfChange;

                            if (dVal < 0) {
                                dVal = 0;
                            }
                            val = dVal + "";
                        } else {
                            if (val == "-2" || val == "-1") {
                                // keep the value the same
                            } else {
                              double  dval = Util.getNumbersFromString(geneToMutate.getValue()).get(0) +
                                        Util.getRandomIntValueInRange(-2, -1) ;
                                if (dVal < 0) {
                                    dVal = 0;
                                }
                                val = dVal + "";
                            }
                        }
                    } else{
                             // keep the same value
                    }
//                            val = Util.getNumbersFromString(geneToMutate.getValue()).get(0) +
//                                    Util.getRandomIntValueInRange(1, 30) + "";
//                        }
//                        val = Util.getNumbersFromString(geneToMutate.getValue()).get(0) +
//                                Util.getRandomIntValueInRange(1, 30) + "";
//                    }
//                    val = Util.getNumbersFromString(geneToMutate.getValue()).get(0) +
//                            Util.getRandomIntValueInRange(-30, 30) + "";
                }


                mutatedGene.setValue(val);
                mutatedChromosome.replaceGene(geneToMutate, mutatedGene);
            }
            mutatedChromosome.addOriginToTrace("mutation_gen" + generationCount);
            nextGenerationMutation.add(mutatedChromosome);
        }
        return nextGenerationMutation;
    }

    private void select() {
        // sort population by fitness score
        sortPopulationByFitnessScore(nextGeneration);

        // add only top "POPULATION_SIZE" chromosomes to the new current
        // population
        currentPopulation = new ArrayList<GAChromosome>();
        for (int i = 0; i < OwlConstants.POPULATION_SIZE; i++) {
            // TODO problem here out of bound
            if (i < nextGeneration.size())
                currentPopulation.add(nextGeneration.get(i));
        }
    }

    private boolean isTerminate(int generationCount, int saturationCount) {

        boolean terminate = false;
		/*if(usabilityScore >= Constants.USABILITY_SCORE_THRESHOLD)
		{
			terminate = true;
			System.out.println("Terminating because usability score greater than or equal to the threshold " + Constants.USABILITY_SCORE_THRESHOLD);
		}
		else */
        if (generationCount >= OwlConstants.NUM_GENERATIONS) {
            terminate = true;
           Logger.debug("Terminating (generation = " + generationCount + ") because max generations reached");
        } else if (saturationCount >= Constants.SATURATION_POINT) {
            terminate = true;
           Logger.debug("Terminating(generation = " + generationCount + ") because saturation point reached");
        } else if (Constants.IS_PURE_RANDOM_SEARCH) {
            terminate = true;
           Logger.debug("Terminating(generation = " + generationCount + ") because pure random search");
        }

        return terminate;
    }

    public void printPopulation(List<GAChromosome> population) {
//        int count = 1;
//        System.out.println("(Size = " + population.size() + ")");
//        for (GAChromosome c : population) {
//            System.out.println(count + ". " + c);
//            count++;
//        }
        Logger.debug("Activity: "+ OwlEye.getOriginalActivityName());
        int count = 1;
        Logger.debug("(Size = " + population.size() + ")");
        for (GAChromosome c : population) {
            Logger.debug(count + ". " + c);
            count++;
        }
    }


    public GAChromosome runGA4() throws IOException, InterruptedException {
        Logger.info("\nRunning initialization");
        int populationSize = Constants.POPULATION_SIZE+1;
        if (Constants.IS_PURE_RANDOM_SEARCH) {
            populationSize = Constants.POPULATION_SIZE * Constants.NUM_GENERATIONS;
        }

           initialize3(populationSize);//

        currentPopulation = calculateFitnessScoreForPopulation(currentPopulation, "initialization");
        int x=1;
        for (GAChromosome c : currentPopulation) {
            try {
               Logger.trace("|||||||||||||||||||Chromosome. " + x + " ||||||||||||||||||||||||||");
               Logger.trace("Chromosome identifier: " + c.getChromosomeIdentifier());
               Logger.trace("Genes: " + c.getGenes().get(0).getValue());
               Logger.trace("Fitness score: " + c.getFitnessFunctionObj().getFitnessScore());
               Logger.trace("Breakdown: " + c.getFitnessFunctionObj().getFitnessScoreSummary());
                x++;
            } catch (Exception e) {
               Logger.error("Failed to print chromosome: " + c.getChromosomeIdentifier());
               Logger.error("Genes: " + c.getGenes().get(0).getValue());
            }
        }


        // Continuing the search process
        removeDuplicatesGAChromosome(currentPopulation);
        sortPopulationByFitnessScore(currentPopulation);
        GAChromosome bestSoFar = currentPopulation.get(0);

        // Start the search loop
        try {
            GAChromosome optimal = startTheSearchLoop(bestSoFar);

            Logger.debug("Reached End of Optimal");
            return optimal;
        }
        catch (Exception e) {
            e.printStackTrace();
            return bestSoFar;
        }
//        finally {
//            return bestSoFar;
//        }

//        GAChromosome optimal = startTheSearchLoop(bestSoFar);

       // GAChromosome optimal = null;




//        return currentPopulation.get(0);

       // return optimal;
    }

    public GAChromosome runGA3() throws IOException, InterruptedException {
        System.out.println("\nRunning initialization");
        int populationSize = Constants.POPULATION_SIZE;
        if (Constants.IS_PURE_RANDOM_SEARCH) {
            populationSize = Constants.POPULATION_SIZE * Constants.NUM_GENERATIONS;
        }

            initialize2(populationSize);// keeping it simple now Sep22

        currentPopulation = calculateFitnessScoreForPopulation(currentPopulation, "initialization");
        int x=1;
        for (GAChromosome c : currentPopulation) {
            try {
                System.out.println("|||||||||||||||||||Chromosome. " + x + " ||||||||||||||||||||||||||");
                System.out.println("Chromosome identifier: " + c.getChromosomeIdentifier());
                System.out.println("Genes: " + c.getGenes().get(0).getValue());
                System.out.println("Fitness score: " + c.getFitnessFunctionObj().getFitnessScore());
                System.out.println("Breakdown: " + c.getFitnessFunctionObj().getFitnessScoreSummary());
                x++;
            } catch (Exception e) {
                System.out.println("Failed to print chromosome" + c.getChromosomeIdentifier());
                System.out.println("Genes: " + c.getGenes().get(0).getValue());
            }
        }


           // Continueing the search process

            sortPopulationByFitnessScore(currentPopulation);







        return currentPopulation.get(0);

    }

    private GAChromosome startTheSearchLoop(GAChromosome bestSoFar) throws IOException, InterruptedException {

        // start loop
        int saturationCount = 0;
        double prevGenFitnessScore = 0;

        while (!isTerminate(generationCount, saturationCount)) {
            try {
                Logger.debug("\n----------------------------- GENERATION " + generationCount + " ------------------------------");
                nextGeneration = new ArrayList<>(currentPopulation);

                // uniform crossover
                Logger.debug("\nRunning crossover");
                List<GAChromosome> nextGenerationCrossover1=null ;
                List<GAChromosome> nextGenerationCrossover2=null ;
                GAChromosome scrollViewCrossOver=null;
                boolean classicCrossOverApproach = false;
                if (classicCrossOverApproach){
                     nextGenerationCrossover1 = crossover();
                }
                else {
                    //List<GAChromosome> nextGenerationCrossoverGPT=crossoverGPT();
                    nextGenerationCrossover1 = crossover();
                     scrollViewCrossOver = crossoverScrollView();
                     nextGenerationCrossover2 = crossoverGPT();
                }

                // uniform random mutation
                Logger.debug("\nRunning mutation");
                List<GAChromosome> nextGenerationMutation = mutation2();

                List<GAChromosome> crossoverMutationPopulation = new ArrayList<>();
                if(scrollViewCrossOver!=null){
                    crossoverMutationPopulation.add(scrollViewCrossOver);
                }
                if(nextGenerationCrossover1!=null){
                    crossoverMutationPopulation.addAll(nextGenerationCrossover1);
                }
                if(nextGenerationCrossover2!=null){
                    crossoverMutationPopulation.addAll(nextGenerationCrossover2);
                }
//                crossoverMutationPopulation.addAll(nextGenerationCrossover);
                crossoverMutationPopulation.addAll(nextGenerationMutation);
//            crossoverMutationPopulation = ff.calculateFitnessScoreForPopulation(crossoverMutationPopulation, "crossover_and_mutation");
                crossoverMutationPopulation= removeExistingChromosomes(currentPopulation,crossoverMutationPopulation);
                crossoverMutationPopulation = calculateFitnessScoreForPopulation(crossoverMutationPopulation, "crossover_and_mutation");

                nextGeneration.addAll(crossoverMutationPopulation);
//            System.out.println("Population after crossover and mutation = ");
                Logger.debug("Population after crossover and mutation = ");
                printPopulation(nextGeneration);

                // select
//            System.out.println("\nRunning selection");
//            select();
//            System.out.println("Population after selection = ");
//            printPopulation(currentPopulation);
                Logger.debug("\nRunning selection");
                select();
                Logger.debug("Population after selection = ");
                printPopulation(currentPopulation);

                if (prevGenFitnessScore == currentPopulation.get(0).getFitnessFunctionObj().getFitnessScore()) {
                    saturationCount++;
                } else {
                    saturationCount = 0;
                }
                prevGenFitnessScore = currentPopulation.get(0).getFitnessFunctionObj().getFitnessScore();
                generationCount++;
                // sortPopulationByFitnessScore(nextGeneration);
                bestSoFar = currentPopulation.get(0);
                paretoFront = findParetoFront(currentPopulation);

            } catch (Exception e) {
                Logger.error("Failed to run generation " + generationCount);
                Logger.error(e.getMessage());
                Logger.error(e.getStackTrace().toString());
                e.printStackTrace();
                continue;
            }
//            Logger.debug("Current best = " + currentBest.getChromosomeIdentifier() + " with fitness score = " + currentBest.getFitnessFunctionObj().getFitnessScore());
//            Logger.debug("Best so far: " + bestSoFar.getChromosomeIdentifier() + " with fitness score = " + bestSoFar.getFitnessFunctionObj().getFitnessScore());
//            if (currentBest.getFitnessFunctionObj().getFitnessScore() < bestSoFar.getFitnessFunctionObj().getFitnessScore()) {
//                bestSoFar = currentBest;
//                Logger.debug("Best so far updated: " + bestSoFar.getChromosomeIdentifier() + " with fitness score = " + bestSoFar.getFitnessFunctionObj().getFitnessScore());
//            }
//            currentPopulation = new ArrayList<>(nextGeneration);
//            sortPopulationByFitnessScore(currentPopulation);
            }
    //    return currentPopulation.get(0); // optimal chromosome
        return bestSoFar;
    }



    public static List<GAChromosome> calculateFitnessScoreForPopulation(List<GAChromosome> population, String searchStep) throws IOException, InterruptedException {
        boolean doesThisIncludeScrollView=false;


        /*** Here we should run the population on Android ***/
        long startFitnessTime = System.nanoTime();
        int chromosomeCount = 0;
        List<GAChromosome> updatedPopulation = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(SALEM.MAX_NO_THREADS_FOR_APP_COMPILING);

        //Ali Get each chromosomem, write each to a  seperate apk then get the score
        population=removeDuplicatesGAChromosome(population);
        for (GAChromosome chromosome : population) {
            //OwlEye.getOriginalLargestUI().getXMLTree();
//            XMLUtils.resetInstance();
//            XMLUtils.getInstance(SALEM.getCurrentActivityMergedFilePath());
            String chromosomeIdentifier = "chromosome_" + searchStep + "_" + GASearch.getGenerationCount() + "_" + chromosomeCount;
            chromosome.setChromosomeIdentifier(chromosomeIdentifier);
            String outPutFile = OwlEye.getOriginalDecompiled() + "_" + chromosomeIdentifier;
            String chromosomeOutPutFolder = Utils.WriteChromosomeChangesToFile(chromosome, outPutFile);
            chromosome.setChromosomeOutPutFolder(chromosomeOutPutFolder);
            chromosomeCount++;
        }
        try {
            Thread.sleep(2000); // SALEM was 6000
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (GAChromosome chromosome : population) {

//            if(!chromosome.getChromosomeIdentifier().contains("chromosome_initialization_0_2")){
//                continue;
//            }
//            XMLUtils.resetInstance();
//            XMLUtils.getInstance(mFix.getCurrentActivityMergedFilePath());
//            String chromosomeIdentifier = "chromosome_" + searchStep + "_" + GASearch.getGenerationCount() + "_" + chromosomeCount;
//            chromosome.setChromosomeIdentifier(chromosomeIdentifier);
//            String outPutFile = mFix.getOriginalDecompiled() + "_" + chromosomeIdentifier;
//            String chromosomeOutPutFolder = Util.WriteChromosomeChangesToFileSegmentRelationGraph(chromosome, outPutFile);
            String chromosomeOutPutFolder = chromosome.getChromosomeOutPutFolder();
            String chromosomeIdentifier = chromosome.getChromosomeIdentifier();
            Runnable worker = new PrepareAppRunner(chromosomeIdentifier, chromosome, chromosomeOutPutFolder);
            executor.execute(worker);
        }
        executor.shutdown();
        // Wait until all threads are finish
        while (!executor.isTerminated()) {

        }
        try {
            Thread.sleep(3000); // SALEM was 6000
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

       Logger.trace("\nFinished all threads to prepare apps.");

//        String detectionOutPutPath = ""; //Todo: Where?
//        detectionOutPutPath = SALEM.Detection_Tool_APK_PATH;
//
        //   AccessibilityDetectionToolAPI.runDetectionTool(newApkName, detectionOutPutPath);
//
//
//            // (3) Parse the file,then: Store the object GoogleAPIResutls.java and add it to mfix.list of issues
//
//            Util.parseIssuesFile(detectionOutPutPath, newApkName, chromosomeIdentifier); // Once this is done we have stored the issues in the map and we have already stored

        // (4) calculate the accessibility score

        //(5) run the app and get the UI of the activity and the screenshot, preprocess the layout in the refined folder
//            String newLayoutFolder = Util.captureUpdatedDynamicUI(mFix.getCurrentActivityName(), detectionOutPutPath, newApkName);
        /*** I changed the logic now the detection is captured during the running of the app and crawling script so no need to separately running the detection***/

        // (5-a) run the app and the crawling script to get it ready to the correct state so we can then go ahead to get updated issue list and then crawler
       boolean threds=true;
       if(threds) {
           Map<Integer, String[]> jobArguments = new HashMap<>();
           Map<Integer, GAChromosome> jobArguments2 = new HashMap<>();



           int i = 0;

           for (GAChromosome chromosome : population) {
               doesThisIncludeScrollView =chromosome.containsScrollViewGene();
                chromosome.setContainsScrollView(doesThisIncludeScrollView);

               String newApkName = chromosome.getNewApkName();
               String capturedUIFolder = OwlEye.getCrawledDynamicRepairVHPath() + newApkName;
              String compiled_output_path= OwlEye.getCompileOutputPath();
              jobArguments.put(i, new String[]{compiled_output_path,newApkName, capturedUIFolder});
                jobArguments2.put(i, chromosome);
               i++;
           }
           AndroidEmulatorAccess emulatorAccess = new AndroidEmulatorAccess(jobArguments2);
           emulatorAccess.startEmulatorAccess2();
//           List<GAChromosome> pareto = findParetoFront(population);
           for (GAChromosome chromosome : population) {
               Utils.writeChromosomeToFile(chromosome);
               chromosomeCount++;
               updatedPopulation.add(chromosome);
           }

//            long endChromosomeRun = System.nanoTime();
//           if (!OwlEye.runtime_map.containsKey("chromosomeCal")) {
//               OwlEye.runtime_map.put("chromosomeCal", 0L);
//           }
//           long curr = OwlEye.runtime_map.get("chromosomeCal");
////            long newTime = curr + (endChromosomeRun - startChromosomeRun);
////            mFix.runtime_map.put("chromosomeCal", newTime);
//           OwlEye.chromosomeCalCount++;
       }
       else {
           for (GAChromosome chromosome : population) {
//            if(!chromosome.getChromosomeIdentifier().contains("chromosome_initialization_0_2")){
//                continue;
//            }

               String newLayoutFolder = null;
               if (OwlEye.CAPTURE_GENERATED_UI_MODE.equals(OwlConstants.AUTOMATOR2_UI_CAPTURE_MODE)) {
                   String newApkName = chromosome.getNewApkName();
                   boolean skipLayout = false;
                   String capturedUIFolder = OwlEye.getCrawledDynamicRepairVHPath() + newApkName;
                   File directory = new File(capturedUIFolder);
                   if (!directory.exists()) {
                       directory.mkdir();
                   }
                   // Sleep for 5 seconds to make sure the app is ready
                   try {
                       Thread.sleep(5000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   String devieName= OwlEye.getDeviceName();
                   String result = Utils.captureTheGeneratedUI(OwlEye.getCompileOutputPath(), newApkName, capturedUIFolder,devieName); // here the apk name for the chromosome
                   if (result == null) {
                       skipLayout = true;
                       newLayoutFolder = null;
                   } else {
                       usc.edu.SALEM.util.Util.preprocessDynamicFolder(capturedUIFolder, capturedUIFolder + "/refined/");
                       String dumpedFileLayout = capturedUIFolder + "/refined/";
                       newLayoutFolder = dumpedFileLayout;
                       //String chromosomedChrawled = newLayoutFolder.split("/refined")[0];
                       chromosome.setCrawledPath(capturedUIFolder);
                       Logger.debug("capturedUIFolder = " + capturedUIFolder);
                   }
                   //System.exit(1);
               }
//               else if (OwlEye.CAPTURE_GENERATED_UI_MODE.equals(OwlConstants.LEGACY_UI_CAPTURE_MODE)) {
//                   String newApkName = chromosome.getNewApkName();
//                   boolean skipLayout = false;
//                   String result = Utils.prepareAppToGetToCorrectState(OwlEye.getCompileOutputPath(), newApkName); // here the apk name for the chromosome
//                   if (result == null) {
//                       SALEM.resetDeviceUIUsingADB();
//                       result = Utils.prepareAppToGetToCorrectState(OwlEye.getCompileOutputPath(), newApkName);
//                       if (result == null) {
//                           skipLayout = true;
//                       }
//                   }
//
//                   if (!skipLayout) {
//                       newLayoutFolder = Utils.captureUpdatedDynamicUI(OwlEye.getOriginalActivityName(), OwlEye.getCompileOutputPath(), newApkName);
//                       String chromosomedChrawled = newLayoutFolder.split("/refined")[0];
//                       chromosome.setCrawledPath(chromosomedChrawled);
////            Util.updateLayoutWithNewValues(newLayoutFolder);
//                   } else {
//                       //we skip reading layout because we are not getting the correct activity
//                       newLayoutFolder = null;
//                   }
//               }
               else {
                   Logger.error("Error: No UI capture mode is selected");
                   System.exit(1);
               }
               if (OwlEye.addSleepIntervals) {
                   try {
                       Thread.sleep(10000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }
               // (7) Calculate the total fitness score
               FitnessFunction ff = new FitnessFunction();
               ff.calculateFitnessScore(chromosome, newLayoutFolder, false);
               Logger.debug("*** ======================================================== **********");
               Logger.debug("*** Running :" + chromosome.getChromosomeIdentifier() + "|| from: " + chromosome.getOriginTrace());
               Logger.debug("Genes: " + chromosome.getGenes());
               Logger.debug("Fitness function: " + ff.getFitnessScoreSummary());
               Logger.debug("*** ======================================================== **********");

               Utils.writeChromosomeToFile(chromosome);
               // (8) increment chromosome counter
               chromosomeCount++;
               updatedPopulation.add(chromosome);

//            long endChromosomeRun = System.nanoTime();
               if (!OwlEye.runtime_map.containsKey("chromosomeCal")) {
                   OwlEye.runtime_map.put("chromosomeCal", 0L);
               }
               long curr = OwlEye.runtime_map.get("chromosomeCal");
//            long newTime = curr + (endChromosomeRun - startChromosomeRun);
//            mFix.runtime_map.put("chromosomeCal", newTime);
               OwlEye.chromosomeCalCount++;
           }
       }


        long endFitnessTime = System.nanoTime();
        if (!OwlEye.runtime_map.containsKey("populationCal")) {
            OwlEye.runtime_map.put("populationCal", 0L);
        }
        long curr = OwlEye.runtime_map.get("populationCal");
        long newTime = curr + (endFitnessTime - startFitnessTime);
        OwlEye.runtime_map.put("populationCal", newTime);
        OwlEye.populationCalCount++;



        return updatedPopulation;
        //TOdo: I added that as placeholder Ali
//        return null;
    }
    public static List<GAChromosome> removeExistingChromosomes(List<GAChromosome> population, List<GAChromosome> newChromosomes) {
        // Create a HashSet and add all chromosomes from the population
        HashSet<GAChromosome> uniquePopulationChromosomes = new HashSet<>(population);

        // Create a list to store the filtered newChromosomes
        List<GAChromosome> filteredNewChromosomes = new ArrayList<>();

        // Iterate through newChromosomes and add chromosomes to filteredNewChromosomes
        // if they do not exist in uniquePopulationChromosomes
        for (GAChromosome chromosome : newChromosomes) {
            if (!uniquePopulationChromosomes.contains(chromosome)) {
                filteredNewChromosomes.add(chromosome);
            }
        }

        return filteredNewChromosomes;
    }
    public static List<GAChromosome> removeDuplicatesGAChromosome(List<GAChromosome> chromosomes) {
        // Create a HashSet and add all individuals
        HashSet<GAChromosome> uniqueChromosomes = new HashSet<>(chromosomes);

        // Convert the HashSet back to a List
        return new ArrayList<>(uniqueChromosomes);
    }


    // pareto front calculation

    public static boolean isDominated(GAChromosome individual1, GAChromosome individual2) {
        int dominatedObjectives = 0;
        int nonDominatedObjectives = 0;
        double[] individual1Scores = getFitnessScore(individual1);
        double[] individual2Scores = getFitnessScore(individual2);
        int numObjectives = 5;
        for (int i = 0; i < numObjectives; i++) {


//            double textScore1=individual1.getFitnessFunctionObj().getTextScore();
//            double missingScore1=individual1.getFitnessFunctionObj().getMissingElementScore();
//            double collisionScore1=individual1.getFitnessFunctionObj().getCollisionScore();
//            double positionScore1=individual1.getFitnessFunctionObj().getPositioningAndAlignmentsScore();
//            double minimumChangesScore1=individual1.getFitnessFunctionObj().getMinimumChangesScore();
//            
//            double textScore2=individual2.getFitnessFunctionObj().getTextScore();
//            double missingScore2=individual2.getFitnessFunctionObj().getMissingElementScore();
//            double collisionScore2=individual2.getFitnessFunctionObj().getCollisionScore();
//            double positionScore2=individual2.getFitnessFunctionObj().getPositioningAndAlignmentsScore();
//            double minimumChangesScore2=individual2.getFitnessFunctionObj().getMinimumChangesScore();
            
            if (individual1Scores[i] <= individual2Scores[i]) {
                nonDominatedObjectives++;
            } else if (individual1Scores[i] > individual2Scores[i]) {
                dominatedObjectives++;
            }
        }

        return dominatedObjectives == numObjectives;
    }

    private static double [] getFitnessScore(GAChromosome individual1) {
        double textScore1=individual1.getFitnessFunctionObj().getTextScore();
        double missingScore1=individual1.getFitnessFunctionObj().getMissingElementScore();
        double collisionScore1=individual1.getFitnessFunctionObj().getCollisionScore();
        double positionScore1=individual1.getFitnessFunctionObj().getPositioningAndAlignmentsScore();
        double minimumChangesScore1=individual1.getFitnessFunctionObj().getMinimumChangesScore();
        return new double[] {textScore1, missingScore1, collisionScore1, positionScore1, minimumChangesScore1};
    }

    public static List<GAChromosome> findParetoFront(List<GAChromosome> population) {
        List<GAChromosome> paretoFront = new ArrayList<>();

        for (GAChromosome individual : population) {
            boolean isDominatedFlag = false;
            
            for (GAChromosome otherIndividual : population) {
                if (individual.equals(otherIndividual)) {
                    continue;
                }

                if (isDominated(individual, otherIndividual)) {
                    isDominatedFlag = true;
                    break;
                }
            }

            if (!isDominatedFlag) {
                paretoFront.add(individual);
            }
        }

        return paretoFront;
    }

    public List<GAChromosome> getParetoFront() {
        return paretoFront;
    }

    public void setParetoFront(List<GAChromosome> paretoFront) {
        this.paretoFront = paretoFront;
    }
}
