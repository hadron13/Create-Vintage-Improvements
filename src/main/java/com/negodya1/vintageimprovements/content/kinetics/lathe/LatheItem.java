package com.negodya1.vintageimprovements.content.kinetics.lathe;

import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.infrastructure.config.VintageConfig;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.kinetics.BlockStressValues;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlock;
import com.simibubi.create.content.processing.AssemblyOperatorUseContext;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.utility.*;

import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CKinetics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.GRAY;

public class LatheItem extends BlockItem {

	public LatheItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}

	@Override
	public InteractionResult place(BlockPlaceContext ctx) {
		InteractionResult result = super.place(ctx);
		if (result != InteractionResult.FAIL)
			return result;
		Direction clickedFace = ctx.getClickedFace();
		if (clickedFace.getAxis() != Axis.Y)
			result = super.place(BlockPlaceContext.at(ctx, ctx.getClickedPos()
					.relative(clickedFace), clickedFace));
		if (result == InteractionResult.FAIL && ctx.getLevel()
				.isClientSide())
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> showBounds(ctx));
		return result;
	}

	@OnlyIn(Dist.CLIENT)
	public void showBounds(BlockPlaceContext context) {
		BlockPos pos = context.getClickedPos();
		Direction direction = context.getHorizontalDirection();
		Vec3 contract = Vec3.atLowerCornerOf(Direction.get(AxisDirection.POSITIVE, direction.getAxis())
			.getNormal());
		if (!(context.getPlayer() instanceof LocalPlayer localPlayer))
			return;

		int xOffset = 1;
		if (direction == Direction.NORTH || direction == Direction.WEST) xOffset *= -1;

		CreateClient.OUTLINER.showAABB(Pair.of("lathe", pos), new AABB(pos.relative(context.getHorizontalDirection()))
						.contract((direction == Direction.NORTH || direction == Direction.SOUTH ? 0 : xOffset),
								0, (direction == Direction.NORTH || direction == Direction.SOUTH ? xOffset : 0))
			.deflate(contract.x, contract.y, contract.z))
			.colored(0xFF_ff5d6c);
		Lang.translate("large_water_wheel.not_enough_space")
			.color(0xFF_ff5d6c)
			.sendStatus(localPlayer);
	}

	@Override
	public void appendHoverText(ItemStack item, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(item, level, list, flag);

		list.add(Components.literal(""));
		list.addAll(getStressImpact());
	}

	private List<Component> getStressImpact() {
		List<Component> list = new ArrayList<>();

		CKinetics config = AllConfigs.server().kinetics;
		LangBuilder rpmUnit = Lang.translate("generic.unit.rpm");
		LangBuilder suUnit = Lang.translate("generic.unit.stress");

		Lang.translate("tooltip.stressImpact")
				.style(GRAY)
				.addTo(list);

		double impact = BlockStressValues.getImpact(VintageBlocks.LATHE_MOVING.get());
		IRotate.StressImpact impactId = impact >= config.highStressImpact.get() ? IRotate.StressImpact.HIGH
				: (impact >= config.mediumStressImpact.get() ? IRotate.StressImpact.MEDIUM : IRotate.StressImpact.LOW);
		LangBuilder builder = Lang.builder()
				.add(Lang.text(TooltipHelper.makeProgressBar(3, impactId.ordinal() + 1))
						.style(impactId.getAbsoluteColor()));

		builder.add(Lang.number(impact))
				.text("x ")
				.add(rpmUnit)
				.addTo(list);

		return list;
	}

}
