#version 400

in vec3 position0;
in vec2 texCoords0;

out vec4 colorOut;

uniform vec2 mousePos;
uniform sampler2D myTexture;

void main() {
	float dx = abs(mousePos[0]-position0[0]);
	float dy = abs(mousePos[1]-position0[1]);
	float dist = pow(dx*dx+dy*dy, 0.5);
	colorOut = texture(myTexture, texCoords0)*vec4(1/dist, 1/dist, 1/dist, 1);
}