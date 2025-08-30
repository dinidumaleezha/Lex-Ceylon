
package com.dinidu.lexceylon.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GeminiResponseParser {
    public static GeminiLegalResponse parse(String raw) throws Exception {
        JsonObject root = JsonParser.parseString(raw).getAsJsonObject();
        JsonArray candidates = root.getAsJsonArray("candidates");
        if (candidates == null || candidates.size() == 0) throw new Exception("No candidates");
        JsonObject content = candidates.get(0).getAsJsonObject().getAsJsonObject("content");
        JsonArray parts = content.getAsJsonArray("parts");
        if (parts == null || parts.size() == 0) throw new Exception("No parts");
        String jsonText = parts.get(0).getAsJsonObject().get("text").getAsString();
        return new Gson().fromJson(jsonText, GeminiLegalResponse.class);
    }
}
