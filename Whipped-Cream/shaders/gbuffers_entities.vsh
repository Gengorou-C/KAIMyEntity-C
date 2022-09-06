//Copyright (c) 2021 Jacob Bingham
//This software is released under the MIT License.
//https://github.com/Zi7ar21/Whipped-Cream/blob/main/LICENSE
#version 150

uniform mat4 gbufferModelView;
uniform mat4 gbufferModelViewInverse;
uniform mat4 shadowModelView;
uniform mat4 shadowProjection;
uniform vec3 shadowLightPosition;

varying vec2 lmcoord;
varying vec2 texcoord;
varying vec4 glcolor;
varying vec4 shadowPos;

#include "/distort.glsl"

attribute vec3 K_Position;
attribute vec2 K_UV0;
attribute ivec2 K_UV2;
uniform mat4 K_ModelViewMat;
uniform mat4 K_ProjMat;
uniform sampler2D K_Sampler2;
uniform int KAIMyEntityV;
out vec4 lightMapColor;

void main()
{
	texcoord = (gl_TextureMatrix[0] * gl_MultiTexCoord0).xy;
	lmcoord  = (gl_TextureMatrix[1] * gl_MultiTexCoord1).xy;
	glcolor = gl_Color;
	
	vec4 viewPos = gl_ModelViewMatrix * gl_Vertex;
	float lightDot = dot(normalize(shadowLightPosition), normalize(gl_NormalMatrix * gl_Normal));
	if(lightDot > 0.0)
{ //vertex is facing towards the sun
		vec4 playerPos = gbufferModelViewInverse * viewPos;
		shadowPos = shadowProjection * (shadowModelView * playerPos); //convert to shadow space
		float distortFactor = getDistortFactor(shadowPos.xy);
		shadowPos.xyz = distort(shadowPos.xyz, distortFactor); //apply shadow distortion
		shadowPos.xyz = shadowPos.xyz * 0.5 + 0.5; //convert from -1 ~ +1 to 0 ~ 1
		shadowPos.z -= SHADOW_BIAS * (distortFactor * distortFactor) / abs(lightDot); //apply shadow bias
	}
	else
{ //vertex is facing away from the sun
		lmcoord.y *= SHADOW_BRIGHTNESS; //guaranteed to be in shadows. reduce light level immediately.
		shadowPos = vec4(0.0); //mark that this vertex does not need to check the shadow map.
	}
	shadowPos.w = lightDot;
	//use consistent transforms for entities and hand so that armor glint doesn't have z-fighting issues.
	gl_Position = gl_ProjectionMatrix * viewPos;

	if(KAIMyEntityV == 1){
		gl_Position = K_ProjMat * K_ModelViewMat * vec4(K_Position, 1.0);
		lightMapColor = texelFetch(K_Sampler2, K_UV2/16, 0);
		texcoord = K_UV0;
	}
}