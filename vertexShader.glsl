#version 130
in vec4 vertexPosition_modelspace;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * vertexPosition_modelspace;
//    gl_Position.w = 1;
//    gl_Position = ftransform();
}