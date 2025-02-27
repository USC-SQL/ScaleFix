package usc.edu.OwlEye.GA;

import usc.edu.OwlEye.OwlConstants;
import usc.edu.OwlEye.fitness.FitnessFunction;
import usc.edu.SALEM.Constants;
//import usc.edu.SALEM.fitness.FitnessFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GAChromosome implements Comparable<GAChromosome> {
    private List<GAGene> genes;
    private FitnessFunction fitnessFunctionObj;
    private String originTrace;
    private String chromosomeIdentifier;
    private String APKPath;  // path of the compile apk we used to run the app
    private String decompiledPath;  // path of decompiled app that we applied the chromosome path tot
    private String newApkName;
    private String chromosomeOutPutFolder;

    private int NoTT;
private boolean containsScrollView = false;

    public GAChromosome() {
        this(new ArrayList<GAGene>());
//        List<GAGene> newGenes = new ArrayList<GAGene>();
////        this.originTrace = "";
        this.fitnessFunctionObj = new FitnessFunction();
        this.genes = new ArrayList<GAGene>();

    }

    public GAChromosome(List<GAGene> genes) {

        this.genes = new ArrayList<GAGene>();
        this.originTrace = "";
        this.fitnessFunctionObj = new FitnessFunction();

        for (GAGene gene : genes) {
            addGene(gene);
        }
    }

    public String getNewApkName() {
        return newApkName;
    }

    public String getChromosomeOutPutFolder() {
        return chromosomeOutPutFolder;
    }

    public void setChromosomeOutPutFolder(String chromosomeOutPutFolder) {
        this.chromosomeOutPutFolder = chromosomeOutPutFolder;
    }

    public void setNewApkName(String newApkName) {
        this.newApkName = newApkName;
    }

    public int getNoTT() {
        return NoTT;
    }

    public void setNoTT(int noTT) {
        NoTT = noTT;
    }

    private String crawledPath;


    public String getAPKPath() {
        return APKPath;
    }

    public void setAPKPath(String APKPath) {
        this.APKPath = APKPath;
    }

    public String getCrawledPath() {
        return crawledPath;
    }

    public void setCrawledPath(String crawledPath) {
        this.crawledPath = crawledPath;
    }

    public String getDecompiledPath() {
        return decompiledPath;
    }

    public void setDecompiledPath(String decompiledPath) {
        this.decompiledPath = decompiledPath;
    }

    public List<GAGene> getGenes() {
        return genes;
    }

    public void setGenes(List<GAGene> genes) {
        this.genes = genes;
    }

    public FitnessFunction getFitnessFunctionObj() {
        return fitnessFunctionObj;
    }

    public void setFitnessFunctionObj(FitnessFunction fitnessFunctionObj) {
        this.fitnessFunctionObj = fitnessFunctionObj;
    }

    public String getOriginTrace() {
        return originTrace;
    }

    public void setOriginTrace(String originTrace) {
        this.originTrace = originTrace;
    }

    public void addOriginToTrace(String origin) {
        originTrace = originTrace + ", " + origin;
    }

    public String getChromosomeIdentifier() {
        return chromosomeIdentifier;
    }

    public void setChromosomeIdentifier(String chromosomeIdentifier) {
        this.chromosomeIdentifier = chromosomeIdentifier;
    }

    public void addGene(GAGene gene) {
        // only add unique genes
        if (!genes.contains(gene)) {
            genes.add(gene);
        }
    }

    public GAGene getGene(int index) {
        return genes.get(index);
    }

    public GAChromosome copy() {
        GAChromosome c = new GAChromosome();
        for (GAGene g : this.genes) {
            c.addGene(g.copy());
        }
        c.originTrace = this.originTrace;
        FitnessFunction ff = new FitnessFunction();
//        ff.setA11yHeuristicsScore(fitnessFunctionObj.getA11yHeuristicsScore());
//        ff.setAestheticScore(fitnessFunctionObj.getAestheticScore());
        ff.setFitnessScore(fitnessFunctionObj.getFitnessScore());
        c.setFitnessFunctionObj(ff);
        return c;
    }

    public void replaceGene(GAGene oldGene, GAGene newGene) {
        int index = genes.indexOf(oldGene);
        genes.remove(index);
        genes.add(index, newGene);
    }

    @Override
    public int compareTo(GAChromosome o) {

        if (OwlConstants.IS_FITNESS_SCORE_MAXIMIZING) {
            // descending order
            if (o.fitnessFunctionObj.getFitnessScore() < this.fitnessFunctionObj.getFitnessScore())
                return -1;
            else if (o.fitnessFunctionObj.getFitnessScore() > this.fitnessFunctionObj.getFitnessScore())
                return 1;
            return 0;
        } else {
            // ascending order
            if (o.fitnessFunctionObj.getFitnessScore() > this.fitnessFunctionObj.getFitnessScore())
                return -1;
            else if (o.fitnessFunctionObj.getFitnessScore() < this.fitnessFunctionObj.getFitnessScore())
                return 1;
            return 0;
        }
    }


    @Override
    public String toString() {
        String result=chromosomeIdentifier + ",\n\t fitnessFunction=" + fitnessFunctionObj + ",\n\t originTrace={" + originTrace + "}]";
        result+="\n\t [genes=" + genes + "]";
        result+="\n------------------------------------------------------------------------";
        return result;
        //" [TT Issues Score="+fitnessFunctionObj.getNoIssuesPercentage()+"]"+
    }
    public boolean containsScrollViewGene(){
        for (GAGene gene:genes){
            if (gene.getChangeType().equalsIgnoreCase(OwlConstants.CHANGE_ADD_NEW_ELEMENT)){
                return true;
            }
        }
        return false;
    }

    public boolean isContainsScrollView() {
        return containsScrollView;
    }

    public void setContainsScrollView(boolean containsScrollView) {
        this.containsScrollView = containsScrollView;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GAChromosome other = (GAChromosome) obj;

        // Compare fitness values
        if (Double.compare(this.fitnessFunctionObj.getFitnessScore() , other.fitnessFunctionObj.getFitnessScore() ) != 0) {
            return false;
        }

        // Compare genes
        if (this.genes.size()!= other.genes.size()) {
            return false;
        }

        for (int i = 0; i < this.genes.size(); i++) {
            if (!this.genes.get(i).equals(other.genes.get(i))) {
                return false;
            }
        }

        return true;
    }
    @Override
    public int hashCode() {
        int result = 17;
        long temp;

        // Hash fitness value
        temp = Double.doubleToLongBits(fitnessFunctionObj.getFitnessScore());
        result = 31 * result + (int) (temp ^ (temp >>> 32));

        // Hash genes using segmentIssueId
        for (GAGene gene : genes) {
            String segmentIssueId = gene.getSegmentIssueId();
            result = 31 * result + (segmentIssueId != null ? segmentIssueId.hashCode() : 0);
        }

        return result;

}
}
