package chriserickson.graphexercise;

import io.vavr.Function1;
import io.vavr.collection.*;

import java.util.Objects;

public final class DirectedGraph<VertexIdType> {
    public final Set<DirectedEdge<VertexIdType>> edges;

    private DirectedGraph(Set<DirectedEdge<VertexIdType>> edges) {
        assert Objects.nonNull(edges);

        this.edges = edges;
    }

    public DirectedGraph mapEdges(Function1<Set<DirectedEdge<VertexIdType>>, Set<DirectedEdge<VertexIdType>>> mapper) {
        return new DirectedGraph(mapper.apply(edges));
    }

    public DirectedGraph addEdge(DirectedEdge edge) {
        return new DirectedGraph(this.edges.add(edge));
    }

    public DirectedGraph addEdges(Iterable<DirectedEdge<VertexIdType>> edges) {
        return new DirectedGraph(this.edges.addAll(edges));
    }

    public DirectedGraph removeEdge(DirectedEdge edge) {
        return new DirectedGraph(this.edges.remove(edge));
    }

    public Set<DirectedEdge<VertexIdType>> getEdges() {
        return this.edges;
    }

    @Override
    public int hashCode() {
        return edges.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DirectedGraph &&
            ((DirectedGraph)obj).edges.equals(this.edges);
    }

    @Override
    public String toString() {
        return "DG: " +
            edges.take(25).foldRight("", (itr, acc) ->
                acc + (acc.isEmpty() ? "" : ",") + "(" + itr.from.id.toString() + "," + itr.to.id.toString() + ")") +
            (edges.size() > 25 ? "..." : "");
    }

    static public DirectedGraph empty() {
        return new DirectedGraph(HashSet.empty());
    }
    static public <VertexIdType> DirectedGraph<VertexIdType> ofEdges(Traversable<DirectedEdge<VertexIdType>> edges) {
        assert Objects.nonNull(edges);

        return new DirectedGraph<>(HashSet.ofAll(edges));
    }
}
