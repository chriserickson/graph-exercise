package chriserickson.graphexercise;

import java.util.Objects;

public final class VertexId<VertexIdType> {
    public final VertexIdType id;

    private VertexId(VertexIdType id) {
        assert Objects.nonNull(id);

        this.id = id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VertexId && ((VertexId)obj).id.equals(id);
    }

    @Override
    public String toString() {
        return "Vertex: " + id.toString();
    }

    public static VertexId fromId(long id) {
        return new VertexId(id);
    }
}
