//Copyright (c) 2021 Jacob Bingham
//This software is released under the MIT License.
//https://github.com/Zi7ar21/Whipped-Cream/blob/main/LICENSE
#version 150

#define COLORED_SHADOWS 1 //0: Stained glass will cast ordinary shadows. 1: Stained glass will cast colored shadows. 2: Stained glass will not cast any shadows. [0 1 2]
#define SHADOW_BRIGHTNESS 0.50 //Light levels are multiplied by this number when the surface is in shadows [0.00 0.05 0.10 0.15 0.20 0.25 0.30 0.35 0.40 0.45 0.50 0.55 0.60 0.65 0.70 0.75 0.80 0.85 0.90 0.95 1.00]

uniform sampler2D lightmap;
uniform sampler2D shadowcolor0;
uniform sampler2D shadowtex0;
uniform sampler2D shadowtex1;
uniform sampler2D texture;

varying vec2 lmcoord;
varying vec2 texcoord;
varying vec4 glcolor;
varying vec4 shadowPos;

const int shadowMapResolution = 1024; //Resolution of the shadow map. Higher numbers mean more accurate shadows. [128 256 512 1024 2048 4096 8192]

//fix artifacts when colored shadows are enabled
const bool shadowcolor0Nearest = true;
const bool shadowtex0Nearest = true;
const bool shadowtex1Nearest = true;

uniform sampler2D K_Sampler0;
uniform int KAIMyEntityF;
in vec4 lightMapColor;

void main()
{
	vec4 color = texture2D(texture, texcoord) * glcolor;
	vec2 lm = lmcoord;
	if(shadowPos.w > 0.0)
	{
		//surface is facing towards shadowLightPosition
		#if COLORED_SHADOWS == 0
		//for normal shadows, only consider the closest thing to the sun,
		//regardless of whether or not it's opaque.
		if(texture2D(shadowtex0, shadowPos.xy).r < shadowPos.z)
		{
		#else
		//for invisible and colored shadows, first check the closest OPAQUE thing to the sun.
		if(texture2D(shadowtex1, shadowPos.xy).r < shadowPos.z)
		{
		#endif
			//surface is in shadows. reduce light level.
			lm.y *= SHADOW_BRIGHTNESS;
		}
		else
		{
			//surface is in direct sunlight. increase light level.
			lm.y = mix(31.0 / 32.0 * SHADOW_BRIGHTNESS, 31.0 / 32.0, sqrt(shadowPos.w));
			#if COLORED_SHADOWS == 1
				//when colored shadows are enabled and there's nothing OPAQUE between us and the sun,
				//perform a 2nd check to see if there's anything translucent between us and the sun.
				if(texture2D(shadowtex0, shadowPos.xy).r < shadowPos.z)
				{
					//surface has translucent object between it and the sun. modify its color.
					//if the block light is high, modify the color less.
					vec4 shadowLightColor = texture2D(shadowcolor0, shadowPos.xy);
					//make colors more intense when the shadow light color is more opaque.
					shadowLightColor.rgb = mix(vec3(1.0), shadowLightColor.rgb, shadowLightColor.a);
					//also make colors less intense when the block light level is high.
					shadowLightColor.rgb = mix(shadowLightColor.rgb, vec3(1.0), lm.x);
					//apply the color.
					color.rgb *= shadowLightColor.rgb;
				}
			#endif
		}
	}
	color *= texture2D(lightmap, lm);

	/* DRAWBUFFERS:0 */
	gl_FragData[0] = color; // gcolor
	if(KAIMyEntityF == 1){
		gl_FragData[0] = texture2D(K_Sampler0, texcoord) * lightMapColor;
	}
}