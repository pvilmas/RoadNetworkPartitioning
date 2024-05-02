package bp.roadnetworkpartitioning;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * This class instances represents statistics information
 * about one graph division by one partitioning algorithm.
 * @author Lucie Roy
 * @version 27-03-2024
 */
public class Data {
    /** Name of the partitioning algorithm. */
    private final SimpleStringProperty algorithmName = new SimpleStringProperty("");
    /**  Time of division. */
    private final SimpleLongProperty algorithmTime = new SimpleLongProperty(0);
    /**  Relative standard deviation from ideal part. */
    private final SimpleDoubleProperty algorithmDeviation = new SimpleDoubleProperty(0);
    /** Total number of cut edges. */
    private final SimpleIntegerProperty algorithmNumberOfCutEdges = new SimpleIntegerProperty(0);
    /**  Minimal number of part neighbours. */
    private final SimpleIntegerProperty algorithmMinNumberOfNeighbours = new SimpleIntegerProperty(0);
    /**  Maximal number of part neighbours. */
    private final SimpleIntegerProperty algorithmMaxNumberOfNeighbours = new SimpleIntegerProperty(0);
    /**  Average number of part neighbours. */
    private final SimpleDoubleProperty algorithmAverageNumberOfNeighbours = new SimpleDoubleProperty(0);

    /**
     * Constructor of class Data with all possible parameters.
     * @param algorithmName             Name of the partitioning algorithm.
     * @param algorithmTime             Time of division.
     * @param algorithmDeviation        Relative standard deviation from ideal part.
     * @param numberOfCutEdges          Total number of cut edges.
     * @param minNumberOfNeighbours     Minimal number of part neighbours.
     * @param maxNumberOfNeighbours     Maximal number of part neighbours.
     * @param averageNumberOfNeighbours Average number of part neighbours.
     */
    public Data(String algorithmName, long algorithmTime, double algorithmDeviation, int numberOfCutEdges,
                 int minNumberOfNeighbours, int maxNumberOfNeighbours, double averageNumberOfNeighbours){
        this.algorithmName.set(algorithmName);
        this.algorithmTime.set(algorithmTime);
        this.algorithmDeviation.set(algorithmDeviation);
        this.algorithmNumberOfCutEdges.set(numberOfCutEdges);
        this.algorithmMinNumberOfNeighbours.set(minNumberOfNeighbours);
        this.algorithmMaxNumberOfNeighbours.set(maxNumberOfNeighbours);
        this.algorithmAverageNumberOfNeighbours.set(averageNumberOfNeighbours);
    }

    /**
     * Gets algorithm name.
     * @return algorithm name.
     */
    public final String getAlgorithmName() {
        return algorithmName.get();
    }

    /**
     * Gets algorithm name property.
     * @return algorithm name property.
     */
    public final SimpleStringProperty algorithmNameProperty() {
        return algorithmName;
    }

    /**
     * Sets algorithm name to property.
     * @param algorithmName algorithm name.
     */
    public final void setAlgorithmName(String algorithmName) {
        this.algorithmName.set(algorithmName);
    }

    /**
     * Gets algorithm time.
     * @return algorithm time.
     */
    public final long getAlgorithmTime() {
        return algorithmTime.get();
    }

    /**
     * Gets algorithm time property.
     * @return algorithm time property.
     */
    public final SimpleLongProperty algorithmTimeProperty() {
        return algorithmTime;
    }

    /**
     * Sets algorithm time to property.
     * @param algorithmTime algorithm time.
     */
    public final void setAlgorithmTime(long algorithmTime) {
        this.algorithmTime.set(algorithmTime);
    }

    /**
     * Gets algorithm relative standard deviation.
     * @return algorithm relative standard deviation.
     */
    public final double getAlgorithmDeviation() {
        return algorithmDeviation.get();
    }

