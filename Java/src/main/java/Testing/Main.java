package Testing;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import java.io.File;
import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // y = ln(x)
        double x = -10;
        while(x <= 10) {
            double y = Math.pow(Math.E, x);
            dataset.addValue(y, "", "" + x);

            x += 0.1;
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Test",
                "X", "Y",
                dataset,
                PlotOrientation.VERTICAL,
                false, false, false
        );

        File f = new File("src/main/Images/test.png");
        ChartUtils.saveChartAsPNG(f, lineChart, 512, 512);
    }
}
