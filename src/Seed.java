import com.jogamp.opengl.GL2;

public class Seed {
    Vector3 pos, speed, acc, rotationAxis;
    double rotationAngle = 0, rotationSpeed = 360, size = 0.5;
    static double minCost = Leaf.minCost + Edge.minCost;
    Model rootModel, leafModel;

    Seed(Vector3 pos, Vector3 speed, Vector3 acc) {
        this.pos = pos;
        this.speed = speed;
        this.acc = acc;
        rotationAxis = speed.cross(acc).norm();
    }

    Seed(Vector3 pos) {
        this(pos, new Vector3(), Vector3.up.mul(-Main.gravity));
    }

    Seed(Vector3 pos, Vector3 speed, Vector3 acc, Plant plant) {
        this(pos, speed, acc);
        rootModel = plant.rootModel;
        leafModel = plant.leafModel;
        rootModel.mutate();
        leafModel.mutate();
    }

    void move() {
        pos = pos.add(speed.add(acc.mul(0.5 * Main.timeStep)).mul(Main.timeStep));
        speed = speed.add(acc.mul(Main.timeStep));
        rotationAngle += rotationSpeed * Main.timeStep;
    }

    void draw(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslated(pos.x, pos.y, pos.z);
        gl.glRotated(rotationAngle, rotationAxis.x, rotationAxis.y, rotationAxis.z);
        gl.glScaled(size, size, size);
        Main.cube(gl);
        gl.glPopMatrix();
    }
}
