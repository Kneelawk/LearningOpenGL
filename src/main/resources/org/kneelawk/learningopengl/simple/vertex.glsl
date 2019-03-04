#version 330 core

layout(location = 0) in vec3 vertexPosition;

layout(location = 1) in vec3 vertexColor;

uniform mat4 vpMatrix;

out vec3 fragmentColor;

void main() {
    fragmentColor = vertexColor;
    gl_Position = vpMatrix * vec4(vertexPosition, 1);
}