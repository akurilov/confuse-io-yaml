package com.github.akurilov.confuse.io.yaml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.github.akurilov.confuse.Config;
import com.github.akurilov.confuse.ConfigProvider;
import com.github.akurilov.confuse.impl.BasicConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public abstract class YamlConfigProviderBase
	implements ConfigProvider {

	@Override
	public Config config(final String pathSep, final Map<String, Object> schema)
	throws IOException {
		final var mapper = new YAMLMapper()
			.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
			.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
			.enable(SerializationFeature.INDENT_OUTPUT);
		final Map<String, Object> configTree;
		try(final var input = configInputStream()) {
			configTree = mapper.readValue(input, new TypeReference<Map<String, Object>>() {});
		}
		return new BasicConfig(pathSep, schema, configTree);
	}

	protected abstract InputStream configInputStream()
	throws IOException;
}
