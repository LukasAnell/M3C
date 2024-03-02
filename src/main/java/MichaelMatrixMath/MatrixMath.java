package MichaelMatrixMath;

public class MatrixMath{

    //precondition: at least 2x2 and a square
    public static int determinant(int[][] arr){
        if(arr.length==2&&arr[0].length==2){
            return arr[0][0]*arr[1][1]-arr[0][1]*arr[1][0];
        }
        int total=0;
        //go through each column of arr
        for(int i=0;i<arr[0].length;i++){
            //
            int[][] expansion = new int[arr.length-1][arr[0].length-1];//cofactor expansion

            //copy minor matrix into another matrix

            //go through the rows of arr, ignore the first bc we're reading the first row
            for(int j=1;j<arr.length;j++){
                int expansionCounter=0; //column of new array
                //go through columns of arr
                for(int k=0;k<arr[0].length;k++){
                    //ignore elements in the same column
                    if(k==i){
                        expansionCounter--;
                    }else{
                        expansion[j-1][expansionCounter]=arr[j][k];
                    }
                    expansionCounter++;
                }
            }

            //positive or negative for determinants
            if(i%2==0){
                total+=arr[0][i]*determinant(expansion);
            }else{
                total-=arr[0][i]*determinant(expansion);
            }


        }

        return total;
    }

    public static Polynomial determinant(Polynomial[][] arr){
        Polynomial result=new Polynomial(new int[1]);
        if(arr.length==2 && arr[0].length == 2){
            return arr[0][0].multiply(arr[1][1]).add(arr[1][0].multiply(arr[0][1]).multiply(-1));
        }
        for(int i=0;i<arr.length;i++){
            Polynomial[][] expansion = new Polynomial[arr.length-1][arr[0].length-1];

            for(int j=1;j<arr.length;j++){
                int expansionCounter = 0;
                for(int k=0;k<arr.length;k++){
                    if(i==k){
                        expansionCounter--;
                    }
                    else{
                        expansion[j-1][expansionCounter]=arr[j][k];
                    }
                    expansionCounter++;
                }
            }

            if(i%2==0){
                result=determinant(expansion).multiply(arr[0][i]).add(result);
            }else{
                result=determinant(expansion).multiply(arr[0][i].multiply(-1)).add(result);
            }

        }
        return result;
    }

    public static Polynomial[][] subtractLambda(int[][] arr){
        Polynomial[][] result = new Polynomial[arr.length][arr[0].length];
        for(int i = 0; i<arr.length; i++){
            for(int j = 0; j<arr[i].length; j++){
                result[i][j] = new Polynomial(new int[]{arr[i][j]});
                if(i==j){
                    result[i][j] = result[i][j].add(new Polynomial(new int[]{0,-1}));
                }
            }
        }
        return result;
    }

//    public static double innerProduct(double[] vector1, double[] vector2){
//        double result = 0;
//        for(int i = 0;i < result.length; i++){
//            result += vector1[i]*vector2[i];
//        }
//        return result;
//
//    }

    public static double[] rowMultiply(double[] row, double scalar){
        double[] result = new double[row.length];
        for(int i = 0; i<row.length; i++){
            result[i]=row[i]*scalar;
        }
        return result;
    }

    public static void rowSwap(double[][] arr, int row1, int row2){
        double[] temp = new double[arr[row1].length];
        for(int i = 0; i<temp.length; i++){
            temp[i] = arr[row1][i];
        }
        for(int i = 0; i<temp.length; i++){
            arr[row1][i] = arr[row2][i];
        }
        for(int i = 0; i<temp.length; i++){
            arr[row2][i] = temp[i];
        }

    }

    public static double[] rowAdd(double[] row1, double[] row2){
        double[] result = new double[row1.length];
        for(int i = 0; i<result.length; i++){
            result[i]=row1[i]+row2[i];
        }
        return result;
    }

    public static double findLeadingDigit(double[] row){
        double leadingDigit = 0;
        //find leading digit
        for(int j = 0; j<row.length; j++){
            if(Math.abs(row[j])>=0.0000001){//check if non zero, for doubles
                leadingDigit = row[j];
                return leadingDigit;
            }
        }
        return leadingDigit;
    }

    public static int leadingDigitIndex(double[] row){
        for(int i = 0; i<row.length; i++){
            if(Math.abs(row[i])>=0.0000001){//check if non zero, for doubles
                return i;
            }

        }
        return -1;
    }

