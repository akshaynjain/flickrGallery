package cp.wc.akshay.flickrgallery.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by akshay on 15/01/18.
 */

public class FlickrResponse {

    @SerializedName("photos")
    private Photos photos;

    public Photos getPhotos() { return this.photos; }

    public void setPhotos(Photos photos) { this.photos = photos; }

    @SerializedName("stat")
    private String stat;

    public String getStat() { return this.stat; }

    public void setStat(String stat) { this.stat = stat; }
}
