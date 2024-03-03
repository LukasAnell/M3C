package MainChallenge;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        /*
        531,403
        539,000
        545,083
        549,812
        553,576
        556,092
        556,859
        556,718
        559,202
        559,374
        560,447
        562,336
        562,551
         */

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
        yAxis.setRange(531403, 600000);
        XYPlot plot = (XYPlot) lineChart.getPlot();
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setRangeAxis(yAxis);
        File f = new File("src/main/Images/population.png");
        ChartUtils.saveChartAsPNG(f, lineChart, 1024, 1024);
    }
}
