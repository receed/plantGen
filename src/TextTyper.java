import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.awt.TextRenderer;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.LinkedList;


public class TextTyper extends TextRenderer {
    LinkedList<String> text = new LinkedList<>();
    double x = 0, y = 0;
    int startX = 1, startY = -1, align = 0;
    TextTyper() {
        super(new Font("SansSerif", Font.PLAIN, 16));
    }

    void setParams(double x, double y, int startX, int startY, int align) {
        this.x = x;
        this.y = y;
        this.startX = startX;
        this.startY = startY;
        this.align = align;
    }

    void put(String ...strings) {
        for (String string : strings)
            text.addAll(Arrays.asList(string.split("\\n")));
    }

    void draw(GLAutoDrawable drawable) {
        double width = 0, height = 0;
        for (String line : text) {
            Rectangle2D rect = getBounds(line);
            height += rect.getHeight();
            width = Math.max(width, rect.getWidth());
        }
        double currentY = y + height * (-startY + 1) / 2, currentX = x - width * (startX + 1) / 2;
        beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
        setColor(0, 1, 0.4f, 0.9f);
        for (String line : text) {
            Rectangle2D rect = getBounds(line);
            currentY -= rect.getHeight();
            draw(line, (int) (currentX + (width - rect.getWidth()) * (align + 1) / 2), (int) currentY);
        }
        endRendering();
        text.clear();
    }
}

