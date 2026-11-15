package com.hbm.items.machine;

import java.util.Locale;

import com.hbm.items.ModItems;
import com.hbm.main.MainRegistry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemZirnoxRodDepleted extends Item {

    public ItemZirnoxRodDepleted(String registryName) {
        this.setTranslationKey(registryName);
        this.setRegistryName(registryName);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.canRepair = false;
        this.setCreativeTab(MainRegistry.controlTab);

        ModItems.ALL_ITEMS.add(this);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        EnumZirnoxTypeDepleted num = getEnum(stack.getItemDamage());
        return super.getTranslationKey() + "." + num.name().toLowerCase(Locale.US);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (EnumZirnoxTypeDepleted type : EnumZirnoxTypeDepleted.VALUES) {
                items.add(new ItemStack(this, 1, type.ordinal()));
            }
        }
    }

    public static EnumZirnoxTypeDepleted getEnum(int meta) {
        return EnumZirnoxTypeDepleted.VALUES[MathHelper.clamp(meta, 0, EnumZirnoxTypeDepleted.VALUES.length - 1)];
    }

    public enum EnumZirnoxTypeDepleted {
        NATURAL_URANIUM_FUEL,
        URANIUM_FUEL,
        THORIUM_FUEL,
        MOX_FUEL,
        PLUTONIUM_FUEL,
        U233_FUEL,
        U235_FUEL,
        LES_FUEL,
        ZFB_MOX_FUEL;

        public static final EnumZirnoxTypeDepleted[] VALUES = values();
    }
}
