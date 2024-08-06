package com.negodya1.vintageimprovements.foundation.advancement;

import com.negodya1.vintageimprovements.VintageImprovements;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public enum VintageAdvancements {

    USE_BELT_GRINDER("use_belt_grinder"),
    USE_COILING_MACHINE("use_coiling_machine"),
    USE_COMPRESSOR("use_compressor"),
    USE_CENTRIFUGE("use_centrifuge"),
    USE_CURVING_PRESS("use_curving_press"),
    USE_HELVE("use_helve"),
    USE_LATHE("use_lathe"),
    USE_LASER("use_laser"),
    USE_VIBRATION_TABLE("use_vibration_table"),
    INSERT_RECIPE_CARD("insert_recipe_card"),
    BELT_GRINDER_SKIN_CHANGE("belt_grinder_skin_change");

    private String id;
    private SimpleVintageTrigger trigger;

	VintageAdvancements(String id) {
        this.id = id;
        trigger = new SimpleVintageTrigger(id);
    };

    public void award(Level level, Player player) {
        if (level.isClientSide()) return;
        if (player instanceof ServerPlayer serverPlayer) {
            trigger.trigger(serverPlayer);
        } else {
            VintageImprovements.logThis("Could not award Vintage Improvements Advancement " + id + " to client-side Player.");
        };
    };

    public static void register() {
        for (VintageAdvancements e : values()) {
            CriteriaTriggers.register(e.trigger);
        };
    };
}
