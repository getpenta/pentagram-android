package org.telegram.pentagram.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils{

    public static <T> String listToJsonOrEmpty(List<T> list) {
        try {
            return new Gson().toJson(list);
        } catch(Exception exception) {
            return "";
        }
    }

    public static <T> ArrayList<T> listFromJson(String json, ArrayList<T> def) {
        try {
            Type type = new TypeToken<ArrayList<T>>(){}.getType();
            ArrayList<T> result = new Gson().fromJson(json, type);
            return result==null ? def : result;
        } catch(Exception e) {
            return def;
        }
    }

    public static String toJsonOrEmpty(Object object) {
        try {
            return new Gson().toJson(object);
        } catch(Exception exception) {
            return "";
        }
    }

    public static <T> T fromJsonOrNull(String json, Class<T> clazz, T def) {
        try {
            return new Gson().fromJson(json, clazz);
        } catch(Exception e) {
            return def;
        }
    }

}
