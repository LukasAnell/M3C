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
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.util.ShapeUtils;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // graphPopulationGrowth();
        matrixFuckery();
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
                12
        };

        double[][] timeLongs = new double[][] {
                timeZero,
                time,
        };

        double[] totalHousingUnits = new double[] {
                234891,
                237735,
                239718,
                240277,
                240961,
                241326,
                242070,
                243402,
                244382,
                245476,
                247926,
                252924,
                255178

        };

        double[] occupiedUnits = new double[] {
                217256,
                220060,
                222584,
                222491,
                222868,
                222098,
                221320,
                221119,
                222748,
                224166,
                229701,
                236191,
                239800
        };

        double[] occupationPercentage = new double[totalHousingUnits.length];

        for(int i = 0; i < totalHousingUnits.length; i++) {
            occupationPercentage[i] = occupiedUnits[i] / totalHousingUnits[i];
        }

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

        double[] housingUnits = new double[] {
                17635,
                17675,
                17134,
                17786,
                18093,
                19228,
                20750,
                22283,
                21634,
                21310,
                18225,
                16733,
                15378
        };

        Matrix housingMatrix = new Matrix(new double[][] {
                housingUnits
        }).transpose();



        System.out.println("inverse " + inverse.getRowDimension() + " " + inverse.getColumnDimension());
        System.out.println("time " + UnTransposedMatrix.getRowDimension() + " " + UnTransposedMatrix.getColumnDimension());
        System.out.println("housing " + housingMatrix.getRowDimension() + " " + housingMatrix.getColumnDimension());

        Matrix weights = inverse.times(transposedMatrix).times(housingMatrix).transpose();
        System.out.println("Weights " + weights.getRowDimension() + " " + weights.getColumnDimension());

        double[] weightArray = weights.getColumnPackedCopy();
        for(int i = 0; i < weightArray.length; i++) {
            System.out.println(weightArray[i]);
        }

        XYSeriesCollection data = new XYSeriesCollection();

        XYSeries regressionEstimate = new XYSeries("Housing Supply vs. Time");
        double x = 0;
        while(x <= 20) {
            double y = multiplyByWeights(x, weights);
            regressionEstimate.add(x, y);
            x++;
        }

        XYSeries givenDataPoints = new XYSeries("Given Points");
        for(int i = 0; i < housingUnits.length; i++) {
            givenDataPoints.add(i, housingUnits[i]);
        }

        data.addSeries(regressionEstimate);
         data.addSeries(givenDataPoints);

        JFreeChart lineChart = ChartFactory.createXYLineChart(
                "Vacant Housing vs. Time",
                "Time", "Vacant Units",
                data,
                PlotOrientation.VERTICAL,
                false, false, false
        );

        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickUnit(new NumberTickUnit(1000));
        yAxis.setRange(12000, 25000);

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
