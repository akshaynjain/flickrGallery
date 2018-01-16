package cp.wc.akshay.flickrgallery;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cp.wc.akshay.flickrgallery.model.Photo;

/**
 * Created by akshay on 15/01/18.
 */

public class ImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    OnLoadMoreListener loadMoreListener;
    boolean isLoading = false, isMoreDataAvailable = true;

    Context context;
    ArrayList<Photo> photo;
    ArrayList<Photo> photoFiltered;


    CustomFilter filter;

    public ImagesAdapter(Context context,ArrayList<Photo> photo) {
        this.context=context;
        this.photo=photo;
        this.photoFiltered=photo;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_items, parent, false);
            return new ViewHolderImages(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(position>=getItemCount()-1 && isMoreDataAvailable && !isLoading && loadMoreListener!=null){
            isLoading = true;
            loadMoreListener.onLoadMore();
        }

        if (holder instanceof ViewHolderImages) {
            Photo photo1=photo.get(position);
            ViewHolderImages userViewHolder = (ViewHolderImages) holder;
            userViewHolder.textView.setText(photo1.getTitle());
            Uri photoPageUri = Uri.parse(photo1.getPhotoPageUrl());
            Picasso.with(context).load(photoPageUri).into(userViewHolder.imageView);
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }


    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(photo.get(position).equals("load")){
            return VIEW_TYPE_LOADING;
        }else{
            return VIEW_TYPE_ITEM;
        }
    }

    private OnLoadMoreListener mOnLoadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public class ViewHolderImages extends RecyclerView.ViewHolder{

        TextView textView;
        ImageView imageView;
        public ViewHolderImages(View itemView) {
            super(itemView);
            textView=(TextView)itemView.findViewById(R.id.textView);
            imageView=(ImageView)itemView.findViewById(R.id.imageView);
        }
    }

    @Override
    public int getItemCount() {
        Log.d("Size",""+photo.size());
        return photo.size();
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    /* notifyDataSetChanged is final method so we can't override it
         call adapter.notifyDataChanged(); after update the list
         */
    public void notifyDataChanged(){
        notifyDataSetChanged();
        isLoading = false;
    }


    interface OnLoadMoreListener{
        void onLoadMore();
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

//    //RETURN FILTER OBJ
//    @Override
//    public Filter getFilter() {
//        if(filter==null)
//        {
//            filter=new CustomFilter(this,photoFiltered);
//        }
//
//        return filter;
//    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    photoFiltered = photo;
                } else {
                    ArrayList<Photo> filteredList = new ArrayList<>();
                    for (Photo row : photo) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    photoFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = photoFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                photo = (ArrayList<Photo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
