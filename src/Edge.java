public class Edge {
    Joint to;
    double width;

    Edge(Joint to, double width) {
        this.to = to;
        this.width = width;
    }
    double square() {
        return Math.PI * Math.pow(width, 2) / 4;
    }

    boolean isRoot() {
        return to.pos.y < 0;
    }
}
