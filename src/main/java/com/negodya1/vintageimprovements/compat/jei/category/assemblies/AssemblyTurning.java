package com.negodya1.vintageimprovements.compat.jei.category.assemblies;

import com.mojang.blaze3d.vertex.PoseStack;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedGrinder;
import com.negodya1.vintageimprovements.compat.jei.category.animations.AnimatedLathe;
import com.negodya1.vintageimprovements.content.kinetics.grinder.PolishingRecipe;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class AssemblyTurning extends SequencedAssemblySubCategory {

    AnimatedLathe lathe;

    public AssemblyTurning() {
        super(25);
        lathe = new AnimatedLathe();
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, PoseStack ms, double mouseX, double mouseY, int index) {
        ms.pushPose();
        ms.translate(0, 51.5f, 0);
        ms.scale(.6f, .6f, .6f);
        lathe.draw(ms, getWidth() / 2, 30);
        ms.popPose();
    }

}
