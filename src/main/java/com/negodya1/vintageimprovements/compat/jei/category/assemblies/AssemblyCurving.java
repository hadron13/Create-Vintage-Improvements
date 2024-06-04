package com.negodya1.vintageimprovements.compat.jei.category.assemblies;

import com.mojang.blaze3d.vertex.PoseStack;
import com.negodya1.vintageimprovements.VintageItems;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedCurvingPress;
import com.negodya1.vintageimprovements.content.kinetics.curving_press.CurvingRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;

public class AssemblyCurving extends SequencedAssemblySubCategory {

    AnimatedCurvingPress press;

    public AssemblyCurving() {
        super(25);
        press = new AnimatedCurvingPress();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SequencedRecipe<?> recipe, IFocusGroup focuses, int x) {
        if (recipe.getRecipe() instanceof CurvingRecipe curving) {
            ItemStack stack;

            switch (curving.getMode()) {
                case 2 -> stack = new ItemStack(VintageItems.CONCAVE_CURVING_HEAD.get());
                case 3 -> stack = new ItemStack(VintageItems.W_SHAPED_CURVING_HEAD.get());
                case 4 -> stack = new ItemStack(VintageItems.V_SHAPED_CURVING_HEAD.get());
                case 5 -> stack = new ItemStack(((CurvingRecipe) recipe.getRecipe()).getItemAsHead());
                default -> stack = new ItemStack(VintageItems.CONVEX_CURVING_HEAD.get());
            }

            builder.addSlot(RecipeIngredientRole.INPUT, x + 4, 15)
                    .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                    .addItemStack(stack);
        }
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, PoseStack ms, double mouseX, double mouseY, int index) {
        press.offset = index;
        ms.pushPose();
        ms.translate(-5, 50, 0);
        ms.scale(.6f, .6f, .6f);
        if (recipe.getRecipe() instanceof CurvingRecipe curving)
            press.draw(ms, getWidth() / 2, 0, curving.getMode());
        else press.draw(ms, getWidth() / 2, 0, 1);
        ms.popPose();
    }

}
