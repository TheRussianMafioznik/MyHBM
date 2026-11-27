package com.hbm.handler;

import java.util.ArrayList;
import java.util.List;

import com.hbm.items.ModItems;
import com.hbm.lib.Library;
import com.hbm.potion.HbmPotion;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ArmorRegistry.HazardClass;
import com.hbm.util.Compat;
import com.hbm.util.I18nUtil;

import api.hbm.item.IGasMask;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class ArmorUtil {

	public static void register() {
		ArmorRegistry.registerHazard(ModItems.gas_mask_filter, HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.GAS_CHLORINE, HazardClass.BACTERIA, HazardClass.NERVE_AGENT);
		ArmorRegistry.registerHazard(ModItems.gas_mask_filter_mono, HazardClass.PARTICLE_COARSE, HazardClass.GAS_MONOXIDE);
		ArmorRegistry.registerHazard(ModItems.gas_mask_filter_combo, HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.GAS_CHLORINE, HazardClass.BACTERIA, HazardClass.GAS_MONOXIDE, HazardClass.NERVE_AGENT);
		ArmorRegistry.registerHazard(ModItems.gas_mask_filter_radon, HazardClass.RAD_GAS, HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.GAS_CHLORINE, HazardClass.BACTERIA, HazardClass.GAS_MONOXIDE, HazardClass.NERVE_AGENT);
		ArmorRegistry.registerHazard(ModItems.gas_mask_filter_rag, HazardClass.PARTICLE_COARSE);
		ArmorRegistry.registerHazard(ModItems.gas_mask_filter_piss, HazardClass.PARTICLE_COARSE, HazardClass.GAS_CHLORINE);

		ArmorRegistry.registerHazard(ModItems.gas_mask, HazardClass.SAND, HazardClass.LIGHT);
		ArmorRegistry.registerHazard(ModItems.gas_mask_m65, HazardClass.SAND);
		ArmorRegistry.registerHazard(ModItems.mask_damp, HazardClass.PARTICLE_COARSE);
		ArmorRegistry.registerHazard(ModItems.mask_piss, HazardClass.PARTICLE_COARSE, HazardClass.GAS_CHLORINE);
		
		ArmorRegistry.registerHazard(ModItems.goggles, HazardClass.LIGHT, HazardClass.SAND);
		ArmorRegistry.registerHazard(ModItems.ashglasses, HazardClass.LIGHT, HazardClass.SAND);

		ArmorRegistry.registerHazard(ModItems.attachment_mask, HazardClass.SAND);
		ArmorRegistry.registerHazard(ModItems.spider_milk, HazardClass.LIGHT);
		
		ArmorRegistry.registerHazard(ModItems.asbestos_helmet, HazardClass.SAND, HazardClass.LIGHT);
		ArmorRegistry.registerHazard(ModItems.hazmat_helmet, HazardClass.SAND);
		ArmorRegistry.registerHazard(ModItems.hazmat_helmet_red, HazardClass.SAND);
		ArmorRegistry.registerHazard(ModItems.hazmat_helmet_grey, HazardClass.SAND);
		ArmorRegistry.registerHazard(ModItems.liquidator_helmet, HazardClass.LIGHT, HazardClass.SAND);
		ArmorRegistry.registerHazard(ModItems.hazmat_paa_helmet, HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.LIGHT, HazardClass.SAND);
		ArmorRegistry.registerHazard(ModItems.paa_helmet, HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.GAS_CHLORINE, HazardClass.BACTERIA, HazardClass.GAS_MONOXIDE, HazardClass.LIGHT, HazardClass.SAND);
		ArmorRegistry.registerHazard(ModItems.t45_helmet, HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.GAS_CHLORINE, HazardClass.BACTERIA, HazardClass.GAS_MONOXIDE, HazardClass.LIGHT, HazardClass.SAND);
		ArmorRegistry.registerHazard(ModItems.t51_helmet, HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.GAS_CHLORINE, HazardClass.BACTERIA, HazardClass.GAS_MONOXIDE, HazardClass.LIGHT, HazardClass.SAND);
		ArmorRegistry.registerHazard(ModItems.ajr_helmet, HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.GAS_CHLORINE, HazardClass.BACTERIA, HazardClass.GAS_MONOXIDE, HazardClass.LIGHT, HazardClass.SAND);
		ArmorRegistry.registerHazard(ModItems.ajro_helmet, HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.GAS_CHLORINE, HazardClass.BACTERIA, HazardClass.GAS_MONOXIDE, HazardClass.LIGHT, HazardClass.SAND);
		ArmorRegistry.registerHazard(ModItems.ajr_alpha_helmet, HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.GAS_CHLORINE, HazardClass.BACTERIA, HazardClass.GAS_MONOXIDE, HazardClass.LIGHT, HazardClass.SAND);
		ArmorRegistry.registerHazard(ModItems.rpa_helmet, HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.GAS_CHLORINE, HazardClass.BACTERIA, HazardClass.GAS_MONOXIDE, HazardClass.LIGHT, HazardClass.SAND);
		ArmorRegistry.registerHazard(ModItems.hev_helmet, HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.GAS_CHLORINE, HazardClass.BACTERIA, HazardClass.GAS_MONOXIDE, HazardClass.LIGHT, HazardClass.SAND);
		ArmorRegistry.registerHazard(ModItems.fau_helmet, HazardClass.RAD_GAS, HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.GAS_CHLORINE, HazardClass.BACTERIA, HazardClass.GAS_MONOXIDE, HazardClass.LIGHT, HazardClass.SAND);
		ArmorRegistry.registerHazard(ModItems.dns_helmet, HazardClass.RAD_GAS, HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.GAS_CHLORINE, HazardClass.BACTERIA, HazardClass.GAS_MONOXIDE, HazardClass.LIGHT, HazardClass.SAND, HazardClass.GAS_CORROSIVE);
		ArmorRegistry.registerHazard(ModItems.schrabidium_helmet, HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.GAS_CHLORINE, HazardClass.BACTERIA, HazardClass.GAS_MONOXIDE, HazardClass.LIGHT, HazardClass.SAND);
		ArmorRegistry.registerHazard(ModItems.euphemium_helmet, HazardClass.RAD_GAS, HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.GAS_CHLORINE, HazardClass.BACTERIA, HazardClass.GAS_MONOXIDE, HazardClass.LIGHT, HazardClass.SAND);
		
		//Ob ihr wirklich richtig steht, seht ihr wenn das Licht angeht!
		registerIfExists("gregtech", "gt.armor.hazmat.universal.head", HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.GAS_CHLORINE, HazardClass.BACTERIA, HazardClass.GAS_MONOXIDE, HazardClass.LIGHT, HazardClass.SAND);
		registerIfExists("gregtech", "gt.armor.hazmat.biochemgas.head", HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.GAS_CHLORINE, HazardClass.BACTERIA, HazardClass.GAS_MONOXIDE, HazardClass.LIGHT, HazardClass.SAND);
		registerIfExists("gregtech", "gt.armor.hazmat.radiation.head", HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE, HazardClass.GAS_CHLORINE, HazardClass.BACTERIA, HazardClass.GAS_MONOXIDE, HazardClass.LIGHT, HazardClass.SAND);
	}
	
	private static void registerIfExists(String domain, String name, HazardClass... classes) {
		Item item = Compat.tryLoadItem(domain, name);
		if(item != null)
			ArmorRegistry.registerHazard(item, classes);
	}
	
	public static boolean checkForFaraday(EntityPlayer player) {
		
		NonNullList<ItemStack> armor = player.inventory.armorInventory;
		
		if(armor.get(0).isEmpty() || armor.get(1).isEmpty() || armor.get(2).isEmpty() || armor.get(3).isEmpty()) return false;
		
		if(ArmorUtil.isFaradayArmor(armor.get(0)) &&
				ArmorUtil.isFaradayArmor(armor.get(1)) &&
				ArmorUtil.isFaradayArmor(armor.get(2)) &&
				ArmorUtil.isFaradayArmor(armor.get(3)))
			return true;
		
		return false;
	}

	public static boolean isFaradayArmor(ItemStack item) {
		if (HazmatRegistry.getCladding(item) > 0)
			return true;

		String name = item.getTranslationKey().toLowerCase(java.util.Locale.ROOT);
		for (String metal : metals) {
			if (name.contains(metal))
				return true;
		}
		return false;
	}
	
	public static boolean checkArmorNull(EntityLivingBase player, EntityEquipmentSlot slot) {
		return player.getItemStackFromSlot(slot) == null || player.getItemStackFromSlot(slot).isEmpty();
	}

	public static final String[] metals = new String[] {
			"chainmail",
			"iron",
			"silver",
			"gold",
			"platinum",
			"tin",
			"lead",
			"liquidator",
			"schrabidium",
			"euphemium",
			"steel",
			"cmb",
			"titanium",
			"alloy",
			"copper",
			"bronze",
			"electrum",
			"t45",
			"bj",
			"starmetal",
			"hazmat", //also count because rubber is insulating
			"rubber",
			"hev",
			"ajr",
			"rpa",
			"spacesuit"
	};

		public static void damageSuit(EntityPlayer player, int slot, int amount) {
		ItemStack armor = player.inventory.armorInventory.get(slot);
		if(armor.isEmpty())
			return;

		armor.setItemDamage(armor.getItemDamage() + amount);

		if(armor.getItemDamage() >= armor.getMaxDamage()) {
			player.inventory.armorInventory.set(slot, ItemStack.EMPTY);
		}
	}
	
	public static void resetFlightTime(EntityPlayer player) {
		if(player instanceof EntityPlayerMP) {
			EntityPlayerMP mp = (EntityPlayerMP) player;
			net.minecraftforge.fml.common.ObfuscationReflectionHelper.setPrivateValue(NetHandlerPlayServer.class, mp.connection, 0, "field_147365_f");
		}
	}

	public static boolean checkForFiend2(EntityPlayer player) {
		
		return ArmorUtil.checkArmorPiece(player, ModItems.jackt2, 2) && Library.checkForHeld(player, ModItems.shimmer_axe);
	}

	public static boolean checkForFiend(EntityPlayer player) {
		
		return ArmorUtil.checkArmorPiece(player, ModItems.jackt, 2) && Library.checkForHeld(player, ModItems.shimmer_sledge);
	}

	public static boolean checkPAA(EntityLivingBase player){
		return checkArmor(player, ModItems.paa_helmet, ModItems.paa_plate, ModItems.paa_legs, ModItems.paa_boots);
	}

	// Drillgon200: Is there a reason for this method? I don't know and I don't
	// care to find out.
	// Alcater: Looks like some kind of hazmat tier 2 check
	public static boolean checkForHaz2(EntityLivingBase player) {
	
		if(checkArmor(player, ModItems.hazmat_paa_helmet, ModItems.hazmat_paa_plate, ModItems.hazmat_paa_legs, ModItems.hazmat_paa_boots) ||
			checkArmor(player, ModItems.paa_helmet, ModItems.paa_plate, ModItems.paa_legs, ModItems.paa_boots) ||
			checkArmor(player, ModItems.liquidator_helmet, ModItems.liquidator_plate, ModItems.liquidator_legs, ModItems.liquidator_boots) || 
			checkArmor(player, ModItems.euphemium_helmet, ModItems.euphemium_plate, ModItems.euphemium_legs, ModItems.euphemium_boots) ||
			checkArmor(player, ModItems.hev_helmet, ModItems.hev_plate, ModItems.hev_legs, ModItems.hev_boots) || 
			checkArmor(player, ModItems.ajr_helmet, ModItems.ajr_plate, ModItems.ajr_legs, ModItems.ajr_boots) || 
			checkArmor(player, ModItems.ajro_helmet, ModItems.ajro_plate, ModItems.ajro_legs, ModItems.ajro_boots) || 
			checkArmor(player, ModItems.ajr_alpha_helmet, ModItems.ajr_b_plate, ModItems.ajr_b_legs, ModItems.ajr_b_boots) ||
			checkArmor(player, ModItems.rpa_helmet, ModItems.rpa_plate, ModItems.rpa_legs, ModItems.rpa_boots) || 
			checkArmor(player, ModItems.fau_helmet, ModItems.fau_plate, ModItems.fau_legs, ModItems.fau_boots) || 
			checkArmor(player, ModItems.dns_helmet, ModItems.dns_plate, ModItems.dns_legs, ModItems.dns_boots)) {
			return true;
		}
	
		return false;
	}

	public static boolean checkForHazmatOnly(EntityLivingBase player) {
		if(checkArmor(player, ModItems.hazmat_helmet, ModItems.hazmat_plate, ModItems.hazmat_legs, ModItems.hazmat_boots) || 
			checkArmor(player, ModItems.hazmat_helmet_red, ModItems.hazmat_plate_red, ModItems.hazmat_legs_red, ModItems.hazmat_boots_red) || 
			checkArmor(player, ModItems.hazmat_helmet_grey, ModItems.hazmat_plate_grey, ModItems.hazmat_legs_grey, ModItems.hazmat_boots_grey) || 
			checkArmor(player, ModItems.hazmat_paa_helmet, ModItems.hazmat_paa_plate, ModItems.hazmat_paa_legs, ModItems.hazmat_paa_boots) ||
			checkArmor(player, ModItems.paa_helmet, ModItems.paa_plate, ModItems.paa_legs, ModItems.paa_boots) ||
			checkArmor(player, ModItems.liquidator_helmet, ModItems.liquidator_plate, ModItems.liquidator_legs, ModItems.liquidator_boots)){
			return true;
		}
		return false;
	}

	public static boolean checkForHazmat(EntityLivingBase player) {
		if(ArmorUtil.checkArmor(player, ModItems.hazmat_helmet, ModItems.hazmat_plate, ModItems.hazmat_legs, ModItems.hazmat_boots) || 
			ArmorUtil.checkArmor(player, ModItems.hazmat_helmet_red, ModItems.hazmat_plate_red, ModItems.hazmat_legs_red, ModItems.hazmat_boots_red) || 
			ArmorUtil.checkArmor(player, ModItems.hazmat_helmet_grey, ModItems.hazmat_plate_grey, ModItems.hazmat_legs_grey, ModItems.hazmat_boots_grey) || 
			ArmorUtil.checkArmor(player, ModItems.t45_helmet, ModItems.t45_plate, ModItems.t45_legs, ModItems.t45_boots) ||
			ArmorUtil.checkArmor(player, ModItems.t51_helmet, ModItems.t51_plate, ModItems.t51_legs, ModItems.t51_boots) || 
			ArmorUtil.checkArmor(player, ModItems.schrabidium_helmet, ModItems.schrabidium_plate, ModItems.schrabidium_legs, ModItems.schrabidium_boots) || 
			checkForHaz2(player)) {
	
			return true;
		}
	
		if(player.isPotionActive(HbmPotion.mutation))
			return true;
	
		return false;
	}

	public static boolean checkForAsbestos(EntityLivingBase player) {
	
		if(ArmorUtil.checkArmor(player, ModItems.asbestos_helmet, ModItems.asbestos_plate, ModItems.asbestos_legs, ModItems.asbestos_boots)) {
			return true;
		}
	
		return false;
	}

	public static boolean checkArmor(EntityLivingBase player, Item helm, Item chest, Item leg, Item shoe) {
		return player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == helm && player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == chest && player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == leg && player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == shoe;
	}

	public static boolean checkArmorPiece(EntityPlayer player, Item armor, int slot) {
		if(player.inventory.armorInventory.get(slot) != null && player.inventory.armorInventory.get(slot).getItem() == armor) {
			return true;
		}
	
		return false;
	}
	
	/*
	 * Default implementations for IGasMask items
	 */
	public static final String FILTERK_KEY = "hfrFilter";
	
	public static void damageGasMaskFilter(EntityLivingBase entity, int damage) {
		
		ItemStack mask = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

		if(mask == null || mask.isEmpty())
			return;

		if(mask.getItem() instanceof IGasMask) {
			damageGasMaskFilter(mask, damage);
		} else if(ArmorModHandler.hasMods(mask)) {

			ItemStack mods[] = ArmorModHandler.pryMods(mask);
			ItemStack modMask = mods[ArmorModHandler.helmet_only];

			if(modMask != null && modMask.getItem() instanceof IGasMask) {
				damageGasMaskFilter(modMask, damage);
				ArmorModHandler.applyMod(mask, modMask); // pryMods returns a copy; persist the wear back into the helmet
			}
		}
	}
	
	public static void damageGasMaskFilter(ItemStack mask, int damage) {
		ItemStack filter = getGasMaskFilter(mask);
		
		if(filter == null) {
			if(ArmorModHandler.hasMods(mask)) {
				ItemStack mods[] = ArmorModHandler.pryMods(mask);
				
				if(mods[ArmorModHandler.helmet_only] != null && mods[ArmorModHandler.helmet_only].getItem() instanceof IGasMask)
					filter = getGasMaskFilter(mods[ArmorModHandler.helmet_only]);
			}
		}
		
		if(filter == null || filter.getMaxDamage() == 0)
			return;
		
		filter.setItemDamage(filter.getItemDamage() + damage);
		
		if(filter.getItemDamage() > filter.getMaxDamage()){
			removeFilter(mask);
		}
		else{
			installGasMaskFilter(mask, filter);
		}
	}
	
	public static void installGasMaskFilter(ItemStack mask, ItemStack filter) {
		
		if(mask == null || filter == null)
			return;
		
		if(!mask.hasTagCompound())
			mask.setTagCompound(new NBTTagCompound());
		
		NBTTagCompound attach = new NBTTagCompound();
		filter.writeToNBT(attach);
		
		mask.getTagCompound().setTag(FILTERK_KEY, attach);
	}
	
	public static void removeFilter(ItemStack mask) {
		
		if(mask == null)
			return;
		
		if(!mask.hasTagCompound())
			return;
		
		mask.getTagCompound().removeTag(FILTERK_KEY);
		if(mask.getTagCompound().isEmpty())
			mask.setTagCompound(null);
	}
	
	public static ItemStack getGasMaskFilter(ItemStack mask) {
		
		if(mask == null)
			return null;
		
		if(!mask.hasTagCompound())
			return null;
		
		NBTTagCompound attach = mask.getTagCompound().getCompoundTag(FILTERK_KEY);
		ItemStack filter = new ItemStack(attach);
		if(filter.isEmpty())
			return null;
		return filter;
	}
	
	public static boolean checkForDigamma(EntityPlayer player) {
		
		if(checkArmor(player, ModItems.fau_helmet, ModItems.fau_plate, ModItems.fau_legs, ModItems.fau_boots))
			return true;

		if(checkArmor(player, ModItems.dns_helmet, ModItems.dns_plate, ModItems.dns_legs, ModItems.dns_boots))
			return true;
		
		if(player.isPotionActive(HbmPotion.stability))
			return true; 
		
		return false;
	}
	
		public static boolean checkForMonoMask(EntityPlayer player) {
		if (player.isPotionActive(HbmPotion.mutation))
			return true;

		ItemStack helmet = player.inventory.armorInventory.get(3);
		if (helmet.isEmpty())
			return false;

		Item item = helmet.getItem();
		if (item == ModItems.gas_mask || item == ModItems.gas_mask_m65 || item == ModItems.gas_mask_mono || item == ModItems.liquidator_helmet) {
			return true;
		}

		if (ArmorModHandler.hasMods(helmet)) {
			ItemStack[] mods = ArmorModHandler.pryMods(helmet);
			return mods[ArmorModHandler.helmet_only] != null && mods[ArmorModHandler.helmet_only].getItem() == ModItems.attachment_mask_mono;
		}

		return false;
	}
	
		public static boolean checkForGoggles(EntityPlayer player) {
		ItemStack helmet = player.inventory.armorInventory.get(3);
		if (helmet.isEmpty()) return false;

		Item item = helmet.getItem();
		return item == ModItems.goggles
				|| item == ModItems.ashglasses
				|| item == ModItems.t45_helmet
				|| item == ModItems.t51_helmet
				|| item == ModItems.ajr_helmet
				|| item == ModItems.ajro_helmet
				|| item == ModItems.ajr_alpha_helmet
				|| item == ModItems.rpa_helmet
				|| item == ModItems.bj_helmet
				|| item == ModItems.hev_helmet
				|| item == ModItems.hazmat_paa_helmet
				|| item == ModItems.paa_helmet;
	}

	/**
	 * Grabs the installed filter or the filter of the attachment, used for attachment rendering
	 * @param mask
	 * @param entity
	 * @return
	 */
	public static ItemStack getGasMaskFilterRecursively(ItemStack mask) {
		
		ItemStack filter = getGasMaskFilter(mask);
		
		if((filter == null || filter.isEmpty()) && ArmorModHandler.hasMods(mask)) {
			
			ItemStack mods[] = ArmorModHandler.pryMods(mask);
			
			if(mods[ArmorModHandler.helmet_only] != null && mods[ArmorModHandler.helmet_only].getItem() instanceof IGasMask)
				filter = ((IGasMask)mods[ArmorModHandler.helmet_only].getItem()).getFilter(mods[ArmorModHandler.helmet_only]);
		}
		
		return filter;
	}

	public static void addGasMaskTooltip(ItemStack mask, World world, List<String> list, ITooltipFlag flagIn){
		
		if(mask == null || !(mask.getItem() instanceof IGasMask))
			return;
		
		ItemStack filter = ((IGasMask)mask.getItem()).getFilter(mask);
		
		if(filter == null) {
			list.add("§c" + I18nUtil.resolveKey("desc.nofilter"));
			return;
		}
		
		list.add("§6" + I18nUtil.resolveKey("desc.infilter"));
		
		int meta = filter.getItemDamage();
		int max = filter.getMaxDamage();
		
		String append = "";
		
		if(max > 0) {
			append = " (" + Library.getPercentage((max - meta) / (double)max) + "%) "+(max-meta)+"/"+max;
		}
		
		List<String> lore = new ArrayList<>();
		list.add("  " + filter.getDisplayName() + append);
		filter.getItem().addInformation(filter, world, lore, flagIn);
		ForgeEventFactory.onItemTooltip(filter, null, lore, flagIn);
		lore.forEach(x -> list.add("§e  " + x));
	}
}
