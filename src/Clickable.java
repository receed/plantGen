abstract class Clickable {
    boolean selected = false;
    abstract double distByRay(Vector3 from, Vector3 dir);
    void select() {
        selected = true;
    }
    void unselect() {
        selected = false;
    }
}
