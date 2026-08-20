#version 150 core

in vec2 aPosition;
in vec2 aUv;
in vec4 aColor;

uniform vec2 uResolution;

out vec2 vUv;
out vec4 vColor;

void main() {
    vec2 clip = vec2((aPosition.x / uResolution.x) * 2.0 - 1.0,
                     1.0 - (aPosition.y / uResolution.y) * 2.0);
    gl_Position = vec4(clip, 0.0, 1.0);
    vUv = aUv;
    vColor = aColor;
}
