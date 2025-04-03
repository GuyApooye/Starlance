uniform sampler2D DiffuseDepthSampler;
uniform sampler2D DiffuseSampler0;
uniform sampler2D MainDepthSampler;
uniform sampler2D HandDepthSampler;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    fragColor = vec4(0.0);

}
