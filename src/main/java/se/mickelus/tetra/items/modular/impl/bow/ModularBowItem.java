package se.mickelus.tetra.items.modular.impl.bow;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ObjectHolder;
import se.mickelus.tetra.ConfigHandler;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.GuiModuleOffsets;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.module.ItemModule;
import se.mickelus.tetra.module.SchematicRegistry;
import se.mickelus.tetra.module.data.ModuleModel;
import se.mickelus.tetra.module.schematic.RemoveSchematic;
import se.mickelus.tetra.module.schematic.RepairSchematic;
import se.mickelus.tetra.network.PacketHandler;
import se.mickelus.tetra.properties.AttributeHelper;
import se.mickelus.tetra.properties.TetraAttributes;
import se.mickelus.tetra.util.CastOptional;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class ModularBowItem extends ModularItem {
	public final static String staveKey = "bow/stave";
	public final static String stringKey = "bow/string";
	public final static String riserKey = "bow/riser";

	public static final String unlocalizedName = "modular_bow";
	public static final double velocityFactor = 1 / 8d;
	private static final GuiModuleOffsets majorOffsets = new GuiModuleOffsets(1, 21, -11, -3);
	private static final GuiModuleOffsets minorOffsets = new GuiModuleOffsets(-14, 23);
	@ObjectHolder(TetraMod.MOD_ID + ":" + unlocalizedName)
	public static ModularBowItem instance;
	protected ModuleModel arrowModel0 = new ModuleModel("draw_0", new ResourceLocation(TetraMod.MOD_ID, "items/module/bow/arrow_0"));
	protected ModuleModel arrowModel1 = new ModuleModel("draw_1", new ResourceLocation(TetraMod.MOD_ID, "items/module/bow/arrow_1"));
	protected ModuleModel arrowModel2 = new ModuleModel("draw_2", new ResourceLocation(TetraMod.MOD_ID, "items/module/bow/arrow_2"));
	protected ItemStack vanillaBow;

	public ModularBowItem() {
		super(new Properties().stacksTo(1).fireResistant());
		setRegistryName(unlocalizedName);

		majorModuleKeys = new String[]{stringKey, staveKey};
		minorModuleKeys = new String[]{riserKey};

		requiredModules = new String[]{stringKey, staveKey};

		vanillaBow = new ItemStack(Items.BOW);

		updateConfig(ConfigHandler.honeBowBase.get(), ConfigHandler.honeBowIntegrityMultiplier.get());

		SchematicRegistry.instance.registerSchematic(new RepairSchematic(this));
		RemoveSchematic.registerRemoveSchematics(this);
	}

	/**
	 * Gets the velocity of the arrow entity from the bow's charge
	 */
	public static float getArrowVelocity(int charge, double strength, float velocityBonus, boolean suspend) {
		float velocity = (float) charge / 20.0F;

		velocity = (velocity * velocity + velocity * 2.0F) / 3.0F;


		if (velocity > 1.0F) {
			velocity = 1.0F;
		}
		// increase velocity for bows that have a higher draw strength than vanilla bows (6 strength)
		velocity = velocity * (float) Math.max(1, 1 + (strength - 6) * velocityFactor);

		if (suspend && charge >= 20) {
			velocity *= 2;
		} else {
			velocity += velocity * velocityBonus;
		}

		return velocity;
	}

	@Override
	public void init(PacketHandler packetHandler) {
		DataManager.synergyData.onReload(() -> synergies = DataManager.instance.getSynergyData("bow/"));
	}

	public void updateConfig(int honeBase, int honeIntegrityMultiplier) {
		this.honeBase = honeBase;
		this.honeIntegrityMultiplier = honeIntegrityMultiplier;
	}

	@Override
	public void clientInit() {
		super.clientInit();
		MinecraftForge.EVENT_BUS.register(new RangedProgressOverlay(Minecraft.getInstance()));
		MinecraftForge.EVENT_BUS.register(new RangedFOVTransformer());
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack itemStack) {
		if (isBroken(itemStack)) {
			return AttributeHelper.emptyMap;
		}

		if (slot == EquipmentSlot.MAINHAND) {
			return getAttributeModifiersCached(itemStack);
		}

		if (slot == EquipmentSlot.OFFHAND) {
			return getAttributeModifiersCached(itemStack).entries().stream()
				.filter(entry -> !(entry.getKey().equals(Attributes.ATTACK_DAMAGE) || entry.getKey().equals(Attributes.ATTACK_DAMAGE)))
				.collect(Multimaps.toMultimap(Map.Entry::getKey, Map.Entry::getValue, ArrayListMultimap::create));
		}

		return AttributeHelper.emptyMap;
	}

	/**
	 * Called when the player stops using an Item (stops holding the right mouse button).
	 */
	public void releaseUsing(ItemStack itemStack, Level world, LivingEntity entity, int timeLeft) {
		if (getEffectLevel(itemStack, ItemEffect.overbowed) > 0 && timeLeft <= 0) {
			entity.stopUsingItem();
			// trigger a small cooldown here to avoid the bow getting drawn again instantly
			CastOptional.cast(entity, Player.class).ifPresent(player -> player.getCooldowns().addCooldown(this, 10));
		} else {
			fireArrow(itemStack, world, entity, timeLeft);
		}
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemStack, Level world, LivingEntity entity) {
		if (getEffectLevel(itemStack, ItemEffect.overbowed) > 0) {
			entity.stopUsingItem();
			CastOptional.cast(entity, Player.class).ifPresent(player -> player.getCooldowns().addCooldown(this, 10));
		}

		return super.finishUsingItem(itemStack, world, entity);
	}

	@Override
	public void onUsingTick(ItemStack itemStack, LivingEntity player, int count) {
		if (getEffectLevel(itemStack, ItemEffect.releaseLatch) > 0 && getProgress(itemStack, player) >= 1) {
			player.releaseUsingItem();
		}
	}

	protected void fireArrow(ItemStack itemStack, Level world, LivingEntity entity, int timeLeft) {
		if (entity instanceof Player) {
			Player player = (Player) entity;
			ItemStack ammoStack = player.getProjectile(vanillaBow);

			boolean playerInfinite = isInfinite(player, itemStack, ammoStack);

			// multiply by 20 to align progress with vanilla bow (fully drawn at 1sec/20ticks)
			int drawProgress = Math.round(getProgress(itemStack, entity) * 20);
			drawProgress = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(itemStack, world, player, drawProgress,
				!ammoStack.isEmpty() || playerInfinite);

			if (drawProgress < 0) {
				return;
			}

			if (!ammoStack.isEmpty() || playerInfinite) {
				if (ammoStack.isEmpty()) {
					ammoStack = new ItemStack(Items.ARROW);
				}

				double strength = getAttributeValue(itemStack, TetraAttributes.drawStrength.get());
				float velocityBonus = getEffectLevel(itemStack, ItemEffect.velocity) / 100f;
				int suspendLevel = getEffectLevel(itemStack, ItemEffect.suspend);
				float projectileVelocity = getArrowVelocity(drawProgress, strength, velocityBonus, suspendLevel > 0);

				if (projectileVelocity > 0.1f) {
					ArrowItem ammoItem = CastOptional.cast(ammoStack.getItem(), ArrowItem.class)
						.orElse((ArrowItem) Items.ARROW);

					boolean infiniteAmmo = player.getAbilities().instabuild || ammoItem.isInfinite(ammoStack, itemStack, player);
					int count = Mth.clamp(getEffectLevel(itemStack, ItemEffect.multishot), 1, infiniteAmmo ? 64 : ammoStack.getCount());

					if (!world.isClientSide) {
						double spread = getEffectEfficiency(itemStack, ItemEffect.multishot);

						int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, itemStack);
						int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, itemStack);
						int flameLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, itemStack);
						int piercingLevel = getEffectLevel(itemStack, ItemEffect.piercing);

						for (int i = 0; i < count; i++) {
							double yaw = player.getYRot() - spread * (count - 1) / 2f + spread * i;
							AbstractArrow projectile = ammoItem.createArrow(world, ammoStack, player);
							projectile.shootFromRotation(player, player.getXRot(), (float) yaw, 0.0F, projectileVelocity * 3.0F, 1.0F);

							if (drawProgress >= 20) {
								projectile.setCritArrow(true);
							}

							// the damage modifier is based on fully drawn damage, vanilla bows deal 3 times base damage + 0-4 crit damage
							projectile.setBaseDamage(projectile.getBaseDamage() - 2 + strength / 3);

							if (powerLevel > 0) {
								projectile.setBaseDamage(projectile.getBaseDamage() + powerLevel * 0.5D + 0.5D);
							}

							// velocity multiplies arrow damage for vanilla projectiles, need to reduce damage if velocity > 1
							if (projectileVelocity > 1) {
								projectile.setBaseDamage(projectile.getBaseDamage() / projectileVelocity);
							}

							if (punchLevel > 0) {
								projectile.setKnockback(punchLevel);
							}

							if (flameLevel > 0) {
								projectile.setSecondsOnFire(100);
							}

							if (piercingLevel > 0) {
								projectile.setPierceLevel((byte) piercingLevel);
							}

							if (suspendLevel > 0 && drawProgress >= 20) {
								projectile.setNoGravity(true);
							}

							if (infiniteAmmo || player.getAbilities().instabuild
								&& (ammoStack.getItem() == Items.SPECTRAL_ARROW || ammoStack.getItem() == Items.TIPPED_ARROW)) {
								projectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
							}

							if (suspendLevel > 0 && drawProgress >= 20) {
								Vec3 projDir = projectile.getDeltaMovement().normalize();
								Vec3 projPos = projectile.position();
								for (int j = 0; j < 4; j++) {
									Vec3 pos = projPos.add(projDir.scale(2 + j * 2));
									((ServerLevel) entity.level).sendParticles(ParticleTypes.END_ROD,
										pos.x(), pos.y(), pos.z(), 1,
										0, 0, 0, 0.01);
								}
							}

							world.addFreshEntity(projectile);

							// vanilla velocity sync breaks when velocity is >3.9 on any axis
							if (projectileVelocity * 3 > 4) {
								TetraMod.packetHandler.sendToAllPlayersNear(new ProjectileMotionPacket(projectile), projectile.blockPosition(), 512, world.dimension());
							}
						}


						applyDamage(1, itemStack, player);
						applyNegativeUsageEffects(entity, itemStack, 1);

						// max draw at 20, has to be drawn at least 3/4th for positive effects
						if (drawProgress > 15) {
							applyPositiveUsageEffects(entity, itemStack, 1);
						}
					}

					float pitchBase = projectileVelocity;
					if (velocityBonus > 0) {
						pitchBase -= pitchBase * velocityBonus;
					} else if (suspendLevel > 0) {
						pitchBase = pitchBase / 2;
					}
					world.playSound(null, player.getX(), player.getY(), player.getZ(),
						SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
						0.8F + projectileVelocity * 0.2f,
						1.9f + world.random.nextFloat() * 0.2F - pitchBase * 0.8F);

					if (!infiniteAmmo && !player.getAbilities().instabuild) {
						ammoStack.shrink(count);
						if (ammoStack.isEmpty()) {
							player.getInventory().removeItem(ammoStack);
						}
					}

					player.awardStat(Stats.ITEM_USED.get(this));
				}
			}
		}
	}

	private boolean isInfinite(Player player, ItemStack bowStack, ItemStack ammoStack) {
		return player.getAbilities().instabuild
			|| (ammoStack.isEmpty() && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bowStack) > 0)
			|| CastOptional.cast(ammoStack.getItem(), ArrowItem.class)
			.map(item -> item.isInfinite(ammoStack, bowStack, player))
			.orElse(false);
	}

	public int getDrawDuration(ItemStack itemStack) {
		return Math.max((int) (20 * getAttributeValue(itemStack, TetraAttributes.drawSpeed.get())), 1);
	}

	/**
	 * Returns a value between 0 - 1 representing how far the bow has been drawn, a value of 1 means that the bow is fully drawn
	 *
	 * @param itemStack
	 * @param entity
	 * @return
	 */
	public float getProgress(ItemStack itemStack, @Nullable LivingEntity entity) {
		return Optional.ofNullable(entity)
			.filter(e -> e.getUseItemRemainingTicks() > 0)
			.filter(e -> itemStack.equals(e.getUseItem()))
			.map(e -> (getUseDuration(itemStack) - e.getUseItemRemainingTicks()) * 1f / getDrawDuration(itemStack))
			.orElse(0f);
	}

	public float getOverbowProgress(ItemStack itemStack, @Nullable LivingEntity entity) {
		int overbowedLevel = getEffectLevel(itemStack, ItemEffect.overbowed);
		if (overbowedLevel > 0) {
			return Optional.ofNullable(entity)
				.filter(e -> itemStack.equals(e.getUseItem()))
				.map(LivingEntity::getUseItemRemainingTicks)
				.map(useCount -> 1 - useCount / (overbowedLevel * 2f))
				.map(progress -> Mth.clamp(progress, 0, 1))
				.orElse(0f);
		}

		return 0;
	}

	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getUseDuration(ItemStack itemStack) {
		int overbowedLevel = getEffectLevel(itemStack, ItemEffect.overbowed);
		if (overbowedLevel > 0) {
			// each level equals a 0.1 seconds, times 20 ticks per second = 2
			return overbowedLevel * 2 + getDrawDuration(itemStack);
		}

		return 37000;
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	@Override
	public boolean canBeDepleted() {
		return true;
	}

	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack bowStack = player.getItemInHand(hand);
		boolean hasAmmo = !player.getProjectile(vanillaBow).isEmpty();

		if (isBroken(bowStack)) {
			return InteractionResultHolder.pass(bowStack);
		}

		InteractionResultHolder<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(bowStack, world, player, hand, hasAmmo);
		if (ret != null) return ret;

		if (!hasAmmo && !player.getAbilities().instabuild && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bowStack) <= 0) {
			return InteractionResultHolder.fail(bowStack);
		} else {
			player.startUsingItem(hand);
			return InteractionResultHolder.consume(bowStack);
		}
	}

	private String getDrawVariant(ItemStack itemStack, @Nullable LivingEntity entity) {
		float progress = getProgress(itemStack, entity);

		if (progress == 0) {
			return "item";
		} else if (progress < 0.65) {
			return "draw_0";
		} else if (progress < 0.9) {
			return "draw_1";
		}
		return "draw_2";
	}

	private ModuleModel getArrowModel(String drawVariant) {
		switch (drawVariant) {
			case "draw_0":
				return arrowModel0;
			case "draw_1":
				return arrowModel1;
			case "draw_2":
				return arrowModel2;
			default:
				return arrowModel0;
		}
	}

	@Override
	public String getModelCacheKey(ItemStack itemStack, LivingEntity entity) {
		return super.getModelCacheKey(itemStack, entity) + ":" + getDrawVariant(itemStack, entity);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ImmutableList<ModuleModel> getModels(ItemStack itemStack, @Nullable LivingEntity entity) {
		String modelType = getDrawVariant(itemStack, entity);

		ImmutableList<ModuleModel> models = getAllModules(itemStack).stream()
			.sorted(Comparator.comparing(ItemModule::getRenderLayer))
			.flatMap(itemModule -> Arrays.stream(itemModule.getModels(itemStack)))
			.filter(Objects::nonNull)
			.filter(model -> model.type.equals(modelType) || model.type.equals("static"))
			.collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));

		if (!modelType.equals("item")) {
			return ImmutableList.<ModuleModel>builder()
				.addAll(models)
				.add(getArrowModel(modelType))
				.build();
		}

		return models;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public GuiModuleOffsets getMajorGuiOffsets() {
		return majorOffsets;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public GuiModuleOffsets getMinorGuiOffsets() {
		return minorOffsets;
	}
}
