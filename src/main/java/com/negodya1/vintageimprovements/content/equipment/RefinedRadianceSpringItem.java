package com.negodya1.vintageimprovements.content.equipment;

import com.negodya1.vintageimprovements.VintageImprovements;
import com.simibubi.create.content.legacy.RefinedRadianceItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class RefinedRadianceSpringItem extends RefinedRadianceItem {

    int stiffness;

    public RefinedRadianceSpringItem(Properties properties, int stiffness) {
        super(properties);
        this.stiffness = stiffness;
    }

    public int getStiffness() {return stiffness;}

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        list.add(Component.translatable(VintageImprovements.MODID + ".item_description.spring_stiffness")
                .append(" " + stiffness).withStyle(ChatFormatting.GOLD));
    }

}
