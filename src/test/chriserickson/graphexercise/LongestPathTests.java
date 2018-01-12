package chriserickson.graphexercise;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.Assert;
import org.junit.Test;

public class LongestPathTests {
    @Test
    public void sanityTest() {
        DirectedGraph graph = simpleDag;

        DirectedAcyclicGraph<String> dag;
        try {
            dag = DirectedAcyclicGraph.of(graph);
        } catch (GraphCycleException e) {
            Assert.fail("Failed to get DAG from simpleDag");
            return;
        }

        Option<List<VertexId<String>>> longestPathFromA = DirectedAcyclicGraph.longestPath(dag, VertexId.fromId("A"));

        Assert.assertTrue("Longest path from A was found", longestPathFromA.isDefined());
        Assert.assertEquals(longestPathFromA.get().map(VertexId::getId), List.of("A", "B", "C", "E"));
    }




    //          B - C
    //         /     \
    // A ---- D ----- E
    //         \
    //          F
    static DirectedGraph simpleDag = DirectedGraph.fromPaths(List.of(
        List.of("A", "B", "C", "E"),
        List.of("A", "D", "E"),
        List.of("D", "F")
    ));
}
