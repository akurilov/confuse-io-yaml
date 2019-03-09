package com.github.akurilov.confuse.io.yaml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TypeNames {

	Map<String, Class> MAP = new HashMap<>() {{
		put("any", Object.class);
		put("boolean", boolean.class);
		put("byte", byte.class);
		put("short", short.class);
		put("char", char.class);
		put("int", int.class);
		put("long", long.class);
		put("float", float.class);
		put("double", double.class);
		put("void", void.class);
		put("string", String.class);
		put("list", List.class);
		put("map", Map.class);
	}};
}
