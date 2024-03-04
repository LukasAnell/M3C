package MainChallenge;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import MichaelMatrixMath.Polynomial;
import XMLHandling.SAXHandler;
import XMLHandling.XmlReader;
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

public class Main {
    public static void main(String[] args) throws IOException {
        // graphPopulationGrowth();
        matrixFuckery();

//        Guess guess = new Guess(new double[] {1, 1, 1, 1});
//        System.out.println(guess);
//        for(int i = 0; i < 2; i++) {
//            gaussNewton(time, vacantHousing, guess);
//        }
//        System.out.println(guess);
    }

    public static void gaussNewton(double[] independent, double[] dependent, Guess guess) {
        double[][] jacobianMatrix = new double[independent.length][4];


        for(int c = 0; c < jacobianMatrix[0].length; c++) {
            for(int r = 0; r < jacobianMatrix.length; r++) {
                switch(c) {
                    case 0:
                        jacobianMatrix[r][c] = guess.evaluateDerivativeOne(independent[r]);
                        break;
                    case 1:
                        jacobianMatrix[r][c] = guess.evaluateDerivativeTwo(independent[r]);
                        break;
                    case 2:
                        jacobianMatrix[r][c] = guess.evaluateDerivativeThree(independent[r]);
                        break;
                    case 3:
                        jacobianMatrix[r][c] = guess.evaluateDerivativeFour(independent[r]);
                        break;
                }
            }
        }

        double[] residualArray = new double[dependent.length];
        for(int i = 0; i < residualArray.length; i++) {
            residualArray[i] = dependent[i] - guess.evaluateActual(independent[i]);
        }
        Matrix residualMatrix = new Matrix(new double[][] {residualArray});
        residualMatrix = residualMatrix.transpose();
        Matrix result = getPseudoInverse(jacobianMatrix);
        System.out.println("result " + result.getRowDimension() + " " + result.getRowDimension());


        Matrix parameterMatrix = new Matrix(new double[][] {guess.parameters});
        parameterMatrix = parameterMatrix.transpose();
        System.out.println("result: " + result.getRowDimension() + " " + result.getColumnDimension());
        System.out.println("residual: " + residualMatrix.getRowDimension() + " " + residualMatrix.getColumnDimension());
        result = parameterMatrix.minus(result.times(residualMatrix));
        guess.parameters = result.getColumnPackedCopy();
    }

