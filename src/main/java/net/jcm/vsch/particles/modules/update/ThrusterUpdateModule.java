package net.jcm.vsch.particles.modules.update;

import foundry.veil.api.quasar.emitters.module.UpdateParticleModule;
import foundry.veil.api.quasar.particle.QuasarParticle;

public class ThrusterUpdateModule implements UpdateParticleModule {
    @Override
    public void update(QuasarParticle quasarParticle) {
        quasarParticle.setRadius(quasarParticle.getRadius() * 0.9f);
    }
}
