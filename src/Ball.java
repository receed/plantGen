import com.jogamp.opengl.GL2;

public class Ball {
    Vector3 pos, speed;

    Ball(Vector3 pos) {
        this.pos = new Vector3(pos);
        speed = new Vector3();
    }

    Ball() {
        pos = new Vector3();
        speed = new Vector3();
    }

    void setSpeed(double x, double z) {
        speed.x = x;
        speed.z = z;
        speed.y = 0;
        if (speed.len() > 1e-6)
            speed = speed.norm();
    }
    void setSpeed(Vector3 v) {
        setSpeed(v.x, v.z);
    }

    void move(double t, GL2 gl) {
        pos = pos.add(speed.mul(t));
        Main.getHeight(pos);
        gl.glColor3d(255, 30, 0);
        gl.glPushMatrix();
        gl.glTranslated(pos.x, pos.y, pos.z);
        gl.glScaled(0.5, 0.5, 0.5);
        Main.sphere(gl, 20);
        gl.glPopMatrix();
    }
}
