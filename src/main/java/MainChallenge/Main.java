package MainChallenge;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        graphingAndValues();
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

        double[] time = new double[] {
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
                13,
                14,
                15
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

        // convert matrix containing time vectors to a Matrix object
        // and initialize transposedTimeMatrix to its transpose
        /*
        * The lines are reversed due to the nature of initializing 2D Arrays,
        * which would represent the vector as its transpose by default,
        * rather than the intended matrix
        */
        Matrix transposedIndependentsMatrix = new Matrix(independents);
        Matrix independentsMatrix = transposedIndependentsMatrix.transpose();

        // multiplies the two previous matrices, will be used in SVD
        Matrix multMatrix = transposedIndependentsMatrix.times(independentsMatrix);


        // SVD is performed on the transposedTimeXTime
        SingularValueDecomposition svd = new SingularValueDecomposition(multMatrix);

        // get U and V matrices
        // and their transposed counterparts
        Matrix UMatrix = svd.getU();

        //U is orthogonal, so its inverse is its transpose
        Matrix transposedUMatrix = UMatrix.transpose();
        Matrix VMatrix = svd.getV();

        //this is also the case with V
        Matrix transposedVMatrix = VMatrix.transpose();

        // get vector containing singular values of multMatrix and find the recipricoals for each
        // and create Matrix object for it
        //In total, it is (A^T * A)^-1 * A^T
        //(A^T * A)^-1 = V * S^-1 U^T
        double[] svds = svd.getSingularValues();
        Matrix svdMatrix = createSVDArray(svds, transposedVMatrix.getColumnDimension(), transposedUMatrix.getRowDimension());

        // create PseudoInverse by multiplying the parts together
        Matrix pseudoInverse = VMatrix.times(svdMatrix).times(transposedUMatrix);

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

        // constructs a Matrix of the dependent values and takes the transpose
        Matrix dependentMatrix = new Matrix(new double[][] {
                dependent
        }).transpose();

        // computes the weights Matrix by multiplying
        // the pseudoInverse by the transposedIndependentsMatrix
        // then by the dependentMatrix, and then taking the transpose
        // weights = Independents^-1 * Dependents
        Matrix weights = pseudoInverse.times(transposedIndependentsMatrix).times(dependentMatrix).transpose();

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

}
