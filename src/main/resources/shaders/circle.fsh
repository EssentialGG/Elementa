#version 110

uniform float u_Radius;
uniform vec2 u_CenterPos;

varying vec2 f_Position;

//out vec4 fragColor;

void main() {
    float v = length(f_Position - u_CenterPos);

    float a = 1.0 - smoothstep(u_Radius - 1.0, u_Radius, v);

    gl_FragColor = gl_Color * vec4(1.0, 1.0, 1.0, a);
}
