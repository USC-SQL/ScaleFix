package TTfix.util;

public class temp_test_delete {

public static void main (String args[]){
    double x= 20.0;
    System.out.println(x+": "+sigmoid(x));
    System.out.println(x+": "+sigmoid2(x));
}
//7495452719005047

    public static double sigmoid(double x) {
        return ((Math.atan(x) + Math.PI / 2) / Math.PI);
    }

    public static double sigmoid2(double x) {
        return  Math.tanh(x);
    }
}