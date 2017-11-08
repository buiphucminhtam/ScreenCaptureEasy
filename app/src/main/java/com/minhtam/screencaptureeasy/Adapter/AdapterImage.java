package com.minhtam.screencaptureeasy.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.minhtam.screencaptureeasy.Interface.OnItemClickListener;
import com.minhtam.screencaptureeasy.R;

import java.io.File;
import java.util.List;

/**
 * Created by Tam on 11/8/2017.
 */

public class AdapterImage extends RecyclerView.Adapter<AdapterImage.ViewHolder> {
    private List<String> listPathImages;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public AdapterImage(Context context, List<String> listPathImages) {
        super();
        this.context = context;
        this.listPathImages = listPathImages;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public AdapterImage.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.image_item_layout,null));
    }

    @Override
    public void onBindViewHolder(AdapterImage.ViewHolder holder, int position) {
        holder.imvItem.setImageURI(Uri.fromFile(new File(listPathImages.get(position))));
    }

    @Override
    public int getItemCount() {
        return listPathImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imvItem;

        public ViewHolder(View itemView) {
            super(itemView);
            imvItem = itemView.findViewById(R.id.imvItemScreenshot);

            imvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener!=null) onItemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }


}
