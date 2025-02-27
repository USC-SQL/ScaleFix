package eval.testSubFunctions;

import usc.edu.SALEM.util.Util;

public class testSigmoid {
    public static void main(String[] args) {

        double minChangeBest=9.972519092078304;
        double positionBest=3.6687000000000003;
        double minChangegenerated=9.055088807694252;
        double positiongenerated=3.9369;

        // print the sigmoid value of each variable with old and new sigmoid function
        System.out.println("oldSigmoid: " );
        System.out.println("\tpositionBest: " + Util.sigmoid(positionBest));
        System.out.println("\tminChangeBest: " + Util.sigmoid(minChangeBest));
        System.out.println("\tpositiongenerated: " + Util.sigmoid(positiongenerated));

        System.out.println("\tminChangegenerated: " + Util.sigmoid(minChangegenerated));

        System.out.println("newSigmoid: " );
        System.out.println("\tpositionBest: " + newSigmoid(positionBest));
        System.out.println("\tminChangeBest: " + newSigmoid(minChangeBest));

        System.out.println("\tpositiongenerated: " + newSigmoid(positiongenerated));

        System.out.println("\tminChangegenerated: " + newSigmoid(minChangegenerated));


        System.out.println(Util.sigmoid(positionBest) + " | " + newSigmoid(positionBest) + " | " + newGTSigmoid(positionBest) );

        System.out.println(Util.sigmoid(positiongenerated) + " | " + newSigmoid(positiongenerated) + " | " + newGTSigmoid(positiongenerated) );
        System.out.println("----------------------------------------------------");
        System.out.println(Util.sigmoid(minChangeBest) + " | " + newSigmoid(minChangeBest) + " | " + newGTSigmoid(minChangeBest) );
        System.out.println(Util.sigmoid(minChangegenerated) + " | " + newSigmoid(minChangegenerated) + " | " + newGTSigmoid(minChangegenerated) );
        double x = 0.5;

//        double oldSigmoid= Util.sigmoid(x);
//        double y = 1 / (1 + Math.exp(-x));
//        System.out.println("new: " +y);
//        System.out.println("old: " +oldSigmoid);
    }

    public static double newSigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }
    public static double newGTSigmoid(double x) {
        double scalingFactor = 0.1;

        return (Math.tanh(scalingFactor * x) + 1) / 2;

    }
}
