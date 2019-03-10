package com.github.akurilov.confuse.io.yaml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.github.akurilov.confuse.Config;
import com.github.akurilov.confuse.SchemaProvider;
import com.github.akurilov.confuse.exceptions.InvalidValueTypeException;
import com.github.akurilov.confuse.impl.BasicConfig;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class YamlSchemaTest
extends YamlSchemaProviderBase {

	@Override
	protected InputStream schemaInputStream()
	throws IOException {
		final URL res = getClass().getResource("/test-schema.yaml");
		if(res == null) {
			throw new FileNotFoundException("resources://test-schema.yaml");
		}
		return res.openStream();
	}

	@Override
	public String id() {
		return "test";
	}

	private static ObjectMapper objectMapper(final Map<String, Object> schema)
	throws NoSuchMethodException {
		return new YAMLMapper()
			.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
			.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
			.enable(SerializationFeature.INDENT_OUTPUT);
	}

	@Test
	public final void testResolvedSchemaContent()
	throws Exception {
		final Map<String, Object> schema = SchemaProvider.resolveAndReduce(
			"test", getClass().getClassLoader()
		);
		assertNotNull(schema);
		assertEquals(Object.class, ((Map<String, Class>) schema.get("objects")).get("any"));
		assertEquals(String.class, ((Map<String, Class>) schema.get("objects")).get("aString"));
		assertEquals(List.class, ((Map<String, Class>) schema.get("objects")).get("aList"));
		assertEquals(Map.class, ((Map<String, Class>) schema.get("objects")).get("aMap"));
		assertEquals(boolean.class, ((Map<String, Class>) schema.get("primitives")).get("aBoolean"));
		assertEquals(int.class, ((Map<String, Class>) schema.get("primitives")).get("anInt"));
		assertEquals(long.class, ((Map<String, Class>) schema.get("primitives")).get("aLong"));
		assertEquals(double.class, ((Map<String, Class>) schema.get("primitives")).get("aDouble"));
	}

	@Test
	public final void testResolvedSchemaConfigMatches()
	throws Exception {
		final Map<String, Object> schema = SchemaProvider.resolveAndReduce("test", getClass().getClassLoader());
		final Map<String, Object> configTree;
		try(final InputStream configInput = getClass().getResource("/test-config.yaml").openStream()) {
			configTree = objectMapper(schema).readValue(configInput, new TypeReference<Map<String, Object>>() {});
		}
		final var config = new BasicConfig("-", schema, configTree);
		assertNotNull(config);
	}

	@Test
	public final void testResolvedSchemaConfigMismatch()
	throws Exception {
		final Map<String, Object> schema = SchemaProvider.resolveAndReduce(
			"test", getClass().getClassLoader()
		);
		final Config config = new BasicConfig("-", schema);

		final List listVal = Arrays.asList(1, 2, 3);
		try {
			config.val("objects-aString", listVal);
			fail();
		} catch(final InvalidValueTypeException e) {
			assertEquals("objects-aString", e.path());
			assertEquals(String.class, e.expectedType());
			assertEquals(listVal.getClass(), e.actualType());
		}

		try {
			config.val("primitives-aBoolean", 0);
			fail();
		} catch(final InvalidValueTypeException e) {
			assertEquals("primitives-aBoolean", e.path());
			assertEquals(boolean.class, e.expectedType());
			assertEquals(Integer.class, e.actualType());
		}
	}
}
