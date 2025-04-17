uniform sampler2D DiffuseDepthSampler;
uniform sampler2D DiffuseSampler0;
uniform sampler2D HandDepthSampler;
uniform sampler2D MainDepthSampler;

in vec2 texCoord;

out vec4 fragColor;

void main() {

    gl_FragDepth = min(texture(DiffuseDepthSampler, texCoord).r, texture(MainDepthSampler, texCoord).r, texture(HandDepthSampler, texCoord).r);
}
