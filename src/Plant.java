import java.util.ArrayList;

public class Plant {
    Joint root = new Joint();
    ArrayList<Leaf> leaves = new ArrayList<>();
    double water = 0;

    double getLight() {
        double sum = 0;
        for (Leaf leaf : leaves)
            sum += leaf.light;
        return sum;
    }

}
