package bp.roadnetworkpartitioning;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class Data {
    /**  */
    private final SimpleStringProperty algorithmName = new SimpleStringProperty("");
    /**  */
    private final SimpleLongProperty algorithmTime = new SimpleLongProperty(0);
    /**  */
    private final SimpleDoubleProperty algorithmDeviation = new SimpleDoubleProperty(0);
    /**  */
    private final SimpleIntegerProperty algorithmNumberOfCutEdges = new SimpleIntegerProperty(0);
    /**  */
    private final SimpleIntegerProperty algorithmMinNumberOfNeighbours = new SimpleIntegerProperty(0);
    /**  */
    private final SimpleIntegerProperty algorithmMaxNumberOfNeighbours = new SimpleIntegerProperty(0);
    /**  */
    private final SimpleDoubleProperty algorithmAverageNumberOfNeighbours = new SimpleDoubleProperty(0);

    /**
     *
     * @param algorithmName
     * @param algorithmTime
     * @param algorithmDeviation
     * @param numberOfCutEdges
     * @param maxNumberOfNeighbours
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

    public final String getAlgorithmName() {
        return algorithmName.get();
    }

    public final SimpleStringProperty algorithmNameProperty() {
        return algorithmName;
    }

    public final void setAlgorithmName(String algorithmName) {
        this.algorithmName.set(algorithmName);
    }

    public final long getAlgorithmTime() {
        return algorithmTime.get();
    }

    public final SimpleLongProperty algorithmTimeProperty() {
        return algorithmTime;
    }

    public final void setAlgorithmTime(long algorithmTime) {
        this.algorithmTime.set(algorithmTime);
    }

    public final double getAlgorithmDeviation() {
        return algorithmDeviation.get();
    }

    public final SimpleDoubleProperty algorithmDeviationProperty() {
        return algorithmDeviation;
    }

    public final void setAlgorithmDeviation(double algorithmDeviation) {
        this.algorithmDeviation.set(algorithmDeviation);
    }

    public final int getAlgorithmNumberOfCutEdges() {
        return algorithmNumberOfCutEdges.get();
    }

    public final SimpleIntegerProperty algorithmNumberOfCutEdgesProperty() {
        return algorithmNumberOfCutEdges;
    }

    public final void setAlgorithmNumberOfCutEdges(int algorithmNumberOfCutEdges) {
        this.algorithmNumberOfCutEdges.set(algorithmNumberOfCutEdges);
    }

    public final int getAlgorithmMinNumberOfNeighbours() {
        return algorithmMinNumberOfNeighbours.get();
    }

    public final SimpleIntegerProperty algorithmMinNumberOfNeighboursProperty() {
        return algorithmMinNumberOfNeighbours;
    }

    public final void setAlgorithmMinNumberOfNeighbours(int algorithmMinNumberOfNeighbours) {
        this.algorithmMinNumberOfNeighbours.set(algorithmMinNumberOfNeighbours);
    }

    public final double getAlgorithmAverageNumberOfNeighbours() {
        return algorithmAverageNumberOfNeighbours.get();
    }

    public final SimpleDoubleProperty algorithmAverageNumberOfNeighboursProperty() {
        return algorithmAverageNumberOfNeighbours;
    }

    public final void setAlgorithmAverageNumberOfNeighbours(int algorithmAverageNumberOfNeighbours) {
        this.algorithmAverageNumberOfNeighbours.set(algorithmAverageNumberOfNeighbours);
    }

    public final int getAlgorithmMaxNumberOfNeighbours() {
        return algorithmMaxNumberOfNeighbours.get();
    }

    public final SimpleIntegerProperty algorithmMaxNumberOfNeighboursProperty() {
        return algorithmMaxNumberOfNeighbours;
    }

    public final void setAlgorithmMaxNumberOfNeighbours(int algorithmMaxNumberOfNeighbours) {
        this.algorithmMaxNumberOfNeighbours.set(algorithmMaxNumberOfNeighbours);
    }
}
