package bp.roadnetworkpartitioning;

import java.util.List;

public class GraphPartition {
    private final List<Integer> vertices;
    private final List<Integer> edges;

    public GraphPartition(List<Integer> vertices, List<Integer> edges){
       this.vertices = vertices;
       this.edges = edges;
    }

    public void addVertex(int id, int part){
        vertices.add(id, part);
    }

    public void addEdge(int id, int part){
        edges.add(id, part);
    }

    public List<Integer> getVertices(){
        return  this.vertices;
    }

    public List<Integer> getEdges(){
        return this.edges;
    }
}