    public static double[][] solveEigenvector(Polynomial[][] arr, double lambda){
        //evaluate arr at the eigenvalue
        double[][] matrix = new double[arr.length][arr[0].length];
        for(int i = 0; i<arr.length; i++){
            for(int j = 0; j<arr[i].length; j++){
                matrix[i][j] = (arr[i][j].evaluate(lambda));
            }
        }



        //gauss Elim
        gaussianElimination(matrix);


        //organize the matrix
        for(int i = 0; i<matrix.length; i++){
            if(leadingDigitIndex(matrix[i])!=-1){
                rowSwap(matrix,i,leadingDigitIndex(matrix[i]));
            }
        }




        //reduce it all the way
        for(int i = matrix.length-1; i>=0;i--){
            for(int j = i; j>0; j--){
                double[] toSubtract = rowMultiply(matrix[i],-matrix[j-1][i]);
                matrix[j-1]=rowAdd(matrix[j-1],toSubtract);
            }
        }

        //print result
        System.out.println("\n eigenvalue: "+(int)(lambda*1000+0.5)/1000.0);
        printMatrix(matrix);
        System.out.println();


        //find rows of zeroes
        int nullityCount = 0;
        int[] nullIndexes = new int[matrix.length];
        for(int i = 0; i<matrix.length; i++){
            boolean isAllZero = true;
            for(int j = 0; j<matrix[i].length; j++){
                if(Math.abs(matrix[i][j])>=Math.pow(10.0,-7)){
                    isAllZero = false;
                }
            }

            if(isAllZero){
                nullIndexes[nullityCount] = i;
                nullityCount++;
            }
        }

        //copy eigenvectors into this array
        double[][] eigenvectors = new double[nullityCount][matrix[0].length];
        for(int i = 0; i<nullityCount; i++){
            int index = nullIndexes[i];
            for(int j = 0; j<matrix[i].length; j++){
                eigenvectors[i][j] = matrix[j][index];
                if(index==j){
                    eigenvectors[i][j] = -1;
                }
            }
        }

        return eigenvectors;
    }

    public static void gaussianElimination(double[][] matrix){

        for(int i = 0; i<matrix.length-1; i++){
            double leadingDigit=findLeadingDigit(matrix[i]);

            //normalize the row
            if(leadingDigit!=0){
                matrix[i] = MatrixMath.rowMultiply(matrix[i],1.00/leadingDigit);

                //round the whole row to 0
                for(int j = 0; j<matrix[i].length; j++){
                    if(Math.abs(matrix[i][j])<Math.pow(10.0,-7)){
                        matrix[i][j]=0;
                    }
                }

                //find column of current row's leading digit
                int currentColumn = leadingDigitIndex(matrix[i]);
                if(currentColumn!=-1){
                    //subtract row i from all of the rows below it
                    for(int j = i; j<matrix.length-1;j++){


                        double otherLeadingDigit = matrix[j+1][currentColumn];

                        //System.out.println("\n"+otherLeadingDigit);

                        double[] toSubtract = MatrixMath.rowMultiply(matrix[i],-otherLeadingDigit);
                        matrix[j+1]=MatrixMath.rowAdd(matrix[j+1],toSubtract);
                        //round the whole row to 0
                        for(int k = 0; k<matrix[i].length; k++){
                            if(Math.abs(matrix[i][k])<Math.pow(10.0,-7)){
                                matrix[i][k]=0;
                            }
                        }
                        //printMatrix(matrix);
                        //System.out.println("-----------------");
                    }
                }

            }
        }//end of int i loop


        //printMatrix(matrix);
        //System.out.println();



        //look for a leading digit in the last row

        int lastRow = matrix.length-1;
        double lastLeadingDigit = findLeadingDigit(matrix[lastRow]);

        //normalize the last row
        if(lastLeadingDigit!=0){
            matrix[lastRow] = MatrixMath.rowMultiply(matrix[lastRow],1.00/lastLeadingDigit);
        }
        //round the whole row to 0
        for(int j = 0; j<matrix[lastRow].length; j++){
            if(matrix[lastRow][j]<Math.pow(10.0,-7)){
                matrix[lastRow][j]=0;
            }
        }

    }

    public static void printEigenvectors(double[][] arr){
        for(int i = 0; i<arr.length; i++){
            for(int j = 0; j<arr[i].length; j++){
                System.out.print((int)(arr[i][j]*100)/100.0 + ",  ");
            }
            System.out.println();
        }
    }

    public static void printMatrix(double[][] matrix){
        for(double[] row : matrix){
            for(double number : row){
                System.out.print((int)(number*100)/100.0 + ",   ");
            }
            System.out.println();
        }
    }


}
