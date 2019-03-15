import com.jogamp.newt.event.MouseListener;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.util.awt.TextRenderer;


public class Main implements GLEventListener {

    public static DisplayMode dm, dm_old;
    private GLU glu = new GLU();
    Camera camera = new Camera();
    private static double strafeUD = 0, strafeLR = 0, strafeFB = 0;
    private static double rotLR = 0, rotUD = 0, ballX = 0, ballZ = 0;
    static boolean fill = true;
    static boolean mousePressed = false;
    static int mouseX = 0, mouseY = 0, oldMouseX = 0, oldMouseY = 0;
    public static ByteBuffer pixels;
    public static int widthTexture;
    public static int heightTexture;
    public static int widthMap = 63;
    public static int heightMap = 63;
    static long time = 0;
    static double[][] height = new double[widthMap][heightMap];
    static final int bufferSize = 500;
    static double[][] maxHeight = new double[bufferSize][bufferSize];
    static Leaf[][] highestLeaf = new Leaf[bufferSize][bufferSize];
    static final int[][] adj = {{0, 0}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0},
            {-1, -1}, {0, -1}, {1, -1}, {1, 0}};
    static Random random = new Random();
    static double cameraDistance = 0;
    static double inf = Double.POSITIVE_INFINITY;
    Ball ball = new Ball(new Vector3(5, 0, 5));
    static Plant plant = new Plant();
    static ArrayList<Plant> plants = new ArrayList<>();
    static double sunAngle = 0;
    static Vector3 sunAxis = new Vector3(0, 0, 1);
    TextRenderer renderer;


    private void tetraedr(GL2 gl){
        gl.glBegin(GL2.GL_TRIANGLES);

        // Front
        gl.glColor3f(0.0f, 1.0f, 1.0f);     gl.glVertex3f(0.0f, 1.0f, 0.0f);
        gl.glColor3f(0.0f, 0.0f, 1.0f);     gl.glVertex3f(-1.0f, -1.0f, 1.0f);
        gl.glColor3f(0.0f, 0.0f, 0.0f);     gl.glVertex3f(1.0f, -1.0f, 1.0f);

        // Right Side Facing Front
        gl.glColor3f(0.0f, 1.0f, 1.0f);     gl.glVertex3f(0.0f, 1.0f, 0.0f);
        gl.glColor3f(0.0f, 0.0f, 1.0f);     gl.glVertex3f(1.0f, -1.0f, 1.0f);
        gl.glColor3f(0.0f, 0.0f, 0.0f);     gl.glVertex3f(0.0f, -1.0f, -1.0f);

        // Left Side Facing Front
        gl.glColor3f(0.0f, 1.0f, 1.0f);     gl.glVertex3f(0.0f, 1.0f, 0.0f);
        gl.glColor3f(0.0f, 0.0f, 1.0f);     gl.glVertex3f(0.0f, -1.0f, -1.0f);
        gl.glColor3f(0.0f, 0.0f, 0.0f);     gl.glVertex3f(-1.0f, -1.0f, 1.0f);

        // Bottom
        gl.glColor3f(0.0f, 0.0f, 0.0f);     gl.glVertex3f(-1.0f, -1.0f, 1.0f);
        gl.glColor3f(0.1f, 0.1f, 0.1f);     gl.glVertex3f(1.0f, -1.0f, 1.0f);
        gl.glColor3f(0.2f, 0.2f, 0.2f);     gl.glVertex3f(0.0f, -1.0f, -1.0f);

        gl.glEnd();
    }

    private void cube(GL2 gl){
        gl.glBegin(gl.GL_QUADS);
        gl.glVertex3f( 0.5f, 0.5f, 0.5f);
        gl.glVertex3f( 0.5f,-0.5f, 0.5f);
        gl.glVertex3f(-0.5f,-0.5f, 0.5f);
        gl.glVertex3f(-0.5f, 0.5f, 0.5f);

        gl.glVertex3f( 0.5f, 0.5f,-0.5f);
        gl.glVertex3f( 0.5f,-0.5f,-0.5f);
        gl.glVertex3f(-0.5f,-0.5f,-0.5f);
        gl.glVertex3f(-0.5f, 0.5f,-0.5f);

        gl.glVertex3f( 0.5f, 0.5f, 0.5f);
        gl.glVertex3f( 0.5f,-0.5f, 0.5f);
        gl.glVertex3f( 0.5f,-0.5f,-0.5f);
        gl.glVertex3f( 0.5f, 0.5f,-0.5f);

        gl.glVertex3f(-0.5f, 0.5f, 0.5f);
        gl.glVertex3f(-0.5f,-0.5f, 0.5f);
        gl.glVertex3f(-0.5f,-0.5f,-0.5f);
        gl.glVertex3f(-0.5f, 0.5f,-0.5f);

        gl.glVertex3f( 0.5f, 0.5f, 0.5f);
        gl.glVertex3f(-0.5f, 0.5f, 0.5f);
        gl.glVertex3f(-0.5f, 0.5f,-0.5f);
        gl.glVertex3f( 0.5f, 0.5f,-0.5f);

        gl.glVertex3f( 0.5f,-0.5f, 0.5f);
        gl.glVertex3f(-0.5f,-0.5f, 0.5f);
        gl.glVertex3f(-0.5f,-0.5f,-0.5f);
        gl.glVertex3f( 0.5f,-0.5f,-0.5f);
        gl.glEnd();
    }

    private void conus(GL2 gl){
        gl.glBegin(gl.GL_TRIANGLE_FAN);
        gl.glVertex3d(0,-0.5,0);
        for (int i = 0 ; i <= 20 ; i++)
            gl.glVertex3d(0.5 * Math.cos(2 * Math.PI * i / 20), -0.5, 0.5 * Math.sin(2 * Math.PI * i / 20));
        gl.glEnd();
        gl.glBegin(gl.GL_TRIANGLE_FAN);
        gl.glVertex3d(0,0.5,0);
        for (int i = 0 ; i <= 20 ; i++)
            gl.glVertex3d(0.5 * Math.cos(2 * Math.PI * i / 20), -0.5, 0.5 * Math.sin(2 * Math.PI * i / 20));
        gl.glEnd();
    }

    static void cylinder(GL2 gl) {
        gl.glBegin(gl.GL_TRIANGLE_FAN);
        gl.glVertex3d(0,-0.5,0);
        for (int i = 0 ; i <= 20 ; i++)
            gl.glVertex3d(0.5*Math.cos(2*Math.PI*i/20),-0.5,0.5*Math.sin(2*Math.PI*i/20));
        gl.glEnd();
        gl.glBegin(gl.GL_TRIANGLE_FAN);
        gl.glVertex3d(0,0.5,0);
        for (int i = 0 ; i <= 20 ; i++)
            gl.glVertex3d(0.5*Math.cos(2*Math.PI*i/20),0.5,0.5*Math.sin(2*Math.PI*i/20));
        gl.glEnd();
        gl.glBegin(gl.GL_QUAD_STRIP);
        for (int i = 0 ; i <= 20 ; i++) {
            gl.glVertex3d(0.5 * Math.cos(2 * Math.PI * i / 20), -0.5, 0.5 * Math.sin(2 * Math.PI * i / 20));
            gl.glVertex3d(0.5 * Math.cos(2 * Math.PI * i / 20), 0.5, 0.5 * Math.sin(2 * Math.PI * i / 20));
        }
        gl.glEnd();
    }

    static void sphere(GL2 gl) {
        double r1,r2,xi1,xi2,h1,h2;
        for (int j = -9,i ; j < 9 ; j++) {
            xi1 = Math.PI * j / 18;
            xi2 =Math.PI * (j + 1) / 18;
            r1 =0.5 * Math.cos(xi1);
            r2 =0.5 * Math.cos(xi2);
            h1 =0.5 * Math.sin(xi1);
            h2 =0.5 * Math.sin(xi2);
            gl.glBegin(gl.GL_QUAD_STRIP);
            for (i =0 ; i <= 20 ; i++) {
                gl.glVertex3d(r1 * Math.cos(2 * Math.PI * i / 20), r1 * Math.sin(2 * Math.PI * i / 20), h1);
                gl.glVertex3d(r2 * Math.cos(2 * Math.PI * i / 20), r2 * Math.sin(2 * Math.PI * i / 20), h2);
            }
            gl.glEnd();
        }
    }

    private void thor(GL2 gl) {
        for (int j =0, i ; j <= 20 ; j++) {
            gl.glBegin(gl.GL_QUAD_STRIP);
            for (i =0 ; i <= 20 ; i++) {
                gl.glVertex3d((0.5 + 0.125 * Math.cos(2 * Math.PI * j / 20)) * Math.cos(2 * Math.PI * i / 20),
                        (0.5 + 0.125 * Math.cos(2 * Math.PI * j / 20)) * Math.sin(2 * Math.PI * i / 20),
                        0.125 * Math.sin(2 * Math.PI * j / 20));
                gl.glVertex3d((0.5 + 0.125 * Math.cos(2 * Math.PI * (j + 1) / 20)) * Math.cos(2 * Math.PI * i / 20),
                        (0.5 + 0.125 * Math.cos(2 * Math.PI * (j + 1) / 20)) * Math.sin(2 * Math.PI * i / 20),
                        0.125 * Math.sin(2 * Math.PI * (j + 1) / 20));
            }
            gl.glEnd();
        }
    }

    static Vector3 getVector3(int x, int y) {
//        if (x < 0 || x >= widthMap || y < 0 || y >= heightMap)
//            return new Vector3(x, 0, y);
        return new Vector3(x, height[Math.min(widthMap, Math.max(0, x))][Math.min(heightMap, Math.max(0, y))], y);
    }

    static Vector3 getNormal(int x, int y) {
        int nx = Math.max(1, Math.min(widthMap - 2, x));
        int ny = Math.max(1, Math.min(heightMap - 2, y));
        return getVector3(nx - 1, ny - 1).normal(getVector3(nx + 1, ny), getVector3(nx, ny + 1));
    }

    static double getHeight(Vector3 v) {
        int x = (int) v.x, z = (int) v.z;
        if ((x + z) % 2 == 0) {
            if (v.z - z < v.x - x)
                return v.getHeight(getVector3(x, z), getVector3(x + 1, z), getVector3(x + 1, z + 1));
            else
                return v.getHeight(getVector3(x, z), getVector3(x, z + 1), getVector3(x + 1, z + 1));
        }
        else {
            if (v.z - z + v.x - x < 1)
                return v.getHeight(getVector3(x, z), getVector3(x + 1, z), getVector3(x, z + 1));
            else
                return v.getHeight(getVector3(x + 1, z + 1), getVector3(x + 1, z), getVector3(x, z + 1));
        }
        /*int x = (int) Math.round((v.x - 1) / 2) * 2 + 1;
        int y = (int) Math.round((v.z - 1) / 2) * 2 + 1;
        Vector2 cv = new Vector2(v.x - x, v.z - y);
        for (int i = 1; i < 9; i++) {
            if (adj[i][0] * cv.y - adj[i][1] * cv.x >= 0 && adj[i + 1][0] * cv.y - adj[i + 1][1] * cv.x <= 0) {
                return v.getHeight(getVector3(x, y), getVector3(x + adj[i][0], y = adj[i][1]),
                        getVector3(x + adj[i + 1][0], y + adj[i + 1][1]));
            }
        }*/
    }

    static void genLandscape() {
        for (int i = 0; i < widthMap; i++)
            for (int j = 0; j < heightMap; j++)
                height[i][j] = 5 + 0 * random.nextDouble();
        for (int k = 0; k < 6; k++) {
            Vector2 peak1 = new Vector2(random, widthMap, heightMap);
            Vector2 peak2 = new Vector2(random, widthMap, heightMap);
//            System.out.println(peak1 + " " + peak2);
            double factor = 6 + random.nextDouble() * 6;
            for (int i = 0; i < widthMap; i++)
                for (int j = 0; j < heightMap; j++) {
                    double dist = new Vector2(i, j).distToSegment(peak1, peak2);
                    double mult = 1 + Math.exp(-Math.pow(dist / widthMap * factor, 2));
                    height[i][j] *= mult;
//                    System.out.println(mult);
//                    System.out.println(i + " " + j + " " + height[i][j]);
                }
        }
    }

    void landscape(GL2 gl) {
        gl.glPushMatrix();
        //gl.glScaled(0.1, 0.1, 0.1);
        gl.glColor3d(0, 0.3, 1);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, new float[] {0, 0.075f, 0.25f, 1}, 0);
        for (int i = 1; i < widthMap; i += 2)
            for (int j = 1; j < heightMap; j += 2) {
                gl.glBegin(GL.GL_TRIANGLE_FAN);
                for (int k = 0; k < 10; k++) {
                    int cx = i + adj[k][0], cy = j + adj[k][1];
//                    gl.glColor3d(0.5 * cx / widthMap,
//                            0.5 - 0.5 * cx / widthMap + 0.5 * cy / heightMap,
//                             0.5 - 0.5 * cy / heightMap);
                    Vector3 n = getNormal(cx, cy);
                    gl.glNormal3d(n.x, n.y, n.z);
                    gl.glVertex3d(cx, height[cx][cy], cy);
                }
                gl.glEnd();
            }
        gl.glPopMatrix();
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, new float[] {0.2f, 0.2f, 0.2f, 1}, 0);
    }


    void skybox(GL2 gl) {
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glPushMatrix();
        gl.glTranslated(camera.pos.x, camera.pos.y, camera.pos.z);
        gl.glScaled(10, 10, 10);
        gl.glBegin(gl.GL_QUADS);
        gl.glColor3d(1, 1, 1);
        gl.glTexCoord2d(0, 0.5); gl.glVertex3d(-5, -5, -5);
        gl.glTexCoord2d(0.25, 0.5); gl.glVertex3d(5, -5, -5);
        gl.glTexCoord2d(0.25, 0.25); gl.glVertex3d(5, 5, -5);
        gl.glTexCoord2d(0, 0.25); gl.glVertex3d(-5, 5, -5);

        gl.glTexCoord2d(0.25, 0.5); gl.glVertex3d(5, -5, -5);
        gl.glTexCoord2d(0.5, 0.5); gl.glVertex3d(5, -5, 5);
        gl.glTexCoord2d(0.5, 0.25); gl.glVertex3d(5, 5, 5);
        gl.glTexCoord2d(0.25, 0.25); gl.glVertex3d(5, 5, -5);

        gl.glTexCoord2d(0.5, 0.5); gl.glVertex3d(5, -5, 5);
        gl.glTexCoord2d(0.75, 0.5); gl.glVertex3d(-5, -5, 5);
        gl.glTexCoord2d(0.75, 0.25); gl.glVertex3d(-5, 5, 5);
        gl.glTexCoord2d(0.5, 0.25); gl.glVertex3d(5, 5, 5);

        gl.glTexCoord2d(0.75, 0.5); gl.glVertex3d(-5, -5, 5);
        gl.glTexCoord2d(1, 0.5); gl.glVertex3d(-5, -5, -5);
        gl.glTexCoord2d(1, 0.25); gl.glVertex3d(-5, 5, -5);
        gl.glTexCoord2d(0.75, 0.25); gl.glVertex3d(-5, 5, 5);

        gl.glTexCoord2d(0.25, 0.25); gl.glVertex3d(5, 5, -5);
        gl.glTexCoord2d(0.5, 0.25); gl.glVertex3d(5, 5, 5);
        gl.glTexCoord2d(0.5, 0); gl.glVertex3d(-5, 5, 5);
        gl.glTexCoord2d(0.25, 0); gl.glVertex3d(-5, 5, -5);

        gl.glTexCoord2d(0.25, 0.75); gl.glVertex3d(-5, -5, -5);
        gl.glTexCoord2d(0.5, 0.75); gl.glVertex3d(-5, -5, 5);
        gl.glTexCoord2d(0.5, 0.5); gl.glVertex3d(5, -5, 5);
        gl.glTexCoord2d(0.25, 0.5); gl.glVertex3d(5, -5, -5);
        gl.glEnd();
        gl.glPopMatrix();
        gl.glEnable(GL2.GL_LIGHTING);
    }

    void fillTriangle(Vector3 a3, Vector3 b3, Vector3 c3, Leaf leaf, double minX, double maxX, double minY, double maxY) {
        Vector2 a = new Vector2(a3), b = new Vector2(b3), c = new Vector2(c3);
        int lX = Math.max((int) (bufferSize * (Math.min(a.x, Math.min(b.x, c.x)) - minX) / (maxX - minX)), 0);
        int rX = Math.min((int) (bufferSize * (Math.max(a.x, Math.max(b.x, c.x)) - minX) / (maxX - minX)), bufferSize);
        int lY = Math.max((int) (bufferSize * (Math.min(a.y, Math.min(b.y, c.y)) - minY) / (maxY - minY)), 0);
        int rY = Math.min((int) (bufferSize * (Math.max(a.y, Math.max(b.y, c.y)) - minY) / (maxY - minY)), bufferSize);
        for (int i = lX; i < rX; i++) {
            for (int j = lY; j < rY; j++) {
                Vector2 d = new Vector2(
                        minX + (maxX - minX) * i / bufferSize,
                        minY + (maxY - minY) * j / bufferSize);
                if (d.inside(a, b, c)) {
                    double h = new Vector3(d).getHeight(a3, b3, c3);
                    if (h > maxHeight[i][j]) {
                        highestLeaf[i][j] = leaf;
                        maxHeight[i][j] = h;
                    }
                }
            }
        }
    }

    void getLighting(ArrayList<Plant> plants, Vector3 axis, double angle, double density) {
        Matrix3x3 rot = Matrix3x3.mRot(axis.norm(), angle);
        for (int i = 0; i < bufferSize; i++) {
            Arrays.fill(maxHeight[i], -inf);
            Arrays.fill(highestLeaf[i], null);
        }
        double minX = inf, maxX = -inf, minY = inf, maxY = -inf;
        for (Plant plant : plants)
            for (Leaf leaf : plant.leaves) {
                leaf.light = 0;
                for (Joint joint : leaf.joints) {
                    minX = Math.min(minX, joint.pos.x);
                    maxX = Math.max(maxX, joint.pos.x);
                    minY = Math.min(minY, joint.pos.y);
                    maxY = Math.max(maxY, joint.pos.y);
                }
            }
        for (Plant plant : plants)
            for (Leaf leaf : plant.leaves) {
                for (int i = 1; i < leaf.joints.size() - 1; i++)
                fillTriangle(rot.mul(leaf.joints.get(0).pos), rot.mul(leaf.joints.get(i).pos), rot.mul(leaf.joints.get(i + 1).pos),
                        leaf, minX, maxX, minY, maxY);
            }
        double lightPerCell = density * (maxX - minX) * (maxY - minY) / Math.pow(bufferSize, 2);
        for (int i = 0; i < bufferSize; i++)
            for (int j = 0; j < bufferSize; j++)
                if (highestLeaf[i][j] != null)
                    highestLeaf[i][j].light += lightPerCell;
    }

    @Override
    public void display( GLAutoDrawable drawable ) {

        final GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );
        gl.glLoadIdentity();

        camera.strafeFB(strafeFB * 0.1);
        cameraDistance -= strafeFB * 0.1;
        camera.strafeLR(strafeLR * 0.1);
        camera.rotLR(rotLR * 0.02 /*+ (oldMouseX - mouseX) * 0.015*/);
        camera.rotUD(rotUD * 0.02 /*+ (oldMouseY - mouseY) * 0.015*/);
        sunAngle += 0.005;
        oldMouseX = mouseX;
        oldMouseY = mouseY;
