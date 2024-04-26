package bp.roadnetworkpartitioning;

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
    private final Map<String, List<Integer>> minNumberOfNeighbours = new HashMap<>();
    private final Map<String, List<Integer>> maxNumberOfNeighbours = new HashMap<>();
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

    private void addTime(String algorithmName, long time) {
        times.putIfAbsent(algorithmName, new ArrayList<>(12));
        times.get(algorithmName).add(time);
    }

    private void addDeviation(String algorithmName, double deviation) {
        deviations.putIfAbsent(algorithmName, new ArrayList<>(12));
        deviations.get(algorithmName).add(deviation);
    }

    private void addNumberOfCutEdges(String algorithmName, int numberOfCutEdge) {
        numberOfCutEdges.putIfAbsent(algorithmName, new ArrayList<>(12));
        numberOfCutEdges.get(algorithmName).add(numberOfCutEdge);
    }

    private void addMinNumberOfNeighbours(String algorithmName, int minNumberOfNeighbour) {
        minNumberOfNeighbours.putIfAbsent(algorithmName, new ArrayList<>(12));
        minNumberOfNeighbours.get(algorithmName).add(minNumberOfNeighbour);
    }

    private void addMaxNumberOfNeighbours(String algorithmName, int maxNumberOfNeighbour) {
        maxNumberOfNeighbours.putIfAbsent(algorithmName, new ArrayList<>(12));
        maxNumberOfNeighbours.get(algorithmName).add(maxNumberOfNeighbour);
    }

    private void addAverageNumberOfNeighbours(String algorithmName, double averageNumberOfNeighbour) {
        averageNumberOfNeighbours.putIfAbsent(algorithmName, new ArrayList<>(12));
        averageNumberOfNeighbours.get(algorithmName).add(averageNumberOfNeighbour);
    }

    private void calculateAverage() {
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
        for (Map.Entry<String, List<Integer>> minNumberOfNeighbourEntry : this.minNumberOfNeighbours.entrySet()) {
            List<Integer> minNumberOfNeighbours = minNumberOfNeighbourEntry.getValue();
            prepareList(minNumberOfNeighbours);
            int totalMinNumberOfNeighbours = 0;
            for (Integer minNumberOfNeighbour: minNumberOfNeighbours) {
                totalMinNumberOfNeighbours += minNumberOfNeighbour;
            }
            minNumberOfNeighbourEntry.setValue(new ArrayList<>(1));
            minNumberOfNeighbourEntry.getValue().add(totalMinNumberOfNeighbours/n);
        }
        for (Map.Entry<String, List<Integer>> maxNumberOfNeighbourEntry : this.maxNumberOfNeighbours.entrySet()) {
            List<Integer> maxNumberOfNeighbours = maxNumberOfNeighbourEntry.getValue();
            prepareList(maxNumberOfNeighbours);
            int totalMaxNumberOfNeighbours = 0;
            for (Integer maxNumberOfNeighbour: maxNumberOfNeighbours) {
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

    private void prepareList(List attributes) {
        attributes.sort(null);
        attributes.remove(0);
        attributes.remove(times.size() - 1);
    }
}
