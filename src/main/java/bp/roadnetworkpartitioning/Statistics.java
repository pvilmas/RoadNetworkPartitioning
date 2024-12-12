package bp.roadnetworkpartitioning;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Statistics {
    private int numberOfRounds = 1;
    private boolean isAverageCalculated = false;
    private final Map<String, List<Long>> times = new HashMap<>();
    private final Map<String, List<Double>> deviations = new HashMap<>();
    private final Map<String, List<Integer>> numberOfCutEdges = new HashMap<>();
    private final Map<String, List<Double>> minNumberOfNeighbours = new HashMap<>();
    private final Map<String, List<Double>> maxNumberOfNeighbours = new HashMap<>();
    private final Map<String, List<Double>> averageNumberOfNeighbours = new HashMap<>();
    private final List<String> columnNames = new ArrayList<>();

    public Statistics() {
        columnNames.add("Algorithm Name");
        columnNames.add("Time [ms]");
        columnNames.add("Relative Standard Deviation [%]");
        columnNames.add("Number of Cut Edges");
        columnNames.add("Minimal Number of Neighbours");
        columnNames.add("Maximal Number of Neighbours");
        columnNames.add("Average Number of Neighbours");
    }

    void addTime(String algorithmName, long time) {
        times.putIfAbsent(algorithmName, new ArrayList<>(12));
        times.get(algorithmName).add(time);
    }

    void addDeviation(String algorithmName, double deviation) {
        deviations.putIfAbsent(algorithmName, new ArrayList<>(12));
        deviations.get(algorithmName).add(deviation);
    }

    void addNumberOfCutEdges(String algorithmName, int numberOfCutEdge) {
        numberOfCutEdges.putIfAbsent(algorithmName, new ArrayList<>(12));
        numberOfCutEdges.get(algorithmName).add(numberOfCutEdge);
    }

    void addMinNumberOfNeighbours(String algorithmName, int minNumberOfNeighbour) {
        minNumberOfNeighbours.putIfAbsent(algorithmName, new ArrayList<>(12));
        minNumberOfNeighbours.get(algorithmName).add((double) minNumberOfNeighbour);
    }

    void addMaxNumberOfNeighbours(String algorithmName, int maxNumberOfNeighbour) {
        maxNumberOfNeighbours.putIfAbsent(algorithmName, new ArrayList<>(12));
        maxNumberOfNeighbours.get(algorithmName).add((double) maxNumberOfNeighbour);
    }

    void addAverageNumberOfNeighbours(String algorithmName, double averageNumberOfNeighbour) {
        averageNumberOfNeighbours.putIfAbsent(algorithmName, new ArrayList<>(12));
        averageNumberOfNeighbours.get(algorithmName).add(averageNumberOfNeighbour);
    }

    void calculateAverage() {
        if (isAverageCalculated) {
            return;
        }
        isAverageCalculated = true;
        int n = numberOfRounds - 2;
        for (Map.Entry<String, List<Long>> timeEntry : this.times.entrySet()) {
            List<Long> times = timeEntry.getValue();
            prepareList(times);
            long totalTime = 0;
            for (Long time: times) {
                totalTime += time;
            }
            timeEntry.setValue(new ArrayList<>(1));
            timeEntry.getValue().add(totalTime/n);
        }
        for (Map.Entry<String, List<Double>> deviationEntry : this.deviations.entrySet()) {
            List<Double> deviations = deviationEntry.getValue();
            prepareList(deviations);
            double totalDeviation = 0;
            for (Double deviation: deviations) {
                totalDeviation += deviation;
            }
            deviationEntry.setValue(new ArrayList<>(1));
            deviationEntry.getValue().add(totalDeviation/n);
        }
        for (Map.Entry<String, List<Integer>> numberOfCutEdgeEntry : this.numberOfCutEdges.entrySet()) {
            List<Integer> numberOfCutEdges = numberOfCutEdgeEntry.getValue();
            prepareList(numberOfCutEdges);
            int totalNumberOfCutEdges = 0;
            for (Integer numberOfCutEdge: numberOfCutEdges) {
                totalNumberOfCutEdges += numberOfCutEdge;
            }
            numberOfCutEdgeEntry.setValue(new ArrayList<>(1));
            numberOfCutEdgeEntry.getValue().add(totalNumberOfCutEdges/n);
        }
        for (Map.Entry<String, List<Double>> minNumberOfNeighbourEntry : this.minNumberOfNeighbours.entrySet()) {
            List<Double> minNumberOfNeighbours = minNumberOfNeighbourEntry.getValue();
            prepareList(minNumberOfNeighbours);
            double totalMinNumberOfNeighbours = 0.0;
            for (Double minNumberOfNeighbour: minNumberOfNeighbours) {
                totalMinNumberOfNeighbours += minNumberOfNeighbour;
            }
            minNumberOfNeighbourEntry.setValue(new ArrayList<>(1));
            minNumberOfNeighbourEntry.getValue().add(totalMinNumberOfNeighbours/n);
        }
        for (Map.Entry<String, List<Double>> maxNumberOfNeighbourEntry : this.maxNumberOfNeighbours.entrySet()) {
            List<Double> maxNumberOfNeighbours = maxNumberOfNeighbourEntry.getValue();
            prepareList(maxNumberOfNeighbours);
            double totalMaxNumberOfNeighbours = 0.0;
            for (Double maxNumberOfNeighbour: maxNumberOfNeighbours) {
                totalMaxNumberOfNeighbours += maxNumberOfNeighbour;
            }
            maxNumberOfNeighbourEntry.setValue(new ArrayList<>(1));
            maxNumberOfNeighbourEntry.getValue().add(totalMaxNumberOfNeighbours/n);
        }
        for (Map.Entry<String, List<Double>> averageNumberOfNeighbourEntry : this.averageNumberOfNeighbours.entrySet()) {
            List<Double> averageNumberOfNeighbours = averageNumberOfNeighbourEntry.getValue();
            prepareList(averageNumberOfNeighbours);
            double totalAverageNumberOfNeighbours = 0;
            for (Double averageNumberOfNeighbour: averageNumberOfNeighbours) {
                totalAverageNumberOfNeighbours += averageNumberOfNeighbour;
            }
            averageNumberOfNeighbourEntry.setValue(new ArrayList<>(1));
            averageNumberOfNeighbourEntry.getValue().add(totalAverageNumberOfNeighbours/n);
        }


    }
    public boolean recordResultsToCSV(int roundCount, int partCount, Graph graph, Map<String, APartitionAlgorithm> algorithms) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
        LocalDateTime now = LocalDateTime.now();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("results_" + graph.getVertices().size() + "-" + graph.getEdges().size()
                        + "_" + roundCount + "_" + partCount + "_" + dtf.format(now) + ".csv"))) {
            int i = 0;
            for (; i < getColumnNames().size() - 1; i++) {
                bw.write(getColumnNames().get(i) + ",");
            }
            bw.write(getColumnNames().get(i) + "\n");
            for (String algorithmName : algorithms.keySet()) {
                for (int j = 0; j < getTimes().get(algorithmName).size(); j++) {
                    bw.write(algorithmName + " " + j + ",");
                    bw.write(getTimes().get(algorithmName).get(j) + ",");
                    bw.write(getDeviations().get(algorithmName).get(j) + ",");
                    bw.write(getNumberOfCutEdges().get(algorithmName).get(j) + ",");
                    bw.write(getMinNumberOfNeighbours().get(algorithmName).get(j) + ",");
                    bw.write(getMaxNumberOfNeighbours().get(algorithmName).get(j) + ",");
                    bw.write(getAverageNumberOfNeighbours().get(algorithmName).get(j) + "\n");
                }
                bw.write("\n\n");
            }

            calculateAverage();
            for (String algorithmName : algorithms.keySet()) {
                bw.write(algorithmName + " - average,");
                bw.write(getTimes().get(algorithmName).get(0) + ",");
                bw.write(getDeviations().get(algorithmName).get(0) + ",");
                bw.write(getNumberOfCutEdges().get(algorithmName).get(0) + ",");
                bw.write(getMinNumberOfNeighbours().get(algorithmName).get(0) + ",");
                bw.write(getMaxNumberOfNeighbours().get(algorithmName).get(0) + ",");
                bw.write(getAverageNumberOfNeighbours().get(algorithmName).get(0) + "\n");
            }
            bw.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public void setNumberOfRounds(int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }

    public boolean isAverageCalculated() {
        return isAverageCalculated;
    }

    public void setAverageCalculated(boolean averageCalculated) {
        isAverageCalculated = averageCalculated;
    }

    public Map<String, List<Long>> getTimes() {
        return times;
    }

    public Map<String, List<Double>> getDeviations() {
        return deviations;
    }

    public Map<String, List<Integer>> getNumberOfCutEdges() {
        return numberOfCutEdges;
    }

    public Map<String, List<Double>> getMinNumberOfNeighbours() {
        return minNumberOfNeighbours;
    }

    public Map<String, List<Double>> getMaxNumberOfNeighbours() {
        return maxNumberOfNeighbours;
    }

    public Map<String, List<Double>> getAverageNumberOfNeighbours() {
        return averageNumberOfNeighbours;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    private void prepareList(List<?> attributes) {
        attributes.sort(null);
        attributes.remove(0);
        attributes.remove(times.size() - 1);
    }
}
