package com.negodya1.vintageimprovements.compat.jei.category.assemblies;

import com.mojang.blaze3d.vertex.PoseStack;
import com.negodya1.vintageimprovements.VintageItems;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedCurvingPress;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedLaser;
import com.negodya1.vintageimprovements.content.kinetics.curving_press.CurvingRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class AssemblyLaserCutting extends SequencedAssemblySubCategory {

    AnimatedLaser laser;

    public AssemblyLaserCutting() {
        super(25);
        laser = new AnimatedLaser();
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        PoseStack ms = graphics.pose();
        laser.offset = index;
        ms.pushPose();
        ms.translate(-5, 50, 0);
        ms.scale(.6f, .6f, .6f);
        laser.draw(graphics, getWidth() / 2, 0);
        ms.popPose();
    }

}
