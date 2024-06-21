package MainChallenge;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static double[] timeZero = new double[] {
            1,
            1,
            1,
            1,
            1,
            1,
            1,
            1,
            1,
            1,
            1,
            1,
            1
    };

    public static double[] time = new double[] {
            0,
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            9,
            10,
            11,
            12,
    };

    public static double[] timeSquared = new double[] {
            0,
            1,
            4,
            9,
            16,
            25,
            36,
            49,
            64,
            81,
            100,
            121,
            144,
    };

    public static double[] timeCubed = new double[] {
            0,
            1,
            64,
            729,
            4096,
            15625,
            46656,
            117649,
            262144,
            531441,
            1000000,
            1771561,
            2985984,
    };

    public static double[] medianHouseholdIncome = new double[] {
            60665,
            61856,
            63470,
            65277,
            67365,
            70594,
            74458,
            79565,
            85562,
            92263,
            97185,
            105391,
            116068,
    };

    public static double[][] independentVars = new double[][] {
            timeZero,
            time,
//            timeSquared,
//            timeCubed,
    };

    public static double[] dependentVars = new double[] {
            287331,
            254806,
            256336,
            288632,
            302913,
            329175,
            356518,
            396982,
            435367,
            453289,
            506074,
            601436,
            666139,
    };

    public static void main(String[] args) throws IOException {
        LinearRegression test = new LinearRegression(
                "Median Listing Price vs. Time",
                "Time",
                "Median Listing Price",
                50,
                0.1,
                independentVars,
                dependentVars
        );
        test.graph();

        // testingOldMethods();
        // graphingAndValues();
        /*
        Guess guess = new Guess(new double[] {1, 1, 1, 1});
        for(int i = 0; i < 2; i++) {
            gaussNewton(time, vacantHousing, guess);
            System.out.println(i);
        }
        System.out.println(guess);

        graph(guess, vacantHousing, 0, 50);
         */
    }

    public static void testingOldMethods() throws IOException {
        double[][] independents = new double[][] {
                timeZero
        };
        Matrix transposedIndependentsMatrix = new Matrix(independents);
        Matrix independentsMatrix = transposedIndependentsMatrix.transpose();

        Matrix multMatrix = transposedIndependentsMatrix.times(independentsMatrix);

        SingularValueDecomposition svd = new SingularValueDecomposition(multMatrix);

        Matrix uMatrix = svd.getU();
        Matrix transposedUMatrix = uMatrix.transpose();

        Matrix vMatrix = svd.getV();
        Matrix transposedVMatrix = vMatrix.transpose();

        double[] svds = svd.getSingularValues();
        Matrix svdMatrix = createSVDArray(svds, transposedVMatrix.getColumnDimension(), transposedUMatrix.getRowDimension());

        Matrix pseudoInverse = vMatrix.times(svdMatrix).times(transposedUMatrix);

        Matrix dependentMatrix = new Matrix(new double[][] {
                dependentVars
        }).transpose();

        // 4x4 times 4x13 -> 4x4
        // 4x4 times 13x1 -> error
        Matrix weights = pseudoInverse.times(transposedIndependentsMatrix).times(dependentMatrix).transpose();

        double[] weightArray = weights.getColumnPackedCopy();
        for(double v: weightArray) {
            System.out.println(v);
        }

        XYSeriesCollection data = new XYSeriesCollection();

        XYSeries regressionEstimate = new XYSeries("Housing Supply vs. Time");

        double x = 0;
        System.out.println();
        while(x <= 50) {
            double y = multiplyByWeights(x, weights);
            if(x == 10 || x == 20 || x == 50) {
                System.out.println(y);
            }
            regressionEstimate.add(x, y);
            x++;
        }

        XYSeries givenDataPoints = new XYSeries("Given Points");
        for(int i = 0; i < dependentVars.length; i++) {
            givenDataPoints.add(i, dependentVars[i]);
        }

        data.addSeries(regressionEstimate);
        data.addSeries(givenDataPoints);

        JFreeChart lineChart = ChartFactory.createXYLineChart(
                "Homelessness vs. Time",
                "Time", "Vacant Units",
                data,
                PlotOrientation.VERTICAL,
                false, false, false
        );
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickUnit(new NumberTickUnit(5000));
        yAxis.setRange(0, 100000);

        XYPlot plot = (XYPlot) lineChart.getPlot();
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setRangeAxis(yAxis);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesLinesVisible(1, false);
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesShapesVisible(1, true);

        File f = new File("src/main/Images/testingOldMethods.png");
        ChartUtils.saveChartAsPNG(f, lineChart, 1024, 1024);
    }

    public static void graph(Guess guess, double[] dependent, double start, double end) throws IOException {
        // initialize XYSeriesCollection
        XYSeriesCollection data = new XYSeriesCollection();

        // initialize new XYSeries that will store dependent vs independent relationship
        XYSeries regressionEstimate = new XYSeries("");

        // loops through given range of x values
        // and computes the y values at a given point
        // then adds ordered pair to XYSeries object
        // this also prints out estimates at 10 years, 20 years, and 50 years
        double x = start;
        System.out.println();
        while(x <= end) {
            double y = guess.evaluateActual(x);
            if(x == 10 || x == 20 || x == 50) {
                System.out.println(y);
            }
            regressionEstimate.add(x, y);
            x++;
        }

        // plots given data points as a scatterplot on the same graph as the line
        XYSeries givenDataPoints = new XYSeries("Given Points");
        for(int i = 0; i < dependent.length; i++) {
            givenDataPoints.add(i, dependent[i]);
        }

        // adds XYSeries objects to seriesCollection object
        data.addSeries(regressionEstimate);
        data.addSeries(givenDataPoints);

        // initializes lineChart with given title
        // and seriesCollection object as data source
        JFreeChart lineChart = ChartFactory.createXYLineChart(
                "Vacant Housing vs. Time",
                "Time", "Vacant Units",
                data,
                PlotOrientation.VERTICAL,
                false, false, false
        );

        // changes scale of yAxis to fit all data points
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickUnit(new NumberTickUnit(5000));
        yAxis.setRange(0, 100000);

        // initializes plot with domain and range grid hidden
        // and rangeAxis set to pre-determined yAxis
        XYPlot plot = (XYPlot) lineChart.getPlot();
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setRangeAxis(yAxis);

        // initializes renderer and makes it so:
        // line graph lines are visible but data points are hidden
        // and
        // scatter plot lines are hidden but data points are visible
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesLinesVisible(1, false);
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesShapesVisible(1, true);

        // saves chart as an image
        File f = new File("src/main/Images/vacantHousesVsTime.png");
        ChartUtils.saveChartAsPNG(f, lineChart, 1024, 1024);
    }

    public static void gaussNewton(double[] independent, double[] dependent, Guess guess) {
        double[][] jacobian2DArray = new double[independent.length][4];

        for(int c = 0; c < jacobian2DArray[0].length; c++) {
            for(int r = 0; r < jacobian2DArray.length; r++) {
                switch(c) {
                    case 0:
                        jacobian2DArray[r][c] = guess.evaluateDerivativeOne(independent[r]);
                        break;
                    case 1:
                        jacobian2DArray[r][c] = guess.evaluateDerivativeTwo(independent[r]);
                        break;
                    case 2:
                        jacobian2DArray[r][c] = guess.evaluateDerivativeThree(independent[r]);
                        break;
                    case 3:
                        jacobian2DArray[r][c] = guess.evaluateDerivativeFour(independent[r]);
                        break;
                }
            }
        }

        Matrix jacobianMatrix = new Matrix(jacobian2DArray).transpose();

        matrixToString(jacobianMatrix);

        Matrix multJacobianMatrix = jacobianMatrix.transpose().times(jacobianMatrix);

        double[] residualArray = new double[dependent.length];
        for(int i = 0; i < residualArray.length; i++) {
            residualArray[i] = dependent[i] - guess.evaluateActual(independent[i]);
        }
        Matrix residualMatrix = new Matrix(new double[][] {residualArray}).transpose();
        Matrix result = getPseudoInverse(multJacobianMatrix).times(jacobianMatrix.transpose());

        System.out.println("result " + result.getRowDimension() + " " + result.getColumnDimension());
        System.out.println("residual: " + residualMatrix.getRowDimension() + " " + residualMatrix.getColumnDimension());

        Matrix parameterMatrix = new Matrix(new double[][] {guess.parameters}).transpose();
        Matrix resultMult = result.times(residualMatrix);
        Matrix paramMinusResult = parameterMatrix.minus(resultMult);
        guess.parameters = paramMinusResult.getColumnPackedCopy();
    }

    public static void graphingAndValues() throws IOException {
        // initialize vectors for time (zero through desired power of x)
        double[] timeZero = new double[] {
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                1
        };

        // initialize matrix containing independent variables
        double[][] independents = new double[][] {
                {
                    // median household income
                        60665,
                        61856,
                        63470,
                        65277,
                        67365,
                        70594,
                        74458,
                        79565,
                        85562,
                        92263,
                        97185,
                        105391,
                        116068
                },
                {
                    // housing prices
                        287331,
                        254806,
                        256336,
                        288632,
                        302913,
                        329175,
                        356518,
                        396982,
                        435367,
                        453289,
                        506074,
                        601436,
                        666139
                },
                {
                    // vacant housing
                        12312,
                        12340,
                        11962,
                        12418,
                        12632,
                        13424,
                        14487,
                        15557,
                        15104,
                        14878,
                        12724,
                        11682,
                        10736
                },
                timeZero
        };

        // vector that stores the dependent values
        double[] dependent = new double[] {
                9022,
                8972,
                8899,
                9106,
                8949,
                10122,
                10730,
                11643,
                12112,
                11199,
                11751,
                5183,
                13368
        };

        // convert matrix containing time vectors to a Matrix object
        // and initialize transposedTimeMatrix to its transpose
        /*
        * The lines are reversed due to the nature of initializing 2D Arrays,
        * which would represent the vector as its transpose by default,
        * rather than the intended matrix
        */
        Matrix transposedIndependentsMatrix = new Matrix(independents);
        Matrix independentsMatrix = transposedIndependentsMatrix.transpose();

        // constructs a Matrix of the dependent values and takes the transpose
        Matrix dependentMatrix = new Matrix(new double[][] {
                dependent
        }).transpose();

        // computes the weights Matrix by multiplying
        // the pseudoInverse by the transposedIndependentsMatrix
        // then by the dependentMatrix, and then taking the transpose
        // weights = Independents^-1 * Dependents
        Matrix weights = computeWeights(dependentMatrix, independentsMatrix);

        // convert weights Matrix to a vector and print them out
        double[] weightArray = weights.getColumnPackedCopy();
        for(double v: weightArray) {
            System.out.println(v);
        }

        // initialize XYSeriesCollection
        XYSeriesCollection data = new XYSeriesCollection();

        // initialize new XYSeries that will store dependent vs independent relationship
        XYSeries regressionEstimate = new XYSeries("Housing Supply vs. Time");

        // loops through given range of x values
        // and computes the y values at a given point
        // then adds ordered pair to XYSeries object
        // this also prints out estimates at 10 years, 20 years, and 50 years
        double x = 0;
        System.out.println();
        while(x <= 50) {
            double y = multiplyByWeights(x, weights);
            if(x == 10 || x == 20 || x == 50) {
                System.out.println(y);
            }
            regressionEstimate.add(x, y);
            x++;
        }

        // plots given data points as a scatterplot on the same graph as the line
        XYSeries givenDataPoints = new XYSeries("Given Points");
        for(int i = 0; i < dependent.length; i++) {
            givenDataPoints.add(i, dependent[i]);
        }

        // adds XYSeries objects to seriesCollection object
        data.addSeries(regressionEstimate);
        data.addSeries(givenDataPoints);

        // initializes lineChart with given title
        // and seriesCollection object as data source
        JFreeChart lineChart = ChartFactory.createXYLineChart(
                "Homelessness vs. Time",
                "Time", "Vacant Units",
                data,
                PlotOrientation.VERTICAL,
                false, false, false
        );

        // changes scale of yAxis to fit all data points
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickUnit(new NumberTickUnit(5000));
        yAxis.setRange(0, 100000);

        // initializes plot with domain and range grid hidden
        // and rangeAxis set to pre-determined yAxis
        XYPlot plot = (XYPlot) lineChart.getPlot();
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setRangeAxis(yAxis);

        // initializes renderer and makes it so:
        // line graph lines are visible but data points are hidden
        // and
        // scatter plot lines are hidden but data points are visible
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesLinesVisible(1, false);
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesShapesVisible(1, true);

        // saves chart as an image
        File f = new File("src/main/Images/housingSupplyVsTime.png");
        ChartUtils.saveChartAsPNG(f, lineChart, 1024, 1024);
    }

    public static Matrix computeWeights(Matrix dependentVals, Matrix independentVals) {
        // Initialize unTransposedIndependentsMatrix to the transpose of independentVals
        /*
        * The lines are reversed due to the nature of initializing 2D Arrays,
        * which would represent the vector as its transpose by default,
        * rather than the intended matrix
        */
        Matrix transposedIndependentsMatrix = independentVals.transpose();

        // multiplies the two previous matrices, will be used in SVD
        Matrix multMatrix = transposedIndependentsMatrix.times(independentVals);

        // gets pseudoInverse
        Matrix pseudoInverse = getPseudoInverse(multMatrix);

        // computes the weights Matrix by multiplying
        // the pseudoInverse by the independentVals Matrix
        // then by the dependentVals Matrix, and then taking the transpose
        // weights = Independents^-1 * Dependents
        return pseudoInverse.times(transposedIndependentsMatrix).times(dependentVals).transpose();
    }

    public static Matrix getPseudoInverse(Matrix multMatrix) {
        // SVD is performed on the multMatrix
        SingularValueDecomposition svd = new SingularValueDecomposition(multMatrix);

        // get U and V matrices
        // and their transposed counterparts
        Matrix UMatrix = svd.getU();

        //U is orthogonal, so its inverse is its transpose
        Matrix transposedUMatrix = UMatrix.transpose();
        Matrix VMatrix = svd.getV();

        //this is also the case with V
        Matrix transposedVMatrix = VMatrix.transpose();

        // get vector containing singular values of multMatrix and find the reciprocals for each
        // and create Matrix object for it
        //In total, it is (A^T * A)^-1 * A^T
        //(A^T * A)^-1 = V * S^-1 U^T
        double[] svds = svd.getSingularValues();
        Matrix svdMatrix = createSVDArray(svds, transposedVMatrix.getColumnDimension(), transposedUMatrix.getRowDimension());

        // create PseudoInverse by multiplying the parts together
        return VMatrix.times(svdMatrix).times(transposedUMatrix);
    }

    public static double multiplyByWeights(double x, Matrix weights) {
        // converts weights Matrix to vector
        double[] weightArray = weights.getColumnPackedCopy();

        // initializes Polynomial object using weights as coefficient
        Polynomial polynomial = new Polynomial(weightArray);

        // evaluates polynomial at given point x
        return polynomial.evaluate(x);
    }

    public static Matrix createSVDArray(double[] svds, int rows, int cols) {
        // initializes new matrix of just 0's of given dimensions
        double[][] svdMatrix = new double[rows][cols];

        // loops through the svdMatrix and replaces the diagonal
        // with the reciprocals of the singular values stored in a vector
        int min = Math.min(rows, cols);
        for(int i = 0; i < min; i++) {
            svdMatrix[i][i] = 1/ svds[i];
        }

        // returns Matrix object storing the singular values on the diagonal
        return new Matrix(svdMatrix);
    }

    public static void matrixToString(Matrix m) {
        double[][] arr = m.getArray();
        for(int c = 0; c < arr[0].length; c++) {
            for(int r = 0; r < arr.length; r++) {
                System.out.print(arr[r][c]+", ");
            }
            System.out.println();
        }
    }
}
