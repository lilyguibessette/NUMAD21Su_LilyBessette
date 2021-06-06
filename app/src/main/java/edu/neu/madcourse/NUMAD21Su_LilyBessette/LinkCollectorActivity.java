package edu.neu.madcourse.NUMAD21Su_LilyBessette;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.net.URL;
import java.util.ArrayList;


public class LinkCollectorActivity extends AppCompatActivity implements LinkCollectorDialogFragment.LinkCollectorDialogListener {
    Button back;
    private final ArrayList<LinkItemCard> linkList = new ArrayList<>();
    private RecyclerView linkRecyclerView;
    private LinkRviewAdapter linkRviewAdapter;
    private RecyclerView.LayoutManager linkRLayoutManger;
    private FloatingActionButton addLinkButton;

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
                startLinkCollectorDialog();
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
                linkList.remove(position);
                linkRviewAdapter.notifyItemRemoved(position);
            }
        });

        itemTouchHelper.attachToRecyclerView(linkRecyclerView);
    }

        public void startLinkCollectorDialog() {
            DialogFragment linkDialog = new LinkCollectorDialogFragment();
            linkDialog.show(getSupportFragmentManager(), "LinkDialogFragment");
        }

    public void onDialogPositiveClick(DialogFragment linkDialog) {
            Dialog addLinkDialog = linkDialog.getDialog();

            String linkName = ((EditText) addLinkDialog.findViewById(R.id.link_name)).getText().toString();
            String linkURL = ((EditText) addLinkDialog.findViewById(R.id.link_url)).getText().toString();

            if (isValidLink(linkURL)) {
                linkDialog.dismiss();
                linkList.add(0, new LinkItemCard(linkName, linkURL));
                linkRviewAdapter.notifyItemInserted(0);
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, R.string.link_add_confirm, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            } else {
                Toast.makeText(LinkCollectorActivity.this, R.string.link_add_error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onDialogNegativeClick(DialogFragment linkDialog) {
            linkDialog.dismiss();
        }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        int size = linkList == null ? 0 : linkList.size();
        outState.putInt(NUMBER_OF_LINKS, size);
        for (int i = 0; i < size; i++) {
            outState.putString(KEY_OF_LINK + i + "0", linkList.get(i).getLinkName());
            outState.putString(KEY_OF_LINK + i + "1", linkList.get(i).getLinkURL());
        }
        super.onSaveInstanceState(outState);
    }

    private void init(Bundle savedInstanceState) {
        initialItemData(savedInstanceState);
        createRecyclerView();
    }

    private void initialItemData(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_OF_LINKS)) {
            if (linkList == null || linkList.size() == 0) {

                int size = savedInstanceState.getInt(NUMBER_OF_LINKS);

                // Retrieve keys we stored in the instance
                for (int i = 0; i < size; i++) {
                    String linkName = savedInstanceState.getString(KEY_OF_LINK + i + "0");
                    String linkURL = savedInstanceState.getString(KEY_OF_LINK + i + "1");
                    LinkItemCard linkItemCard = new LinkItemCard(linkName, linkURL);
                    linkList.add(linkItemCard);
                }
            }
        }
    }

    private void createRecyclerView() {
        linkRLayoutManger = new LinearLayoutManager(this);
        linkRecyclerView = findViewById(R.id.link_collector_recycler_view);
        linkRecyclerView.setHasFixedSize(true);
        linkRviewAdapter = new LinkRviewAdapter(linkList);
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

    public static boolean isValidLink(String url)
    {
        try {
            new URL(url).toURI();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

}

