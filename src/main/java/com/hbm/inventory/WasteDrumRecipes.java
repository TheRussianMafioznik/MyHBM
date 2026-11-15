package com.hbm.inventory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.items.ModItems;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class WasteDrumRecipes {

	public static LinkedHashMap<ComparableStack, ItemStack> recipes = new LinkedHashMap<>();
	private static HashSet<ComparableStack> outputs = new HashSet<>();
	
	public static void registerRecipes() {

		// Старые рецепты вашего форка
		addRecipe(ModItems.waste_uranium_hot, new ItemStack(ModItems.waste_uranium, 1));
		addRecipe(ModItems.waste_thorium_hot, new ItemStack(ModItems.waste_thorium, 1));
		addRecipe(ModItems.waste_plutonium_hot, new ItemStack(ModItems.waste_plutonium, 1));
		addRecipe(ModItems.waste_mox_hot, new ItemStack(ModItems.waste_mox, 1));
		addRecipe(ModItems.waste_schrabidium_hot, new ItemStack(ModItems.waste_schrabidium, 1));

		// СЮДА мы добавим рецепты охлаждения отработанных стержней ZIRNOX после их переноса
		addRecipe(ModItems.waste_natural_uranium_hot, new ItemStack(ModItems.waste_natural_uranium, 1));
        addRecipe(ModItems.waste_u233_hot, new ItemStack(ModItems.waste_u233, 1));
        addRecipe(ModItems.waste_u235_hot, new ItemStack(ModItems.waste_u235, 1));
        addRecipe(ModItems.waste_zfb_mox_hot, new ItemStack(ModItems.waste_zfb_mox, 1));
	}

	public static void addRecipe(ComparableStack input, ItemStack output) {
		recipes.put(input, output);
		outputs.add(new ComparableStack(output));
	}

	public static void addRecipe(ItemStack input, ItemStack output) {
		addRecipe(new ComparableStack(input), output);
	}

	public static void addRecipe(Item input, ItemStack output) {
		addRecipe(new ComparableStack(input), output);
	}
	
	public static ItemStack getOutput(ItemStack stack) {
		if(stack == null || stack.isEmpty())
			return null;
		return recipes.get(new ComparableStack(stack));
	}

	public static ItemStack getOutput(Item item) {
		if(item == null)
			return null;
		return recipes.get(new ComparableStack(item));
	}

	public static boolean hasRecipe(ItemStack stack) {
		if(stack == null || stack.isEmpty())
			return false;
		return recipes.containsKey(new ComparableStack(stack));
	}

	public static boolean hasRecipe(Item item) {
		if(item == null)
			return false;
		return recipes.containsKey(new ComparableStack(item));
	}

	public static boolean isCold(ItemStack stack) {
		if(stack == null || stack.isEmpty())
			return false;
		return outputs.contains(new ComparableStack(stack));
	}

	public static boolean isCold(Item item) {
		if(item == null)
			return false;
		return outputs.contains(new ComparableStack(item));
	}
}
