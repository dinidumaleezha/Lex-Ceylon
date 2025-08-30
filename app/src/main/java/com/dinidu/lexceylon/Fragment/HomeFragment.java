package com.dinidu.lexceylon.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinidu.lexceylon.Activity.AdviceActivity;
import com.dinidu.lexceylon.Activity.PushEmailActivity;
import com.dinidu.lexceylon.Activity.RecentChatActivity;
import com.dinidu.lexceylon.Activity.SearchLawActivity;
import com.dinidu.lexceylon.Adapter.HistoryAdapter;
import com.dinidu.lexceylon.Class.LanguageHelper;
import com.dinidu.lexceylon.Class.ShaderSpan;
import com.dinidu.lexceylon.Model.HistoryModel;
import com.dinidu.lexceylon.R;
import com.dinidu.lexceylon.Utils.SharedPrefManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView gradientTextView;
    private TextView greetingText;
    private RecyclerView recyclerHistory;
    private List<HistoryModel> historyList;
    private HistoryAdapter adapter;
    FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LanguageHelper.loadLocale(getContext());
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        RelativeLayout emailGenerator = root.findViewById(R.id.emailPage);
        emailGenerator.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), PushEmailActivity.class));
        });

        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        Toast.makeText(getContext(), uid, Toast.LENGTH_SHORT).show();

        greetingText = root.findViewById(R.id.greetingText);
        greetingText.setText(getGreetingMessage());


        SharedPrefManager prefManager = new SharedPrefManager(getContext());
        gradientTextView = root.findViewById(R.id.hello_title);

        String fullName = prefManager.getName();

        if (fullName != null && !fullName.isEmpty()) {
            String[] nameParts = fullName.split(" ");

            if (nameParts.length >= 1) {
                String firstName = nameParts[0];
                String lastName = (nameParts.length >= 2) ? nameParts[1] : "";

                helloTitleLoad(firstName);
            } else {
                helloTitleLoad("User");
            }
        } else {
            helloTitleLoad("User");
        }


        RelativeLayout lawSearch = root.findViewById(R.id.law_search);
        lawSearch.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SearchLawActivity.class));
        });

        RelativeLayout btnGetAdvice = root.findViewById(R.id.btnGetAdvice);
        btnGetAdvice.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AdviceActivity.class));
        });

        LinearLayout recent_chat = root.findViewById(R.id.recent_chat);
        recent_chat.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), RecentChatActivity.class));
        });

        recyclerHistory = root.findViewById(R.id.recyclerHistory);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(historyList);
        recyclerHistory.setAdapter(adapter);

        loadUserHistory();
        return root;
    }

    private void helloTitleLoad(String firstName) {

        String name = firstName != null ? firstName.trim() : "User";
        String fullText = "Hello, " + name;

        SpannableString spannable = new SpannableString(fullText);

        int helloColor = Color.parseColor("#0080ff");

        int[] gradientColors = new int[]{
                Color.parseColor("#9B59B6"), // purple
                Color.parseColor("#E91E63"), // pink
                Color.parseColor("#F06292")  // lighter pink
        };

        int helloLength = "Hello,".length();
        int nameStart = helloLength + 1; // index of 'T' in "Thushara"
        int nameLength = fullText.length() - nameStart;

        spannable.setSpan(new ForegroundColorSpan(helloColor), 0, helloLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextPaint paint = gradientTextView.getPaint();
        float textSize = gradientTextView.getTextSize();

        Shader textShader = new LinearGradient(
                0, 0, paint.measureText(fullText.substring(nameStart, fullText.length())), 0,
                gradientColors,
                null,
                Shader.TileMode.CLAMP);

        spannable.setSpan(new ShaderSpan(textShader), nameStart, fullText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        gradientTextView.setText(spannable);
    }

    private String getGreetingMessage() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 12) {
            return getString(R.string.good_morning);
        } else if (hour >= 12 && hour < 17) {
            return getString(R.string.good_afternoon);
        } else if (hour >= 17 && hour < 21) {
            return getString(R.string.good_evening);
        } else {
            return getString(R.string.good_night);
        }
    }

    private void loadUserHistory() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("UserHistory")
                .child(uid);
        ///addListenerForSingleValueEvent
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear();

                if (!snapshot.exists()) {
                    Toast.makeText(getContext(), "No history data found", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    return;
                }

                for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                    HistoryModel history = recordSnapshot.getValue(HistoryModel.class);
                    if (history != null) {
                        historyList.add(history);
                    }
                }

                Collections.reverse(historyList);

                if (historyList.size() > 3) {
                    historyList = historyList.subList(0, 3);
                }

                adapter = new HistoryAdapter(historyList);
                recyclerHistory.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load history: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}