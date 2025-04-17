package net.jcm.vsch.mixin.accessor;

import foundry.veil.api.quasar.particle.RenderData;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RenderData.class, remap = false)
public interface RenderDataAccessor {
    @Accessor("renderType")
    RenderType getRenderType();

    @Accessor("renderType")
    void setRenderType(RenderType renderType);
}
