package se.mickelus.tetra.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
public class GenericTrigger<T extends AbstractCriterionTriggerInstance> extends SimpleCriterionTrigger<T> {
	private final ResourceLocation id;

	private final TriggerDeserializer<T> deserializer;

	public GenericTrigger(String id, TriggerDeserializer<T> deserializer) {
		super();
		this.id = new ResourceLocation(id);
		this.deserializer = deserializer;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	protected T createInstance(JsonObject json, EntityPredicate.Composite entityPredicate, DeserializationContext conditionsParser) {
		return deserializer.apply(json, entityPredicate, conditionsParser);
	}

	/**
	 * Fulfills all criterion instances that pass the validation predicate.
	 *
	 * @param player              The player that the criterion is to be fulfilled for
	 * @param validationPredicate A predicate used to check which criterion will be fulfilled
	 */
	public void fulfillCriterion(ServerPlayer player, Predicate<T> validationPredicate) {
		trigger(player, validationPredicate);
	}

	public interface TriggerDeserializer<T> {
		T apply(JsonObject json, EntityPredicate.Composite entityPredicate, DeserializationContext conditionsParser);
	}
}
