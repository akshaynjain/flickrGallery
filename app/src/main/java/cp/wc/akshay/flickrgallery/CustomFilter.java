package cp.wc.akshay.flickrgallery;

import android.widget.Filter;

import java.util.ArrayList;

import cp.wc.akshay.flickrgallery.model.Photo;

/**
 * Created by akshay on 16/01/18.
 */

public class CustomFilter extends Filter {

    ImagesAdapter adapter;
    ArrayList<Photo> photos;

    public CustomFilter(ImagesAdapter adapter, ArrayList<Photo> photos) {
        this.adapter = adapter;
        this.photos = photos;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
     //CHECK CONSTRAINT VALIDITY
        if(constraint != null && constraint.length() > 0)
        {
            //CHANGE TO UPPER
            constraint=constraint.toString().toUpperCase();
            //STORE OUR FILTERED PLAYERS
            ArrayList<Photo> filteredPlayers=new ArrayList<>();

            for (int i=0;i<photos.size();i++)
            {
                //CHECK
                if(photos.get(i).getTitle().toUpperCase().contains(constraint.toString().toUpperCase()))
                {
                    //ADD PLAYER TO FILTERED PLAYERS
                    filteredPlayers.add(photos.get(i));
                }
            }

            results.count=filteredPlayers.size();
            results.values=filteredPlayers;
        }else
        {
            results.count=photos.size();
            results.values=photos;

        }


        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.photo= (ArrayList<Photo>) results.values;
        //REFRESH
        adapter.notifyDataSetChanged();
    }
}
