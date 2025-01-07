package Testing;

import org.jfree.data.category.DefaultCategoryDataset;

class ChartData {
    public static DefaultCategoryDataset getDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1, "Foo", "A");
        dataset.addValue(10, "Foo", "B");
        dataset.addValue(5, "Foo", "C");
        dataset.addValue(2, "Bar", "A");
        dataset.addValue(3, "Bar", "B");
        dataset.addValue(8, "Bar", "C");
        return dataset;
    }
}
