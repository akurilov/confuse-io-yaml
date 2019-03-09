package com.github.akurilov.confuse.io.yaml;

import com.github.akurilov.confuse.SchemaProvider;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public abstract class YamlSchemaProviderBase
implements SchemaProvider {

	@Override
	public Map<String, Object> schema()
	throws Exception {
		final var yaml = new Yaml();
		try(final var in = schemaInputStream()) {
			final var rawMap = (Map<String, Object>) yaml.load(in);

		}
	}

	protected abstract InputStream schemaInputStream()
	throws IOException;
}
