package com.hbm.handler.guncfg;

import java.util.ArrayList;
import java.util.Arrays;

import com.hbm.explosion.ExplosionLarge;
import com.hbm.explosion.ExplosionNT;
import com.hbm.explosion.ExplosionNT.ExAttrib;
import com.hbm.handler.BulletConfigSyncingUtil;
import com.hbm.handler.BulletConfiguration;
import com.hbm.handler.GunConfiguration;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.items.ModItems;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.lib.ModDamageSource;
import com.hbm.packet.AuxParticlePacketNT;
import com.hbm.potion.HbmPotion;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationKeyframe;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.HbmAnimations.AnimType;
import com.hbm.render.misc.RenderScreenOverlay.Crosshair;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class Gun4GaugeFactory {

	private static GunConfiguration getShotgunConfig() {
		GunConfiguration config = new GunConfiguration();

		config.rateOfFire = 15;
		config.roundsPerCycle = 1;
		config.gunMode = GunConfiguration.MODE_NORMAL;
		config.firingMode = GunConfiguration.FIRE_MANUAL;
		config.reloadDuration = 10;
		config.firingDuration = 0;
		config.ammoCap = 4;
		config.reloadType = GunConfiguration.RELOAD_SINGLE;
		config.allowsInfinity = true;
		config.hasSights = true;
		config.crosshair = Crosshair.L_CIRCLE;
		config.reloadSound = GunConfiguration.RSOUND_SHOTGUN;

		return config;
	}

	private static void add4GaugeAmmo(GunConfiguration config) {
		config.config = new ArrayList<>(Arrays.asList(
				BulletConfigSyncingUtil.G4_NORMAL,
				BulletConfigSyncingUtil.G4_SLUG,
				BulletConfigSyncingUtil.G4_FLECHETTE,
				BulletConfigSyncingUtil.G4_FLECHETTE_PHOSPHORUS,
				BulletConfigSyncingUtil.G4_EXPLOSIVE,
				BulletConfigSyncingUtil.G4_SEMTEX,
				BulletConfigSyncingUtil.G4_BALEFIRE,
				BulletConfigSyncingUtil.G4_KAMPF,
				BulletConfigSyncingUtil.G4_CANISTER,
				BulletConfigSyncingUtil.G4_SLEEK,
				BulletConfigSyncingUtil.G4_CLAW,
				BulletConfigSyncingUtil.G4_VAMPIRE,
				BulletConfigSyncingUtil.G4_VOID
		));
	}

	public static GunConfiguration getKS23Config() {
		GunConfiguration config = getShotgunConfig();

		config.durability = 3000;
		config.firingSound = HBMSoundHandler.revolverShootAlt;
		config.firingPitch = 0.65F;
		config.name = "KS-23";
		config.manufacturer = "Tulsky Oruzheiny Zavod";
		add4GaugeAmmo(config);

		return config;
	}

	public static GunConfiguration getSauerConfig() {
		GunConfiguration config = getShotgunConfig();

		config.rateOfFire = 20;
		config.ammoCap = 0;
		config.reloadType = GunConfiguration.RELOAD_NONE;
		config.firingMode = GunConfiguration.FIRE_AUTO;
		config.durability = 3000;
		config.firingSound = HBMSoundHandler.sauerGun;
		config.firingPitch = 1.0F;
		config.name = "Sauer Shotgun";
		config.manufacturer = "Cube 2: Sauerbraten";

		config.animations.put(AnimType.CYCLE, new BusAnimation()
				.addBus("SAUER_RECOIL", new BusAnimationSequence()
						.addKeyframe(new BusAnimationKeyframe(0.5, 0, 0, 50))
						.addKeyframe(new BusAnimationKeyframe(0, 0, 0, 50)))
				.addBus("SAUER_TILT", new BusAnimationSequence()
						.addKeyframe(new BusAnimationKeyframe(0.0, 0, 0, 200))
						.addKeyframe(new BusAnimationKeyframe(0, 0, 30, 150))
						.addKeyframe(new BusAnimationKeyframe(45, 0, 30, 150))
						.addKeyframe(new BusAnimationKeyframe(45, 0, 30, 200))
						.addKeyframe(new BusAnimationKeyframe(0, 0, 30, 150))
						.addKeyframe(new BusAnimationKeyframe(0, 0, 0, 150)))
				.addBus("SAUER_COCK", new BusAnimationSequence()
						.addKeyframe(new BusAnimationKeyframe(0, 0, 0, 500))
						.addKeyframe(new BusAnimationKeyframe(1, 0, 0, 100))
						.addKeyframe(new BusAnimationKeyframe(0, 0, 0, 100)))
				.addBus("SAUER_SHELL_EJECT", new BusAnimationSequence()
						.addKeyframe(new BusAnimationKeyframe(0, 0, 0, 500))
						.addKeyframe(new BusAnimationKeyframe(0, 0, 1, 500))));

		add4GaugeAmmo(config);
		return config;
	}

	public static BulletConfiguration get4GaugeConfig() {
		BulletConfiguration bullet = BulletConfigFactory.standardBuckshotConfig();

		bullet.ammo = ModItems.ammo_4gauge;
		bullet.dmgMin = 3;
		bullet.dmgMax = 6;
		bullet.bulletsMin *= 2;
		bullet.bulletsMax *= 2;

		return bullet;
	}

	public static BulletConfiguration get4GaugeSlugConfig() {
		BulletConfiguration bullet = BulletConfigFactory.standardBulletConfig();

		bullet.ammo = ModItems.ammo_4gauge_slug;
		bullet.dmgMin = 15;
		bullet.dmgMax = 20;
		bullet.wear = 7;
		bullet.style = BulletConfiguration.STYLE_NORMAL;

		return bullet;
	}

	private static BulletConfiguration get4GaugeGrenadeBase(Item ammo) {
		BulletConfiguration bullet = BulletConfigFactory.standardGrenadeConfig();

		bullet.ammo = ammo;
		bullet.velocity *= 2;
		bullet.gravity *= 2;
		bullet.dmgMin = 10;
		bullet.dmgMax = 15;
		bullet.wear = 25;
		bullet.trail = 1;

		return bullet;
	}

	public static BulletConfiguration get4GaugeExplosiveConfig() {
		return get4GaugeGrenadeBase(ModItems.ammo_4gauge_explosive);
	}

	public static BulletConfiguration get4GaugeSleekConfig() {
		BulletConfiguration bullet = BulletConfigFactory.standardAirstrikeConfig();
		bullet.ammo = ModItems.ammo_4gauge_sleek;
		return bullet;
	}

	public static BulletConfiguration get4GaugeFlechetteConfig() {
		BulletConfiguration bullet = BulletConfigFactory.standardBuckshotConfig();

		bullet.ammo = ModItems.ammo_4gauge_flechette;
		bullet.dmgMin = 5;
		bullet.dmgMax = 8;
		bullet.bulletsMin *= 2;
		bullet.bulletsMax *= 2;
		bullet.wear = 15;
		bullet.style = BulletConfiguration.STYLE_FLECHETTE;
		bullet.HBRC = 2;
		bullet.LBRC = 95;

		return bullet;
	}

	public static BulletConfiguration get4GaugeFlechettePhosphorusConfig() {
		BulletConfiguration bullet = get4GaugeFlechetteConfig();

		bullet.ammo = ModItems.ammo_4gauge_flechette_phosphorus;
		bullet.incendiary = 5;

		PotionEffect eff = new PotionEffect(HbmPotion.phosphorus, 20 * 20, 0, true, false);
		eff.getCurativeItems().clear();
		bullet.effects = new ArrayList<>();
		bullet.effects.add(eff);

		bullet.bImpact = (b, x, y, z) -> {
			NBTTagCompound data = new NBTTagCompound();
			data.setString("type", "vanillaburst");
			data.setString("mode", "flame");
			data.setInteger("count", 15);
			data.setDouble("motion", 0.05D);
			PacketThreading.createAllAroundThreadedPacket(
					new AuxParticlePacketNT(data, b.posX, b.posY, b.posZ),
					new TargetPoint(b.dimension, b.posX, b.posY, b.posZ, 50));
		};

		return bullet;
	}

	public static BulletConfiguration get4GaugeMiningConfig() {
		BulletConfiguration bullet = get4GaugeGrenadeBase(ModItems.ammo_4gauge_semtex);
		bullet.explosive = 0.0F;

		bullet.bImpact = (b, x, y, z) -> {
			if (b.world.isRemote) return;
			ExplosionNT explosion = new ExplosionNT(b.world, null, b.posX, b.posY, b.posZ, 4);
			explosion.atttributes.add(ExAttrib.ALLDROP);
			explosion.atttributes.add(ExAttrib.NOHURT);
			explosion.explode();
			ExplosionLarge.spawnParticles(b.world, b.posX, b.posY, b.posZ, 15);
		};

		return bullet;
	}

	public static BulletConfiguration get4GaugeBalefireConfig() {
		BulletConfiguration bullet = get4GaugeGrenadeBase(ModItems.ammo_4gauge_balefire);
		bullet.explosive = 0.0F;

		bullet.bImpact = (b, x, y, z) -> {
			if (b.world.isRemote) return;
			ExplosionNT explosion = new ExplosionNT(b.world, null, b.posX, b.posY, b.posZ, 6);
			explosion.atttributes.add(ExAttrib.BALEFIRE);
			explosion.explode();
			ExplosionLarge.spawnParticles(b.world, b.posX, b.posY, b.posZ, 30);
		};

		return bullet;
	}

	public static BulletConfiguration getGrenadeKampfConfig() {
		BulletConfiguration bullet = BulletConfigFactory.standardRocketConfig();

		bullet.ammo = ModItems.ammo_4gauge_kampf;
		bullet.spread = 0.0F;
		bullet.gravity = 0.0D;
		bullet.wear = 15;
		bullet.explosive = 3.5F;
		bullet.style = BulletConfiguration.STYLE_GRENADE;
		bullet.trail = 4;
		bullet.vPFX = "smoke";

		return bullet;
	}

		public static BulletConfiguration getGrenadeCanisterConfig() {
		BulletConfiguration bullet = BulletConfigFactory.standardRocketConfig();

		bullet.ammo = ModItems.ammo_4gauge_canister;
		bullet.spread = 0.0F;
		bullet.gravity = 0.0D;
		bullet.wear = 15;
		bullet.explosive = 1F;
		bullet.style = BulletConfiguration.STYLE_GRENADE;
		bullet.trail = 4;
		bullet.vPFX = "smoke";

		bullet.bUpdate = b -> {
			if (!b.world.isRemote && b.ticksExisted > 10) {
				b.setDead();
				for (int i = 0; 50 > i; i++) {
					com.hbm.entity.projectile.EntityBulletBase bolt = new com.hbm.entity.projectile.EntityBulletBase(b.world, BulletConfigSyncingUtil.M44_AP);
					bolt.setPosition(b.posX, b.posY, b.posZ);
					bolt.shoot(b.motionX, b.motionY, b.motionZ, 0.25F, 0.1F);
					b.world.spawnEntity(bolt);
				}
			}
		};

		return bullet;
	}

	public static BulletConfiguration get4GaugeClawConfig() {
		BulletConfiguration bullet = get4GaugeConfig();

		bullet.ammo = ModItems.ammo_4gauge_claw;
		bullet.dmgMin = 6;
		bullet.dmgMax = 9;
		bullet.bulletsMin *= 2;
		bullet.bulletsMax *= 2;
		bullet.leadChance = 100;

		bullet.bHurt = (b, hit) -> {
			if (!b.world.isRemote && hit instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase) hit;
				float f = Math.max(0, living.getHealth() - 2);
				living.setHealth(f);
				if (f == 0) living.onDeath(ModDamageSource.lead);
			}
		};

		return bullet;
	}

	public static BulletConfiguration get4GaugeVampireConfig() {
		BulletConfiguration bullet = get4GaugeConfig();

		bullet.ammo = ModItems.ammo_4gauge_vampire;
		bullet.dmgMin = 6;
		bullet.dmgMax = 9;
		bullet.bulletsMin *= 2;
		bullet.bulletsMax *= 2;
		bullet.leadChance = 100;
		bullet.style = BulletConfiguration.STYLE_FLECHETTE;

		bullet.bHurt = (b, hit) -> {
			if (!b.world.isRemote && hit instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase) hit;
				if (living.isEntityUndead()) {
					living.attackEntityFrom(ModDamageSource.lead, 10.0F);
				}
				if (b.shooter != null) {
					b.shooter.heal(1.5F);
				}
			}
		};

		return bullet;
	}

	public static BulletConfiguration get4GaugeVoidConfig() {
		BulletConfiguration bullet = get4GaugeConfig();

		bullet.ammo = ModItems.ammo_4gauge_void;
		bullet.dmgMin = 6;
		bullet.dmgMax = 9;
		bullet.bulletsMin *= 2;
		bullet.bulletsMax *= 2;
		bullet.leadChance = 0;

		bullet.bHurt = (b, hit) -> {
			if (!b.world.isRemote && hit instanceof EntityPlayer) {
				((EntityPlayer) hit).inventory.dropAllItems();
			}
		};

		return bullet;
	}
}
