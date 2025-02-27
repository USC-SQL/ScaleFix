package usc.edu.OwlEye.fitness;

public abstract class FitnessObjective {
    protected double objectiveScore;
    protected double rawScore;
    protected int numberOfViolations;
    protected double amountOfViolation;



    public FitnessObjective() {
        this.objectiveScore = Double.MIN_VALUE;
    }

    public double calculateObjectiveScore() {
        return 100.0;
    }

    public double getObjectiveScore() {
        return objectiveScore;
    }

    public void setObjectiveScore(double objectiveScore) {
        this.objectiveScore = objectiveScore;
    }

    public double getRawScore() {
        return rawScore;
    }

    public void setRawScore(double rawScore) {
        this.rawScore = rawScore;
    }
    public int getNumberOfViolations() {
        return numberOfViolations;
    }
    public void setNumberOfViolations(int numberOfViolations) {
        this.numberOfViolations = numberOfViolations;
    }
    public double getAmountOfViolation() {
        return amountOfViolation;
    }
    public void setAmountOfViolation(double amountOfViolation) {
        this.amountOfViolation = amountOfViolation;
    }
}
