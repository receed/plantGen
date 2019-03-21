import com.jogamp.opengl.glu.GLU;

public class Camera {
    Vector3 pos, dir, up;
    Camera() {
        pos = new Vector3(0, 0, 0);
        dir = new Vector3(0, 0, -1);
        up = new Vector3(0, 1, 0);
    }
    Vector3 right() {
        return dir.cross(up);
    }
    void look(GLU glu) {
        Vector3 look = pos.add(dir);
        glu.gluLookAt(pos.x, pos.y, pos.z, look.x, look.y, look.z, up.x, up.y, up.z);
    }
    void rotLR(double phi) {
        dir = Matrix3x3.mRot(up, phi).mul(dir);
    }
    void rotUD(double phi) {
        Matrix3x3 m = Matrix3x3.mRot(up.cross(dir), phi);
        dir = m.mul(dir);
        up = m.mul(up);
    }
    void strafeUD(double k) {
        pos = pos.add(up.mul(k));
    }
    void strafeLR(double k) {
        pos = pos.add(dir.cross(up).mul(k));
    }
    void strafeFB(double k) {
        pos = pos.add(dir.mul(k));
    }
}