    /**
     * Gets algorithm relative standard deviation property.
     * @return algorithm relative standard deviation property.
     */
    public final SimpleDoubleProperty algorithmDeviationProperty() {
        return algorithmDeviation;
    }

    /**
     * Sets algorithm relative standard deviation  to property.
     * @param algorithmDeviation  relative standard deviation.
     */
    public final void setAlgorithmDeviation(double algorithmDeviation) {
        this.algorithmDeviation.set(algorithmDeviation);
    }

    /**
     * Gets algorithm number of cut edges.
     * @return algorithm number of cut edges.
     */
    public final int getAlgorithmNumberOfCutEdges() {
        return algorithmNumberOfCutEdges.get();
    }

    /**
     * Gets algorithm number of cut edges property.
     * @return algorithm number of cut edges property.
     */
    public final SimpleIntegerProperty algorithmNumberOfCutEdgesProperty() {
        return algorithmNumberOfCutEdges;
    }

    /**
     * Sets algorithm number of cut edges to property.
     * @param algorithmNumberOfCutEdges  algorithm number of cut edges.
     */
    public final void setAlgorithmNumberOfCutEdges(int algorithmNumberOfCutEdges) {
        this.algorithmNumberOfCutEdges.set(algorithmNumberOfCutEdges);
    }

    /**
     * Gets algorithm min. number of neighbours.
     * @return algorithm min. number of neighbours.
     */
    public final int getAlgorithmMinNumberOfNeighbours() {
        return algorithmMinNumberOfNeighbours.get();
    }

    /**
     * Gets algorithm min. number of neighbours property.
     * @return algorithm min. number of neighbours property.
     */
    public final SimpleIntegerProperty algorithmMinNumberOfNeighboursProperty() {
        return algorithmMinNumberOfNeighbours;
    }

    /**
     * Sets algorithm min. number of neighbours to property.
     * @param algorithmMinNumberOfNeighbours  algorithm min. number of neighbours.
     */
    public final void setAlgorithmMinNumberOfNeighbours(int algorithmMinNumberOfNeighbours) {
        this.algorithmMinNumberOfNeighbours.set(algorithmMinNumberOfNeighbours);
    }

    /**
     * Gets algorithm average number of neighbours.
     * @return algorithm average number of neighbours.
     */
    public final double getAlgorithmAverageNumberOfNeighbours() {
        return algorithmAverageNumberOfNeighbours.get();
    }

    /**
     * Gets algorithm average number of neighbours property.
     * @return algorithm average number of neighbours property.
     */
    public final SimpleDoubleProperty algorithmAverageNumberOfNeighboursProperty() {
        return algorithmAverageNumberOfNeighbours;
    }

    /**
     * Sets algorithm average number of neighbours to property.
     * @param algorithmAverageNumberOfNeighbours  algorithm average number of neighbours.
     */
    public final void setAlgorithmAverageNumberOfNeighbours(int algorithmAverageNumberOfNeighbours) {
        this.algorithmAverageNumberOfNeighbours.set(algorithmAverageNumberOfNeighbours);
    }

    /**
     * Gets algorithm max. number of neighbours.
     * @return algorithm max. number of neighbours.
     */
    public final int getAlgorithmMaxNumberOfNeighbours() {
        return algorithmMaxNumberOfNeighbours.get();
    }

    /**
     * Gets algorithm max. number of neighbours property.
     * @return algorithm max. number of neighbours property.
     */
    public final SimpleIntegerProperty algorithmMaxNumberOfNeighboursProperty() {
        return algorithmMaxNumberOfNeighbours;
    }

    /**
     * Sets algorithm max. number of neighbours property.
     * @param algorithmMaxNumberOfNeighbours  algorithm max. number of neighbours.
     */
    public final void setAlgorithmMaxNumberOfNeighbours(int algorithmMaxNumberOfNeighbours) {
        this.algorithmMaxNumberOfNeighbours.set(algorithmMaxNumberOfNeighbours);
    }
}
