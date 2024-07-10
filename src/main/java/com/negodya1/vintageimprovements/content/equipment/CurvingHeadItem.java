package com.negodya1.vintageimprovements.content.equipment;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CurvingHeadItem extends Item {

    public CurvingHeadItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

}
