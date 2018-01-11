package chriserickson.graphexercise;

import io.vavr.collection.List;

import java.util.Objects;

public final class DirectedEdge<VertexIdType> {
    public final VertexId<VertexIdType> from;
    public final VertexId<VertexIdType> to;
    public final int hashCode;

    private DirectedEdge(VertexId from, VertexId to) {
        assert Objects.nonNull(from) && Objects.nonNull(to);

        this.from = from;
        this.to = to;
        this.hashCode = List.of(from, to).hashCode();
    }

    public VertexId<VertexIdType> getFrom() {
        return from;
    }

    public VertexId<VertexIdType> getTo() {
        return to;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof DirectedEdge &&
            ((DirectedEdge)obj).from.equals(this.from) &&
            ((DirectedEdge)obj).to.equals(this.to);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    static public <VertexIdType> DirectedEdge<VertexIdType> betweenVerticies(VertexId<VertexIdType> from, VertexId<VertexIdType> to) {
        return new DirectedEdge(from, to);
    }
}
