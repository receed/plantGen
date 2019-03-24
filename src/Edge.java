public class Edge {
    Joint to;
    private double width;
    double growCost;
    static double glucosePerRoot = 1;

    Edge(Joint to, double width) {
        this.to = to;
        this.width = width;
        if (isRoot())
            growCost = square() * glucosePerRoot;
    }
    double square() {
        return Math.PI * Math.pow(width, 2) / 4;
    }
    double getWidth() {
        return width;
    }
    void setWidth(double width) {
        this.width = width;
        if (isRoot())
            growCost = square() * glucosePerRoot;
    }


    boolean isRoot() {
        return to.pos.y < 0;
    }
}
