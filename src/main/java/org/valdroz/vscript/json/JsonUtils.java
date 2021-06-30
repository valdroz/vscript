package org.valdroz.vscript.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author Valerijus Drozdovas
 * Created on 4/16/21
 */
public class JsonUtils {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {
    }.getType();


    public static void set(JsonObject jo, String path, JsonElement value) {
        if (value == null) {
            setJsonObjectPathElement(jo, path, JsonNull.INSTANCE);
        } else {
            setJsonObjectPathElement(jo, path, value);
        }
    }

    public static void set(JsonObject jo, String path, String value) {
        if (value == null) {
            setJsonObjectPathElement(jo, path, JsonNull.INSTANCE);
        } else {
            setJsonObjectPathElement(jo, path, new JsonPrimitive(value));
        }
    }

    public static void set(JsonObject jo, String path, Number value) {
        if (value == null) {
            setJsonObjectPathElement(jo, path, JsonNull.INSTANCE);
        } else {
            setJsonObjectPathElement(jo, path, new JsonPrimitive(value));
        }
    }

    public static void set(JsonObject jo, String path, Boolean value) {
        if (value == null) {
            setJsonObjectPathElement(jo, path, JsonNull.INSTANCE);
        } else {
            setJsonObjectPathElement(jo, path, new JsonPrimitive(value));
        }
    }

    public static void set(JsonObject jo, String path, Character value) {
        if (value == null) {
            setJsonObjectPathElement(jo, path, JsonNull.INSTANCE);
        } else {
            setJsonObjectPathElement(jo, path, new JsonPrimitive(value));
        }
    }

    public static <T> void setArray(JsonObject jo, String path, Iterable<T> values) {
        if (values == null) {
            setJsonObjectPathElement(jo, path, JsonNull.INSTANCE);
        } else {
            JsonArray arr = new JsonArray();
            values.forEach(s -> {
                if (s != null) {
                    if (s instanceof String) {
                        arr.add((String) s);
                    } else if (s instanceof Number) {
                        arr.add((Number) s);
                    } else if (s instanceof Boolean) {
                        arr.add((Boolean) s);
                    } else if (s instanceof Character) {
                        arr.add((Character) s);
                    } else if (s instanceof JsonElement) {
                        arr.add((JsonElement) s);
                    }
                }
            });
            setJsonObjectPathElement(jo, path, arr);
        }
    }


    public static void setJsonObjectPathElement(JsonObject jo, String path, JsonElement value) {
        String[] _path = path.split("\\.", 2);
        PathIndex _pathIndex = getPathIndex(_path[0]);
        if (_path.length == 1) {
            if (_pathIndex.idx < 0) {
                if (jo.has(_path[0])) {
                    jo.remove(_path[0]);
                }
                jo.add(_path[0], value);
            } else {
                com.google.gson.JsonElement element = jo.get(_pathIndex.path);
                JsonArray array;
                if (element == null) {
                    array = new JsonArray();
                    jo.add(_pathIndex.path, array);
                } else {
                    array = jo.getAsJsonArray(_pathIndex.path);
                }
                while (array.size() <= _pathIndex.idx) {
                    array.add((JsonNull.INSTANCE));
                }
                array.set(_pathIndex.idx, value);
            }
        } else {
            if (_pathIndex.idx < 0) {
                if (!jo.has(_path[0])) {
                    jo.add(_path[0], new JsonObject());
                }
                JsonElement element = jo.get(_path[0]);
                setJsonObjectPathElement(element.getAsJsonObject(), _path[1], value);
            } else {
                JsonElement element = jo.get(_pathIndex.path);
                JsonArray array;
                if (element == null) {
                    array = new JsonArray();
                    jo.add(_pathIndex.path, array);
                } else {
                    array = jo.getAsJsonArray(_pathIndex.path);
                }
                while (array.size() <= _pathIndex.idx) {
                    array.add(new JsonObject());
                }
                setJsonObjectPathElement(array.get(_pathIndex.idx).getAsJsonObject(), _path[1], value);
            }
        }
    }

    private static PathIndex getPathIndex(String path) {
        try {
            PathIndex pathIndex = new PathIndex();
            String[] items = path.split("\\[");
            if (items.length == 1) {
                pathIndex.idx = -1;
                pathIndex.path = path;
                return pathIndex;
            }
            pathIndex.path = items[0];
            pathIndex.idx = Integer.parseInt(items[1].split("]")[0]);
            return pathIndex;
        } catch (Exception ex) {
            throw new RuntimeException("Cannot understand path element \"" + path + "\"");
        }
    }

    public static Map<String, Object> toMap(JsonObject jo) {
        if (jo == null) {
            return null;
        }
        return GSON.fromJson(jo, MAP_TYPE);
    }

    public static JsonObject merge(JsonObject target, JsonObject from) {
        JsonObject result = target.deepCopy();
        from.entrySet().forEach(entry -> {
                    JsonElement je1 = result.get(entry.getKey());
                    JsonElement je2 = entry.getValue().deepCopy();
                    if (je2 != null) {
                        if (je1 != null) {
                            if (je1.isJsonObject() && je2.isJsonObject()) {
                                result.add(entry.getKey(), merge(je1.getAsJsonObject(), je2.getAsJsonObject()));
                            } else if (je1.isJsonArray() && je2.isJsonArray()) {
                                JsonArray ja1 = je1.getAsJsonArray();
                                JsonArray ja2 = je2.getAsJsonArray();
                                ja2.forEach(ja1::add);
                            } else {
                                result.add(entry.getKey(), entry.getValue().deepCopy());
                            }
                        } else {
                            result.add(entry.getKey(), entry.getValue().deepCopy());
                        }
                    }
                }
        );
        return result;
    }

    private static class PathIndex {
        public int idx;
        public String path;
    }


}
