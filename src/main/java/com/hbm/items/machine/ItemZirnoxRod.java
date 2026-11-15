package com.hbm.items.machine;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.hbm.items.ModItems;
import com.hbm.main.MainRegistry;
import com.hbm.util.BobMathUtil;
import com.hbm.util.I18nUtil;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemZirnoxRod extends Item {

    public ItemZirnoxRod(String registryName) {
        this.setTranslationKey(registryName);
        this.setRegistryName(registryName);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        this.canRepair = false;
        this.setCreativeTab(MainRegistry.controlTab);

        ModItems.ALL_ITEMS.add(this);
    }

    public static void incrementLifeTime(ItemStack stack) {
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());

        int time = stack.getTagCompound().getInteger("life");
        stack.getTagCompound().setInteger("life", time + 1);
    }

    public static void setLifeTime(ItemStack stack, int time) {
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());

        stack.getTagCompound().setInteger("life", time);
    }

    public static int getLifeTime(ItemStack stack) {
        if (!stack.hasTagCompound()) return 0;
        return stack.getTagCompound().getInteger("life");
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getDurabilityForDisplay(stack) > 0D;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        EnumZirnoxType num = getEnum(stack.getItemDamage());
        return (double) getLifeTime(stack) / (double) num.maxLife;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        EnumZirnoxType num = getEnum(stack.getItemDamage());
        return super.getTranslationKey() + "." + num.name().toLowerCase(Locale.US);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (EnumZirnoxType type : EnumZirnoxType.VALUES) {
                items.add(new ItemStack(this, 1, type.ordinal()));
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn) {
        EnumZirnoxType num = getEnum(stack.getItemDamage());
        list.add(TextFormatting.YELLOW + I18nUtil.resolveKey("trait.rbmk.depletion", ((int) ((((double) getLifeTime(stack)) / (double) num.maxLife) * 100000)) / 1000D + "%"));
        String[] loc;

        if (num.breeding)
            loc = I18nUtil.resolveKeyArray("desc.item.zirnoxBreedingRod", BobMathUtil.getShortNumber(num.maxLife));
        else
            loc = I18nUtil.resolveKeyArray("desc.item.zirnoxRod", num.heat, BobMathUtil.getShortNumber(num.maxLife));

        Collections.addAll(list, loc);
    }

    public static EnumZirnoxType getEnum(int meta) {
        return EnumZirnoxType.VALUES[MathHelper.clamp(meta, 0, EnumZirnoxType.VALUES.length - 1)];
    }

    public enum EnumZirnoxType {
        NATURAL_URANIUM_FUEL(250_000, 30),
        URANIUM_FUEL(200_000, 50),
        TH232_FUEL(20_000, 0, true),
        THORIUM_FUEL(200_000, 40),
        MOX_FUEL(165_000, 75),
        PLUTONIUM_FUEL(175_000, 65),
        U233_FUEL(150_000, 100),
        U235_FUEL(165_000, 85),
        LES_FUEL(150_000, 150),
        LITHIUM_FUEL(20_000, 0, true),
        ZFB_MOX_FUEL(50_000, 35);

        public static final EnumZirnoxType[] VALUES = values();

        public final int maxLife;
        public final int heat;
        public final boolean breeding;

        EnumZirnoxType(int life, int heat, boolean breeding) {
            this.maxLife = life;
            this.heat = heat;
            this.breeding = breeding;
        }

        EnumZirnoxType(int life, int heat) {
            this.maxLife = life;
            this.heat = heat;
            this.breeding = false;
        }
    }
}