//        camera.look(glu);
//        ball.setSpeed(camera.dir);

        if (fill)
            gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
        else
            gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
        Vector3 sunDir = new Vector3(Math.cos(sunAngle) * 10, Math.sin(sunAngle) * 10, 0);
//        ball.setSpeed(camera.dir.x * ballZ,
//                camera.dir.cross(camera.up).z * -ballX);
//        camera.pos = ball.pos.add(camera.dir.mul(-cameraDistance));
//        System.out.println(cameraDistance);
//        System.out.println(ball.pos + " " + camera.pos + " " + camera.dir);
        camera.look(glu);
//        ball.move(0.1, gl);
        gl.glPushMatrix();
        landscape(gl);
        time++;
//        plant.root.genLeaves(0.002, null, plant, random);
        plant.root.dfs(time, gl);
//        ball.setSpeed(ballX, ballZ);
//        camera.pos = ball.pos.add(camera.dir.mul(-2));
//        System.out.println(ball.pos.x + " " + ball.pos.y + " " + ball.pos.z);
        skybox(gl);
        getLighting(plants, sunAxis, sunAngle, 1);
        gl.glPushMatrix();
        gl.glTranslated(sunDir.x, sunDir.y, sunDir.z);
        gl.glScaled(0.4, 0.4, 0.4);
        gl.glColor3d(0.8, 1, 1);
        sphere(gl);
        gl.glPopMatrix();
        renderer.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
        renderer.setColor(0, 1, 0.4f, 0.9f);
        renderer.draw("Light received: " + plant.getLight(), 5, 5);
        renderer.endRendering();
        float[] light_dir = {(float) sunDir.x, (float) sunDir.y, (float) sunDir.z};
        gl.glLightfv(GL2.GL_LIGHT0  , GL2.GL_POSITION, light_dir, 0);
    }

    @Override
    public void dispose( GLAutoDrawable drawable ) {
        // TODO Auto-generated method stub
    }

    @Override
    public void init( GLAutoDrawable drawable ) {

        final GL2 gl = drawable.getGL().getGL2();
        gl.glShadeModel( GL2.GL_SMOOTH );
        gl.glClearColor( 0f, 0f, 0f, 0f );
        gl.glClearDepth( 1.0f );
        gl.glEnable( GL2.GL_DEPTH_TEST );
        gl.glDepthFunc( GL2.GL_LEQUAL );
        gl.glHint( GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST );
        int[] textureId = new int[1];
        gl.glGenTextures(1, textureId, 0);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureId[0]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, Main.widthTexture,  Main.heightTexture,
                0,GL.GL_RGB, GL.GL_UNSIGNED_BYTE, Main.pixels);
