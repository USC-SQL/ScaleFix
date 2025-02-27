//package NSGAII;
//
//import org.uma.jmetal.problem.integerproblem.impl.AbstractIntegerProblem;
//import org.uma.jmetal.solution.integersolution.IntegerSolution;
//import org.uma.jmetal.operator.mutation.MutationOperator;
//import java.util.ArrayList;
//import java.util.List;
//
//public class AndroidLayoutProblem extends AbstractIntegerProblem {
//    private static final int NUMBER_OF_VARIABLES = 10;  // Set the number of layout elements to modify
//    private static final int NUMBER_OF_OBJECTIVES = 1;  // Set the number of objectives (fitness functions)
//
//    public AndroidLayoutProblem() {
//        setNumberOfVariables(NUMBER_OF_VARIABLES);
//        setNumberOfObjectives(NUMBER_OF_OBJECTIVES);
//        setName("AndroidLayoutProblem");
//
//        List<Integer> lowerLimit = new ArrayList<>(getNumberOfVariables());
//        List<Integer> upperLimit = new ArrayList<>(getNumberOfVariables());
//
//        for (int i = 0; i < getNumberOfVariables(); i++) {
//            lowerLimit.add(0);  // Set the minimum value for each variable
//            upperLimit.add(1);  // Set the maximum value for each variable
//        }
//
//        setLowerLimit(lowerLimit);
//        setUpperLimit(upperLimit);
//    }
//
//    @Override
//    public void evaluate(IntegerSolution solution) {
//        int[] objectives = new int[getNumberOfObjectives()];
//
//        // Define your fitness function here. This example prefers solutions with fewer changes.
//        int changes = 0;
//        for (int i = 0; i < solution.getNumberOfVariables(); i++) {
//            changes += solution.getVariableValue(i);
//        }
//        objectives[0] = changes;
//
//        solution.setObjectives(objectives);
//    }
//}