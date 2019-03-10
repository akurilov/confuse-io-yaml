package com.github.akurilov.confuse.io.yaml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.github.akurilov.confuse.SchemaProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class YamlSchemaProviderBase
implements SchemaProvider {

	@Override
	public Map<String, Object> schema()
	throws IOException  {
		final var m = new YAMLMapper()
			.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
			.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
			.enable(SerializationFeature.INDENT_OUTPUT);
		final Map<String, Object> rawSchema;
		try(final var input = schemaInputStream()) {
			rawSchema = m.readValue(input, new TypeReference<Map<String, Object>>() {});
		}
		return deserializeTypes(rawSchema);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> deserializeTypes(final Map<String, Object> rawNode)
	throws ClassCastException {
		final Map<String, Object> node = new HashMap<>();
		for(final Map.Entry<String, Object> entry: rawNode.entrySet()) {
			final String key = entry.getKey();
			final Object val = entry.getValue();
			if(val instanceof String) {
				final Class resolvedType = TypeNames.MAP.get(val);
				if(null == resolvedType) {
					throw new IllegalArgumentException("The type \"" + val + "\" couldn't be resolved");
				} else {
					node.put(key, resolvedType);
				}
			} else {
				node.put(key, deserializeTypes((Map<String, Object>) val));
			}
		}
		return node;
	}

	protected abstract InputStream schemaInputStream()
	throws IOException;
}
