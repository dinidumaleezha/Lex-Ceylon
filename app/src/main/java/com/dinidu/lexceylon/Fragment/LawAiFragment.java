package com.dinidu.lexceylon.Fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.graphics.Typeface;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dinidu.lexceylon.Adapter.ChatAdapter;
import com.dinidu.lexceylon.Model.ChatMessage;
import com.dinidu.lexceylon.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LawAiFragment extends Fragment {

    private static final String API_KEY = "AIzaSyDudJCWvLjtB-JkY3qO5ZOZStdbrO6aQks";
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    private EditText etPrompt;
    private RelativeLayout btnSend;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList = new ArrayList<>();

    private OkHttpClient client = new OkHttpClient();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_law_ai, container, false);

        View rootLayout = root.findViewById(R.id.main);
        LinearLayout inputBox = rootLayout.findViewById(R.id.inputBox);

        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootLayout.getWindowVisibleDisplayFrame(r);

            int screenHeight = rootLayout.getRootView().getHeight();
            int keyboardHeight = screenHeight - r.bottom;

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) inputBox.getLayoutParams();
            layoutParams.bottomMargin = (keyboardHeight > screenHeight * 0.15) ? keyboardHeight : 0;
            inputBox.setLayoutParams(layoutParams);
        });

        etPrompt = root.findViewById(R.id.askInput);
        btnSend = root.findViewById(R.id.btnSend);
        recyclerView = root.findViewById(R.id.recyclerView);

        chatAdapter = new ChatAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(chatAdapter);

        btnSend.setOnClickListener(v -> {
            String prompt = etPrompt.getText().toString().trim();
            if (!prompt.isEmpty()) {
                addMessage(prompt, ChatMessage.TYPE_SENT);
                etPrompt.setText("");
                sendToGemini(prompt);
            }
        });

        return root;
    }

    private void addMessage(String text, int type) {
        requireActivity().runOnUiThread(() -> {
            messageList.add(new ChatMessage(text, type));
            chatAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.scrollToPosition(messageList.size() - 1);
        });
    }

    private void sendToGemini(String prompt) {
        try {
            JSONObject jsonBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();

            part.put("text", prompt);
            parts.put(part);
            content.put("parts", parts);
            contents.put(content);
            jsonBody.put("contents", contents);

            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(GEMINI_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    requireActivity().runOnUiThread(() -> {
                        addMessage("දෝෂයකි: " + e.getMessage(), ChatMessage.TYPE_RECEIVED);
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String resp = response.body().string();
                            JSONObject json = new JSONObject(resp);
                            JSONArray candidates = json.getJSONArray("candidates");
                            JSONObject first = candidates.getJSONObject(0);
                            JSONObject content = first.getJSONObject("content");
                            JSONArray parts = content.getJSONArray("parts");
                            String reply = parts.getJSONObject(0).getString("text");
                            requireActivity().runOnUiThread(() -> {
                                addMessage(reply, ChatMessage.TYPE_RECEIVED);
                            });
                        } catch (Exception e) {
                            requireActivity().runOnUiThread(() -> {
                                addMessage("Parsing error: " + e.getMessage(), ChatMessage.TYPE_RECEIVED);
                            });
                        }
                    } else {
                        requireActivity().runOnUiThread(() -> {
                            if (response.code() == 401 || response.code() == 403) {
                                addMessage("ඔබගේ API key වලංගු නොවේ. කරුණාකර පරීක්ෂා කරන්න.", ChatMessage.TYPE_RECEIVED);
                            } else if (response.code() == 404) {
                                addMessage("සෙවීමේ දෝෂයකි (404). කරුණාකර API URL එක නිවැරදිද බලන්න.", ChatMessage.TYPE_RECEIVED);
                            } else {
                                addMessage("දෝෂයකි: " + response.code(), ChatMessage.TYPE_RECEIVED);
                            }
                        });
                    }
                }
            });

        } catch (Exception e) {
            requireActivity().runOnUiThread(() -> {
                addMessage("Request error: " + e.getMessage(), ChatMessage.TYPE_RECEIVED);
            });
        }
    }

    // Markdown-style bold (*text*) to Spannable bold
    private SpannableString parseMarkdown(String input) {
        SpannableString spannable = new SpannableString(input);
        Pattern pattern = Pattern.compile("\\\\(.?)\\\\*");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            int start = matcher.start(1);
            int end = matcher.end(1);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }
}