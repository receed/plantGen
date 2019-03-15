import com.jogamp.opengl.GL2;

import java.util.ArrayList;
import java.util.Random;

public class Joint {
    Vector3 pos;
    long visited = 0;
    ArrayList<Leaf> leaves = new ArrayList<>();
    ArrayList<Edge> edges = new ArrayList<>();

    Joint() {
        pos = new Vector3();
    }
    Joint(Vector3 v) {
        pos = new Vector3(v);
    }
    void dfs(long time, GL2 gl) {
        visited = time;
        for (Leaf leaf : leaves)
            if (leaf.visited < time)
                leaf.dfs(time, gl);
        for (Edge edge : edges)
            if (edge.to.visited < time) {
                gl.glColor3d(1, 0, 0);
                gl.glPushMatrix();
                Vector3 v = edge.to.pos.sub(pos), axes = Vector3.up.cross(v);
                gl.glTranslated(pos.x, pos.y, pos.z);
                gl.glRotated(Math.toDegrees(v.getAngle(Vector3.up)), axes.x, axes.y, axes.z);
                gl.glScaled(edge.width, v.len(), edge.width);
                gl.glTranslated(0, 0.5, 0);
                Main.cylinder(gl);
                gl.glPopMatrix();
                edge.to.dfs(time, gl);
            }
    }
    void grow(double k, Vector3 v, Leaf parent) {
        pos = pos.add(v);
        for (Leaf leaf : leaves)
            if (leaf != parent)
                leaf.grow(k, v, this);
    }
    void genLeaf(Plant plant, Random random) {
        Vector3 a = Vector3.random(0.1, 0.5, random);
        Vector3 b = Vector3.random(0.1, 0.5, random);
        if (a.y < 0)
            a.y = -a.y;
        if (b.y < 0)
            b.y = -b.y;
        Leaf leaf = new Leaf(plant);
        leaf.connect(this);
        leaf.connect(new Joint(pos.add(a.mul(5))));
        leaf.connect(new Joint(pos.add(b.mul(5))));
        plant.leaves.add(leaf);
    }
    void genLeaves(double prob, Leaf parent, Plant plant, Random random) {
        double nprob = prob;
        while (random.nextDouble() < nprob) {
            genLeaf(plant, random);
            nprob *= 0.99;
        }
        for (Leaf leaf : leaves)
            if (leaf != parent)
                for (Joint joint : leaf.joints)
                    if (joint != this)
                        joint.genLeaves(prob * 0.3, leaf, plant, random);
    }
    void genRoots(double prob, Random random) {
        double nprob = prob;
        while (random.nextDouble() < nprob) {
            Vector3 a = Vector3.random(0.1, 0.5, random);
            if (a.y > 0)
                a.y = -a.y;
            edges.add(new Edge(new Joint(pos.add(a)), 0.02));
            nprob *= 0.99;
        }
        for (Edge edge : edges)
            edge.to.genRoots(prob * 0.3, random);
    }
}
