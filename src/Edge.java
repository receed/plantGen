public class Edge {
    Joint to;
    double width;

    Edge(Joint to, double width) {
        this.to = to;
        this.width = width;
    }

    boolean isRoot() {
        return to.pos.y < 0;
    }
}
