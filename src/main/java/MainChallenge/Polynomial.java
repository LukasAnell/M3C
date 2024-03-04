package MainChallenge;

//polynomial in vector form
public class Polynomial{
    // stores the coefficients of the polynomial object in ascending powers
    public double[] coefficients;

    // constructor for a Polynomial object given an array of coefficients in ascending powers
    public Polynomial(double[] arr){
        coefficients = arr;
    }

    //evaluate polynomial at input
    public double evaluate(double input){
        double total = 0;
        for(int i = 0; i<this.coefficients.length; i++){
            total += this.coefficients[i] * Math.pow(input, i);
        }
        return total;
    }

    // returns polynomial as a String
    public String toString(){
        String result = "";
        for(int i=coefficients.length-1;i>0;i--){
            result+=coefficients[i]+"x^"+ (i) +" + ";
        }
        result+=coefficients[0];
        return result;
    }
}
