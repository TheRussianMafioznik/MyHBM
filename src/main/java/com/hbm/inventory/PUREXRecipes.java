package com.hbm.inventory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.hbm.blocks.ModBlocks;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.items.ModItems;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class PUREXRecipes {

    public static final Map<String, PUREXRecipe> recipeMap = new LinkedHashMap<>();
    public static final List<PUREXRecipe> recipes = new ArrayList<>();

    public static void registerRecipes() {
        recipeMap.clear();
        recipes.clear();

        long pilePower = 100;
        long zirnoxPower = 1_000;
        long pwrPower = 2_500;
        long watzPower = 10_000;
        long vitrification = 1_000;

        // Специальные рецепты топлива и слитков
        add("purex.uzh", "Топливо UZH", 600, 1_000,
            new AStack[] { new ComparableStack(ModItems.billet_uranium_fuel), new OreDictStack("billetZirconium", 3) },
            new FluidStack[] { fluid("nitric_acid", 1000), fluid("hydrogen", 4000) },
            new ItemStack[] { stack("billet_uzh", 4) }, null);

        add("purex.flashgold", "Слиток Бейлфайр-золота", 600, 1_000,
            new AStack[] { new OreDictStack("billetAu198", 1), new ComparableStack(ModItems.pellet_charged) },
            new FluidStack[] { fluid("amat", 1_000) },
            new ItemStack[] { new ItemStack(ModItems.billet_balefire_gold, 2) }, null);

        add("purex.flashlead", "Слиток Вспышкосвинца", 600, 1_000,
            new AStack[] { new OreDictStack("billetPb209", 1), new ComparableStack(ModItems.billet_balefire_gold) },
            new FluidStack[] { fluid("amat", 1_000) },
            new ItemStack[] { new ItemStack(ModItems.billet_flashlead, 1) }, null);

        // CP-1 (Реакторные стержни Чикагской поленницы)
        registerPileRod("purex.pilepu239", 0, pilePower, new ItemStack(ModItems.billet_pu239, 2), new ItemStack(ModItems.billet_uranium, 1));
        registerPileRod("purex.pilergp", 1, pilePower, new ItemStack(ModItems.billet_pu_mix, 2), new ItemStack(ModItems.billet_nuclear_waste, 1));
        registerPileRod("purex.pilethorium", 2, pilePower, new ItemStack(ModItems.billet_thorium_fuel, 2), new ItemStack(ModItems.billet_nuclear_waste, 1));
        registerPileRod("purex.pilewaste", 3, pilePower, new ItemStack(ModItems.billet_nuclear_waste, 2), new ItemStack(ModItems.billet_polonium, 1));

        // Отходы ZIRNOX (9 видов)
        addZirnox("purex.zirnoxnu", ModItems.waste_natural_uranium, zirnoxPower,
            new ItemStack(ModItems.nugget_u238, 1), new ItemStack(ModItems.nugget_pu_mix, 2), new ItemStack(ModItems.nugget_pu239, 1), new ItemStack(ModItems.nuclear_waste_tiny, 2));

        addZirnox("purex.zirnoxmeu", ModItems.waste_uranium, zirnoxPower,
            new ItemStack(ModItems.nugget_pu_mix, 1), new ItemStack(ModItems.nugget_plutonium, 2), new ItemStack(ModItems.nugget_technetium, 1), new ItemStack(ModItems.nuclear_waste_tiny, 2));

        addZirnox("purex.zirnoxthmeu", ModItems.waste_thorium, zirnoxPower,
            new ItemStack(ModItems.nugget_u238, 1), new ItemStack(ModItems.nugget_th232, 1), new ItemStack(ModItems.nugget_u233, 2), new ItemStack(ModItems.nuclear_waste_tiny, 2));

        addZirnox("purex.zirnoxmox", ModItems.waste_mox, zirnoxPower,
            new ItemStack(ModItems.nugget_pu_mix, 1), new ItemStack(ModItems.nugget_technetium, 1), new ItemStack(ModItems.nugget_u238, 1), new ItemStack(ModItems.nuclear_waste_tiny, 3));

        addZirnox("purex.zirnoxmep", ModItems.waste_plutonium, zirnoxPower,
            new ItemStack(ModItems.nugget_pu_mix, 2), new ItemStack(ModItems.nugget_technetium, 1), new ItemStack(ModItems.nuclear_waste_tiny, 3));

        addZirnox("purex.zirnoxheu233", ModItems.waste_u233, zirnoxPower,
            new ItemStack(ModItems.nugget_u235, 1), new ItemStack(ModItems.nugget_neptunium, 1), new ItemStack(ModItems.nugget_technetium, 1), new ItemStack(ModItems.nuclear_waste_tiny, 3));

        addZirnox("purex.zirnoxheu235", ModItems.waste_u235, zirnoxPower,
            new ItemStack(ModItems.nugget_pu238, 1), new ItemStack(ModItems.nugget_neptunium, 1), new ItemStack(ModItems.nugget_technetium, 1), new ItemStack(ModItems.nuclear_waste_tiny, 3));

        addZirnox("purex.zirnoxles", ModItems.waste_schrabidium, zirnoxPower,
            new ItemStack(ModItems.nugget_beryllium, 2), new ItemStack(ModItems.nugget_pu239, 1), new ItemStack(ModItems.nuclear_waste_tiny, 3));

        addZirnox("purex.zirnoxzfbmox", ModItems.waste_zfb_mox, zirnoxPower,
            new ItemStack(ModItems.nugget_zirconium, 3), new ItemStack(ModItems.nugget_technetium, 1), new ItemStack(ModItems.nugget_pu_mix, 1), new ItemStack(ModItems.nuclear_waste_tiny, 1));

        // ТВЭЛы PWR (если есть в вашем форке)
        Item pwrDepleted = Item.getByNameOrId("hbm:pwr_fuel_depleted");
        if (pwrDepleted != null) {
            addMeta("purex.pwrmeu", pwrDepleted, 0, pwrPower, new ItemStack(ModItems.nugget_u238, 3), new ItemStack(ModItems.nugget_plutonium, 4), new ItemStack(ModItems.nugget_technetium, 2), new ItemStack(ModItems.nuclear_waste_tiny, 3));
            addMeta("purex.pwrheu233", pwrDepleted, 1, pwrPower, new ItemStack(ModItems.nugget_u235, 3), new ItemStack(ModItems.nugget_pu238, 3), new ItemStack(ModItems.nugget_technetium, 1), new ItemStack(ModItems.nuclear_waste_tiny, 5));
            addMeta("purex.pwrheu235", pwrDepleted, 2, pwrPower, new ItemStack(ModItems.nugget_neptunium, 3), new ItemStack(ModItems.nugget_pu238, 3), new ItemStack(ModItems.nugget_technetium, 1), new ItemStack(ModItems.nuclear_waste_tiny, 5));
            addMeta("purex.pwrmen", pwrDepleted, 3, pwrPower, new ItemStack(ModItems.nugget_u238, 3), new ItemStack(ModItems.nugget_pu239, 4), new ItemStack(ModItems.nugget_technetium, 2), new ItemStack(ModItems.nuclear_waste_tiny, 3));
            addMeta("purex.pwrhen237", pwrDepleted, 4, pwrPower, new ItemStack(ModItems.nugget_pu238, 2), new ItemStack(ModItems.nugget_pu239, 4), new ItemStack(ModItems.nugget_technetium, 1), new ItemStack(ModItems.nuclear_waste_tiny, 5));
            addMeta("purex.pwrmox", pwrDepleted, 5, pwrPower, new ItemStack(ModItems.nugget_u238, 3), new ItemStack(ModItems.nugget_pu240, 4), new ItemStack(ModItems.nugget_technetium, 2), new ItemStack(ModItems.nuclear_waste_tiny, 3));
            addMeta("purex.pwrmep", pwrDepleted, 6, pwrPower, new ItemStack(ModItems.nugget_lead, 2), new ItemStack(ModItems.nugget_pu_mix, 4), new ItemStack(ModItems.nugget_technetium, 2), new ItemStack(ModItems.nuclear_waste_tiny, 3));
            addMeta("purex.pwrhep239", pwrDepleted, 7, pwrPower, new ItemStack(ModItems.nugget_pu_mix, 2), new ItemStack(ModItems.nugget_pu240, 4), new ItemStack(ModItems.nugget_technetium, 1), new ItemStack(ModItems.nuclear_waste_tiny, 5));
        }

        // Пеллеты Watz
        Item watzDepleted = Item.getByNameOrId("hbm:watz_pellet_depleted");
        if (watzDepleted != null) {
            addWatz("purex.watzschrab", watzDepleted, 0, watzPower, new ItemStack(ModItems.nugget_solinium, 15), new ItemStack(ModItems.nugget_euphemium, 3), new ItemStack(ModItems.nuclear_waste, 2));
            addWatz("purex.watzhes", watzDepleted, 1, watzPower, new ItemStack(ModItems.nugget_solinium, 17), new ItemStack(ModItems.nugget_euphemium, 1), new ItemStack(ModItems.nuclear_waste, 2));
            addWatz("purex.watzmes", watzDepleted, 2, watzPower, new ItemStack(ModItems.nugget_solinium, 12), new ItemStack(ModItems.nugget_tantalium, 6), new ItemStack(ModItems.nuclear_waste, 2));
addWatz("purex.watzles", watzDepleted, 3, watzPower, new ItemStack(ModItems.nugget_solinium, 9), new ItemStack(ModItems.nugget_tantalium, 9), new ItemStack(ModItems.nuclear_waste, 2));
addWatz("purex.watzhen", watzDepleted, 4, watzPower, new ItemStack(ModItems.nugget_pu239, 12), new ItemStack(ModItems.nugget_technetium, 6), new ItemStack(ModItems.nuclear_waste, 2));
addWatz("purex.watzmeu", watzDepleted, 5, watzPower, new ItemStack(ModItems.nugget_pu239, 12), new ItemStack(ModItems.nugget_bismuth, 6), new ItemStack(ModItems.nuclear_waste, 2));
addWatz("purex.watzmep", watzDepleted, 6, watzPower, new ItemStack(ModItems.nugget_pu241, 12), new ItemStack(ModItems.nugget_bismuth, 6), new ItemStack(ModItems.nuclear_waste, 2));
addWatz("purex.watzlead", watzDepleted, 7, watzPower, new ItemStack(ModItems.nugget_lead, 6), new ItemStack(ModItems.nugget_bismuth, 12), new ItemStack(ModItems.nuclear_waste, 2));
addWatz("purex.watzboron", watzDepleted, 8, watzPower, new ItemStack(ModItems.powder_coal_tiny, 12), new ItemStack(ModItems.nugget_co60, 6), new ItemStack(ModItems.nuclear_waste, 2));
addWatz("purex.watzdu", watzDepleted, 9, watzPower, new ItemStack(ModItems.nugget_polonium, 12), new ItemStack(ModItems.nugget_pu238, 6), new ItemStack(ModItems.nuclear_waste, 2));
}
// Витрификация отходов (остекловывание)
add("purex.vitliquid", "Остекловывание жидких отходов", 100, vitrification,
new AStack[] { new ComparableStack(ModBlocks.sand_lead) },
new FluidStack[] { fluid("wastefluid", 1_000) },
new ItemStack[] { new ItemStack(ModItems.nuclear_waste_vitrified) }, null);
add("purex.vitgaseous", "Остекловывание газообразных отходов", 100, vitrification,
new AStack[] { new ComparableStack(ModBlocks.sand_lead) },
new FluidStack[] { fluid("wastegas", 1_000) },
new ItemStack[] { new ItemStack(ModItems.nuclear_waste_vitrified) }, null);
add("new FluidStack[0]", "Остекловывание твёрдых отходов", 300, vitrification,
new AStack[] { new ComparableStack(ModBlocks.sand_lead), new ComparableStack(ModItems.nuclear_waste, 4) },
new FluidStack[0],
new ItemStack[] { new ItemStack(ModItems.nuclear_waste_vitrified, 4) }, null);
// Получение Шрабидия
add("purex.schraranium", "Переработка шрарания", 200, 1_000,
new AStack[] { new ComparableStack(ModItems.ingot_schraranium) },
new FluidStack[] { new FluidStack(ModForgeFluids.kerosene, 2_000), new FluidStack(ModForgeFluids.nitric_acid, 1_000) },
new ItemStack[] { new ItemStack(ModItems.nugget_schrabidium, 3), new ItemStack(ModItems.nugget_uranium, 3), new ItemStack(ModItems.nugget_neptunium, 2) }, null);
add("purex.schrabzirnox", "Экстракция шрабидия (ZIRNOX Pu)", 200, 50_000,
new AStack[] { new ComparableStack(ModItems.waste_plutonium) },
new FluidStack[] { new FluidStack(ModForgeFluids.solvent, 4_000), new FluidStack(ModForgeFluids.schrabidic, 500) },
new ItemStack[] { new ItemStack(ModItems.powder_schrabidium, 1), new ItemStack(ModItems.nugget_technetium, 3), new ItemStack(ModItems.nuclear_waste_tiny, 4) }, null);
if (pwrDepleted != null) {
add("purex.schrabpwr", "Экстракция шрабидия (PWR MEP)", 200, 50_000,
new AStack[] { new ComparableStack(pwrDepleted, 1, 6) },
new FluidStack[] { new FluidStack(ModForgeFluids.solvent, 4_000), new FluidStack(ModForgeFluids.schrabidic, 500) },
new ItemStack[] { new ItemStack(ModItems.powder_schrabidium, 1), new ItemStack(ModItems.nugget_technetium, 3), new ItemStack(ModItems.nuclear_waste_tiny, 4) }, null);
add("purex.schrabmen", "Экстракция шрабидия (PWR MEN)", 200, 50_000,
new AStack[] { new ComparableStack(pwrDepleted, 1, 3) },
new FluidStack[] { new FluidStack(ModForgeFluids.solvent, 4_000), new FluidStack(ModForgeFluids.schrabidic, 500) },
new ItemStack[] { new ItemStack(ModItems.powder_schrabidium, 1), new ItemStack(ModItems.nugget_technetium, 3), new ItemStack(ModItems.nuclear_waste_tiny, 4) }, null);
}
}
private static void registerPileRod(String id, int meta, long power, ItemStack... outputs) {
Item pileRod = Item.getByNameOrId("hbm:pile_rod");
Fluid acid = FluidRegistry.getFluid("sulfuric_acid");
if (pileRod != null && acid != null) {
add(id, "Переработка стержня CP-1", 40, power,
new AStack[] { new ComparableStack(pileRod, 1, meta) },
new FluidStack[] { new FluidStack(acid, 100) },
outputs, null);
}
}
private static void addZirnox(String id, Item input, long power, ItemStack... outputs) {
if (input == null) return;
add(id, "Переработка: " + new ItemStack(input).getDisplayName(), 100, power,
new AStack[] { new ComparableStack(input) },
new FluidStack[] { new FluidStack(ModForgeFluids.kerosene, 500), new FluidStack(ModForgeFluids.nitric_acid, 250) },
outputs, null);
}
private static void addMeta(String id, Item input, int meta, long power, ItemStack... outputs) {
add(id, "Переработка: " + new ItemStack(input, 1, meta).getDisplayName(), 100, power,
new AStack[] { new ComparableStack(input, 1, meta) },
new FluidStack[] { new FluidStack(ModForgeFluids.kerosene, 500), new FluidStack(ModForgeFluids.nitric_acid, 250) },
outputs, null);
}
private static void addWatz(String id, Item input, int meta, long power, ItemStack... outputs) {
Fluid watz = FluidRegistry.getFluid("watz");
add(id, "Переработка пеллеты Watz", 60, power,
new AStack[] { new ComparableStack(input, 1, meta) },
new FluidStack[] { new FluidStack(ModForgeFluids.kerosene, 500), new FluidStack(ModForgeFluids.nitric_acid, 250) },
outputs, watz != null ? new FluidStack(watz, 1_000) : null);
}
private static FluidStack fluid(String name, int amount) {
Fluid f = FluidRegistry.getFluid(name);
return f != null ? new FluidStack(f, amount) : null;
}
private static ItemStack stack(String itemId, int count) {
Item it = Item.getByNameOrId("hbm:" + itemId);
return it != null ? new ItemStack(it, count) : ItemStack.EMPTY;
}
public static void add(String id, String title, int duration, long powerPerTick, AStack[] inputs, FluidStack[] inFluids, ItemStack[] outputs, FluidStack outFluid) {
for (FluidStack f : inFluids) {
if (f == null) return;
}
for (ItemStack out : outputs) {
if (out == null || out.isEmpty()) return;
}
PUREXRecipe rec = new PUREXRecipe(id, title, duration, powerPerTick, inputs, inFluids, outputs, outFluid);
recipeMap.put(id, rec);
recipes.add(rec);
}
public static PUREXRecipe getRecipeById(String id) {
if (id == null || id.isEmpty() || "null".equals(id)) return null;
return recipeMap.get(id);
}
public static PUREXRecipe findMatchingRecipe(ItemStack[] slots, FluidStack[] tanks) {
for (PUREXRecipe rec : recipes) {
if (rec.matches(slots, tanks)) return rec;
}
return null;
}
public static boolean isValidInput(ItemStack stack) {
if (stack == null || stack.isEmpty()) return false;
for (PUREXRecipe rec : recipes) {
for (AStack in : rec.inputs) {
if (in.matchesRecipe(stack, true)) return true;
}
}
return false;
}
    public static List<ItemStack> getVariants(AStack in) {
        List<ItemStack> list = new ArrayList<>();
        if (in instanceof ComparableStack) {
            list.add(((ComparableStack) in).toStack());
        } else if (in instanceof OreDictStack) {
            OreDictStack ods = (OreDictStack) in;
            for (ItemStack s : net.minecraftforge.oredict.OreDictionary.getOres(ods.name)) {
                ItemStack copy = s.copy();
                copy.setCount(ods.count());
                list.add(copy);
            }
        }
        return list;
    }

    public static class PUREXRecipe {
        public final String id;
        public final String title;
        public final int duration;
        public final long powerPerTick;
        public final AStack[] inputs;
        public final FluidStack[] inputFluids;
        public final ItemStack[] outputs;
        public final FluidStack outputFluid;

        public PUREXRecipe(String id, String title, int duration, long powerPerTick, AStack[] inputs, FluidStack[] inputFluids, ItemStack[] outputs, FluidStack outputFluid) {
            this.id = id;
            this.title = title;
            this.duration = duration;
            this.powerPerTick = powerPerTick;
            this.inputs = inputs;
            this.inputFluids = inputFluids;
            this.outputs = outputs;
            this.outputFluid = outputFluid;
        }

        public ItemStack getIcon() {
            if (inputs != null && inputs.length > 0) {
                List<ItemStack> vars = getVariants(inputs[0]);
                if (!vars.isEmpty()) return vars.get(0);
            }
            return outputs.length > 0 ? outputs[0] : ItemStack.EMPTY;
        }

        public boolean matchesSearch(String query) {
            String q = query.toLowerCase(Locale.ROOT);
            if (title.toLowerCase(Locale.ROOT).contains(q) || id.toLowerCase(Locale.ROOT).contains(q)) return true;
            for (AStack in : inputs) {
                for (ItemStack st : getVariants(in)) {
                    if (st.getDisplayName().toLowerCase(Locale.ROOT).contains(q)) return true;
                }
            }
            for (ItemStack out : outputs) {
                if (out.getDisplayName().toLowerCase(Locale.ROOT).contains(q)) return true;
            }
            return false;
        }

        public boolean matches(ItemStack[] slots, FluidStack[] tanks) {
            for (AStack req : inputs) {
                int needed = req.count();
                for (ItemStack slot : slots) {
                    if (slot != null && !slot.isEmpty() && req.matchesRecipe(slot, true)) {
                        needed -= slot.getCount();
                        if (needed <= 0) break;
                    }
                }
                if (needed > 0) return false;
            }
            for (FluidStack req : inputFluids) {
                boolean found = false;
                for (FluidStack avail : tanks) {
                    if (avail != null && avail.getFluid() == req.getFluid() && avail.amount >= req.amount) {
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
            }
            return true;
        }
    }
}
