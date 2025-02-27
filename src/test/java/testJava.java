import usc.edu.SALEM.util.Util;

import java.util.Random;

public class testJava {
    public static void main(String[] args) {
        int currentValue = 2;
        double x = 0.7;
       double res= Util.getRandomIntValueInRange(30, 150);
       double res2=res* 2/100;
       double res3=Math.ceil(res2);

        System.out.println("\n\nres: "+res);
        System.out.println("res2: "+res2);
        System.out.println("res3: "+res3);
//        System.out.println("res4: "+Math.ceil(Util.getRandomIntValueInRange(10, 50)* 600/100));

//
//        for (int i = 0; i < 10; i++) {
//            double randomFactor = biasedRandomFactor(x);
//
//            double newValue = currentValue * (1 + randomFactor);
//            System.out.println("randomFactor: "+randomFactor+ " New value: " + newValue);
//            // round to whole number
//            newValue = Math.ceil(newValue);
//            System.out.println("Rounded value: " + newValue);
//        }
    }

    public static double biasedRandomFactor(double x) {
        Random random = new Random();
        double base = random.nextDouble() * x;
        double tail = (random.nextDouble() < 0.3) ? (1 - x) * random.nextDouble() : 0; // Adjust the probability (0.1) to control how often the value exceeds x.
        return base + tail;

        }

}