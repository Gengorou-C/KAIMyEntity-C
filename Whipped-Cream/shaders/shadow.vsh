//Copyright (c) 2021 Jacob Bingham
//This software is released under the MIT License.
//https://github.com/Zi7ar21/Whipped-Cream/blob/main/LICENSE
#version 150

attribute vec4 mc_Entity;

varying vec2 lmcoord;
varying vec2 texcoord;
varying vec4 glcolor;

#include "/distort.glsl"

attribute vec3 K_Position;
uniform mat4 K_ModelViewMat;
uniform mat4 K_ProjMat;
uniform int KAIMyEntityV;

void main()
{
	texcoord = (gl_TextureMatrix[0] * gl_MultiTexCoord0).xy;
	lmcoord  = (gl_TextureMatrix[1] * gl_MultiTexCoord1).xy;
	glcolor = gl_Color;

	#ifdef EXCLUDE_FOLIAGE
	if(mc_Entity.x == 10000.0)
	{
		gl_Position = vec4(10.0);
	}
	else
	{
	#endif
		gl_Position = ftransform();
		if(KAIMyEntityV == 1){
			gl_Position = K_ProjMat * K_ModelViewMat * vec4(K_Position, 1.0);
		}
		gl_Position.xyz = distort(gl_Position.xyz);
	#ifdef EXCLUDE_FOLIAGE
	}
	#endif
}