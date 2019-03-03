#version 330 core

in vec3 position;

out vec3 color;

void main() {
    color = (position + 1) / 2;
}
