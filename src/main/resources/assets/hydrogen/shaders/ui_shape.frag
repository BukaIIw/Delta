#version 150 core

in vec2 vLocal;
in vec2 vSize;
in vec4 vRadii;
in vec4 vColorTL;
in vec4 vColorTR;
in vec4 vColorBR;
in vec4 vColorBL;
in vec4 vStrokeColor;
in vec4 vEffects;
in vec4 vShadowColor;

out vec4 fragColor;

float roundedBoxDistance(vec2 point, vec2 size, vec4 radii) {
    vec2 centered = point - size * 0.5;
    float radius;
    if (centered.x < 0.0) {
        radius = centered.y < 0.0 ? radii.x : radii.w;
    } else {
        radius = centered.y < 0.0 ? radii.y : radii.z;
    }
    radius = clamp(radius, 0.0, min(size.x, size.y) * 0.5);
    vec2 q = abs(centered) - (size * 0.5 - vec2(radius));
    return length(max(q, vec2(0.0))) + min(max(q.x, q.y), 0.0) - radius;
}

void main() {
    float distance = roundedBoxDistance(vLocal, vSize, vRadii);
    float softness = max(vEffects.x, 0.35);
    float coverage = 1.0 - smoothstep(-softness, softness, distance);

    vec2 uv = clamp(vLocal / max(vSize, vec2(0.001)), 0.0, 1.0);
    vec4 top = mix(vColorTL, vColorTR, uv.x);
    vec4 bottom = mix(vColorBL, vColorBR, uv.x);
    vec4 fill = mix(top, bottom, uv.y);

    float inner = 1.0;
    if (vEffects.y > 0.0) {
        inner = 1.0 - smoothstep(-vEffects.y - softness, -vEffects.y + softness, distance);
    }
    vec4 surface = mix(vStrokeColor, fill, inner);
    surface.a *= coverage;

    float sigma = max(vEffects.z * 0.48, 0.001);
    float outside = max(distance, 0.0);
    float shadowMask = exp(-(outside * outside) / (2.0 * sigma * sigma));
    float shadowAlpha = vShadowColor.a * shadowMask * (1.0 - coverage * fill.a);
    float combinedAlpha = surface.a + shadowAlpha * (1.0 - surface.a);
    vec3 premultiplied = surface.rgb * surface.a
        + vShadowColor.rgb * shadowAlpha * (1.0 - surface.a);
    vec3 combinedColor = combinedAlpha > 0.0001 ? premultiplied / combinedAlpha : vec3(0.0);

    // Tiny deterministic dither prevents banding in low-alpha gradients.
    float dither = fract(sin(dot(gl_FragCoord.xy, vec2(12.9898, 78.233))) * 43758.5453) - 0.5;
    combinedColor += dither / 510.0 * combinedAlpha;
    fragColor = vec4(combinedColor, combinedAlpha);
}
