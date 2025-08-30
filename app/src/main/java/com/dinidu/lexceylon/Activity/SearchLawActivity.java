package com.dinidu.lexceylon.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinidu.lexceylon.Adapter.LawAdapter;
import com.dinidu.lexceylon.Model.LawItem;
import com.dinidu.lexceylon.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchLawActivity extends AppCompatActivity {

    private EditText searchInput;
    private RecyclerView recyclerView;
    private LawAdapter adapter;

    private DatabaseReference dbRef;
    private final List<LawItem> lawList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_law);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RelativeLayout backBtnContainer = findViewById(R.id.backBtnContainer);
        backBtnContainer.setOnClickListener(v -> finish());

        FirebaseApp.initializeApp(this);
        dbRef = FirebaseDatabase.getInstance().getReference("laws");

        searchInput = findViewById(R.id.searchInput);
        recyclerView = findViewById(R.id.search_rv);

        setupRecycler();
        loadAllLaws();
        setupSearchListener();
        setupBackButton();
    }

    private void setupRecycler() {
        adapter = new LawAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClick(item ->
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show()
        );
    }

    private void setupBackButton() {
        ImageView back = findViewById(R.id.backBtnIcon);
        if (back != null) back.setOnClickListener(v -> onBackPressed());
    }

    private void loadAllLaws() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                lawList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    LawItem item = ds.getValue(LawItem.class);
                    if(item != null) lawList.add(item);
                }
                adapter.submitList(new ArrayList<>(lawList));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(SearchLawActivity.this, "DB Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearchListener() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String q = s.toString().trim().toLowerCase(Locale.US);
                List<LawItem> filtered = new ArrayList<>();
                for (LawItem item : lawList) {
                    if (item.getTitle() != null && item.getTitle().toLowerCase(Locale.US).contains(q)) {
                        filtered.add(item);
                    }
                }
                adapter.submitList(filtered);
            }
        });
    }
}
