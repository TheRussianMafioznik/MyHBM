package com.hbm.inventory.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.hbm.inventory.PUREXRecipes;
import com.hbm.inventory.PUREXRecipes.PUREXRecipe;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.lib.RefStrings;
import com.hbm.packet.NBTControlPacket;
import com.hbm.packet.PacketDispatcher;
import com.hbm.tileentity.machine.TileEntityMachinePUREX;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class GUIScreenPUREXSelector extends GuiScreen {

    protected static final ResourceLocation texture = new ResourceLocation(RefStrings.MODID + ":textures/gui/processing/gui_recipe_selector.png");

    protected int xSize = 176;
    protected int ySize = 132;
    protected int guiLeft;
    protected int guiTop;

    protected List<PUREXRecipe> recipes = new ArrayList<>();
    protected GuiTextField search;
    protected int pageIndex;
    protected int size;
    protected String selection;
    public static final String NULL_SELECTION = "null";

    protected TileEntityMachinePUREX tile;
    protected GuiScreen previousScreen;

    public static void openSelector(TileEntityMachinePUREX tile, String selection, GuiScreen previousScreen) {
        FMLCommonHandler.instance().showGuiScreen(new GUIScreenPUREXSelector(tile, selection, previousScreen));
    }

    public GUIScreenPUREXSelector(TileEntityMachinePUREX tile, String selection, GuiScreen previousScreen) {
        this.tile = tile;
        this.selection = (selection == null || selection.isEmpty()) ? NULL_SELECTION : selection;
        this.previousScreen = previousScreen;
        regenerateRecipes();
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        Keyboard.enableRepeatEvents(true);
        this.search = new GuiTextField(0, this.fontRenderer, guiLeft + 28, guiTop + 111, 102, 12);
        this.search.setTextColor(-1);
        this.search.setDisabledTextColour(-1);
        this.search.setEnableBackgroundDrawing(false);
        this.search.setMaxStringLength(32);
    }

    private void regenerateRecipes() {
        this.recipes.clear();
        this.recipes.addAll(PUREXRecipes.recipes);
        resetPaging();
    }

    private void search(String query) {
        this.recipes.clear();
        if (query.isEmpty()) {
            regenerateRecipes();
        } else {
            for (PUREXRecipe recipe : PUREXRecipes.recipes) {
                if (recipe.matchesSearch(query)) {
                    this.recipes.add(recipe);
                }
            }
            resetPaging();
        }
    }

    private void resetPaging() {
        this.pageIndex = 0;
        this.size = Math.max(0, (int) Math.ceil((this.recipes.size() - 40) / 8D));
    }

    private void playClick() {
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(HBMSoundHandler.rbmk_az5_cover, 0.5F));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        this.drawDefaultBackground();
        this.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, f);
        this.search.drawTextBox();
        this.handleScroll();

        if (guiLeft + 7 <= mouseX && guiLeft + 7 + 144 > mouseX && guiTop + 17 < mouseY && guiTop + 17 + 90 >= mouseY) {
            for (int i = pageIndex * 8; i < pageIndex * 8 + 40; i++) {
                if (i >= this.recipes.size()) break;
                int ind = i - pageIndex * 8;
                int ix = 7 + 18 * (ind % 8);
                int iy = 17 + 18 * (ind / 8);

                if (guiLeft + ix <= mouseX && guiLeft + ix + 18 > mouseX && guiTop + iy < mouseY && guiTop + iy + 18 >= mouseY) {
                    drawRecipeTooltip(recipes.get(i), mouseX, mouseY);
                }
            }
        }

        if (guiLeft + 151 <= mouseX && guiLeft + 151 + 18 > mouseX && guiTop + 71 < mouseY && guiTop + 71 + 18 >= mouseY) {
            PUREXRecipe sel = PUREXRecipes.getRecipeById(selection);
            if (sel != null) drawRecipeTooltip(sel, mouseX, mouseY);
        }

        if (guiLeft + 152 <= mouseX && guiLeft + 152 + 16 > mouseX && guiTop + 90 < mouseY && guiTop + 90 + 16 >= mouseY) {
            this.drawHoveringText(TextFormatting.YELLOW + "Закрыть", mouseX, mouseY);
        }

        if (guiLeft + 134 <= mouseX && guiLeft + 134 + 16 > mouseX && guiTop + 108 < mouseY && guiTop + 108 + 16 >= mouseY) {
            this.drawHoveringText(TextFormatting.YELLOW + "Очистить поиск", mouseX, mouseY);
        }

        if (guiLeft + 8 <= mouseX && guiLeft + 8 + 16 > mouseX && guiTop + 108 < mouseY && guiTop + 108 + 16 >= mouseY) {
            this.drawHoveringText(TextFormatting.ITALIC + "Нажмите ENTER для поиска", mouseX, mouseY);
        }
    }

        public static List<String> getRecipeTooltipLines(PUREXRecipe recipe) {
        List<String> lines = new ArrayList<>();
        lines.add(TextFormatting.GOLD + recipe.title);
        lines.add(TextFormatting.GRAY + "Время: " + (recipe.duration / 20.0) + " с | Энергия: " + recipe.powerPerTick + " HE/т");
        lines.add(TextFormatting.AQUA + "Вход:");
        for (AStack in : recipe.inputs) {
            List<ItemStack> variants = PUREXRecipes.getVariants(in);
            if (!variants.isEmpty()) {
                lines.add("  " + in.count() + "x " + variants.get(0).getDisplayName());
            }
        }
        for (FluidStack f : recipe.inputFluids) {
            lines.add("  " + f.amount + " mB " + f.getLocalizedName());
        }
        lines.add(TextFormatting.GREEN + "Выход:");
        for (ItemStack out : recipe.outputs) {
            lines.add("  " + out.getCount() + "x " + out.getDisplayName());
        }
        if (recipe.outputFluid != null) {
            lines.add("  " + recipe.outputFluid.amount + " mB " + recipe.outputFluid.getLocalizedName());
        }
        return lines;
    }

    public void drawRecipeTooltip(PUREXRecipe recipe, int mouseX, int mouseY) {
        this.drawHoveringText(getRecipeTooltipLines(recipe), mouseX, mouseY);
    }

    protected void handleScroll() {
        if (!Mouse.isButtonDown(0) && !Mouse.isButtonDown(1) && Mouse.next()) {
            int scroll = Mouse.getEventDWheel();
            if (scroll > 0 && this.pageIndex > 0) this.pageIndex--;
            if (scroll < 0 && this.pageIndex < this.size) this.pageIndex++;
        }
    }

    private void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        if (this.search.isFocused()) drawTexturedModalRect(guiLeft + 26, guiTop + 108, 0, 132, 106, 16);
        if (guiLeft + 152 <= mouseX && guiLeft + 152 + 16 > mouseX && guiTop + 18 < mouseY && guiTop + 18 + 16 >= mouseY) drawTexturedModalRect(guiLeft + 152, guiTop + 18, 176, 0, 16, 16);
        if (guiLeft + 152 <= mouseX && guiLeft + 152 + 16 > mouseX && guiTop + 36 < mouseY && guiTop + 36 + 16 >= mouseY) drawTexturedModalRect(guiLeft + 152, guiTop + 36, 176, 16, 16, 16);
        if (guiLeft + 152 <= mouseX && guiLeft + 152 + 16 > mouseX && guiTop + 90 < mouseY && guiTop + 90 + 16 >= mouseY) drawTexturedModalRect(guiLeft + 152, guiTop + 90, 176, 32, 16, 16);
        if (guiLeft + 134 <= mouseX && guiLeft + 134 + 16 > mouseX && guiTop + 108 < mouseY && guiTop + 108 + 16 >= mouseY) drawTexturedModalRect(guiLeft + 134, guiTop + 108, 176, 48, 16, 16);
        if (guiLeft + 8 <= mouseX && guiLeft + 8 + 16 > mouseX && guiTop + 108 < mouseY && guiTop + 108 + 16 >= mouseY) drawTexturedModalRect(guiLeft + 8, guiTop + 108, 176, 64, 16, 16);

        for (int i = pageIndex * 8; i < pageIndex * 8 + 40; i++) {
            if (i >= recipes.size()) break;
            int ind = i - pageIndex * 8;
            PUREXRecipe recipe = recipes.get(i);
            if (recipe.id.equals(this.selection)) this.drawTexturedModalRect(guiLeft + 7 + 18 * (ind % 8), guiTop + 17 + 18 * (ind / 8), 192, 0, 18, 18);
        }

        for (int i = pageIndex * 8; i < pageIndex * 8 + 40; i++) {
            if (i >= recipes.size()) break;
            int ind = i - pageIndex * 8;
            PUREXRecipe recipe = recipes.get(i);
            this.renderItem(recipe.getIcon(), 8 + 18 * (ind % 8), 18 + 18 * (ind / 8));
            this.mc.getTextureManager().bindTexture(texture);
        }

        PUREXRecipe sel = PUREXRecipes.getRecipeById(selection);
        if (sel != null) {
            this.renderItem(sel.getIcon(), 152, 72);
        }
    }

    public void renderItem(ItemStack stack, int x, int y) {
        if (stack == null || stack.isEmpty()) return;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.enableGUIStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        GlStateManager.enableRescaleNormal();

        itemRender.zLevel = 100.0F;
        itemRender.renderItemAndEffectIntoGUI(stack, guiLeft + x, guiTop + y);
        itemRender.zLevel = 0.0F;

        GlStateManager.enableAlpha();
        GlStateManager.disableLighting();
    }

    @Override
    protected void mouseClicked(int x, int y, int k) throws IOException {
        super.mouseClicked(x, y, k);
this.search.mouseClicked(x, y, k);
if (guiLeft + 152 <= x && guiLeft + 152 + 16 > x && guiTop + 18 < y && guiTop + 18 + 16 >= y) {
playClick();
if (this.pageIndex > 0) this.pageIndex--;
return;
}
if (guiLeft + 152 <= x && guiLeft + 152 + 16 > x && guiTop + 36 < y && guiTop + 36 + 16 >= y) {
playClick();
if (this.pageIndex < this.size) this.pageIndex++;
return;
}
if (guiLeft + 134 <= x && guiLeft + 134 + 16 > x && guiTop + 108 < y && guiTop + 108 + 16 >= y) {
this.search.setText("");
this.search("");
this.search.setFocused(true);
return;
}
for (int i = pageIndex * 8; i < pageIndex * 8 + 40; i++) {
if (i >= this.recipes.size()) break;
int ind = i - pageIndex * 8;
int ix = 7 + 18 * (ind % 8);
int iy = 17 + 18 * (ind / 8);
if (guiLeft + ix <= x && guiLeft + ix + 18 > x && guiTop + iy < y && guiTop + iy + 18 >= y) {
String newSelection = recipes.get(i).id;
this.selection = !newSelection.equals(selection) ? newSelection : NULL_SELECTION;
playClick();
return;
}
}
if (guiLeft + 151 <= x && guiLeft + 151 + 18 > x && guiTop + 71 < y && guiTop + 71 + 18 >= y) {
if (!NULL_SELECTION.equals(this.selection)) {
this.selection = NULL_SELECTION;
playClick();
return;
}
}
if (guiLeft + 152 <= x && guiLeft + 152 + 16 > x && guiTop + 90 < y && guiTop + 90 + 16 >= y) {
FMLCommonHandler.instance().showGuiScreen(previousScreen);
}
}
@Override
public void onGuiClosed() {
Keyboard.enableRepeatEvents(false);
NBTTagCompound data = new NBTTagCompound();
data.setString("selection", this.selection);
PacketDispatcher.wrapper.sendToServer(new NBTControlPacket(data, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ()));
}
@Override
protected void keyTyped(char typedChar, int keyCode) {
if (keyCode == Keyboard.KEY_RETURN) {
this.search.setFocused(!this.search.isFocused());
return;
}
if (this.search.textboxKeyTyped(typedChar, keyCode)) {
search(this.search.getText());
return;
}
if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
FMLCommonHandler.instance().showGuiScreen(previousScreen);
}
}
@Override
public boolean doesGuiPauseGame() {
return false;
}
}