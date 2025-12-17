package com.alok.trafficanalyzer;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class Analyzer {

    static class Packet {
        Instant timestamp;
        String srcIp;
        String dstIp;
        String protocol;
        int length;

        Packet(Instant ts, String s, String d, String p, int l) {
            this.timestamp = ts;
            this.srcIp = s;
            this.dstIp = d;
            this.protocol = p;
            this.length = l;
        }
    }

    public static void main(String[] args) {
        String csv = "data/packets.csv";
        String outDir = "out";

        if (args.length >= 1)
            csv = args[0];
        if (args.length >= 2)
            outDir = args[1];

        try {
            Files.createDirectories(Paths.get(outDir));
            List<Packet> packets = loadCsv(csv);

            if (packets.isEmpty()) {
                System.err.println("No valid packets found in CSV.");
                return;
            }

            plotHistogram(packets, outDir + "/packets_vs_size.png");
            plotPacketsVsTime(packets, outDir + "/packets_vs_time.png");
            plotProtocolDistribution(packets, outDir + "/protocol_distribution.png");
            plotSizeVsTime(packets, outDir + "/size_vs_time.png");
            plotTopTalkers(packets, outDir + "/top_talkers.png");
            writeReport(packets, csv, outDir);

            System.out.println("Analysis done. Check output in folder: " + outDir);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load CSV into Packet objects
    static List<Packet> loadCsv(String path) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(path));
        if (lines.size() < 2)
            return Collections.emptyList();

        boolean hasHeader = lines.get(0).toLowerCase().contains("timestamp");
        int start = hasHeader ? 1 : 0;

        List<Packet> list = new ArrayList<>();
        for (int i = start; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty())
                continue;
            String[] parts = line.split(",", -1);
            if (parts.length < 5)
                continue;

            try {
                Instant ts = Instant.parse(parts[0]);
                String src = parts[1];
                String dst = parts[2];
                String proto = parts[3];
                int len = Integer.parseInt(parts[4]);
                list.add(new Packet(ts, src, dst, proto, len));
            } catch (Exception ex) {
                System.err.println("Skipping bad line: " + line);
            }
        }
        return list;
    }

    // Histogram of packet sizes
    static void plotHistogram(List<Packet> pkts, String file) throws IOException {
        double[] vals = pkts.stream().mapToDouble(p -> p.length).toArray();
        if (vals.length == 0)
            return;

        HistogramDataset ds = new HistogramDataset();
        ds.addSeries("Packet Sizes", vals, 50);
        JFreeChart chart = ChartFactory.createHistogram("Packets vs Size", "Size", "Count", ds);
        ChartUtils.saveChartAsPNG(new File(file), chart, 800, 600);
    }

    // Packets over time (curve)
    static void plotPacketsVsTime(List<Packet> pkts, String file) throws IOException {
        TimeSeries tsSeries = new TimeSeries("Packets/sec");
        Map<Instant, Long> counts = pkts.stream()
                .collect(Collectors.groupingBy(p -> p.timestamp, Collectors.counting()));

        counts.keySet().stream().sorted()
                .forEach(inst -> tsSeries.add(new Millisecond(Date.from(inst)), counts.get(inst)));

        TimeSeriesCollection ds = new TimeSeriesCollection(tsSeries);
        JFreeChart chart = ChartFactory.createTimeSeriesChart("Packets vs Time", "Time", "Count", ds);

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false); // **curve**
        plot.setRenderer(renderer);

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new java.text.SimpleDateFormat("HH:mm:ss"));

        ChartUtils.saveChartAsPNG(new File(file), chart, 900, 500);
    }

    // Protocol distribution bar chart
    static void plotProtocolDistribution(List<Packet> pkts, String file) throws IOException {
        Map<String, Long> map = pkts.stream().collect(Collectors.groupingBy(p -> p.protocol, Collectors.counting()));
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        map.forEach((k, v) -> ds.addValue(v, "Protocol", k));

        JFreeChart chart = ChartFactory.createBarChart("Protocol Distribution", "Protocol", "Count", ds);
        ChartUtils.saveChartAsPNG(new File(file), chart, 800, 500);
    }

    // Packet size over time (curve)
    static void plotSizeVsTime(List<Packet> pkts, String file) throws IOException {
        TimeSeries tsSeries = new TimeSeries("Size");
        pkts.forEach(p -> tsSeries.addOrUpdate(new Millisecond(Date.from(p.timestamp)), p.length));

        TimeSeriesCollection ds = new TimeSeriesCollection(tsSeries);
        JFreeChart chart = ChartFactory.createTimeSeriesChart("Size vs Time", "Time", "Bytes", ds);

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false); // **curve**
        plot.setRenderer(renderer);

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new java.text.SimpleDateFormat("HH:mm:ss"));

        ChartUtils.saveChartAsPNG(new File(file), chart, 900, 500);
    }

    // Top 10 source IPs
    static void plotTopTalkers(List<Packet> pkts, String file) throws IOException {
        Map<String, Long> map = pkts.stream().collect(Collectors.groupingBy(p -> p.srcIp, Collectors.counting()));
        List<Map.Entry<String, Long>> sorted = map.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .collect(Collectors.toList());

        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (Map.Entry<String, Long> e : sorted)
            ds.addValue(e.getValue(), "Top Talkers", e.getKey());

        JFreeChart chart = ChartFactory.createBarChart("Top Source IPs", "Source IP", "Count", ds);
        ChartUtils.saveChartAsPNG(new File(file), chart, 800, 500);
    }

    // Simple report
    static void writeReport(List<Packet> pkts, String csv, String outDir) {
        Path rp = Paths.get(outDir, "report.txt");
        try (BufferedWriter w = Files.newBufferedWriter(rp)) {
            w.write("Report for " + csv + "\n");
            w.write("Total packets: " + pkts.size() + "\n");
            Map<String, Long> protoCount = pkts.stream()
                    .collect(Collectors.groupingBy(p -> p.protocol, Collectors.counting()));
            w.write("Protocol count: " + protoCount + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
