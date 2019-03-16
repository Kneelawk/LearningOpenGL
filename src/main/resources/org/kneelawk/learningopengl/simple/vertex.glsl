#version 330 core

uniform mat4 vpMatrix;

layout(location = 0) in vec3 vertexPosition;

layout(location = 1) in vec2 vertexUV;

layout(location = 2) in mat4 modelMatrix;

out vec2 fragmentUV;

void main() {
    fragmentUV = vertexUV;
    gl_Position = vpMatrix * modelMatrix * vec4(vertexPosition, 1);
}
