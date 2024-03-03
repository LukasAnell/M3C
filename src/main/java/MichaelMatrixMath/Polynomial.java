package MichaelMatrixMath;

//polynomial in vector form
public class Polynomial{
    public double[] coefficients;
    public Polynomial(double[] arr){
        coefficients = arr;
    }
    //Multiply two polynomials
    public Polynomial multiply(Polynomial other){
        Polynomial result = new Polynomial(new double[this.coefficients.length + other.coefficients.length-1]);
        for(int i=0;i<this.coefficients.length;i++){
            for(int j=0;j<other.coefficients.length;j++){
                result.coefficients[i+j]+=this.coefficients[i]*other.coefficients[j];
            }
        }
        return result;
    }

    public Polynomial multiply(double scalar){
        Polynomial result = new Polynomial(new double[this.coefficients.length]);
        for(int i = 0; i<this.coefficients.length;i++){
            result.coefficients[i]=scalar*this.coefficients[i];
        }
        return result;
    }

    //add two polynomials
    public Polynomial add(Polynomial other){
        Polynomial result = new Polynomial(new double[Math.max(this.coefficients.length,other.coefficients.length)]);
        for(int i=0;i<this.coefficients.length;i++){
            result.coefficients[i]+=this.coefficients[i];
        }
        for(int i=0;i<other.coefficients.length;i++){
            result.coefficients[i]+=other.coefficients[i];
        }
        return result;
    }
    //evaluate polynomial at input
    public double evaluate(double input){
        double total = 0;
        for(int i = 0; i<this.coefficients.length; i++){
            total+=this.coefficients[i]*Math.pow(input,i);
        }
        return total;
    }

    public String toString(){
        String result = "";
        for(int i=coefficients.length-1;i>0;i--){
            result+=coefficients[i]+"x^"+ (i) +" + ";
        }
        result+=coefficients[0];
        return result;
    }
}
