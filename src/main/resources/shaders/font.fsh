#version 110

varying vec2 pos;

uniform sampler2D msdf;
uniform vec4 fgColor;

uniform float doffset;
uniform float hint_amount;
uniform float subpixel_amount;

uniform vec2 sdf_texel;

float median(float r, float g, float b) {
    return max(min(r, g), min(max(r, g), b));
}

float smoother(float edge0, float edge1, float x) {
    // Scale, bias and saturate x to 0..1 range
    x = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
    // Evaluate polynomial
    return x * x * (3.0 - 2.0 * x);
}

float getDistanceFromSDF(sampler2D msdf, vec2 pos) {
    vec3 raw = texture2D(msdf, pos).rgb;
    return median(raw.r, raw.g, raw.b);// - 0.5;
}

vec3 subpixel(float v, float a) {
    float vt      = 0.6 * v;// 1.0 will make your eyes bleed
    vec3  rgb_max = vec3(-vt, 0.0, vt);
    float top     = abs(vt);
    float bottom  = -top - 1.0;
    float cfloor  = mix(top, bottom, a);
    vec3  res     = clamp(rgb_max - vec3(cfloor), 0.0, 1.0);
    return res;
}
void main() {
    // Sampling the texture, L pattern
    float sdf       = getDistanceFromSDF( msdf, pos );
    float sdf_north = getDistanceFromSDF( msdf, pos + vec2( 0.0, sdf_texel.y ) );
    float sdf_east  = getDistanceFromSDF( msdf, pos + vec2( sdf_texel.x, 0.0 ) );

    // Estimating stroke direction by the distance field gradient vector
    vec2  sgrad     = vec2( sdf_east - sdf, sdf_north - sdf );
    float sgrad_len = max( length( sgrad ), 1.0 / 128.0 );
    vec2  grad      = sgrad / vec2( sgrad_len );
    float vgrad = abs( grad.y ); // 0.0 - vertical stroke, 1.0 - horizontal one

    float horz_scale  = 1.1; // Blurring vertical strokes along the X axis a bit
    float vert_scale  = 0.8; // While adding some contrast to the horizontal strokes
    float hdoffset    = mix( doffset * horz_scale, doffset * vert_scale, vgrad );
    float res_doffset = mix( doffset, hdoffset, hint_amount );

    float alpha       = smoother( 0.5 - res_doffset, 0.5 + res_doffset, sdf );
    // Additional contrast
//    alpha             = pow( alpha, 1.0 + 0.2 * vgrad * hint_amount );

    // Discarding pixels beyond a threshold to minimise possible artifacts.
    if ( alpha < 20.0 / 256.0 ) discard;

    gl_FragColor = vec4(fgColor.rgb, alpha);

    /*vec3 channels = subpixel( grad.x * 0.5 * subpixel_amount, alpha );

    float subpixelOverride = min(1.0, max(0.0, 1.5 - 0.5*(channels.x + channels.y + channels.z)));

    vec4 fgColorBlended = fgColor;
    fgColorBlended.rgb += subpixelOverride;

    float finalAlpha = min(1.0, channels.r + channels.g + channels.b);
    gl_FragColor = fgColorBlended * vec4(channels, finalAlpha);*/
}