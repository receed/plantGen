import com.jogamp.opengl.GL2;

public class Seed {
    Vector3 pos, speed, acc, rotationAxis;
    double rotationAngle = 0, rotationSpeed = 360 / 1e3, size = 0.5;

    Seed(Vector3 pos, Vector3 speed, Vector3 acc) {
        this.pos = pos;
        this.speed = speed;
        this.acc = acc;
        rotationAxis = speed.cross(acc).norm();
    }

    Seed(Vector3 pos) {
        this(pos, new Vector3(), Vector3.up.mul(-Main.gravity));
    }

    void move() {
        pos = pos.add(speed.add(acc.mul(0.5 * Main.timeDelta)).mul(Main.timeDelta));
        speed = speed.add(acc.mul(Main.timeDelta));
        rotationAngle += rotationSpeed * Main.timeDelta;
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
