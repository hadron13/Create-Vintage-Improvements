package com.negodya1.vintageimprovements.compat.jei.category.assemblies;

import com.mojang.blaze3d.vertex.PoseStack;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedHelve;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedVacuumChamber;
import com.negodya1.vintageimprovements.content.kinetics.helve_hammer.HammeringRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.Components;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import static com.simibubi.create.compat.jei.category.CreateRecipeCategory.getRenderedSlot;

public class AssemblyHammering extends SequencedAssemblySubCategory {

    AnimatedHelve helve;

    public AssemblyHammering() {
        super(25);
        helve = new AnimatedHelve();
    }

    public void setRecipe(IRecipeLayoutBuilder builder, SequencedRecipe<?> recipe, IFocusGroup focuses, int x) {
        if (recipe.getRecipe().getIngredients().size() <= 1) return;

        int offset = 0;

        for (int i = 1; i < recipe.getRecipe().getIngredients().size(); i++) {
            IRecipeSlotBuilder slot = builder
                    .addSlot(RecipeIngredientRole.INPUT, x + 4, 15 + offset * 16)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addIngredients(recipe.getRecipe().getIngredients().get(i));
            offset++;
        }

        if (recipe.getRecipe() instanceof HammeringRecipe hammeringRecipe)
            if (!hammeringRecipe.getAnvilBlock().getDefaultInstance().is(Items.AIR))
                builder.addSlot(RecipeIngredientRole.INPUT, x + 4, 15 + offset * 16)
                        .setBackground(getRenderedSlot(), -1, -1)
                        .addItemStack(hammeringRecipe.getAnvilBlock().getDefaultInstance());
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, PoseStack ms, double mouseX, double mouseY, int index) {
        ms.pushPose();
        ms.translate(4, 38, 0);
        ms.scale(.5f, .5f, .5f);

        if (recipe.getRecipe() instanceof HammeringRecipe hammeringRecipe)
            if (!hammeringRecipe.getAnvilBlock().getDefaultInstance().is(Items.AIR)) {
                helve.draw(ms, getWidth() / 2, 30,2);
                helve.renderBlock(ms, getWidth() / 2, 30, hammeringRecipe.getAnvilBlock());
            }
            else helve.draw(ms, getWidth() / 2, 30, 1);

        ms.popPose();

        if (recipe.getRecipe() instanceof HammeringRecipe hammering && hammering.getHammerBlows() > 1) {
            Font font = Minecraft.getInstance().font;

            ms.pushPose();
            AllIcons.I_SEQ_REPEAT.render(ms, 5, 20);
            Component repeat = Components.literal("x" + hammering.getHammerBlows());
            font.draw(ms, repeat, 5, 35, 0x888888);

            ms.popPose();
        }
    }

}
