#version 300 es

layout(lines) in;
layout(triangle_qstrip, max_vertices=4) out;
in float width;

void main() {
    vec4 v = gl_in[1].gl_Position - fl_in[0].gl_Position;
    vec4 d = vec4(-v.y, v.x, 0, 0).normalize() * (width / 2);
    for (int i = 0; i < 2; i++) {
        gl_Position = gl_in[i].gl_Position + v;
        EmitVertex();
        gl_Position = gl_in[i].gl_Position - v;
        EmitVertex();
    }
}
