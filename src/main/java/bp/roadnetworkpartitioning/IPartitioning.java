package bp.roadnetworkpartitioning;

import java.util.HashMap;

public interface IPartitioning {

    public GraphPartition divide();

    public void setParameters(HashMap<String, String> parameters);
}
