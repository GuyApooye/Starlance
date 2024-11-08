package net.jcm.vsch.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.ShipTeleportData;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.entity.handling.VSEntityHandler;
import org.valkyrienskies.mod.common.entity.handling.VSEntityManager;
import org.valkyrienskies.mod.common.entity.handling.WorldEntityHandler;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeleportUtils {


    /**
     * This function took us like 3 days to make. You better appreciate it. <br>
     * </br>
     * It will teleport the given ship, using the level, to the
     * dimension with id of newDim at x, y, z. <br>
     * </br>
     * But most importantly, it will also teleport any players or entities that are
     * currently being dragged by the ship to the new dimension, and their correct
     * position relative to the ship that was moved.
     *
     * @param ship   The ship to move
     * @param level  The ships current level
     * @param newDim Normal dimension id string format (not VS format)
     * @param x x position in world to tp to
     * @param y y position in world to tp to
     * @param z z position in world to tp to
     */
    public static void DimensionTeleportShip(Ship ship, ServerLevel level, String newDim, double x, double y, double z) {

        // ----- Prepare dimension destination ----- //

        // Convert back into a stupid stupid VS dimension string
        String VSnewDimension = VSCHUtils.dimToVSDim(newDim);

        // Prepare ship teleport info for later
        ShipTeleportData teleportData = new ShipTeleportDataImpl(new Vector3d(x, y, z), ship.getTransform().getShipToWorldRotation(), new Vector3d(), new Vector3d(), VSnewDimension, null);

        // ----- AABB magic ----- //

        // Get the AABB of the last tick and the AABB of the current tick
        AABB prevWorldAABB = VectorConversionsMCKt.toMinecraft(VSCHUtils.transformToAABBd(ship.getPrevTickTransform(), ship.getShipAABB())).inflate(10);
        AABB currentWorldAABB = VectorConversionsMCKt.toMinecraft(ship.getWorldAABB()).inflate(10);

        // Combine the AABB's into one big one
        //		AABB totalAABB = currentWorldAABB.minmax(prevWorldAABB);

        Vec3 oldShipCenter = prevWorldAABB.deflate(10).getCenter();
        Vec3 newoldShipCenter = currentWorldAABB.deflate(10).getCenter();

        // ---------- //

        // ----- Get all entities BEFORE teleporting ship ----- //

        // Save the distances from entities to the ship for afterwards
        Map<Entity, Vec3> entityOffsets = new HashMap<Entity, Vec3>();
        Map<Entity, Vec3> shipyardentityOffsets = new HashMap<Entity, Vec3>();

        // Save entities that actually need to be teleported

        entityOffsets.putAll(calculateOffsets(level,prevWorldAABB,oldShipCenter,false));
        entityOffsets.putAll(calculateOffsets(level,currentWorldAABB,newoldShipCenter,false));
        shipyardentityOffsets.putAll(calculateOffsets(level,currentWorldAABB,newoldShipCenter,true));
        // ---------- //

        // ----- Teleport ship ----- //

        // Do we actually use this? Eh can't be bothered to check
        // Yes we do. Don't remove this
        ServerShipWorldCore shipWorld = VSGameUtilsKt.getShipObjectWorld(level);

        // Teleport ship to new dimension at origin
        ServerShip serverShip = (ServerShip) ship;
        shipWorld.teleportShip(serverShip, teleportData);

        // ---------- //

        // ----- Teleport entities AFTER ship ----- //

        // Get a level object from the VS dimension string of the dim we're going to
        ServerLevel newLevel = VSCHUtils.VSDimToLevel(level.getServer(), VSnewDimension);
        Vec3 newShipCenter = VectorConversionsMCKt.toMinecraft(ship.getWorldAABB()).getCenter();
        Vec3 newShipyardCenter = VectorConversionsMCKt.toMinecraft(ship.getShipAABB().center(new Vector3d(0, 0, 0)));

        teleportEntitiesToOffsets(entityOffsets,newShipCenter,newLevel);
        teleportEntitiesToOffsets(shipyardentityOffsets,newShipyardCenter,newLevel);


    }

    public static HashMap<Entity, Vec3> calculateOffsets(ServerLevel level, AABB aabb, Vec3 center, Boolean shipyard) {

        // Find all entities nearby the ship
        HashMap<Entity, Vec3> offsets = new HashMap<Entity, Vec3>();
        for (Entity entity : level.getEntities(null, aabb)) {

            VSEntityHandler handler = VSEntityManager.INSTANCE.getHandler(entity);
            if (handler.getClass() == WorldEntityHandler.class && !shipyard) {

                // If the entity is riding another
                if (entity.getVehicle() != null) {
                    // Dismount them
                    entity.dismountTo(entity.getX(), entity.getY(), entity.getZ());
                }

                // Get the offset from the entities position to the ship
                Vec3 entityShipOffset = entity.getPosition(0).subtract(center);

                offsets.put(entity, entityShipOffset);

            } else if (shipyard) {
                // If the entity is riding another
                if (entity.getVehicle() != null) {
                    // Dismount them
                    entity.dismountTo(entity.getX(), entity.getY(), entity.getZ());
                }

                // Get the offset from the entities position to the ship
                Vec3 entityShipOffset = entity.getPosition(0).subtract(center);

                offsets.put(entity, entityShipOffset);
            }


        }
        return offsets;
    }

    public static void teleportEntitiesToOffsets(Map<Entity, Vec3> entityOffsets, Vec3 center, ServerLevel newLevel){
        for (Entity entity : entityOffsets.keySet()) {
            if (entity instanceof ServerPlayer) {

                Vec3 shipOffset = entityOffsets.get(entity);
                Vec3 newPosition = center.add(shipOffset);

                // Players need a different teleport command to entities
                ((ServerPlayer) entity).teleportTo(newLevel, newPosition.x, newPosition.y, newPosition.z, entity.getYRot(), entity.getXRot());
            } else {
                Vec3 shipOffset = entityOffsets.get(entity);
                Vec3 newPosition = center.add(shipOffset);

                entity.teleportTo(newLevel, newPosition.x, newPosition.y, newPosition.z, null, entity.getYRot(), entity.getXRot());
            }
        }
    }
}