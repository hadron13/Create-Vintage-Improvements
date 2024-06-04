package com.negodya1.vintageimprovements.compat.jei.category.assemblies;

import com.mojang.blaze3d.vertex.PoseStack;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedGrinder;
import com.negodya1.vintageimprovements.content.kinetics.grinder.PolishingRecipe;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class AssemblyPolishing extends SequencedAssemblySubCategory {

    AnimatedGrinder grinder;

    public AssemblyPolishing() {
        super(25);
        grinder = new AnimatedGrinder();
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, PoseStack ms, double mouseX, double mouseY, int index) {
        ms.pushPose();
        ms.translate(0, 51.5f, 0);
        ms.scale(.6f, .6f, .6f);
        grinder.draw(ms, getWidth() / 2, 30);
        ms.popPose();

        if (recipe.getRecipe() instanceof PolishingRecipe polishing) {
            Font font = Minecraft.getInstance().font;

            ms.pushPose();

            switch (polishing.getSpeedLimits()) {
                case 1 -> font.draw(ms, Components.literal("█░░"), 0, 52, 0x00FF00);
                case 2 -> font.draw(ms, Components.literal("██░"), 0, 52, 0xFFFF00);
                case 3 -> font.draw(ms, Components.literal("███"), 0, 52, 0xFF0000);
                default -> font.draw(ms, Components.literal("░░░"), 0, 52, 0xFFFFFF);
            }
            ms.popPose();
        }
    }

}
