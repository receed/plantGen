import com.jogamp.opengl.GL2;

import java.util.ArrayList;

public class Leaf {
    long visited = 0;
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

    void dfs(long time, GL2 gl) {
        draw(gl);
        visited = time;
        for (Joint joint : joints)
            if (joint.visited < time)
                joint.dfs(time, gl);
    }
    void grow(double k, Vector3 v, Joint parent) {
        for (Joint joint : joints)
            if (joint != parent)
                joint.grow(1, joint.pos.sub(parent.pos).mul(k - 1), this);
    }
    void connect(Joint joint) {
        joints.add(joint);
        joint.leaves.add(this);
    }
}
