import com.jogamp.opengl.GL2;

import java.util.ArrayList;
import java.util.LinkedList;

public class Joint extends Clickable {
    double absorbLength = 0.5, absorbRate = 0.1;
    Vector3 pos, growth;
    long visited = 0;
    double water = 0, glucose = 0, waterDelta = 0, glucoseDelta = 0, volume = 0.001, youngModulus = 2;
    double totalGrowCost = 0, subtreeMass = 0, subtreeMoment = 0, maxMomentFactor = 3;
    boolean criticalMass = false, deleted = false;

    LinkedList<Leaf> leaves = new LinkedList<>();
    ArrayList<Edge> edges = new ArrayList<>();
    Plant plant;
    Edge parentEdge;


    Joint(Plant plant) {
        this(plant, new Vector3());
    }
    Joint(Plant plant, Vector3 v) {
        this.plant = plant;
        plant.joints.add(this);
        pos = new Vector3(v);
        growth = new Vector3();
    }
    Edge addEdge(Joint to, double width) {
        Edge edge = new Edge(to, width);
        edges.add(edge);
        to.parentEdge = new Edge(this, width);
        return edge;
    }
    Joint parent() {
        return parentEdge == null ? null : parentEdge.to;
    }
    void drawEdge(Edge edge, GL2 gl) {
        gl.glColor3d(1, 0, 0);
        gl.glPushMatrix();
        Vector3 v = edge.to.pos.sub(pos), axes = Vector3.up.cross(v);
        gl.glTranslated(pos.x, pos.y, pos.z);
        gl.glRotated(Math.toDegrees(v.angle(Vector3.up)), axes.x, axes.y, axes.z);
        gl.glScaled(edge.width, v.len(), edge.width);
        gl.glTranslated(0, 0.5, 0);
        Main.cylinder(gl);
        gl.glPopMatrix();
    }
    void drawSelected(GL2 gl) {
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, new float[] {1, 0, 0, 1}, 0);
        gl.glPushMatrix();
        gl.glTranslated(pos.x, pos.y, pos.z);
        gl.glScaled(0.05, 0.05, 0.05);
        gl.glColor3d(1, 0, 0);
        Main.sphere(gl, 10);
        gl.glPopMatrix();
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, new float[] {0, 0, 0, 1}, 0);
    }
    void draw(GL2 gl) {
        if (selected)
            drawSelected(gl);
        for (Leaf leaf : leaves)
            leaf.draw(gl);
        for (Edge edge : edges)
            if (edge.isRoot())
                drawEdge(edge, gl);
    }
    void grow() {
        pos = pos.add(growth);
        for (Edge edge : edges) {
            edge.to.growth = edge.to.growth.add(growth);
            edge.to.grow();
        }
        growth = new Vector3();
    }
    void grow(double len) {
        if (parentEdge != null)
            growth = growth.add(pos.sub(parentEdge.to.pos).norm(len));
    }
    Leaf addLeaf(Edge edge1, Edge edge2) {
        Leaf leaf = new Leaf(plant);
        leaves.add(leaf);
        leaf.joint = this;
        leaf.edges.add(edge1);
        leaf.edges.add(edge2);
        leaf.countSquare();
        return leaf;
    }
    Leaf genLeaf(double minLength, double maxLength) {
        Vector3 a = Vector3.random(minLength, maxLength, Main.random);
        Vector3 b = Vector3.random(minLength, maxLength, Main.random);
        if (a.y < 0)
            a.y = -a.y;
        if (b.y < 0)
            b.y = -b.y;
        Joint joint1 = new Joint(plant, pos.add(a)), joint2 = new Joint(plant, pos.add(b));
        return addLeaf(addEdge(joint1, 0.01), addEdge(joint2, 0.01));
    }
    Leaf genLeaf() {
        return genLeaf(0.1, 0.5);
    }
    Leaf modelGenLeaf() {
        Vector3 par = parentEdge == null ? new Vector3() : pos.sub(parentEdge.to.pos);
        double[] plan = plant.leafModel.apply(pos.x, pos.y, pos.z, par.x, par.y, par.z, water, glucose);
        Vector3 a = new Vector3(plan[0], Math.abs(plan[1]), plan[2]).norm(0.01 + 0.04 * Model.logistic(plan[3]));
        Vector3 b = new Vector3(plan[4], Math.abs(plan[5]), plan[6]).norm(0.01 + 0.04 * Model.logistic(plan[7]));
        Joint joint1 = new Joint(plant, pos.add(a)), joint2 = new Joint(plant, pos.add(b));
        return addLeaf(addEdge(joint1, 0.01), addEdge(joint2, 0.01));
    }
    void genLeaves(double prob, double probFactor1, double probFactor2) {
        double nprob = prob;
        while (Main.random.nextDouble() < nprob) {
            genLeaf();
            nprob *= probFactor1;
        }
        for (Edge edge : edges)
            if (!edge.isRoot())
                edge.to.genLeaves(prob * probFactor2, probFactor1, probFactor2);
    }
    void genLeaves(double prob) {
        genLeaves(prob, 0.97, 0.25);
    }
    Edge genRoot(double minLength, double maxLength) {
        Vector3 a = Vector3.random(minLength, maxLength, Main.random);
        if (a.y > 0)
            a.y = -a.y;
        return addEdge(new Joint(plant, pos.add(a)), 0.01);
    }
    Edge genRoot() {
        return genRoot(0.1, 0.5);
    }
    Edge modelGenRoot() {
        Vector3 par = parentEdge == null ? new Vector3() : pos.sub(parentEdge.to.pos);
        double[] plan = plant.rootModel.apply(pos.x, pos.y, pos.z, par.x, par.y, par.z, water, glucose);
        Vector3 a = new Vector3(plan[0], -Math.abs(plan[1]), plan[2]).norm(0.01 + 0.04 * Model.logistic(plan[3]));
        return addEdge(new Joint(plant, pos.add(a)), 0.01);
    }
    void genRoots(double prob, double probFactor1, double probFactor2) {
        double nprob = prob;
        while (Main.random.nextDouble() < nprob) {
            genRoot();
            nprob *= probFactor1;
        }
        for (Edge edge : edges)
            if (edge.isRoot())
                edge.to.genRoots(prob * probFactor2, probFactor1, probFactor2);
    }
    void genRoots(double prob) {
        genRoots(prob, 0.99, 0.8);
    }
    void absorb(Edge edge) {
        if (!edge.isRoot())
            return;
        Vector3 v = edge.to.pos.sub(pos);
        double l = v.len();
        for (double d = 0; d < l; d += absorbLength) {
            Vector3 absorbPos = pos.add(v.mul(d / l));
            double length = Math.min(absorbLength, l - d);
            int x = (int) Math.round((absorbPos.x + Main.waterSize / 2) / Main.waterSize * Main.waterMapSize),
                    y = (int) Math.round((absorbPos.z + Main.waterSize / 2) / Main.waterSize * Main.waterMapSize),
                    z = (int) Math.round(-absorbPos.y / Main.waterDepth * Main.waterMapDepth);
            if (Main.insideWaterMap(x, y, z)) {
                double absorbed = Math.min(Math.PI * edge.width * length * absorbRate * Main.timeStep, Main.waterMap[x][y][z] * 0.6);
                water += absorbed;
                Main.waterMap[x][y][z] -= absorbed;
                Main.absorbed[x][y][z] = true;
            }
        }
    }
    void absorb() {
        for (Edge edge : edges) {
            if (edge.isRoot()) {
                absorb(edge);
                edge.to.absorb();
            }
        }
        plant.water += water;
    }

    void genSeed() {
        Main.seeds.add(new Seed(new Vector3(pos), Vector3.random(1, 2, Main.random).reflectUp(),
                Vector3.up.mul(-0.2), plant));
    }
    @Override
    double distByRay(Vector3 from, Vector3 dir) {
        Vector3 v = pos.sub(from);
        if (v.angle(dir) > Math.toRadians(1.2))
            return Double.POSITIVE_INFINITY;
        return v.len() - 0.03;
    }
    double pressure() {
        return Math.max(water - volume, 0) / volume * youngModulus;
    }
    void flow() {
        double p = pressure();
        for (Edge edge : edges) {
            double p1 = edge.to.pressure();
            double squaredSpeed = ((p - p1) / Main.waterDensity + (pos.y - edge.to.pos.y) * Main.gravity) * 2;
            double flowed = Math.sqrt(Math.abs(squaredSpeed)) * Math.signum(squaredSpeed) * Main.timeStep * edge.square();
            double glucoseFlowed = 0;
            // as if water flowed to edge.to, back and again to edge.to, mixing glucose every time
            flowed = Math.max(Math.min(flowed, water * 0.8), -edge.to.water * 0.8);
            if (Math.abs(flowed) < Main.eps)
                continue;
            if (flowed > 0)
                glucoseFlowed = flowed * (2 * glucose / water - edge.to.glucose / (edge.to.water + flowed));
            else if (flowed < 0)
                glucoseFlowed = flowed * (glucose / (water - flowed) - 2 * edge.to.glucose / edge.to.water);
//            assert Double.isFinite(glucoseFlowed);
            waterDelta -= flowed;
            edge.to.waterDelta += flowed;
            glucoseFlowed = Math.max(Math.min(glucoseFlowed, glucose), -edge.to.glucose);
            glucoseDelta -= glucoseFlowed;
            edge.to.glucoseDelta += glucoseFlowed;
        }
    }
    void pushDeltas() {
        water = Math.max(0, water + waterDelta);
        glucose = Math.max(0, glucose + glucoseDelta);
        waterDelta = 0;
        glucoseDelta = 0;
    }
    @Override
    public String toString() {
        return String.format("Joint\nWater: %f\nGlucose: %f\nVolume: %f\n", water, glucose, volume);
    }
    String[] info() {
        return new String[] {"Joint",
                "Position: " + pos,
                String.format("Water: %f", water),
                String.format("Glucose: %f", glucose),
                String.format("Volume: %f", volume)};
    }
    void countGrowCosts() {
        totalGrowCost = 0;
        for (Edge edge : edges)
            edge.growCost = edge.isRoot() ? edge.square() * Edge.glucosePerRoot : 0;
        for (Leaf leaf : leaves)
            for (int i = 0; i < leaf.edges.size() - 1; i++) {
                double s = pos.square(leaf.edges.get(i).to.pos, leaf.edges.get(i + 1).to.pos);
                leaf.edges.get(i).growCost += s / pos.dist(leaf.edges.get(i).to.pos) * Leaf.glucosePerSquare;
                leaf.edges.get(i + 1).growCost += s / pos.dist(leaf.edges.get(i + 1).to.pos) * Leaf.glucosePerSquare;
            }
        for (Edge edge : edges)
            totalGrowCost += edge.growCost;
    }
    double edgeCost(Edge edge) {
        return edge.to.pos.sub(pos).len() * edge.square() * Edge.glucosePerRoot;
    }
    void randomGrow() {
        countGrowCosts();
        assert Double.isFinite(glucose);
        assert Double.isFinite(totalGrowCost);
        if (pos.y > 0 && glucose > Seed.minCost && Main.random.nextDouble() < Main.timeStep * 100) {
            genSeed();
            glucose -= Seed.minCost;
        }
        else if (Main.random.nextDouble() < glucose / (glucose + totalGrowCost * 0.2) * Main.timeStep) {
            if (this == plant.root && Main.random.nextDouble() < 0.5 || this != plant.root && pos.y < 0) {
                if (glucose > Edge.minCost)
                    glucose -= edgeCost(genRoot(0.01, 0.05));
            }
            else if (glucose > Leaf.minCost)
                glucose -= genLeaf(0.01, 0.05).square * Leaf.glucosePerSquare;
        }
        else if (!edges.isEmpty()) {
            Edge edge = edges.get(Main.random.nextInt(edges.size()));
            if (Main.random.nextDouble() < glucose / (glucose + edge.growCost * 0.05) * Main.timeStep) {
                double len = Math.min(glucose / 2 / edge.growCost, 0.5);
                glucose -= len * edge.growCost;
                edge.to.grow(len);
            }
        }
        assert Double.isFinite(glucose);
    }

    void modelGrow() {
        countGrowCosts();
        if (pos.y > 0 && glucose > Seed.minCost && Main.random.nextDouble() < Main.timeStep * 100) {
            genSeed();
            glucose -= Seed.minCost;
        }
        else if (!criticalMass && Main.random.nextDouble() < glucose / (glucose + totalGrowCost * 0.2) * Main.timeStep) {
            if (this == plant.root && Main.random.nextDouble() < 0.5 || this != plant.root && pos.y < 0) {
                if (glucose > Edge.minCost)
                    glucose -= edgeCost(modelGenRoot());
            }
            else if (glucose > Leaf.minCost)
                glucose -= modelGenLeaf().square * Leaf.glucosePerSquare;
        }
        else if (!criticalMass && !edges.isEmpty()) {
            Edge edge = edges.get(Main.random.nextInt(edges.size()));
            if (Main.random.nextDouble() < glucose / (glucose + edge.growCost * 0.03) * Main.timeStep) {
                double len = Math.min(glucose / 2 / edge.growCost, 0.5);
                glucose -= len * edge.growCost;
                edge.to.grow(len);
            }
        }
    }
    void countMoments() {
        subtreeMoment = 0;
        subtreeMass = 0;
        criticalMass = false;
        for (Edge edge : edges)
            if (!edge.isRoot()) {
                subtreeMoment += edge.to.subtreeMoment + edge.to.subtreeMass * pos.dist(edge.to.pos);
                subtreeMass += edge.to.subtreeMass;
            }
        double effectiveSquare = 0;
        for (Leaf leaf : leaves) {
            subtreeMass += leaf.square * Leaf.glucosePerSquare;
            effectiveSquare += leaf.square;
        }
        double maxMoment = Math.pow(effectiveSquare, 2) * maxMomentFactor;
        if (subtreeMoment > maxMoment)
            criticalMass = true;
    }
}