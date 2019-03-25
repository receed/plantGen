import java.util.ArrayList;
import java.util.LinkedList;

public class Plant {
    Joint root;
    LinkedList<Leaf> leaves = new LinkedList<>();
    LinkedList<Joint> joints = new LinkedList<>();
    double water = 0;

    double getLight() {
        double sum = 0;
        for (Leaf leaf : leaves)
            sum += leaf.light;
        return sum;
    }

    Plant() {
        root = new Joint(this);
    }

    Plant(Seed seed) {
        root = new Joint(this, seed.pos);
        root.genLeaves(1, 0, 0);
        root.genRoots(1, 0, 0);
    }

    void flow() {
        for (Joint joint : joints)
            joint.flow();
        for (Joint joint : joints)
            joint.pushDeltas();
    }
}
