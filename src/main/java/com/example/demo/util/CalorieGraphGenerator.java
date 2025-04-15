package com.example.demo.util;

import com.example.demo.record.service.RecordService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;

@Component
public class CalorieGraphGenerator {

    private static final String[] DAYS = {"월", "화", "수", "목", "금", "토", "일"};
    private static final int IMAGE_WIDTH = 384;
    private static final int IMAGE_HEIGHT = 256;
    private static final int TICK_UNIT = 500;
    private static final int RANGE_MARGIN = 1000;
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 16);
    private static final Font TICK_FONT = new Font("Arial", Font.PLAIN, 20);
    private static final Color LAST_WEEK_COLOR = Color.decode("#FFF1B0");
    private static final Color THIS_WEEK_COLOR = Color.decode("#8E9F7C");
    private static final Color BASELINE_COLOR = Color.decode("#008080");
    private static final Shape POINT_SHAPE = new Ellipse2D.Double(-5, -5, 10, 10);
    private static final String OUTPUT_PATH = "src/main/resources/static/images/calories_chart.png";

    private final RecordService recordService;

    @Autowired
    public CalorieGraphGenerator(RecordService recordService) {
        this.recordService = recordService;
    }

    public void generateGraph(Integer userId, int baseline) {
        LocalDate today = LocalDate.now();
        LocalDate startOfThisWeek = getStartOfWeek(today);
        LocalDate startOfLastWeek = startOfThisWeek.minusWeeks(1);

        int[] lastWeekCalories = getWeeklyCalories(userId, startOfLastWeek);
        int[] thisWeekCalories = getWeeklyCalories(userId, startOfThisWeek);

        XYSeriesCollection dataset = createDataset(lastWeekCalories, thisWeekCalories, baseline);
        JFreeChart chart = createChart(dataset, baseline);

        saveChartAsImage(chart, OUTPUT_PATH);
    }

    private int[] getWeeklyCalories(Integer userId, LocalDate startDate) {
        int[] calories = new int[7];
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            Map<String, String> record = recordService.getTodayFoodRecord(userId, Date.valueOf(date));
            calories[i] = parseCalorie(record);
        }
        return calories;
    }

    private int parseCalorie(Map<String, String> record) {
        try {
            return (record != null && record.get("total") != null)
                    ? Integer.parseInt(record.get("total"))
                    : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private XYSeriesCollection createDataset(int[] lastWeek, int[] thisWeek, int baseline) {
        XYSeries lastWeekSeries = new XYSeries("Last Week");
        XYSeries thisWeekSeries = new XYSeries("This Week");
        XYSeries baselineSeries = new XYSeries("Baseline");

        for (int i = 0; i < 7; i++) {
            lastWeekSeries.add(i, lastWeek[i]);
            thisWeekSeries.add(i, thisWeek[i]);
            baselineSeries.add(i, baseline);
        }
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(lastWeekSeries);
        dataset.addSeries(thisWeekSeries);
        dataset.addSeries(baselineSeries);
        return dataset;
    }

    private JFreeChart createChart(XYSeriesCollection dataset, int baseline) {
        JFreeChart chart = ChartFactory.createScatterPlot("", "", "", dataset, PlotOrientation.VERTICAL, false, false, false);
        XYPlot plot = chart.getXYPlot();

        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setOutlineVisible(false);
        chart.setPadding(new RectangleInsets(10, 10, 10, 10));

        configureXAxis(plot);
        configureYAxis(plot, baseline);
        configureRenderer(plot);

        return chart;
    }

    private void configureXAxis(XYPlot plot) {
        SymbolAxis xAxis = new SymbolAxis("", DAYS);
        xAxis.setTickLabelFont(TICK_FONT);
        xAxis.setTickLabelsVisible(true);
        xAxis.setGridBandsVisible(false);
        plot.setDomainAxis(xAxis);
    }

    private void configureYAxis(XYPlot plot, int baseline) {
        NumberAxis yAxis = new NumberAxis();
        int rounded = Math.round(baseline / 100f) * 100;
        yAxis.setRange(rounded - RANGE_MARGIN, rounded + RANGE_MARGIN);
        yAxis.setTickUnit(new NumberTickUnit(TICK_UNIT));
        plot.setRangeAxis(yAxis);
    }

    private void configureRenderer(XYPlot plot) {
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, POINT_SHAPE);
        renderer.setSeriesPaint(0, LAST_WEEK_COLOR);

        renderer.setSeriesLinesVisible(1, false);
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesShape(1, POINT_SHAPE);
        renderer.setSeriesPaint(1, THIS_WEEK_COLOR);

        renderer.setSeriesLinesVisible(2, true);
        renderer.setSeriesShapesVisible(2, false);
        renderer.setSeriesPaint(2, BASELINE_COLOR);

        renderer.setDefaultItemLabelFont(LABEL_FONT);
        plot.setRenderer(renderer);
    }

    private void saveChartAsImage(JFreeChart chart, String path) {
        BufferedImage image = chart.createBufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT);
        File outputFile = new File(path);

        try {
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }
            ImageIO.write(image, "PNG", outputFile);
            System.out.println("✅ Chart saved to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Failed to save chart: " + e.getMessage());
        }
    }

    private LocalDate getStartOfWeek(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int diff = dayOfWeek.getValue() % 7; // 일요일 = 0
        return date.minusDays(diff);
    }
}
