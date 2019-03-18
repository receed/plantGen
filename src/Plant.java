import java.util.ArrayList;
import java.util.LinkedList;

public class Plant {
    Joint root;
    LinkedList<Leaf> leaves = new LinkedList<>();
    double water = 0;

    double getLight() {
        double sum = 0;
        for (Leaf leaf : leaves)
            sum += leaf.light;
        return sum;
    }

    Plant() {
        root = new Joint();
    }

    Plant(Seed seed) {
        System.out.println(seed.pos);
        root = new Joint(seed.pos);
        root.genLeaves(1, 0, 0, this, Main.random);
        root.genRoots(1, 0, 0, Main.random);
    }
}
