package edu.neu.madcourse.NUMAD21Su_LilyBessette;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class LinkCollectorActivity extends AppCompatActivity {
    Button back;
    private ArrayList<LinkItemCard> linkItemList = new ArrayList<>();
    private RecyclerView linkRecyclerView;
    private LinkRviewAdapter linkRviewAdapter;
    private RecyclerView.LayoutManager linkRLayoutManger;
    private FloatingActionButton addButton;

    private static final String KEY_OF_LINK = "KEY_OF_LINK";
    private static final String NUMBER_OF_LINKS = "NUMBER_OF_LINKS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linkcollector);
        init(savedInstanceState);
        back = findViewById(R.id.backbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LinkCollectorActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        addLinkButton = findViewById(R.id.addLinkButton);
        addLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // use dialog for add link
                showLinkDialog();
                int pos = 0;
                addLink(pos);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Toast.makeText(LinkCollectorActivity.this, "Delete link", Toast.LENGTH_SHORT).show();
                int position = viewHolder.getLayoutPosition();
                linkItemList.remove(position);
                linkRviewAdapter.notifyItemRemoved(position);
            }
        });

        itemTouchHelper.attachToRecyclerView(linkRecyclerView);
    }



    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        int size = linkItemList == null ? 0 : linkItemList.size();
        outState.putInt(NUMBER_OF_LINKS, size);

        // Need to generate unique key for each item
        // This is only a possible way to do, please find your own way to generate the key
        for (int i = 0; i < size; i++) {
            outState.putString(KEY_OF_LINK + i + "0", linkItemList.get(i).getLinkName());
            outState.putString(KEY_OF_LINK + i + "1", linkItemList.get(i).getLinkURL());
        }
        super.onSaveInstanceState(outState);
    }

    private void init(Bundle savedInstanceState) {
        initialItemData(savedInstanceState);
        createRecyclerView();
    }

    private void initialItemData(Bundle savedInstanceState) {
        // Not the first time to open this Activity
        if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_OF_LINKS)) {
            if (linkItemList == null || linkItemList.size() == 0) {

                int size = savedInstanceState.getInt(NUMBER_OF_LINKS);

                // Retrieve keys we stored in the instance
                for (int i = 0; i < size; i++) {
                    String linkName = savedInstanceState.getString(KEY_OF_LINK + i + "0");
                    String linkURL = savedInstanceState.getString(KEY_OF_LINK + i + "1");
                    LinkItemCard linkItemCard = new LinkItemCard(linkName, linkURL);
                    linkItemList.add(linkItemCard);
                }
            }
        }
    }

    private void createRecyclerView() {
        linkRLayoutManger = new LinearLayoutManager(this);
        linkRecyclerView = findViewById(R.id.link_collector_recycler_view);
        linkRecyclerView.setHasFixedSize(true);
        linkRviewAdapter = new LinkRviewAdapter(linkItemList);
        LinkItemClickListener linkClickListener = new LinkItemClickListener() {
            @Override
            public void onLinkItemClick(String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        };
        linkRviewAdapter.setOnLinkItemClickListener(linkClickListener);
        linkRecyclerView.setAdapter(linkRviewAdapter);
        linkRecyclerView.setLayoutManager(linkRLayoutManger);
    }


    // need to change this
    private void addLink(int position) {
        linkItemList.add(position, new LinkItemCard("Add Link Name", "Add Link URL"));
        Toast.makeText(LinkCollectorActivity.this, "Add a new link", Toast.LENGTH_SHORT).show();
        linkRviewAdapter.notifyItemInserted(position);
    }

}

