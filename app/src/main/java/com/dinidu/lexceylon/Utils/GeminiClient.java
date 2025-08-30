
package com.dinidu.lexceylon.Utils;

import static android.provider.Settings.System.getString;

import android.content.Context;

import com.dinidu.lexceylon.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URL;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class GeminiClient {
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json");

    public static void ask(Context ctx, String prompt, okhttp3.Callback callback) {
        JsonObject genConfig = new JsonObject();
        genConfig.addProperty("temperature", 0.25);
        genConfig.addProperty("response_mime_type", "application/json");

        JsonObject part = new JsonObject();
        part.addProperty("text", prompt);

        JsonObject content = new JsonObject();
        content.addProperty("role", "user");
        JsonArray parts = new JsonArray();
        parts.add(part);
        content.add("parts", parts);

        JsonObject root = new JsonObject();
        JsonArray contents = new JsonArray();
        contents.add(content);
        root.add("contents", contents);
        root.add("generationConfig", genConfig);

        //String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro:generateContent?key=" + BuildConfig.GEMINI_API_KEY;
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyDudJCWvLjtB-JkY3qO5ZOZStdbrO6aQks";
        RequestBody body = RequestBody.create(root.toString(), JSON);
        Request request = new Request.Builder().url(url).post(body).build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