    public static void matrixFuckery() throws IOException {
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

        double[] timeSquared = new double[] {
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
                169,
                196,
                225
        };

        double[] timeCubed = new double[] {
                0,
                1,
                8,
                27,
                64,
                125,
                216,
                343,
                512,
                729,
                1000,
                1331,
                1728,
                2197,
                2744,
                3375
        };

        double[] timeQuartic = new double[] {
                0,
                1,
                16,
                81,
                256,
                625,
                1296,
                2401,
                4096,
                6561,
                10000,
                14641,
                20736,
                28561,
                38416,
                50625
        };

        double[] timeQuintic = new double[] {
                0,
                1,
                32,
                243,
                1024,
                3125,
                7776,
                16807,
                32768,
                59049,
                100000,
                161051,
                248832,
                371293,
                537824,
                759375
        };

        double[] timeHexic = new double[] {
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
                4826809,
                7529536,
                11390625
        };

        double[][] timeLongs = new double[][] {
                timeZero,
                time,
                timeSquared,
                timeCubed,
                timeQuartic,
                timeQuintic,
                timeHexic
        };

//        double[] totalHousingUnits = new double[] {
//                302465,
//                304164,
//                306694,
//                309205,
//                311286,
//                315950,
//                322795,
//                334739,
//                344503,
//                354475,
//                367337,
//                362809,
//                372436
//        };
//
//        double[] occupiedUnits = new double[] {
//                280453,
//                282480,
//                285476,
//                288439,
//                290822,
//                296633,
//                304157,
//                314850,
//                323446,
//                331836,
//                344629,
//                337361,
//                345246
//
//        };

        Matrix transposedMatrix = new Matrix(timeLongs);
        Matrix UnTransposedMatrix = transposedMatrix.transpose();

        Matrix transposedTimesUnTransposed = transposedMatrix.times(UnTransposedMatrix);

        SingularValueDecomposition svd = new SingularValueDecomposition(transposedTimesUnTransposed);
        Matrix UMatrix = svd.getU();
        Matrix transposedUMatrix = UMatrix.transpose();
        Matrix VMatrix = svd.getV();
        Matrix transposedVMatrix = VMatrix.transpose();
        double[] svds = svd.getSingularValues();
        Matrix svdMatrix = createSVDArray(svds, transposedVMatrix.getColumnDimension(), transposedUMatrix.getRowDimension());
        Matrix inverse = VMatrix.times(svdMatrix);
        inverse = inverse.times(transposedUMatrix);
        Matrix transposeInverse = inverse.transpose();

//        double[] occupationPercentage = new double[totalHousingUnits.length];
//
//        for(int i = 0; i < totalHousingUnits.length; i++) {
//            occupationPercentage[i] = occupiedUnits[i] / totalHousingUnits[i];
//        }
        double[] vacantUnits = new double[] {
                1276,
                1276,
                2002,
                2002,
                1639,
                1431,
                1171,
                1254,
                1287,
                1222,
                1318,
                1340,
                1524,
                1586,
                1567,
                1277
        };

        Matrix housingMatrix = new Matrix(new double[][] {
                vacantUnits
        }).transpose();

        Matrix weights = inverse.times(transposedMatrix).times(housingMatrix).transpose();

        double[] weightArray = weights.getColumnPackedCopy();
        for(int i = 0; i < weightArray.length; i++) {
            System.out.println(weightArray[i]);
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
        for(int i = 0; i < vacantUnits.length; i++) {
            givenDataPoints.add(i, vacantUnits[i]);
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
        yAxis.setTickUnit(new NumberTickUnit(100));
        yAxis.setRange(0, 3000);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setTickUnit(new NumberTickUnit(1));
        XYPlot plot = (XYPlot) lineChart.getPlot();
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setRangeAxis(yAxis);
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesLinesVisible(1, false);
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesShapesVisible(1, true);
        File f = new File("src/main/Images/housingSupplyVsTime.png");
        ChartUtils.saveChartAsPNG(f, lineChart, 1024, 1024);
    }


    private static Matrix getPseudoInverse(double[][] jacobian) {
        Matrix transposedMatrix = new Matrix(jacobian);
        Matrix UnTransposedMatrix = transposedMatrix.transpose();

        Matrix transposedTimesUnTransposed = transposedMatrix.times(UnTransposedMatrix);

        SingularValueDecomposition svd = new SingularValueDecomposition(transposedTimesUnTransposed);
        Matrix UMatrix = svd.getU();
        Matrix transposedUMatrix = UMatrix.transpose();
        Matrix VMatrix = svd.getV();
        Matrix transposedVMatrix = VMatrix.transpose();
        double[] svds = svd.getSingularValues();

        Matrix svdMatrix = createSVDArray(svds, transposedVMatrix.getColumnDimension(), transposedUMatrix.getRowDimension());
        Matrix inverse = VMatrix.times(svdMatrix);
        inverse = inverse.times(transposedUMatrix);
        System.out.println("inverse: " + inverse.getRowDimension() + " " + inverse.getColumnDimension());
        inverse = inverse.times(transposedMatrix);
        System.out.println("inverse: " + inverse.getRowDimension() + " " + inverse.getColumnDimension());
        return inverse;
    }

    public static double multiplyByWeights(double x, Matrix weights) {
        double[] weightArray = weights.getColumnPackedCopy();

        Polynomial polynomial = new Polynomial(weightArray);
        return polynomial.evaluate(x);
    }

    public static Matrix createSVDArray(double[] svds, int rows, int cols) {
        double[][] svdMatrix = new double[rows][cols];
        for(int r = 0; r < svdMatrix.length; r++) {
            for(int c = 0; c < svdMatrix[0].length; c++) {
                if(r == c && r < svds.length) {
                    svdMatrix[r][c] = 1 / svds[r];
                }
            }
        }
        return new Matrix(svdMatrix);
    }

    public static void readSpreadsheet() {
        SAXHandler e = XmlReader.getSAXHandler();
        assert e != null;
    }

    public static void graphPopulationGrowth() throws IOException {
        XYSeries series = new XYSeries("Population");
        series.add(2010, 531403);
        series.add(2011, 539000);
        series.add(2012, 545083);
        series.add(2013, 549812);
        series.add(2014, 553576);
        series.add(2015, 556092);
        series.add(2016, 556859);
        series.add(2017, 556718);
        series.add(2018, 559202);
        series.add(2019, 559374);
        series.add(2020, 560447);
        series.add(2021, 562336);
        series.add(2022, 562551);

        XYSeriesCollection data = new XYSeriesCollection(series);

        JFreeChart lineChart = ChartFactory.createXYLineChart(
                "Total Population",
                "X", "Y",
                data,
                PlotOrientation.VERTICAL,
                false, false, false
        );
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickUnit(new NumberTickUnit(10000));
        yAxis.setRange(531403, 570000);
        XYPlot plot = (XYPlot) lineChart.getPlot();
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setRangeAxis(yAxis);
        File f = new File("src/main/Images/population.png");
        ChartUtils.saveChartAsPNG(f, lineChart, 1024, 1024);
    }
}
