import com.jogamp.opengl.GL2;

public class Seed {
    Vector3 pos, speed, acc, rotationAxis;
    double rotationAngle = 0, rotationSpeed = 360, size = 0.08;
    static double minCost = Leaf.minCost + Edge.minCost;
    Model rootModel, leafModel;
    Seed link;
    double linkTimeLeft, linkTime = 4;
    static double swapProbability = 0.2;

    Seed(Vector3 pos, Vector3 speed, Vector3 acc) {
        this.pos = pos;
        this.speed = speed;
        this.acc = acc;
        rotationAxis = speed.cross(acc).norm();
    }

    Seed(Vector3 pos) {
        this(pos, new Vector3(), Vector3.up.mul(-Main.gravity));
    }

    Seed(Vector3 pos, Vector3 speed, Vector3 acc, Plant plant) {
        this(pos, speed, acc);
        rootModel = plant.rootModel;
        leafModel = plant.leafModel;
        rootModel.mutate();
        leafModel.mutate();
    }

    void move() {
        pos = pos.add(speed.add(acc.mul(0.5 * Main.timeStep)).mul(Main.timeStep));
        speed = speed.add(acc.mul(Main.timeStep));
        rotationAngle += rotationSpeed * Main.timeStep;
        if (link != null) {
            linkTimeLeft -= Main.timeStep;
            link.linkTimeLeft -= Main.timeStep;
            if (linkTimeLeft <= 0) {
                linkTimeLeft = 0;
                link.linkTimeLeft = 0;
                link = null;
            }
        }
//        else
//            linkTimeLeft = 0;
    }

    void draw(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslated(pos.x, pos.y, pos.z);
        gl.glRotated(rotationAngle, rotationAxis.x, rotationAxis.y, rotationAxis.z);
        gl.glScaled(size, size, size);
        Main.cube(gl);
        gl.glPopMatrix();
        if (link != null)
            Main.line(gl, pos, link.pos, 0.03 * linkTimeLeft / linkTime);
    }
    void swap(Seed other) {
        if (linkTimeLeft > 0 || other.linkTimeLeft > 0)
            return;
        Model newRootModel = rootModel.recombinate(other.rootModel);
        other.rootModel = other.rootModel.recombinate(rootModel);
        rootModel = newRootModel;
        Model newLeafModel = leafModel.recombinate(other.leafModel);
        other.leafModel = other.leafModel.recombinate(leafModel);
        leafModel = newLeafModel;
        link = other;
        linkTimeLeft = linkTime;
        other.linkTimeLeft = linkTime;
    }

    void detach() {
        pos.y = 0;
        if (link != null)
            link.linkTimeLeft = 0;
    }
}
