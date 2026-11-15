package com.hbm.inventory;

import java.util.ArrayList;
import java.util.List;

import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.items.ModItems;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class PUREXRecipes {

    public static final List<PUREXRecipe> recipes = new ArrayList<>();

    public static void registerRecipes() {
        // 1. Обычный природный уран (Natural Uranium)
        addRecipe(
            new ComparableStack(ModItems.waste_natural_uranium),
            new FluidStack[] { new FluidStack(ModForgeFluids.kerosene, 500), new FluidStack(ModForgeFluids.nitric_acid, 250) },
            new ItemStack[] {
                new ItemStack(ModItems.nugget_u238, 1),
                new ItemStack(ModItems.nugget_pu_mix, 2),
                new ItemStack(ModItems.nugget_pu239, 1),
                new ItemStack(ModItems.nuclear_waste_tiny, 2)
            },
            100, 1000
        );

        // 2. Обогащённый уран (MEU / Uranium)
        addRecipe(
            new ComparableStack(ModItems.waste_uranium),
            new FluidStack[] { new FluidStack(ModForgeFluids.kerosene, 500), new FluidStack(ModForgeFluids.nitric_acid, 250) },
            new ItemStack[] {
                new ItemStack(ModItems.nugget_pu_mix, 1),
                new ItemStack(ModItems.nugget_plutonium, 2),
                new ItemStack(ModItems.nugget_technetium, 1),
                new ItemStack(ModItems.nuclear_waste_tiny, 2)
            },
            100, 1000
        );

        // 3. Торий (Thorium)
        addRecipe(
            new ComparableStack(ModItems.waste_thorium),
            new FluidStack[] { new FluidStack(ModForgeFluids.kerosene, 500), new FluidStack(ModForgeFluids.nitric_acid, 250) },
            new ItemStack[] {
                new ItemStack(ModItems.nugget_u238, 1),
                new ItemStack(ModItems.nugget_th232, 1),
                new ItemStack(ModItems.nugget_u233, 2),
                new ItemStack(ModItems.nuclear_waste_tiny, 2)
            },
            100, 1000
        );

        // 4. MOX-топливо
        addRecipe(
            new ComparableStack(ModItems.waste_mox),
            new FluidStack[] { new FluidStack(ModForgeFluids.kerosene, 500), new FluidStack(ModForgeFluids.nitric_acid, 250) },
            new ItemStack[] {
                new ItemStack(ModItems.nugget_pu_mix, 1),
                new ItemStack(ModItems.nugget_technetium, 1),
                new ItemStack(ModItems.nugget_u238, 1),
                new ItemStack(ModItems.nuclear_waste_tiny, 3)
            },
            100, 1000
        );

        // 5. Плутоний (Plutonium - стандартная переработка)
        addRecipe(
            new ComparableStack(ModItems.waste_plutonium),
            new FluidStack[] { new FluidStack(ModForgeFluids.kerosene, 500), new FluidStack(ModForgeFluids.nitric_acid, 250) },
            new ItemStack[] {
                new ItemStack(ModItems.nugget_pu_mix, 2),
                new ItemStack(ModItems.nugget_technetium, 1),
                new ItemStack(ModItems.nuclear_waste_tiny, 3)
            },
            100, 1000
        );

        // 6. Плутоний -> Шрабидий (через Solvent + Schrabidic Acid)
        addRecipe(
            new ComparableStack(ModItems.waste_plutonium),
            new FluidStack[] { new FluidStack(ModForgeFluids.solvent, 4000), new FluidStack(ModForgeFluids.schrabidic, 500) },
            new ItemStack[] {
                new ItemStack(ModItems.powder_schrabidium, 1),
                new ItemStack(ModItems.nugget_technetium, 3),
                new ItemStack(ModItems.nuclear_waste_tiny, 4)
            },
            200, 50000
        );

        // 7. Уран-233
        addRecipe(
            new ComparableStack(ModItems.waste_u233),
            new FluidStack[] { new FluidStack(ModForgeFluids.kerosene, 500), new FluidStack(ModForgeFluids.nitric_acid, 250) },
            new ItemStack[] {
                new ItemStack(ModItems.nugget_u235, 1),
                new ItemStack(ModItems.nugget_neptunium, 1),
                new ItemStack(ModItems.nugget_technetium, 1),
                new ItemStack(ModItems.nuclear_waste_tiny, 3)
            },
            100, 1000
        );

        // 8. Уран-235
        addRecipe(
            new ComparableStack(ModItems.waste_u235),
            new FluidStack[] { new FluidStack(ModForgeFluids.kerosene, 500), new FluidStack(ModForgeFluids.nitric_acid, 250) },
            new ItemStack[] {
                new ItemStack(ModItems.nugget_pu238, 1),
                new ItemStack(ModItems.nugget_neptunium, 1),
                new ItemStack(ModItems.nugget_technetium, 1),
                new ItemStack(ModItems.nuclear_waste_tiny, 3)
            },
            100, 1000
        );

        // 9. LES (Шрабидиевые отходы)
        addRecipe(
            new ComparableStack(ModItems.waste_schrabidium),
            new FluidStack[] { new FluidStack(ModForgeFluids.kerosene, 500), new FluidStack(ModForgeFluids.nitric_acid, 250) },
            new ItemStack[] {
                new ItemStack(ModItems.nugget_beryllium, 2),
                new ItemStack(ModItems.nugget_pu239, 1),
                new ItemStack(ModItems.nuclear_waste_tiny, 3)
            },
            100, 1000
        );

        // 10. ZFB MOX (Циркониевый MOX)
        addRecipe(
            new ComparableStack(ModItems.waste_zfb_mox),
            new FluidStack[] { new FluidStack(ModForgeFluids.kerosene, 500), new FluidStack(ModForgeFluids.nitric_acid, 250) },
            new ItemStack[] {
                new ItemStack(ModItems.nugget_zirconium, 3),
                new ItemStack(ModItems.nugget_technetium, 1),
                new ItemStack(ModItems.nugget_pu_mix, 1),
                new ItemStack(ModItems.nuclear_waste_tiny, 1)
            },
            100, 1000
        );
    }

    public static void addRecipe(ComparableStack input, FluidStack[] fluids, ItemStack[] outputs, int duration, long powerPerTick) {
        recipes.add(new PUREXRecipe(input, fluids, outputs, duration, powerPerTick));
    }

    public static PUREXRecipe getRecipe(ItemStack input, FluidStack f0, FluidStack f1, FluidStack f2) {
        if (input == null || input.isEmpty()) return null;
        ComparableStack comp = new ComparableStack(input);

        for (PUREXRecipe rec : recipes) {
            if (rec.input.equals(comp) && rec.matchesFluids(f0, f1, f2)) {
                return rec;
            }
        }
        return null;
    }

    public static boolean isValidInput(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        ComparableStack comp = new ComparableStack(stack);
        for (PUREXRecipe rec : recipes) {
            if (rec.input.equals(comp)) return true;
        }
        return false;
    }

    public static class PUREXRecipe {
        public final ComparableStack input;
        public final FluidStack[] inputFluids;
        public final ItemStack[] outputs;
        public final int duration;
        public final long powerPerTick;

        public PUREXRecipe(ComparableStack input, FluidStack[] inputFluids, ItemStack[] outputs, int duration, long powerPerTick) {
            this.input = input;
            this.inputFluids = inputFluids;
            this.outputs = outputs;
            this.duration = duration;
            this.powerPerTick = powerPerTick;
        }

        public boolean matchesFluids(FluidStack f0, FluidStack f1, FluidStack f2) {
            FluidStack[] available = new FluidStack[] { f0, f1, f2 };
            for (FluidStack req : inputFluids) {
                boolean found = false;
                for (FluidStack avail : available) {
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
