import com.jogamp.opengl.GL2;

import java.util.ArrayList;
import java.util.LinkedList;

public class Plant {
    Joint root;
    LinkedList<Leaf> leaves = new LinkedList<>();
    ArrayList<Joint> joints = new ArrayList<>();
    Model leafModel, rootModel;
    double water = 0;

    double getLight() {
        double sum = 0;
        for (Leaf leaf : leaves)
            sum += leaf.light;
        return sum;
    }

    Plant() {
        root = new Joint(this);
        leafModel = new Model(8, 8, 8);
        leafModel.randomInit();
        rootModel = new Model(8, 6, 4);
        rootModel.randomInit();
    }

    Plant(Seed seed) {
        root = new Joint(this, seed.pos);
        root.genLeaf(0.01, 0.05);
        root.genRoot(0.01, 0.05);
        if (seed.rootModel == null) {
            leafModel = new Model(8, 8, 8);
            leafModel.randomInit();
            rootModel = new Model(8, 6, 4);
            rootModel.randomInit();
        }
        else {
            rootModel = seed.rootModel;
            leafModel = seed.leafModel;
        }
    }

    void flow() {
        for (Joint joint : joints)
            joint.flow();
        for (Joint joint : joints)
            joint.pushDeltas();
    }

    void photosynthesis() {
        for (Joint joint : joints)
            for (Leaf leaf : joint.leaves)
                leaf.photosynthesis(1e9);
    }

    void randomGrow() {
        for (Joint joint : new ArrayList<>(joints))
            joint.randomGrow();
        root.grow();
    }
    void modelGrow() {
        for (Joint joint : new ArrayList<>(joints))
            joint.modelGrow();
        root.grow();
    }
    void absorb() {
        root.absorb();
    }
    void countLeafSquares() {
        for (Leaf leaf : leaves)
            leaf.countSquare();
    }
    void draw(GL2 gl) {
        for (Joint joint : joints)
            joint.draw(gl);
    }
    void countMoments() {
        for (int i = joints.size() - 1; i >= 0; i--)
            if (joints.get(i).pos.y > 0)
                joints.get(i).countMoments();
        for (Joint joint : joints)
            if (joint.criticalMass)
                for (Edge edge : joint.edges)
                    if (!edge.isRoot())
                        edge.to.criticalMass = true;
    }
}
