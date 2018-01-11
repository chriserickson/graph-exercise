package chriserickson.graphexercise;

public class GraphCycleException extends Exception {
    public GraphCycleException() {
        super("Graph has at least one cycle");
    }
}
