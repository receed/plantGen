import com.jogamp.opengl.GL2;

import java.util.ArrayList;

public class Leaf {
    double light = 0;
    ArrayList<Joint> joints = new ArrayList<>();
    Plant plant;

    Leaf(Plant plant) {
        this.plant = plant;
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
