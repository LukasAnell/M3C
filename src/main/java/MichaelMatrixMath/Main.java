package MichaelMatrixMath;

public class Main {
    public static void main(String[] args) {
        int[][] tester = new int[][]{
                {4,-9,6,12},
                {0,-1,4,6},
                {2,-11,8,16},
                {-1,3,0,-1}
        };
        //MatrixMath.gaussianElimination(tester);

        Polynomial[][] input = MatrixMath.subtractLambda(tester);

        Polynomial characteristic = MatrixMath.determinant(input);

        System.out.println(characteristic);

        ComplexSolution[] eigenvalues = ComplexSolution.solvePolynomialAndShowWork(characteristic);

        for(ComplexSolution solution : eigenvalues){
            System.out.print(solution +", ");
        }
        System.out.println();
        // {all eigvecs {eigenvectors with shared values{eigenvector}}}
        double[][][] allEigenvectors = new double[eigenvalues.length][][];
        for(int i = 0; i<eigenvalues.length;i++){
            allEigenvectors[i] = MatrixMath.solveEigenvector(input,eigenvalues[i].real);
            MatrixMath.printEigenvectors(allEigenvectors[i]);
            System.out.println();
        }




    /*
    Polynomial b = new Polynomial(new int[]{1,0,0,-1});

    ComplexSolution dividend = new ComplexSolution(6,3);
    ComplexSolution divisor = new ComplexSolution(7,4);
    System.out.println(dividend.divide(divisor)+" , "+b.evaluate(dividend));


    for(ComplexSolution complexthing : ComplexSolution.solvePolynomialAndShowWork(b)){
      System.out.print(complexthing+", ");
    }
    */


    }
}
