#version 130

varying vec2 texCoord;
out vec4 color;

const vec4 bgColor = vec4(0f, 0f, 0f, 0f);

uniform sampler2D msdf;
uniform float pxRange;
uniform vec4 fgColor;
uniform vec2 glyphSize;

float median(float r, float g, float b) {
    return max(min(r, g), min(max(r, g), b));
}

void main() {
    vec2 msdfUnit = pxRange / glyphSize;
    vec3 sampled = texture(msdf, texCoord).rgb;
//    float sigDist = median(sampled.r, sampled.g, sampled.b);
//    float w = fwidth(sigDist);
//    float opacity = smoothstep(0.5 - w, 0.5 + w, sigDist);
    float sigDist = median(sampled.r, sampled.g, sampled.b) - 0.5;
    sigDist *= dot(msdfUnit, 0.5 / fwidth(texCoord));
    float opacity = clamp(sigDist + 0.5, 0.0, 1.0);
    color = mix(bgColor, fgColor, opacity);
//    color = vec4(pxRange / 24.0, 0, 0, 1);
}
