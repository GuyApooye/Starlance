package net.jcm.vsch.client.renderer;


import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import foundry.veil.Veil;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import foundry.veil.api.client.render.shader.VeilShaders;
import foundry.veil.mixin.accessor.RenderTypeAccessor;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

public class VSCHRenderType extends RenderType {
    public VSCHRenderType(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    private static final ResourceLocation BLOOM_BUFFER = new ResourceLocation("vsch", "bloom_particle");

    private static final RenderStateShard.ShaderStateShard PARTICLE;
    private static final RenderStateShard.ShaderStateShard PARTICLE_ADD;
    private static final RenderType THRUSTER_PARTICLE;

    public static RenderType thrusterParticle() {
        return THRUSTER_PARTICLE;
    }

    static {
        PARTICLE = VeilRenderBridge.shaderState(VeilShaders.PARTICLE);
        PARTICLE_ADD = VeilRenderBridge.shaderState(VeilShaders.PARTICLE_ADD);

        boolean additive = false;
        ResourceLocation texture = Veil.veilPath("textures/special/blank.png");

        RenderType.CompositeState state = CompositeState.builder().setShaderState(PARTICLE_ADD).setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setWriteMaskState(COLOR_DEPTH_WRITE).setOutputState(VeilRenderBridge.outputState(BLOOM_BUFFER)).createCompositeState(false);
        THRUSTER_PARTICLE = create("vsch:quasar_particle_thruster_bloom", DefaultVertexFormat.PARTICLE, VertexFormat.Mode.QUADS, 131072, false, !additive, state);
    }
}
