package bp.roadnetworkpartitioning;

import java.util.HashMap;

public interface IPartitioning {

    public GraphPartition divide();

    public void setParameters(HashMap<String, String> parameters);

    public void setGraph(Graph graph);

    public String getName();

    public String getDescription();
}
