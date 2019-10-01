#version 120

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 BlurDir = vec2(1.0, 1.0);
uniform float Radius = 5.0;

void main() {
    vec4 blurred = vec4(0.0);
    float totalStrength = 0.0;
    float totalAlpha = 0.0;
    float totalSamples = 0.0;
    for(float r = -Radius; r <= Radius; r += 1.0) {
        vec4 sample = texture2D(DiffuseSampler, texCoord + (1.0 / InSize) * r * BlurDir);

        // Accumulate average alpha
        totalAlpha = totalAlpha + sample.a;
        totalSamples = totalSamples + 1.0;

        // Accumulate smoothed blur
        float strength = 1.0 - abs(r / Radius);
        totalStrength = totalStrength + strength;
        blurred = blurred + sample;
    }

    gl_FragColor = vec4(blurred.rgb / (Radius * 2.0 + 1.0), totalAlpha);
//    gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
}