//        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable( GL2.GL_COLOR_MATERIAL);
        gl.glColorMaterial (GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE) ;
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, new float[] {1, 1, 1, 0}, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, new float[] {0.4f, 0.4f, 0.4f, 0}, 0);
        gl.glEnable(GL2.GL_LIGHT0);
        renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 20));
    }

    @Override
    public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height ) {

        // TODO Auto-generated method stub
        final GL2 gl = drawable.getGL().getGL2();

        final float h = ( float ) width / ( float ) height;
        gl.glViewport( 0, 0, width, height );
        gl.glMatrixMode( GL2.GL_PROJECTION );
        gl.glLoadIdentity();

        glu.gluPerspective( 45.0f, h, 1.0, 100.0 );
        gl.glMatrixMode( GL2.GL_MODELVIEW );
        gl.glLoadIdentity();
    }

    public static void main( String[] args ) {

        final GLProfile profile = GLProfile.get( GLProfile.GL2 );
        GLCapabilities capabilities = new GLCapabilities( profile );

        // The canvas
        final GLCanvas glcanvas = new GLCanvas( capabilities );
        Main main = new Main();

        glcanvas.addGLEventListener(main);
        glcanvas.setSize( 1200, 700 );

        try {
            BufferedImage image = ImageIO.read(new File("skybox2.jpg"));
            widthTexture = image.getWidth();
            heightTexture = image.getHeight();
            DataBufferByte dataBufferByte =(DataBufferByte) image.getData().getDataBuffer();
            pixels = ByteBuffer.wrap(dataBufferByte.getData());
            byte r,b;
            for (int i = 0 ; i < heightTexture * widthTexture ; i++) {
                b = pixels.get(3 * i);
                r = pixels.get(3 * i + 2);
                pixels.put(3 * i, r);
                pixels.put(3 * i + 2, b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        genLandscape();
        plants.add(plant);
        plant.root.genLeaves(1, null, plant, random);
        plant.root.genRoots(1, random);
//        plant.root.genLeaf(plant, random);
        final JFrame frame = new JFrame( " Multicolored main" );
        frame.getContentPane().add( glcanvas );
        frame.setSize( frame.getContentPane().getPreferredSize() );
        frame.setVisible( true );
        glcanvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_W:
                        Main.strafeFB = 1; break;
                    case KeyEvent.VK_S:
                        Main.strafeFB = -1; break;
                    case KeyEvent.VK_A:
                        Main.strafeLR = -1; break;
                    case KeyEvent.VK_D:
                        Main.strafeLR = 1; break;
                    case KeyEvent.VK_K:
                        Main.rotUD = 1; break;
                    case KeyEvent.VK_J:
                        Main.rotUD = -1; break;
                    case KeyEvent.VK_H:
                        Main.rotLR = -1; break;
                    case KeyEvent.VK_L:
                        Main.rotLR = 1; break;
                    case KeyEvent.VK_SPACE:
                        fill = !fill; break;
                    case KeyEvent.VK_R:
                        genLandscape(); break;
                    case KeyEvent.VK_ESCAPE:
                        System.exit(0); break;
                    case KeyEvent.VK_UP:
                        ballZ = 1; break;
                    case KeyEvent.VK_DOWN:
                        ballZ = -1; break;
                    case KeyEvent.VK_LEFT:
                        ballX = 1; break;
                    case KeyEvent.VK_RIGHT:
                        ballX = -1; break;
                }
            }
            @Override
            public void keyReleased(KeyEvent keyEvent) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_S:
                        Main.strafeFB = 0; break;
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_D:
                        Main.strafeLR = 0; break;
                    case KeyEvent.VK_K:
                    case KeyEvent.VK_J:
                        Main.rotUD = 0; break;
                    case KeyEvent.VK_H:
                    case KeyEvent.VK_L:
                        Main.rotLR = 0; break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_DOWN:
                        ballZ = 0; break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_RIGHT:
                        ballX = 0; break;
                }
            }
        });
        glcanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent  e) {
                Main.oldMouseX = Main.mouseX = e.getX();
                Main.oldMouseY = Main.mouseY = e.getY();
                mousePressed = true;
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
            }
        });
        glcanvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Main.mouseX = e.getX();
                Main.mouseY = e.getY();
            }
        });
        final FPSAnimator animator = new FPSAnimator(glcanvas, 30,true);

        animator.start();
    }


}