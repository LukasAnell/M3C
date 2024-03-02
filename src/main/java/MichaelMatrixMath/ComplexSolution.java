package MichaelMatrixMath;

public class ComplexSolution{
    public double real;
    public double complex;

    public ComplexSolution(double r, double c){
        real=r;
        complex=c;
    }
    //addition
    public ComplexSolution add(ComplexSolution other){
        ComplexSolution result = new ComplexSolution(0,0);
        result.real=this.real+other.real;
        result.complex=this.complex+other.complex;
        return result;
    }
    //multiplucation
    public ComplexSolution multiply(ComplexSolution other){
        ComplexSolution result = new ComplexSolution(0,0);
        result.real = this.real*other.real - this.complex*other.complex;
        result.complex = this.complex*other.real + this.real*other.complex;
        return result;
    }
    //multiplucation, but using a real instead
    public ComplexSolution multiply(double scalar){
        ComplexSolution result = new ComplexSolution(0,0);
        result.real = this.real*scalar;
        result.complex = this.complex*scalar;
        return result;
    }
    //exponentiation
    public ComplexSolution exp(int power){
        if(power==0){
            return new ComplexSolution(1,0);
        }
        return this.multiply(exp(power-1));
    }

    public String toString(){
        return (Math.round(this.real*1000)/1000.0) +" + " + (Math.round(this.complex*1000)/1000.0) +"i"; //rounding numbers
    }

    //division
    public ComplexSolution divide(ComplexSolution other){
        ComplexSolution result = new ComplexSolution(0,0);
        result.real = (this.real*other.real+this.complex*other.complex)/(other.real*other.real+other.complex*other.complex);
        result.complex = (this.complex*other.real-this.real*other.complex)/(other.real*other.real+other.complex*other.complex);
        return result;

    }

    public static ComplexSolution[] solvePolynomialAndShowWork(Polynomial poly){
        System.out.println(poly);
        ComplexSolution[] solutions = new ComplexSolution[poly.coefficients.length-1];

        //setting initial values on the ehlrich circle
        for(int i=0; i<solutions.length; i++){
            double radius = Math.pow(Math.abs((double)(poly.coefficients[0])/(poly.coefficients[poly.coefficients.length-1])),1.00/poly.coefficients.length)+0.1;

            double realComponent = radius*Math.cos(2*Math.PI/(poly.coefficients.length-1)*i+Math.PI/2);
            double complexComponent = radius*Math.sin(2*Math.PI/(poly.coefficients.length-1)*i+Math.PI/2);
            solutions[i]=new ComplexSolution(realComponent, complexComponent);
        }


        //start calculating
        boolean isCloseEnough = false;
        for(int i=0; !isCloseEnough&&i<1000;i++){ //repeat until close enough
            if(i==999){
                System.out.println("yippee");
            }

            //store previous iteration in temp
            ComplexSolution[] temp = new ComplexSolution[solutions.length];

            for(int j=0; j<temp.length;j++){
                temp[j]=solutions[j];
            }

            //print it
      /*
      for(ComplexSolution ComplexNumber: solutions){
        System.out.print(ComplexNumber+",  ");
      }
      System.out.println();
      */

            //iterate
            for(int k = 0; k<solutions.length; k++){
                ComplexSolution denominator=new ComplexSolution(1,0);

                //create the denominator
                for(int p=0;p<temp.length;p++){
                    //don't include current root
                    if(k!=p){
                        denominator=denominator.multiply(temp[k].add(temp[p].multiply(-1)));
                    }
                }

                //norm the polynomial by multiplying the denominator by the leading coefficient
                denominator = denominator.multiply(poly.coefficients[poly.coefficients.length-1]);

                //durand kerner -> x_n+1 = x_n - f(n)/denominator
                solutions[k]= poly.evaluate(temp[k]).divide(denominator).multiply(-1).add(temp[k]);
            }

            double distance=0;
            for(int s=0;s<temp.length;s++){//check difference between temp and solution
                double realComponent = Math.pow(solutions[s].real-temp[s].real,2);
                double complexComponent = Math.pow(solutions[s].complex-temp[s].complex,2);
                distance+= Math.pow(complexComponent+realComponent,0.5);

            }
            if(distance/temp.length<=Math.pow(10.0,-7)){
                isCloseEnough=true;
            }
        }
        return solutions;
    }
}
