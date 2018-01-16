package cp.wc.akshay.flickrgallery.model;

import android.net.Uri;
import android.util.Log;

/**
 * Created by akshay on 15/01/18.
 */

public class Photo {

    String  type;
    private String id;

    public Photo(String type) {
        this.type = type;
    }

    public String getId() { return this.id; }

    public void setId(String id) { this.id = id; }

    private String owner;

    public String getOwner() { return this.owner; }

    public void setOwner(String owner) { this.owner = owner; }

    private String secret;

    public String getSecret() { return this.secret; }

    public void setSecret(String secret) { this.secret = secret; }

    private String server;

    public String getServer() { return this.server; }

    public void setServer(String server) { this.server = server; }

    private int farm;

    public int getFarm() { return this.farm; }

    public void setFarm(int farm) { this.farm = farm; }

    private String title;

    public String getTitle() { return this.title; }

    public void setTitle(String title) { this.title = title; }

    private int ispublic;

    public int getIspublic() { return this.ispublic; }

    public void setIspublic(int ispublic) { this.ispublic = ispublic; }

    private int isfriend;

    public int getIsfriend() { return this.isfriend; }

    public void setIsfriend(int isfriend) { this.isfriend = isfriend; }

    private int isfamily;

    public int getIsfamily() { return this.isfamily; }

    public void setIsfamily(int isfamily) { this.isfamily = isfamily; }

    enum size {
        _s , _t ,_m
    };

    public String getPhotoPageUrl() {

        String FARMID = String.valueOf(farm);
        String SERVERID = server;
        String SECRET = secret;
        String ID = id;

        StringBuilder sb = new StringBuilder();

        sb.append("http://farm");
        sb.append(FARMID);
        sb.append(".static.flickr.com/");
        sb.append(SERVERID);
        sb.append("/");
        sb.append(ID);
        sb.append("_");
        sb.append(SECRET);
        sb.append("_c");
        sb.append(".jpg");

        Log.d("URL",""+sb);

//        return "https://www.flickr.com/photos/" + id + "/" + owner+"/"+farm;
        return  sb.toString();
    }
}
