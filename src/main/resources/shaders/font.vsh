#version 110

varying vec2 pos;

void main() {
    pos = gl_MultiTexCoord0.st;

    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
