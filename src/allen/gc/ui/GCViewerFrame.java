package allen.gc.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

import allen.gc.Config;
import allen.gc.DataHolder;
import allen.gc.GCData;
import allen.gc.GCDataType;

/**
 * GCViewerFrame.
 * 
 * @author xinzhi.zhang
 * */
public class GCViewerFrame extends ApplicationFrame {

    private static final long   serialVersionUID = 1L;

    public static GCViewerFrame gcViewer         = new GCViewerFrame(
                                                         Config.Title);

    private GCViewerFrame(String title) {
        super(title);
        repaintGCViewer();
    }

    public void repaintGCViewer() {

        JPanel contentPanel = new JPanel(new BorderLayout());
        JPanel jPanelForChartPanel = new JPanel();
        GCDataType[] gcDataTypes = GCDataType.values();

        jPanelForChartPanel.setLayout(new GridLayout(gcDataTypes.length, 1));

        DataHolder dataHolder = new DataHolder();
        List<String> timeLines = dataHolder.getSortedTimeLine();
        System.out.println(timeLines);

        int width = Config.WidthPerChartPanel;

        for (int i = 0; i < gcDataTypes.length; i++) {

            CategoryDataset dataset = createDataset(dataHolder, timeLines,
                    gcDataTypes[i]);

            width = Math.max(width, dataset.getColumnCount()
                    * Config.WidthPerGCData);

            JFreeChart chart = createChart(dataset);
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(width,
                    Config.HeightPerChartPanel));
            jPanelForChartPanel.add(chartPanel);

        }

        JScrollPane jScrollPane = new JScrollPane(jPanelForChartPanel);
        jScrollPane.setPreferredSize(new Dimension(width,
                Config.HeightPerChartPanel * gcDataTypes.length));

        contentPanel.add(SelectPanel.selectPanel, BorderLayout.NORTH);
        contentPanel.add(jScrollPane, BorderLayout.CENTER);
        contentPanel.add(FilterPanel.filterPanel, BorderLayout.WEST);
        setContentPane(contentPanel);

        this.validate();
        repaint();
    }

    private static CategoryDataset createDataset(DataHolder dataHolder,
            List<String> timeLines, GCDataType gcDataType) {

        Map<String, TreeMap<String, GCData>> datas = dataHolder.datas
                .get(gcDataType);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < timeLines.size(); i++) {
            String time = timeLines.get(i);
            for (String event : datas.keySet()) {
                TreeMap<String, GCData> map = datas.get(event);
                for (Entry<String, GCData> entry : map.entrySet()) {
                    if (entry.getKey().equals(time)) {
                        dataset.addValue(entry.getValue().getValue(), event,
                                entry.getKey());
                    }
                }
            }
        }
        return dataset;
    }

    private static JFreeChart createChart(CategoryDataset dataset) {
        // create the chart...
        JFreeChart chart = ChartFactory.createLineChart("", // chart title
                "timeline", // domain axis label
                "value", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
                );
        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        plot.setBackgroundPaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.white);

        CategoryAxis categoryAxis = plot.getDomainAxis();
        categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);

        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot
                .getRenderer();

        renderer.setShapesVisible(true);

        return chart;
    }

}