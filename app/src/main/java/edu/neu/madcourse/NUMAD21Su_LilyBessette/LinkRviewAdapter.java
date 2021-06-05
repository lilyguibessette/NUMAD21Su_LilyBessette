package edu.neu.madcourse.NUMAD21Su_LilyBessette;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LinkRviewAdapter extends RecyclerView.Adapter<LinkRviewHolder>{
    private final ArrayList<LinkItemCard> linkItemList;
    private LinkItemClickListener linkListener;

    public LinkRviewAdapter(ArrayList<LinkItemCard> linkItemList) {
        this.linkItemList = linkItemList;
    }

    public void setOnLinkItemClickListener(LinkItemClickListener linkListener) {
        this.linkListener = linkListener;
    }

    @Override
    public LinkRviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.link_item_card, parent, false);
        return new LinkRviewHolder(view, linkListener);
    }

    @Override
    public void onBindViewHolder(LinkRviewHolder holder, int position)  {
        LinkItemCard currentItem = linkItemList.get(position);
        holder.linkName.setText(currentItem.getLinkName());
        holder.linkURL.setText(currentItem.getLinkURL());
    }

    @Override
    public int getItemCount() {
        return linkItemList.size();
    }

}
