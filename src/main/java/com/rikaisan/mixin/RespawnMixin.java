package com.rikaisan.mixin;

import com.rikaisan.RespectWorldSpawn;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import net.minecraft.world.SaveProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public abstract class RespawnMixin {
	@Shadow public abstract ServerWorld getServerWorld();

	// Fix spawn angle not being used
	@ModifyArg(method = "<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/server/world/ServerWorld;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/network/packet/c2s/common/SyncedClientOptions;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;refreshPositionAndAngles(Lnet/minecraft/util/math/Vec3d;FF)V"), index = 1)
	private float useWorldSpawnAngle(float requestedYaw) {
		return getServerWorld().getGameRules().getBoolean(RespectWorldSpawn.RESPECT_SPAWN_ROTATION) ? getServerWorld().getSpawnAngle() : requestedYaw;
	}

	// No need to do much, Adventure mode already has this behaviour.
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/SaveProperties;getGameMode()Lnet/minecraft/world/GameMode;"), method = "getWorldSpawnPos")
	private GameMode getGameMode(SaveProperties instance) {
		return getServerWorld().getGameRules().getBoolean(RespectWorldSpawn.RESPECT_SPAWN_HEIGHT) ? GameMode.ADVENTURE : instance.getGameMode();
	}
}