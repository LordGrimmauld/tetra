package se.mickelus.tetra.data.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.advancements.critereon.ItemPredicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Type;

@ParametersAreNonnullByDefault
public class ItemPredicateDeserializer implements JsonDeserializer<ItemPredicate> {
	private static final Logger logger = LogManager.getLogger();

	public static ItemPredicate deserialize(JsonElement json) {
		try {
			return ItemPredicate.fromJson(json);
		} catch (JsonParseException e) {
			logger.debug("Failed to parse item predicate from \"{}\": '{}'", json, e.getMessage());
			// todo: debug level log
			return null;
		}
	}

	@Override
	public ItemPredicate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return deserialize(json);
	}
}
