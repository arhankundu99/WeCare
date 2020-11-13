package com.blackbrick.wecare.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blackbrick.wecare.R;
import com.blackbrick.wecare.classes.post;
import com.bumptech.glide.Glide;

import java.util.List;

// An adapter is the one which helps us to fill data in ui component
// An adapter is the one which will connect our data to our view (in this case recyclerview)
// We are using recycler adapter because we want to display scrolling items

// PostRecyclerAdapter is a custom Adapter
public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder> {

    public List<post> postsList;
    public Context context;

    //constructor
    public PostRecyclerAdapter(List<post> postsList){
        this.postsList = postsList;
    }
    //provide a reference to the views of each item
    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private TextView descView;
        private ImageView postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDescText(String descText){
            descView = mView.findViewById(R.id.post_desc);
            descView.setText(descText);
        }
        public void setPostImage(Uri postImageUri){
            postImage = mView.findViewById(R.id.post_image);
            Glide.with(context).load(postImageUri).fitCenter().into(postImage);
        }
    }

    // create viewholders for items
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }



    //binds the data to the views
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String desc = postsList.get(position).getDesc();
        String imageUrl = postsList.get(position).getImageUrl();
        Uri imageUri = Uri.parse(imageUrl);

        holder.setPostImage(imageUri);
        holder.setDescText(desc);
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }
}
