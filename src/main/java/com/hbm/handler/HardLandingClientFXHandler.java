package com.hbm.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.hbm.items.gear.ArmorFSB;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HardLandingClientFXHandler {

    private static final int BREAKER_ID_BASE = 196813;

    private boolean airborne;
    private float maxFallDistance;

    private final List<CrackEntry> activeCracks = new ArrayList<>();

    private int shakeTicks;
    private float shakeStrength;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.isGamePaused()) return;

        if (mc.player == null || mc.world == null) {
            clearAll(mc);
            return;
        }

        tickCracks(mc);
        tickShake();

        EntityPlayerSP player = mc.player;

        if (!player.onGround) {
            airborne = true;
            if (player.fallDistance > maxFallDistance) {
                maxFallDistance = player.fallDistance;
            }
            return;
        }

        if (airborne) {
            float dist = maxFallDistance;
            airborne = false;
            maxFallDistance = 0F;

            if (dist > 3F && hasHardLanding(player)) {
                triggerLandingFx(mc, player, dist);
            }
        }
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if (shakeTicks <= 0 || shakeStrength <= 0F) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;

        float t = mc.player.ticksExisted + (float) event.getRenderPartialTicks();
        float fade = Math.min(1F, shakeTicks / 10F);
        float amp = shakeStrength * fade;

        event.setYaw(event.getYaw() + (float) Math.sin(t * 1.7F) * amp * 1.4F);
        event.setPitch(event.getPitch() + (float) Math.cos(t * 2.3F) * amp * 1.0F);
        event.setRoll(event.getRoll() + (float) Math.sin(t * 2.9F) * amp * 0.7F);
    }

        private void triggerLandingFx(Minecraft mc, EntityPlayerSP player, float dist) {

        BlockPos center = getImpactPos(mc, player);
        IBlockState centerState = mc.world.getBlockState(center);

        if (centerState.getBlock().isAir(centerState, mc.world, center) || centerState.getMaterial().isLiquid()) {
            return;
        }

        spawnLandingParticles(mc, center, dist);

        if (dist >= 15F) {
            float radius;
            int tendrilCount;
            int tendrilMaxLen;
            int stage;
            int duration;

            if (dist >= 40F) {
                radius = 3.0F + dist * 0.05F;
                tendrilCount = 8 + mc.world.rand.nextInt(6);
                tendrilMaxLen = 3 + mc.world.rand.nextInt(3);
                stage = Math.min(9, 6 + (int) ((dist - 40F) * 0.2F));
                duration = 150 + mc.world.rand.nextInt(51);
            } else if (dist >= 25F) {
                radius = 2.0F + (dist - 25F) * 0.06F;
                tendrilCount = 4 + mc.world.rand.nextInt(4);
                tendrilMaxLen = 2 + mc.world.rand.nextInt(2);
                stage = Math.min(9, 4 + (int) ((dist - 25F) * 0.4F));
                duration = 100 + mc.world.rand.nextInt(61);
            } else {
                radius = 1.2F + (dist - 15F) * 0.05F;
                tendrilCount = 2 + mc.world.rand.nextInt(2);
                tendrilMaxLen = 1 + mc.world.rand.nextInt(2);
                stage = Math.min(9, 2 + (int) ((dist - 15F) * 0.3F));
                duration = 60 + mc.world.rand.nextInt(41);
            }

            applyCracks(mc, center, radius, tendrilCount, tendrilMaxLen, stage, duration);
        }

        if (dist >= 40F) {
            shakeTicks = Math.min(45, 18 + (int) ((dist - 40F) * 0.5F));
            shakeStrength = Math.min(8.0F, 4.0F + (dist - 40F) * 0.15F);
        } else if (dist >= 25F) {
            shakeTicks = Math.min(35, 12 + (int) ((dist - 25F) * 0.8F));
            shakeStrength = Math.min(5.5F, 2.5F + (dist - 25F) * 0.15F);
        } else if (dist >= 15F) {
            shakeTicks = Math.min(18, 6 + (int) ((dist - 15F) * 0.6F));
            shakeStrength = Math.min(2.5F, 1.0F + (dist - 15F) * 0.12F);
        } else if (dist >= 5F) {
            shakeTicks = Math.min(8, 3 + (int) ((dist - 5F) * 0.3F));
            shakeStrength = Math.min(0.6F, 0.15F + (dist - 5F) * 0.04F);
        }
    }

        private void spawnLandingParticles(Minecraft mc, BlockPos pos, float dist) {
        int dustCount;
        int crackCount;
        double radius;
        double speed;

        if (dist >= 40F) {
            dustCount = 200;
            crackCount = 100;
            radius = 2.5D;
            speed = 0.55D;
        } else if (dist >= 25F) {
            dustCount = Math.min(160, 40 + Math.round((dist - 25F) * 8F));
            crackCount = Math.min(80, 20 + Math.round((dist - 25F) * 4F));
            radius = Math.min(2.0D, 0.8D + (dist - 25F) * 0.08D);
            speed = Math.min(0.45D, 0.15D + (dist - 25F) * 0.02D);
        } else if (dist >= 15F) {
            dustCount = Math.min(60, 15 + Math.round((dist - 15F) * 4.5F));
            crackCount = Math.min(30, 6 + Math.round((dist - 15F) * 2.4F));
            radius = Math.min(1.2D, 0.4D + (dist - 15F) * 0.08D);
            speed = Math.min(0.25D, 0.1D + (dist - 15F) * 0.015D);
        } else {
            dustCount = Math.min(20, 6 + Math.round(dist * 1.2F));
            crackCount = Math.min(8, 2 + Math.round(dist * 0.5F));
            radius = 0.4D;
            speed = 0.1D;
        }

        int particleRadius = dist >= 40F ? 3 : (dist >= 25F ? 2 : 1);

        List<BlockPos> surfacePositions = new ArrayList<>();
        List<Integer> surfaceStateIds = new ArrayList<>();
        for (int dx = -particleRadius; particleRadius >= dx; dx++) {
            for (int dz = -particleRadius; particleRadius >= dz; dz++) {
                BlockPos check = pos.add(dx, 0, dz);
                IBlockState st = mc.world.getBlockState(check);
                if (!st.getBlock().isAir(st, mc.world, check) && !st.getMaterial().isLiquid()) {
                    surfacePositions.add(check);
                    surfaceStateIds.add(Block.getStateId(st));
                }
            }
        }

        if (surfacePositions.isEmpty()) {
            surfacePositions.add(pos);
            surfaceStateIds.add(Block.getStateId(mc.world.getBlockState(pos)));
        }

        Random rand = mc.world.rand;
        int surfaceCount = surfacePositions.size();

        for (int i = 0; dustCount > i; i++) {
            int idx = rand.nextInt(surfaceCount);
            BlockPos bp = surfacePositions.get(idx);
            int stateId = surfaceStateIds.get(idx);

            double px = bp.getX() + 0.5D + (rand.nextDouble() - 0.5D) * radius * 2D;
            double py = bp.getY() + 1.02D;
            double pz = bp.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * radius * 2D;
            double mx = (rand.nextDouble() - 0.5D) * speed * 2D;
            double my = rand.nextDouble() * speed * 1.5D;
            double mz = (rand.nextDouble() - 0.5D) * speed * 2D;

            mc.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, px, py, pz, mx, my, mz, stateId);
        }

        for (int i = 0; crackCount > i; i++) {
            int idx = rand.nextInt(surfaceCount);
            BlockPos bp = surfacePositions.get(idx);
            int stateId = surfaceStateIds.get(idx);

            double px = bp.getX() + 0.5D + (rand.nextDouble() - 0.5D) * radius * 1.6D;
            double py = bp.getY() + 1.01D;
            double pz = bp.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * radius * 1.6D;
            double mx = (rand.nextDouble() - 0.5D) * speed * 1.8D;
            double my = rand.nextDouble() * speed * 1.2D;
            double mz = (rand.nextDouble() - 0.5D) * speed * 1.8D;

            mc.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, px, py, pz, mx, my, mz, stateId);
        }
    }


    private void applyCracks(Minecraft mc, BlockPos center, float baseRadius, int tendrilCount, int tendrilMaxLen, int stage, int duration) {
        clearCracks(mc);

        Random rand = mc.world.rand;
        Set<BlockPos> crackPositions = new HashSet<>();

        int scanRange = (int) Math.ceil(baseRadius + tendrilMaxLen + 2);

        for (int dx = -scanRange; dx <= scanRange; dx++) {
            for (int dz = -scanRange; dz <= scanRange; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                double noise = (rand.nextDouble() - 0.5D) * 1.2D;
                double effectiveDist = dist + noise;

                double probability;
                if (effectiveDist <= 0.8D) {
                    probability = 1.0D;
                } else if (effectiveDist <= baseRadius * 0.5D) {
                    probability = 0.9D - (effectiveDist - 0.8D) * 0.3D;
                } else if (effectiveDist <= baseRadius) {
                    probability = 0.75D - (effectiveDist - baseRadius * 0.5D) * 1.0D;
                } else if (effectiveDist <= baseRadius + 1.0D) {
                    probability = 0.25D - (effectiveDist - baseRadius) * 0.2D;
                } else {
                    probability = 0.0D;
                }

                if (rand.nextDouble() < probability) {
                    crackPositions.add(center.add(dx, 0, dz));
                }
            }
        }

        for (int t = 0; t < tendrilCount; t++) {
            double angle = rand.nextDouble() * Math.PI * 2.0D;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            int cx = (int) Math.round(cos * (baseRadius * 0.8D));
            int cz = (int) Math.round(sin * (baseRadius * 0.8D));

            int length = 1 + rand.nextInt(tendrilMaxLen);
            double curAngle = angle;

            for (int step = 0; step < length; step++) {
                curAngle += (rand.nextDouble() - 0.5D) * 1.2D;
                cos = Math.cos(curAngle);
                sin = Math.sin(curAngle);

                cx += (int) Math.round(cos);
                cz += (int) Math.round(sin);

                crackPositions.add(center.add(cx, 0, cz));

                if (rand.nextInt(3) == 0) {
                    int bx = cx + rand.nextInt(3) - 1;
                    int bz = cz + rand.nextInt(3) - 1;
                    crackPositions.add(center.add(bx, 0, bz));
                }

                if (rand.nextInt(4) == 0) {
                    int bx = cx + (int) Math.round(Math.cos(curAngle + Math.PI * 0.5D));
                    int bz = cz + (int) Math.round(Math.sin(curAngle + Math.PI * 0.5D));
                    crackPositions.add(center.add(bx, 0, bz));
                }
            }
        }

        int id = BREAKER_ID_BASE;
        for (BlockPos bp : crackPositions) {
            IBlockState st = mc.world.getBlockState(bp);
            if (st.getBlock().isAir(st, mc.world, bp) || st.getMaterial().isLiquid()) {
                continue;
            }

            double dx = bp.getX() - center.getX();
            double dz = bp.getZ() - center.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);

            int blockStage;
            if (dist <= 0.8D) {
                blockStage = stage;
            } else if (dist <= baseRadius * 0.5D) {
                blockStage = Math.max(1, stage - (int) ((dist - 0.8D) * 1.5D));
            } else if (dist <= baseRadius) {
                blockStage = Math.max(0, (int) (stage * 0.5F) - (int) ((dist - baseRadius * 0.5D) * 2.0D));
            } else {
                blockStage = Math.max(0, 1 - (int) ((dist - baseRadius) * 1.5D));
            }

            blockStage = Math.min(9, Math.max(0, blockStage + rand.nextInt(2) - 1));

            int blockDuration = (int) (duration - dist * 8.0D - rand.nextInt(20));
            if (blockDuration < 30) blockDuration = 30;

            mc.renderGlobal.sendBlockBreakProgress(id, bp, blockStage);
            activeCracks.add(new CrackEntry(id, bp, blockDuration));
            id++;
        }
    }

        private void tickCracks(Minecraft mc) {
        if (activeCracks.isEmpty()) return;

        Iterator<CrackEntry> it = activeCracks.iterator();
        while (it.hasNext()) {
            CrackEntry entry = it.next();
            entry.ticksLeft--;
            if (0 >= entry.ticksLeft) {
                mc.renderGlobal.sendBlockBreakProgress(entry.breakerId, entry.pos, -1);
                it.remove();
            }
        }
    }

    private void clearCracks(Minecraft mc) {
        for (CrackEntry entry : activeCracks) {
            mc.renderGlobal.sendBlockBreakProgress(entry.breakerId, entry.pos, -1);
        }
        activeCracks.clear();
    }

    private void tickShake() {
        if (shakeTicks > 0) {
            shakeTicks--;
            shakeStrength *= 0.87F;
            if (shakeStrength < 0.02F) {
                shakeStrength = 0F;
            }
        }
    }

    private BlockPos getImpactPos(Minecraft mc, EntityPlayerSP player) {
        BlockPos pos = new BlockPos(player.posX, player.posY - 0.2D, player.posZ);
        IBlockState state = mc.world.getBlockState(pos);

        if (state.getBlock().isAir(state, mc.world, pos) || state.getMaterial().isReplaceable()) {
            pos = pos.down();
        }

        return pos;
    }

    private void clearAll(Minecraft mc) {
        airborne = false;
        maxFallDistance = 0F;
        shakeTicks = 0;
        shakeStrength = 0F;

        if (mc.renderGlobal != null) {
            clearCracks(mc);
        }
    }

        private boolean hasHardLanding(EntityPlayerSP player) {
        return hasHardLanding(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD))
                && hasHardLanding(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST))
                && hasHardLanding(player.getItemStackFromSlot(EntityEquipmentSlot.LEGS))
                && hasHardLanding(player.getItemStackFromSlot(EntityEquipmentSlot.FEET));
    }

    private boolean hasHardLanding(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (!(stack.getItem() instanceof ArmorFSB)) return false;
        return ((ArmorFSB) stack.getItem()).isHardLanding();
    }

    private static class CrackEntry {
        final int breakerId;
        final BlockPos pos;
        int ticksLeft;

        CrackEntry(int breakerId, BlockPos pos, int ticksLeft) {
            this.breakerId = breakerId;
            this.pos = pos;
            this.ticksLeft = ticksLeft;
        }
    }
}
