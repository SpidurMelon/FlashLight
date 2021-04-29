#version 400

in vec3 position;
in vec3 color;

out vec2 vertexPos;
out vec3 color0;

void main() {
	gl_Position = vec4(position, 1);
	
	vertexPos = vec2(position[0], position[1]);
	color0 = color;
}