#version 150 core

in vec2 aUnitPosition;
in vec4 aRect;
in vec4 aRadii;
in vec4 aColorTL;
in vec4 aColorTR;
in vec4 aColorBR;
in vec4 aColorBL;
in vec4 aStrokeColor;
in vec4 aEffects;
in vec4 aShadowColor;

uniform vec2 uResolution;

out vec2 vLocal;
out vec2 vSize;
out vec4 vRadii;
out vec4 vColorTL;
out vec4 vColorTR;
out vec4 vColorBR;
out vec4 vColorBL;
out vec4 vStrokeColor;
out vec4 vEffects;
out vec4 vShadowColor;

void main() {
    float expansion = aEffects.z * 2.0 + 2.0;
    vec2 outerSize = aRect.zw + vec2(expansion * 2.0);
    vec2 position = aRect.xy - vec2(expansion) + aUnitPosition * outerSize;
    vec2 clip = vec2((position.x / uResolution.x) * 2.0 - 1.0,
                     1.0 - (position.y / uResolution.y) * 2.0);
    gl_Position = vec4(clip, 0.0, 1.0);

    vLocal = aUnitPosition * outerSize - vec2(expansion);
    vSize = aRect.zw;
    vRadii = aRadii;
    vColorTL = aColorTL;
    vColorTR = aColorTR;
    vColorBR = aColorBR;
    vColorBL = aColorBL;
    vStrokeColor = aStrokeColor;
    vEffects = aEffects;
    vShadowColor = aShadowColor;
}
