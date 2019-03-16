#version 330 core

uniform sampler2D textureSampler;

in vec2 fragmentUV;

out vec3 color;

void main() {
    color = texture(textureSampler, fragmentUV).rgb;
}
