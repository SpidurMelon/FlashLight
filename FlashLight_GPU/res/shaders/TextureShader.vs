#version 400

in vec3 position;
in vec2 texCoords;

out vec3 position0;
out vec2 texCoords0;

uniform vec2 worldTranslation;

void main() {
	vec3 absPosition = position+vec3(worldTranslation, 0);
	gl_Position = vec4(absPosition, 1);
	
	position0 = position;
	texCoords0 = texCoords;
}