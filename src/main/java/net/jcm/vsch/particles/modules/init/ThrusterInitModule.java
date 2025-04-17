package net.jcm.vsch.particles.modules.init;

import foundry.veil.api.quasar.emitters.module.InitParticleModule;
import foundry.veil.api.quasar.particle.QuasarParticle;
import net.jcm.vsch.client.renderer.VSCHRenderType;
import net.jcm.vsch.mixin.accessor.RenderDataAccessor;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class ThrusterInitModule implements InitParticleModule {

    private final Vector3d direction;
    private final Vector3f rotation;

    public ThrusterInitModule(Vector3d direction, Vector3f rotation) {
        this.direction = direction;
        this.rotation = rotation;
    }

    @Override
    public void init(QuasarParticle quasarParticle) {
        quasarParticle.getVelocity().set(direction);
        quasarParticle.getRotation().set(rotation);
        ((RenderDataAccessor)quasarParticle.getRenderData()).setRenderType(VSCHRenderType.thrusterParticle());
    }
}
