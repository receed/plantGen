import java.util.Random;

class Vector2 {
    public static double eps = 1e-8;
    public double x, y;
    Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }
    Vector2() {
        this(0, 0);
    }
    Vector2(Vector3 v) {
        x = v.x;
        y = v.z;
    }
    Vector2(Random random, double maxX, double maxY) {
        x = random.nextDouble() * maxX;
        y = random.nextDouble() * maxY;
    }
    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }
    double len() {
        return Math.sqrt(x * x + y * y);
    }
    Vector2 norm() {
        double l = len();
        if (l < 1e-15)
            return this;
        return mul(1 / l);
    }
    Vector2 add(Vector2 v) {
        return new Vector2(x + v.x, y + v.y);
    }
    Vector2 sub(Vector2 v) {
        return new Vector2(x - v.x, y - v.y);
    }
    Vector2 mul(double k) {
        return new Vector2(x * k, y * k);
    }
    double dot(Vector2 v) {
        return x * v.x + y * v.y;
    }
    double cross(Vector2 v) {
        return x * v.y - y * v.x;
    }
    double distTo(Vector2 v) {
        return sub(v).len();
    }
    double distToLine(Vector2 v1, Vector2 v2) {
        return sub(v1).cross(sub(v2)) / v1.distTo(v2);
    }
    double distToSegment(Vector2 v1, Vector2 v2) {
        if (v2.sub(v1).dot(sub(v1)) < 0)
            return distTo(v1);
        if (v1.sub(v2).dot(sub(v2)) < 0)
            return distTo(v2);
        return distToLine(v1, v2);
    }
    double cross(Vector2 a, Vector2 b) {
        return a.sub(this).cross(b.sub(this));
    }
    boolean inside(Vector2 a, Vector2 b, Vector2 c) {
        if (a.cross(b, c) < 0)
            return inside(a, c, b);
        return a.cross(b, this) > 0 && b.cross(c, this) > 0 && c.cross(a, this) > 0;
    }
}