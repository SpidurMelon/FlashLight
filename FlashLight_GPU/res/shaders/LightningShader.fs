#version 400

#define PI 3.14159265

in vec2 vertexPos;
in vec3 color0;
out vec4 colorOut;

const int maxLights = 12;
uniform vec2 lightsPos[maxLights];
uniform vec4 lightsData[maxLights];

uniform vec2 screenSize;

float calcAngle(in vec2 point1, in vec2 point2) {
	float dx = point2.x-point1.x;
	float dy = (point2.y-point1.y)*(screenSize.y/screenSize.x);
	float preAngle = atan(dy/dx) + PI/2;
	if (dx > 0) {
		preAngle += PI;
	}
	return preAngle;
}

float calcDistance(in vec2 point1, in vec2 point2) {
	float dx = point2.x-point1.x;
	float dy = (point2.y-point1.y)*(screenSize.y/screenSize.x);
	return pow(dx*dx+dy*dy , 0.5);
}

void main() {
	float brightness = 0.0;
	
	for (int i = 0; i < lightsPos.length(); i++) {
		if (lightsData[i].y != 0 && lightsData[i].z != 0 && lightsData[i].w != 0) {
			float lightAngle = lightsData[i].x;
			float lightWidth = lightsData[i].y;
			float lightRadius = lightsData[i].z;
			float lightBrightness = lightsData[i].w;
			
			float dAngle = abs(lightAngle-calcAngle(lightsPos[i], vertexPos));
			if (dAngle < 0) {
				dAngle += 2*PI;
			}
			
			if (dAngle < lightWidth || dAngle > 2*PI-lightWidth) {
				float lightDist = calcDistance(lightsPos[i], vertexPos);
				brightness += max(lightBrightness-lightBrightness*(lightDist/lightRadius),0);
			}
		}
	}

	colorOut = vec4(color0*brightness, 1);
}

