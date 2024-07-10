package com.negodya1.vintageimprovements.content.kinetics.lathe.recipe_card;

import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintageItems;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.content.kinetics.lathe.LatheMovingBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.lathe.LatheRotatingBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.lathe.TurningRecipe;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.logistics.tunnel.TunnelFlapPacket;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.recipe.RecipeConditions;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RecipeCardItem extends Item implements MenuProvider {

	public RecipeCardItem(Properties properties) {super(properties);}

	@Override
	public Component getDisplayName() {
		return getDescription();
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		ItemStack heldItem = player.getMainHandItem();
		return RecipeCardMenu.create(id, inv, heldItem);
	}

	public static ItemStackHandler getFrequencyItems(ItemStack stack) {
		ItemStackHandler newInv = new ItemStackHandler(1);
		if (VintageItems.RECIPE_CARD.get() != stack.getItem())
			throw new IllegalArgumentException("Cannot get frequency items from non-recipe card: " + stack);
		CompoundTag invNBT = stack.getOrCreateTagElement("Items");
		if (!invNBT.isEmpty())
			newInv.deserializeNBT(invNBT);
		return newInv;
	}

	public static ItemStackHandler getResultItems(ItemStack stack) {
		ItemStackHandler newInv = new ItemStackHandler(1);
		if (VintageItems.RECIPE_CARD.get() != stack.getItem())
			throw new IllegalArgumentException("Cannot get frequency items from non-recipe card: " + stack);
		CompoundTag invNBT = stack.getOrCreateTagElement("Results");
		if (!invNBT.isEmpty())
			newInv.deserializeNBT(invNBT);
		return newInv;
	}

	protected static int getIndex(ItemStack stack) {
		if (VintageItems.RECIPE_CARD.get() != stack.getItem())
			throw new IllegalArgumentException("Cannot get index from non-recipe card: " + stack);
		if (stack.getOrCreateTagElement("Recipe").contains("Index"))
			return stack.getOrCreateTagElement("Recipe").getInt("Index");
		return -1;
	}

	public static boolean haveRecipe(ItemStack stack) {
		return stack.getOrCreateTagElement("Recipe").contains("Index");
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack heldItem = player.getItemInHand(hand);

		if (!player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
			if (!world.isClientSide && player instanceof ServerPlayer && player.mayBuild())
				NetworkHooks.openGui((ServerPlayer) player, this, buf -> {
					buf.writeItem(heldItem);
				});
			return InteractionResultHolder.success(heldItem);
		}
		return InteractionResultHolder.pass(heldItem);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		int index = getIndex(stack);
		ItemStack ingredient = getFrequencyItems(stack).getStackInSlot(0);
		ItemStack result = getResultItems(stack).getStackInSlot(0);

		if (!ingredient.isEmpty())
			list.add(Components.translatable(VintageImprovements.MODID + ".item_description.ingredient")
				.append(" ").append(Components.translatable(ingredient.getDescriptionId())).withStyle(ChatFormatting.WHITE));
		if (!result.isEmpty())
			list.add(Components.translatable(VintageImprovements.MODID + ".item_description.result")
				.append(" ").append(Components.translatable(result.getDescriptionId())).withStyle(ChatFormatting.GOLD));
		if (index >= 0)
			list.add(Components.translatable(VintageImprovements.MODID + ".item_description.recipe_index")
				.append(" " + index).withStyle(ChatFormatting.DARK_GRAY));
	}

}
