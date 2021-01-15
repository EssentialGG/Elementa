#version 110

varying vec2 texCoord;
//out vec4 color;

const vec4 bgColor = vec4(0, 0, 0, 0);

//uniform sampler2D msdf;
uniform float distanceFactor;
//uniform vec4 fgColor;

uniform sampler2D msdf;
//uniform float pxRange;
uniform vec4 fgColor;
//uniform vec2 glyphSize;
//uniform float widthMultiplier;

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
//    vec3 sampled = texture(msdf, texCoord).rgb;
//    float sigDist = distanceFactor * (median(sampled.r, sampled.g, sampled.b) - 0.5);
//    float opacity = clamp(sigDist + 0.5, 0.0, 1.0);
//    color = fgColor * opacity;
    vec2 pos = texCoord;// vec2(texCoord.x, 1.0 - texCoord.y);
//    vec2 msdfUnit = pxRange / glyphSize;
//    vec3 sampled = texture2D(msdf, pos).rgb;
//    float sigDist = median(sampled.r, sampled.g, sampled.b);
//    float w = widthMultiplier / pxRange;
//    float opacity = smoother(0.5 - w, 0.5 + w, sigDist);
    vec3 sampled = texture2D(msdf, pos).rgb;
    float sigDist = distanceFactor * (median(sampled.r, sampled.g, sampled.b) - 0.5);
    float opacity = clamp(sigDist + 0.5, 0.0, 1.0);

//    sigDist *= dot(msdfUnit, vec2(1.0));
//    float opacity = clamp(sigDist + 0.5, 0.0, 1.0);
//    float dst = dot(abs(coord-center), vec2(1.0));
//    float aaf = fwidth(sigDist);
//    float opacity = smoother(pxRange - aaf, pxRange, sigDist);
//    float smoothing = 0.25;
//    float opacity = smoother(0.5 - smoothing, 0.5 + smoothing, sigDist);
//    sigDist *= pxRange;
//    sigDist *= dot(msdfUnit, vec2(1.0));
//    float opacity = clamp(sigDist, 0.0, 1.0);
    gl_FragColor = vec4(fgColor.rgb, fgColor.a * opacity);
}
