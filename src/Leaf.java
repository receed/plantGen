import com.jogamp.opengl.GL2;
import sun.font.TrueTypeFont;

import java.util.ArrayList;

public class Leaf {
    double light = 0, square = 0, evaporationRate = 0.1;
    boolean lacksWater = false;
    ArrayList<Joint> joints = new ArrayList<>();
    Plant plant;

    Leaf(Plant plant) {
        this.plant = plant;
    }

    void countSquare() {
        for (int i = 1; i + 1 < joints.size(); i++)
            square += joints.get(0).pos.square(joints.get(i).pos, joints.get(i + 1).pos);
    }

    void evaporate() {
        double evaporated = square * evaporationRate * (1 - Main.humidity) * Main.timeDelta;
        if (evaporated < joints.get(0).water) {
            lacksWater = true;
            joints.get(0).water = 0;
        }
        else {
            lacksWater = false;
            joints.get(0).water -= evaporated;
        }
    }

    void draw(GL2 gl) {
        gl.glColor3d(0.25, 1, 0.1);
        Vector3 n = joints.get(0).pos.normal(joints.get(1).pos, joints.get(2).pos);
        gl.glNormal3d(n.x, n.y, n.z);
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        for (Joint joint : joints)
            joint.pos.draw(gl);
        gl.glEnd();
    }
}
