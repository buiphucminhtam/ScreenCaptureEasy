package ot.screenshot.capture.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import ot.screenshot.capture.Interface.OnItemClickListener;
import ot.screenshot.capture.R;
import ot.screenshot.capture.Util.ToastManager;

/**
 * Created by Tam on 11/8/2017.
 */

public class AdapterImage extends RecyclerView.Adapter<AdapterImage.ViewHolder> {
    private List<String> listPathImages;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private AlertDialog alertDialog;

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
        CardView cardView = (CardView) holder.itemView;
        cardView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT));
        Picasso.with(context).load(new File(listPathImages.get(position))).into(holder.imvItem);
    }

    @Override
    public int getItemCount() {
        return listPathImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imvItem, imvRemoveItem;

        public ViewHolder(View itemView) {
            super(itemView);
            imvItem = itemView.findViewById(R.id.imvItemScreenshot);
            imvRemoveItem = itemView.findViewById(R.id.imvRemoveItem);

            imvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener!=null) onItemClickListener.onItemClick(getAdapterPosition());
                }
            });

            imvRemoveItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initAlertDialog(getAdapterPosition());
                    alertDialog.show();
                }
            });
        }

        private void initAlertDialog(final int position) {
            alertDialog = new AlertDialog.Builder(context)
            .setTitle(R.string.alertDelete)
            .setPositiveButton(R.string.positiveConfirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    File file = new File(listPathImages.get(position));
                    if (file.delete()) {
                        ToastManager.getInstanse().showToast(context, context.getString(R.string.deleteSuccess), Toast.LENGTH_SHORT);
                        listPathImages.remove(position);
                        notifyItemRangeRemoved(position,listPathImages.size());
                        if (listPathImages.size() == 0) {
                            notifyDataSetChanged();
                        }
                    } else {
                        ToastManager.getInstanse().showToast(context, context.getString(R.string.deleteFailed), Toast.LENGTH_SHORT);
                    }
                }
            })
            .setNegativeButton(R.string.negativeConfirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            })
            .create();
        }
    }


}
