package com.dinidu.lexceylon.Activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinidu.lexceylon.Adapter.HistoryAdapter;
import com.dinidu.lexceylon.Model.HistoryModel;
import com.dinidu.lexceylon.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecentChatActivity extends AppCompatActivity {

    private RecyclerView recyclerHistory;
    private List<HistoryModel> historyList;
    private List<HistoryModel> filteredList;
    private HistoryAdapter adapter;
    private EditText searchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_chat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RelativeLayout backBtnContainer = findViewById(R.id.backBtnContainer);
        backBtnContainer.setOnClickListener(v -> finish());

        recyclerHistory = findViewById(R.id.recyclerHistory);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));

        searchBox = findViewById(R.id.searchInput);
        historyList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new HistoryAdapter(filteredList);
        recyclerHistory.setAdapter(adapter);

        loadUserHistory();

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        // Attach custom swipe callback without RecyclerViewSwipeDecorator library
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private final ColorDrawable background = new ColorDrawable(Color.RED);
            private final Drawable deleteIcon = ContextCompat.getDrawable(RecentChatActivity.this, R.drawable.delete_icon);
            private final int iconMargin = (int) (16 * getResources().getDisplayMetrics().density); // 16dp margin

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                deleteItem(position);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX,
                                    float dY, int actionState, boolean isCurrentlyActive) {

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Draw red background behind item while swiping
                    int itemViewTop = viewHolder.itemView.getTop();
                    int itemViewBottom = viewHolder.itemView.getBottom();
                    int itemViewRight = viewHolder.itemView.getRight();

                    background.setBounds(itemViewRight + (int)dX, itemViewTop, itemViewRight, itemViewBottom);
                    background.draw(c);

                    // Calculate position of delete icon centered vertically
                    int iconTop = itemViewTop + (itemViewBottom - itemViewTop - deleteIcon.getIntrinsicHeight()) / 2;
                    int iconLeft = itemViewRight - iconMargin - deleteIcon.getIntrinsicWidth();
                    int iconRight = itemViewRight - iconMargin;
                    int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    deleteIcon.draw(c);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerHistory);
    }

    private void loadUserHistory() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("UserHistory")
                .child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear();
                filteredList.clear();

                if (!snapshot.exists()) {
                    Toast.makeText(RecentChatActivity.this, "No history data found", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                    HistoryModel history = recordSnapshot.getValue(HistoryModel.class);
                    if (history != null) {
                        historyList.add(history);
                    }
                }
                filteredList.addAll(historyList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecentChatActivity.this, "Failed to load history: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filter(String query) {
        filteredList.clear();
        String lowerCaseQuery = query.toLowerCase();

        if (lowerCaseQuery.isEmpty()) {
            filteredList.addAll(historyList);
        } else {
            for (HistoryModel item : historyList) {
                if (item.getTitle() != null &&
                        item.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void deleteItem(int position) {
        if (position < 0 || position >= filteredList.size()) return;

        HistoryModel deletedItem = filteredList.get(position);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("UserHistory")
                    .child(user.getUid());

            // Delete from Firebase by matching title
            ref.orderByChild("title").equalTo(deletedItem.getTitle())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                                recordSnapshot.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(RecentChatActivity.this,
                                    "Delete failed: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        historyList.remove(deletedItem);
        filteredList.remove(position);
        adapter.notifyItemRemoved(position);

        Toast.makeText(this, "Deleted: " + deletedItem.getTitle(), Toast.LENGTH_SHORT).show();
    }

}
