import com.jogamp.opengl.GL2;
import sun.font.TrueTypeFont;

import java.util.ArrayList;

public class Leaf extends Clickable {
    double light = 0, square = 0, glucoseSynthesized;
    static double evaporationRate = 0.1, waterPerGlucose = (12.0 + 16 * 2) / (12 + 2 + 16), lightPerGlucose;
    boolean lacksWater = false;
    Joint joint;
    ArrayList<Edge> edges = new ArrayList<>();
    Plant plant;

    Leaf(Plant plant) {
        this.plant = plant;
        plant.leaves.add(this);
    }

    void countSquare() {
        for (int i = 0; i + 1 < edges.size(); i++)
            square += joint.pos.square(edges.get(i).to.pos, edges.get(i + 1).to.pos);
    }

    void evaporate() {
        double evaporated = square * evaporationRate * (1 - Main.humidity) * Main.timeDelta;
        if (evaporated < joint.water) {
            lacksWater = true;
            joint.water = 0;
        }
        else {
            lacksWater = false;
            joint.water -= evaporated;
        }
    }

    void photosynthesis(double maxWater) {
        glucoseSynthesized = Math.min(Math.min(maxWater, joint.water) / waterPerGlucose, light / lightPerGlucose);
        joint.water -= glucoseSynthesized * waterPerGlucose;
        joint.glucose += glucoseSynthesized;
    }

    void draw(GL2 gl) {
        if (selected) {
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, new float[] {1, 0, 0, 1}, 0);
            gl.glColor3d(1, 0, 0);
        }
        else if (lacksWater)
            gl.glColor3d(0.59, 0.16, 0.11);
        else
            gl.glColor3d(0.25, 1, 0.1);
        Vector3 n = joint.pos.normal(edges.get(0).to.pos, edges.get(1).to.pos);
        gl.glNormal3d(n.x, n.y, n.z);
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        joint.pos.draw(gl);
        for (Edge edge : edges)
            edge.to.pos.draw(gl);
        gl.glEnd();
        if (selected)
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, new float[] {0, 0, 0, 1}, 0);
    }
    @Override
    public double distByRay(Vector3 from, Vector3 dir) {
        return from.distByRay(dir, joint.pos, edges.get(0).to.pos, edges.get(1).to.pos);
    }
    @Override
    public String toString() {
        return String.format("Leaf\nSquare: %.3f\nLight: %.3f\nGlucose produced: %.3f\n", square, light, glucoseSynthesized);
    }
    String[] info() {
        return new String[] {"Leaf",
                String.format("Square: %.3f", square),
                String.format("Light: %.2f", light),
                String.format("Glucose produced: %.2f", glucoseSynthesized)};
    }
    ArrayList<Vector3> getPoints() {
        ArrayList<Vector3> list = new ArrayList<>();
        list.add(joint.pos);
        for (Edge edge : edges)
            list.add(edge.to.pos);
        return list;
    }
}
