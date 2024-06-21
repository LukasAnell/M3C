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
import java.util.Collections;

public class LinearRegression {
    private String title;
    private String xAxisLabel;
    private String yAxisLabel;
    private int stepCount;
    private double stepSize;
    private double[][] independentVariables;
    private double[] dependentVariable;

    public LinearRegression(
            String t,
            String xAL,
            String yAL,
            int sC,
            double sS,
            double[][] iV,
            double[] dV
    ) {
        title = t;
        xAxisLabel = xAL;
        yAxisLabel = yAL;
        stepCount = sC;
        stepSize = sS;
        independentVariables = iV;
        dependentVariable = dV;
    }

    public void graph() throws IOException {
        Matrix weights = computeWeights();
        System.out.println(Arrays.toString(weights.getColumnPackedCopy()));

        XYSeriesCollection data = new XYSeriesCollection();
        XYSeries regressionEstimate = new XYSeries(title);

        double x = (double) -stepCount / 2;
        double max = Integer.MIN_VALUE;
        while(x <= (stepCount + (double) stepCount / 2)) {
            double y = multiplyByWeights(x, weights);
            max = Math.max(max, y);
            // System.out.println(y);
            regressionEstimate.add(x, y);
            x += stepSize;
        }

        XYSeries givenDataPoints = new XYSeries("Given Points");
        for(int i = 0; i < dependentVariable.length; i++) {
            givenDataPoints.add(i, dependentVariable[i]);
        }

        data.addSeries(regressionEstimate);
        data.addSeries(givenDataPoints);

        JFreeChart lineChart = ChartFactory.createXYLineChart(
                title,
                xAxisLabel, yAxisLabel,
                data,
                PlotOrientation.VERTICAL,
                true, false, false
        );

        NumberAxis xAxis = new NumberAxis();
        xAxis.setTickUnit(new NumberTickUnit(stepSize * stepCount));
        xAxis.setRange(Math.min((double) -stepCount / 2, 0), stepCount + (double) stepCount / 2);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickUnit(new NumberTickUnit(500000));
        double e = Math.abs(max + max * 0.1);
        yAxis.setRange(-e, e);

        XYPlot plot = lineChart.getXYPlot();
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setRangeAxis(yAxis);
        plot.setDomainAxis(xAxis);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesLinesVisible(1, false);
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesShapesVisible(1, true);

        File f = new File("src/main/Images/" + title + ".png");
        ChartUtils.saveChartAsPNG(f, lineChart, 1024, 1024);
    }

    private double multiplyByWeights(double x, Matrix weights) {
        double[] weightArray = weights.getColumnPackedCopy();
        Polynomial polynomial = new Polynomial(weightArray);
        return polynomial.evaluate(x);
    }

    private Matrix computeWeights() {
        Matrix transposedIndependentsMatrix = new Matrix(independentVariables);
        Matrix independentsMatrix = transposedIndependentsMatrix.transpose();

        Matrix dependentMatrix = new Matrix(new double[][] {
                dependentVariable
        }).transpose();

        Matrix multMatrix = transposedIndependentsMatrix.times(independentsMatrix);
        Matrix pseudoInverseMultMatrix = getPseudoInverse(multMatrix);

        return pseudoInverseMultMatrix
                .times(transposedIndependentsMatrix)
                .times(dependentMatrix)
                .transpose();
    }

    private Matrix getPseudoInverse(Matrix multMatrix) {
        SingularValueDecomposition svd = new SingularValueDecomposition(multMatrix);

        Matrix uMatrix = svd.getU();
        Matrix transposedUMatrix = uMatrix.transpose();
        Matrix vMatrix = svd.getV();
        // Matrix transposedVMatrix = vMatrix.transpose();

        Matrix svdMatrix = replaceDiagonal(svd.getS());

        return vMatrix.times(svdMatrix).times(transposedUMatrix);
    }

    private Matrix replaceDiagonal(Matrix svdMatrix) {
        double[][] svdArray = svdMatrix.getArray();
        for(int i = 0; i < Math.min(svdArray.length, svdArray[0].length); i++) {
            svdArray[i][i] = 1 / svdArray[i][i];
        }
        return new Matrix(svdArray);
    }

    /*
    private Matrix createSVDMatrix(double[] singularValues, int rows, int cols) {
        double[][] svdArray = new double[rows][cols];

        for(int i = 0; i < Math.min(rows, cols); i++) {
            svdArray[i][i] = 1.0 / singularValues[i];
        }

        return new Matrix(svdArray);
    }
     */

    public void setStepCount(int sC) {
        stepCount = sC;
    }

    public void setStepSize(double sS) {
        stepSize = sS;
    }

    public void setYAxisLabel(String yAL) {
        yAxisLabel = yAL;
    }

    public void setXAxisLabel(String xAL) {
        xAxisLabel = xAL;
    }

    public void setTitle(String t) {
        title = t;
    }

    public void setIndependentVariables(double[][] iV) {
        independentVariables = iV;
    }

    public void setDependentVariable(double[] dV) {
        dependentVariable = dV;
    }

    public double[] getDependentVariable() {
        return dependentVariable;
    }

    public double[][] getIndependentVariables() {
        return independentVariables;
    }

    public double getStepSize() {
        return stepSize;
    }

    public int getStepCount() {
        return stepCount;
    }

    public String getYAxisLabel() {
        return yAxisLabel;
    }

    public String getXAxisLabel() {
        return xAxisLabel;
    }

    public String getTitle() {
        return title;
    }

    public String toString() {
        return title;
    }
}
