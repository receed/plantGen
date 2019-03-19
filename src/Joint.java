import com.jogamp.opengl.GL2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Joint {
    double absorbLength = 0.5, absorbRate = 0.1;
    Vector3 pos;
    long visited = 0;
    double water = 0, glucose = 0;
    LinkedList<Leaf> leaves = new LinkedList<>();
    LinkedList<Edge> edges = new LinkedList<>();

    Joint() {
        pos = new Vector3();
    }
    Joint(Vector3 v) {
        pos = new Vector3(v);
    }
    void drawEdge(Edge edge, GL2 gl) {
        gl.glColor3d(1, 0, 0);
        gl.glPushMatrix();
        Vector3 v = edge.to.pos.sub(pos), axes = Vector3.up.cross(v);
        gl.glTranslated(pos.x, pos.y, pos.z);
        gl.glRotated(Math.toDegrees(v.getAngle(Vector3.up)), axes.x, axes.y, axes.z);
        gl.glScaled(edge.width, v.len(), edge.width);
        gl.glTranslated(0, 0.5, 0);
        Main.cylinder(gl);
        gl.glPopMatrix();
    }
    void dfs(long time, GL2 gl) {
        visited = time;
        for (Leaf leaf : leaves)
            leaf.draw(gl);
        for (Edge edge : edges)
            if (edge.to.visited < time) {
                if (edge.isRoot())
                    drawEdge(edge, gl);
                edge.to.dfs(time, gl);
            }
        if (pos.y > 0 && Main.random.nextDouble() < Main.timeDelta * 6e-6)
            genSeed();
    }
    void grow(Vector3 v) {
        pos = pos.add(v);
        for (Edge edge : edges)
            edge.to.grow(v);
    }
    void genLeaf(Plant plant, Random random) {
        Vector3 a = Vector3.random(0.1, 0.5, random);
        Vector3 b = Vector3.random(0.1, 0.5, random);
        if (a.y < 0)
            a.y = -a.y;
        if (b.y < 0)
            b.y = -b.y;
        Leaf leaf = new Leaf(plant);
        leaves.add(leaf);
        Joint joint1 = new Joint(pos.add(a.mul(5))), joint2 = new Joint(pos.add(b.mul(5)));
        leaf.joints.add(this);
        leaf.joints.add(joint1);
        leaf.joints.add(joint2);
        leaf.countSquare();
        plant.leaves.add(leaf);
        edges.add(new Edge(joint1, 0.008));
        edges.add(new Edge(joint2, 0.008));
    }
    void genLeaves(double prob, double probFactor1, double probFactor2, Plant plant, Random random) {
        double nprob = prob;
        while (random.nextDouble() < nprob) {
            genLeaf(plant, random);
            nprob *= probFactor1;
        }
        for (Edge edge : edges)
            if (!edge.isRoot())
                edge.to.genLeaves(prob * probFactor2, probFactor1, probFactor2, plant, random);
    }
    void genLeaves(double prob, Plant plant, Random random) {
        genLeaves(prob, 0.97, 0.25, plant, random);
    }
    void genRoots(double prob, double probFactor1, double probFactor2, Random random) {
        double nprob = prob;
        while (random.nextDouble() < nprob) {
            Vector3 a = Vector3.random(0.1, 0.5, random);
            if (a.y > 0)
                a.y = -a.y;
            edges.add(new Edge(new Joint(pos.add(a)), 0.008));
            nprob *= probFactor1;
        }
        for (Edge edge : edges)
            if (edge.isRoot())
                edge.to.genRoots(prob * probFactor2, probFactor1, probFactor2, random);
    }
    void genRoots(double prob, Random random) {
        genRoots(prob, 0.99, 0.8, random);
    }
    void absorb(Edge edge) {
        if (!edge.isRoot())
            return;
        Vector3 v = edge.to.pos.sub(pos);
        double l = v.len();
        for (double d = 0; d < l; d += absorbLength) {
            Vector3 absorbPos = pos.add(v.mul(d / l));
            double length = Math.min(absorbLength, l - d);
            int x = (int) Math.round(absorbPos.x), y = (int) Math.round(absorbPos.y), z = (int) Math.round(absorbPos.z);
            if (Main.insideWaterMap(x, y, z)) {
                double absorbed = Math.min(Math.PI * edge.width * length * absorbRate, Main.waterMap[x][y][z]);
                water += absorbed;
                Main.waterMap[x][y][z] -= absorbed;
            }
        }
    }
    void absorb(Plant plant) {
        for (Edge edge : edges) {
            if (edge.isRoot()) {
                absorb(edge);
                edge.to.absorb(plant);
            }
        }
        plant.water += water;
    }

    void genSeed() {
        Main.seeds.add(new Seed(new Vector3(pos), Vector3.random(1e-3, 2e-3, Main.random),
                Vector3.up.mul(-1e-7)));
    }
}