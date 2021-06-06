package edu.neu.madcourse.NUMAD21Su_LilyBessette;

import android.content.Intent;
import android.net.Uri;

public class LinkItemCard {
    private final String linkName;
    private final String linkURL;

    //Constructor for each link item
    public LinkItemCard(String linkName, String linkURL) {
        this.linkName = linkName;
        this.linkURL = linkURL;
    }

    public String getLinkName() {
        return linkName;
    }

    public String getLinkURL() {
        return linkURL;
    }
}
