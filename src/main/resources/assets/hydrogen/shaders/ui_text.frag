#version 150 core

uniform sampler2D uAtlas;
uniform vec2 uAtlasSize;
uniform float uDistanceRange;

in vec2 vUv;
in vec4 vColor;

out vec4 fragColor;

float median3(float r, float g, float b) {
    return max(min(r, g), min(max(r, g), b));
}

void main() {
    vec3 sampleValue = texture(uAtlas, vUv).rgb;
    float signedDistance = median3(sampleValue.r, sampleValue.g, sampleValue.b) - 0.5;
    vec2 unitRange = vec2(uDistanceRange) / uAtlasSize;
    vec2 screenTexSize = vec2(1.0) / max(fwidth(vUv), vec2(0.000001));
    float screenRange = max(0.5 * dot(unitRange, screenTexSize), 1.0);
    float opacity = clamp(signedDistance * screenRange + 0.5, 0.0, 1.0);
    fragColor = vec4(vColor.rgb, vColor.a * opacity);
}
