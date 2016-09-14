package com.alifanurani.topedsimplesearch.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alifanurani.topedsimplesearch.R;
import com.alifanurani.topedsimplesearch.SearchModel.Data;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by USER on 9/13/2016.
 */
public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    private List<Data> products;
    DisplayImageOptions options;

    private static final int TYPE_ITEM = 1;

    public ProductAdapter() {
        products = new ArrayList<Data>();

    }

    public ProductAdapter(List<Data> datas) {
        products = datas;
        options = new DisplayImageOptions.Builder()
                .delayBeforeLoading(0)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true) // add this line
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer())
                .build();
    }

    public ProductAdapter(Context context) {
        this.mContext = context;
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {

        @BindView(R.id.imgProduct) ImageView imageView;
        @BindView(R.id.nameProduct) TextView nameView;
        @BindView(R.id.priceProduct) TextView priceView;
        @BindView(R.id.productCard) View container;

        public ViewHolderItem(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    // 1
    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product, parent, false);


        switch (viewType) {
            case TYPE_ITEM: return new ViewHolderItem(view);
        }

        ViewHolderItem viewHolder = new ViewHolderItem(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ImageLoader imageLoader = ImageLoader.getInstance();

        switch (viewHolder.getItemViewType()) {

            case TYPE_ITEM:
                ViewHolderItem itemViewHolder = (ViewHolderItem) viewHolder;
                Data product = products.get(position);
                itemViewHolder.nameView.setText(product.getName());
                try {
                    imageLoader.displayImage(product.getImage_uri(), itemViewHolder.imageView);
                } catch (Exception e) {
                    Log.d("IMAGE LOADER", e.toString());
                }
                itemViewHolder. priceView.setText(product.getPrice());


                break;
        }

    }

    @Override
    public int getItemViewType(int position) {

        return TYPE_ITEM;
    }



}
