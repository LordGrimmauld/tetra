package se.mickelus.tetra.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se.mickelus.tetra.TetraMod;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class MergingDataStore<V, U> extends DataStore<V> {
	private static final Logger logger = LogManager.getLogger();

	protected Class<U> arrayClass;

	public MergingDataStore(Gson gson, String directory, Class<V> entryClass, Class<U> arrayClass) {
		super(gson, directory, entryClass);

		this.arrayClass = arrayClass;
	}

	@Override
	protected Map<ResourceLocation, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
		logger.debug("Reading data for {} data store...", directory);
		Map<ResourceLocation, JsonElement> map = Maps.newHashMap();
		int i = this.directory.length() + 1;

		for (ResourceLocation fullLocation : resourceManager.listResources(directory, rl -> rl.endsWith(".json"))) {
			if (!TetraMod.MOD_ID.equals(fullLocation.getNamespace())) {
				continue;
			}

			String path = fullLocation.getPath();
			ResourceLocation location = new ResourceLocation(fullLocation.getNamespace(), path.substring(i, path.length() - jsonExtLength));

			JsonArray allResources = new JsonArray();

			try {
				for (Resource resource : resourceManager.getResources(fullLocation)) {
					try (
						InputStream inputstream = resource.getInputStream();
						Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))
					) {
						JsonObject json = GsonHelper.fromJson(gson, reader, JsonObject.class);

						if (json != null) {
							if (shouldLoad(json)) {
								allResources.add(json);
							} else {
								logger.debug("Skipping data '{}' from '{}' due to condition", fullLocation, resource.getSourceName());
							}
						} else {
							logger.error("Couldn't load data from '{}' in data pack '{}' as it's empty or null",
								fullLocation, resource.getSourceName());
						}
					} catch (RuntimeException | IOException e) {
						logger.error("Couldn't load data from '{}' in data pack '{}'", fullLocation, resource.getSourceName(), e);
					} finally {
						IOUtils.closeQuietly(resource);
					}
				}
			} catch (IOException e) {
				logger.error("Couldn't load data from '{}'", fullLocation, e);
			}

			if (allResources.size() > 0) {
				map.put(location, allResources);
			}
		}

		return map;
	}

	@Override
	public void loadFromPacket(Map<ResourceLocation, String> data) {
		Map<ResourceLocation, JsonElement> splashList = data.entrySet().stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				entry -> GsonHelper.fromJson(gson, entry.getValue(), JsonArray.class)
			));

		parseData(splashList);
	}

	public void parseData(Map<ResourceLocation, JsonElement> splashList) {
		logger.info("Loaded {} {}", String.format("%3d", splashList.values().size()), directory);
		dataMap = splashList.entrySet().stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				entry -> mergeData(gson.fromJson(entry.getValue(), arrayClass))
			));

		processData();

		listeners.forEach(Runnable::run);
	}

	protected abstract V mergeData(U collection);
}
