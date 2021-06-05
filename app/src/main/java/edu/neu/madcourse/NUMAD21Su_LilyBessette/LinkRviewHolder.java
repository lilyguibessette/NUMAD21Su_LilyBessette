package edu.neu.madcourse.NUMAD21Su_LilyBessette;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class LinkRviewHolder extends RecyclerView.ViewHolder {
    public TextView linkName;
    public TextView linkURL;

    public LinkRviewHolder(View linkItemView, LinkItemClickListener linkListener) {
        super(linkItemView);
        linkName = linkItemView.findViewById(R.id.link_name);
        linkURL = linkItemView.findViewById(R.id.link_url);

        linkItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linkListener != null) {
                    int position = getLayoutPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        linkListener.onLinkItemClick(linkURL.getText().toString());
                    }
                }
            }
        });
    }


}
