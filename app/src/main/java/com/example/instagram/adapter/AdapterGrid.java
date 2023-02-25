package com.example.instagram.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

public class AdapterGrid extends ArrayAdapter<String> {

    Context cont;
    int layoutRes;
    List<String> urlsFotos;

    public AdapterGrid(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.cont = context;
        this.layoutRes = resource;
        this.urlsFotos = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        MyViewHolder viewHolder;

        // caso a view nao esteja inflada precisamos inflar
        if( convertView == null ){
            viewHolder = new MyViewHolder();
            LayoutInflater layoutInflate = (LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflate.inflate( layoutRes, parent, false );

            viewHolder.progressBar = convertView.findViewById(R.id.progressBarGridPerfil);
            viewHolder.imageView = convertView.findViewById(R.id.imageViewGridPerfil);

            convertView.setTag( viewHolder );
        }else{
            viewHolder = (MyViewHolder) convertView.getTag();
        }

        // recupera dados da imagem
        String urlImagem = getItem( position );
        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage( urlImagem, viewHolder.imageView,
            new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                viewHolder.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                viewHolder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                viewHolder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                viewHolder.progressBar.setVisibility(View.GONE);
            }
        });

        return convertView;
    }

    public class MyViewHolder {
        ImageView imageView;
        ProgressBar progressBar;

    }

}
