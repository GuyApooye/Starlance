package net.jcm.vsch.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.post.PostProcessingManager;
import net.lointain.cosmos.client.particle.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {BlueThrustedsmallParticle.class, BluethrustedParticle.class, BluethrustedParticle.class, DarkThrustParticle.class, RedblastParticle.class, PlumeParticle.class, ThrustedlargeParticle.class, ThrustedParticle.class}, remap = false)
public class CosmosParticlesMixin {
    private static final ResourceLocation BLOOM_BUFFER = new ResourceLocation("vsch", "bloom");
    private static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
        public void begin(BufferBuilder p_107455_, TextureManager p_107456_) {
            AdvancedFbo buffer = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(BLOOM_BUFFER);
            if (buffer != null) buffer.bind(false);
            RenderSystem.disableBlend();
            RenderSystem.disableCull();
            RenderSystem.depthMask(true);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            p_107455_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        public void end(Tesselator p_107458_) {
            p_107458_.end();
            AdvancedFbo.getMainFramebuffer().bind(true);
            PostProcessingManager manager = VeilRenderSystem.renderer().getPostProcessingManager();
            PostPipeline pipeline = manager.getPipeline(BLOOM_BUFFER);
            if (pipeline != null) manager.runPipeline(pipeline);
        }

        public String toString() {
            return "PARTICLE_SHEET_LIT_BLOOM";
        }
    };

    @Inject(method = "getRenderType", at = {@At("HEAD")}, cancellable = true)
    private void getBloomRenderType(CallbackInfoReturnable<ParticleRenderType> cir) {
        cir.setReturnValue(RENDER_TYPE);
    }
}
