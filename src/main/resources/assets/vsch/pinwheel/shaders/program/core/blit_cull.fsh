uniform sampler2D DiffuseDepthSampler;
uniform sampler2D DiffuseSampler0;
uniform sampler2D MainDepthSampler;
uniform sampler2D HandDepthSampler;

in vec2 texCoord;

out vec4 fragColor;

void main() {

    gl_FragDepth = min(texture(MainDepthSampler, texCoord).r, texture(HandDepthSampler, texCoord).r);
    float fragDepth = texture(DiffuseDepthSampler, texCoord).r;
    fragColor = vec4(0.0, 0.0, 0.0, 0.0);

    if (gl_FragDepth >= fragDepth) {
        gl_FragDepth = fragDepth;
        fragColor = texture(DiffuseSampler0, texCoord);
    }
}
