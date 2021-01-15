#version 110

varying vec2 texCoord;

void main() {
    texCoord = gl_MultiTexCoord0.st;

    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
