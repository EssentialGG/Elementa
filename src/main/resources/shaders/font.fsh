#version 110

varying vec2 pos;

const float shadowOffset = 0.008;
const float shadowSmoothing = 0.25;

uniform float distanceFactor;
uniform sampler2D msdf;
uniform vec4 fgColor;
uniform vec4 shadowColor;

float median(float r, float g, float b) {
    return max(min(r, g), min(max(r, g), b));
}

float smoother(float edge0, float edge1, float x) {
    // Scale, bias and saturate x to 0..1 range
    x = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
    // Evaluate polynomial
    return x * x * (3.0 - 2.0 * x);
}

void main() {
    vec3 sampled = texture2D(msdf, pos).rgb;
    float sigDist = distanceFactor * (median(sampled.r, sampled.g, sampled.b) - 0.5);
    float opacity = clamp(sigDist + 0.5, 0.0, 1.0);
    gl_FragColor = vec4(fgColor.rgb, fgColor.a * opacity);

//    TODO: Fix shadow effect
//    float shadowDistance = texture2D(msdf, pos - shadowOffset).a;
//    float shadowOpacity = smoother(0.5 - shadowSmoothing, 0.5 + shadowSmoothing, shadowDistance);
//    vec4 shadow = vec4(shadowColor.rgb, shadowColor.a * shadowOpacity);
//
//    gl_FragColor = mix(shadow, text, text.a);
}
