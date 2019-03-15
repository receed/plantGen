import com.jogamp.opengl.GL2;

import java.util.Random;

class Vector3 {
    public double x, y, z;
    static Vector3 up = new Vector3(0, 1, 0);

    Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    Vector3() {
        this(0, 0, 0);
    }
    Vector3(Vector3 v) {
        this(v.x, v.y, v.z);
    }
    Vector3(Vector2 v) {this(v.x, 0, v.y);}
    @Override
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", x, y, z);
    }
    double len() {
        return Math.sqrt(x * x + y * y + z * z);
    }
    Vector3 norm(double length) {
        double l = len();
        if (l < 1e-15)
            return this;
        return mul(length / l);
    }
    Vector3 norm() {
        return norm(1);
    }
    Vector3 add(Vector3 v) {
        return new Vector3(x + v.x, y + v.y, z + v.z);
    }
    Vector3 sub(Vector3 v) {
        return new Vector3(x - v.x, y - v.y, z - v.z);
    }
    Vector3 mul(double k) {
        return new Vector3(x * k, y * k, z * k);
    }
    double dot(Vector3 v) {
        return x * v.x + y * v.y + z * v.z;
    }
    Vector3 cross(Vector3 v) {
        return new Vector3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }
    double dist(Vector3 v) {
        return sub(v).len();
    }
    double getHeight(Vector3 a, Vector3 b, Vector3 c) {
        Vector3 v = b.sub(a).cross(c.sub(a));
        double d = a.dot(v);
        y = 0;
        y = (d - dot(v)) / v.y;
        return y;
    }
    void draw(GL2 gl) {
        gl.glVertex3d(x, y, z);
    }
    static Vector3 random(double minLength, double maxLength, Random random) {
        Vector3 v = new Vector3(random.nextDouble() - 0.5, random.nextDouble() - 0.5, random.nextDouble() - 0.5);
        double length = minLength + random.nextDouble() * (maxLength - minLength);
        return v.norm(length);
    }
    double getAngle(Vector3 v) {
        return Math.acos(dot(v) / len() / v.len());
    }
    Vector3 normal(Vector3 a, Vector3 b) {
        return a.sub(this).cross(b.sub(this)).norm();
    }
}