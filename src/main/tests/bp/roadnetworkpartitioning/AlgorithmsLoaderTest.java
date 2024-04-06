package bp.roadnetworkpartitioning;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for @see AlgorithmsLoader
 */
class AlgorithmsLoaderTest {

    @Test
    void findAlgorithms() {
        Map<String, APartitionAlgorithm> algorithmsMap = AlgorithmsLoader.findAlgorithms();
        String[] actualAlgorithms = {"METIS", "Inertial Flow", "SParTSim"};
        Arrays.sort(actualAlgorithms);
        String[] algorithms = algorithmsMap.keySet().toArray(new String[0]);
        Arrays.sort(algorithms);
        assertArrayEquals(actualAlgorithms, algorithms);
    }
}