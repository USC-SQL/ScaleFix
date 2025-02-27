package eval.testSubFunctions;
import java.util.Random;

public class testRandom {


        public static void main(String[] args) {
            Random random = new Random();
            double randomDouble = random.nextDouble();
            System.out.println("Random number: " + randomDouble);
        }

        public static int getRandomWeighted(double random, double probabilityOfOne) {
            return random < probabilityOfOne ? 1 : 0;
        }

}
