package chriserickson.graphexercise;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Option;

public final class DirectedAcyclicGraph<VertexIdType> {
    public final DirectedGraph<VertexIdType> directedGraph;
    public final List<VertexId<VertexIdType>> topologicalOrder;

    private DirectedAcyclicGraph(
        DirectedGraph<VertexIdType> directedGraph,
        List<VertexId<VertexIdType>> topologicalOrder) {

        this.directedGraph = directedGraph;
        this.topologicalOrder = topologicalOrder;
    }

    public DirectedGraph<VertexIdType> getDirectedGraph() { return this.directedGraph; }

    public Set<DirectedEdge<VertexIdType>> getEdges() { return this.directedGraph.edges; }

    public List<VertexId<VertexIdType>> getTopologicalOrder() { return this.topologicalOrder; }

    @Override
    public int hashCode() {
        return directedGraph.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        // The topological sort itself can be different but the graphs themselves are equal
        return obj instanceof DirectedAcyclicGraph && ((DirectedAcyclicGraph)obj).directedGraph.equals(this.directedGraph);
    }

    @Override
    public String toString() {
        return "DAG: " +
            topologicalOrder.take(50).foldRight("", (itr, acc) -> acc + (acc.isEmpty() ? "" : ",") + itr.id.toString()) +
            (topologicalOrder.size() > 50 ? "..." : "");
    }

    static public <VertexIdType>
        DirectedAcyclicGraph<VertexIdType> of(DirectedGraph<VertexIdType> graph)
        throws GraphCycleException {
            List<VertexId<VertexIdType>> topologicalOrder = kahnTopologicalSort(graph);

            return new DirectedAcyclicGraph(graph, topologicalOrder);
        }

    // https://en.wikipedia.org/wiki/Topological_sorting#Kahn.27s_algorithm
    static <IdType>
        List<VertexId<IdType>> kahnTopologicalSort(final DirectedGraph<IdType> graph) throws GraphCycleException {
            final Tuple2<Set<VertexId<IdType>>, Set<VertexId<IdType>>> fromAndToVerticies =
                graph.edges
                    .foldRight(
                        Tuple.of(HashSet.<VertexId<IdType>>empty(), HashSet.<VertexId<IdType>>empty()),
                        (itr, acc) ->
                            acc.map(
                                (froms) -> froms.add(itr.from),
                                (tos) -> tos.add(itr.to)
                            )
                    );

            final Set<VertexId<IdType>> verticiesWithNoIncomingEdges =
                fromAndToVerticies._1.filter(i -> !fromAndToVerticies._2.contains(i));

            // Variable names are matched to the Wikipedia article.
            List<VertexId<IdType>> sortedVerticies = List.empty();
            Set<VertexId<IdType>> itrVerticiesWithoutIncomingEdges = verticiesWithNoIncomingEdges;
            DirectedGraph<IdType> itrGraph = graph;

            while (itrVerticiesWithoutIncomingEdges.nonEmpty()) {
                VertexId<IdType> n = itrVerticiesWithoutIncomingEdges.head();
                itrVerticiesWithoutIncomingEdges = itrVerticiesWithoutIncomingEdges.tail();
                sortedVerticies.append(n);

                Set<DirectedEdge<IdType>> edgesFromN = itrGraph.edges.filter(e -> e.from.equals(n));

                for (DirectedEdge<IdType> e : edgesFromN) {
                    VertexId<IdType> m = e.to;
                    itrGraph = itrGraph.removeEdge(e);
                    if (!itrGraph.edges.exists(i -> i.to.equals(m)))
                        itrVerticiesWithoutIncomingEdges.add(m);
                }
            }

            if (itrGraph.edges.nonEmpty())
                throw new GraphCycleException();
            else
                return sortedVerticies;
        }

    public static <IdType>
        Map<VertexId<IdType>, Long> computeMaximumPathLengthsToEachNode(final DirectedAcyclicGraph<IdType> graph) {
            return graph.topologicalOrder.foldRight(HashMap.empty(), (node, acc) ->
                graph
                    .getEdges()
                    .filter(e -> e.from.equals(node))
                    .foldRight(acc, (edge, innerAcc) ->
                        innerAcc.put(edge.to, innerAcc.getOrElse(edge.to, 0L) + 1)
                    )
            );
        }

    public static <IdType> Option<List<VertexId<IdType>>> longestPath(
        final DirectedAcyclicGraph<IdType> graph,
        final VertexId<IdType> vertex) {

        // Only grab the portion of the topological sort to the right of the node we are asking about.
        final List<VertexId<IdType>> verticiesToProcess =
            graph
                .topologicalOrder
                .reverse()
                .takeUntil(i -> i.equals(vertex));



        // Fold to the right from the reversed topological sort. The longest path from a terminal node is just itself.
        // Note, this is not deterministic, multiple longest paths may exist.
        Map<VertexId, List<VertexId<IdType>>> longestPathByFromNode = 
            verticiesToProcess
                .foldRight(HashMap.empty(), (node,  pathAccumulator) ->

                    // For all children of this node, look for the one with the
                    // longest path. Prepend this element to that list and store for this element.
                    pathAccumulator.put(
                        node,
                        pathAccumulator
                            .filterKeys(
                                graph.getEdges()
                                    .filter(e -> e.from.equals(node))
                                    .map(DirectedEdge::getTo)
                                    ::contains
                            )
                            .values()
                            .maxBy(List::length)
                            .map(l -> l.prepend(node))
                            .getOrElse(List.of(node))
                    )
                );

        return longestPathByFromNode.get(vertex);
    }
}
