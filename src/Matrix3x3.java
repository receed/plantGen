class Matrix3x3 {
    Vector3 a, b, c;

    Matrix3x3(Vector3 a, Vector3 b, Vector3 c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    Matrix3x3(double ax, double ay, double az, double bx, double by, double bz, double cx, double cy, double cz) {
        this.a = new Vector3(ax, ay, az);
        this.b = new Vector3(bx, by, bz);
        this.c = new Vector3(cx, cy, cz);
    }

    Matrix3x3() {
        a = new Vector3();
        b = new Vector3();
        c = new Vector3();
    }

    static Matrix3x3 id() {
        return new Matrix3x3(
                1, 0, 0,
                0, 1, 0,
                0, 0, 1
        );
    }

    Matrix3x3 mul(double k) {
        return new Matrix3x3(a.mul(k), b.mul(k), c.mul(k));
    }

    Vector3 mul(Vector3 v) {
        return new Vector3(a.dot(v), b.dot(v), c.dot(v));
    }

    Matrix3x3 trans() {
        return new Matrix3x3(
                a.x, b.x, c.x,
                a.y, b.y, c.y,
                a.z, b.z, c.z
        );
    }

    Matrix3x3 mul(Matrix3x3 m) {
        Matrix3x3 m1 = m.trans();
        return new Matrix3x3(
                a.dot(m1.a), a.dot(m1.b), a.dot(m1.c),
                b.dot(m1.a), b.dot(m1.b), b.dot(m1.c),
                c.dot(m1.a), c.dot(m1.b), c.dot(m1.c));
    }

    static Matrix3x3 mRot(Vector3 v, double a) {
        //Vector3 v = nv.norm();
        Matrix3x3 s = new Matrix3x3(
                0, v.z, -v.y,
                -v.z, 0, v.x,
                v.y, -v.x, 0);
        return id().add(s.mul(Math.sin(a))).add(s.mul(s).mul(1 - Math.cos(a)));
    }

    Matrix3x3 add(Matrix3x3 m) {
        return new Matrix3x3(a.add(m.a), b.add(m.b), c.add(m.c));
    }

    Matrix3x3 sub(Matrix3x3 m) {
        return new Matrix3x3(a.sub(m.a), b.sub(m.b), c.sub(m.c));
    }
}