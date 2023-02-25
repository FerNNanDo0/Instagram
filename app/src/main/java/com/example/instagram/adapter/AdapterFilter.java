package com.example.instagram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

public class AdapterFilter extends RecyclerView.Adapter<AdapterFilter.MyViewHolder> {

    List<ThumbnailItem> listFilter;
    Context context;
    View view;

    public AdapterFilter( List<ThumbnailItem> listFilt , Context cont){
        listFilter = listFilt;
        context = cont;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_filtros, parent, false);

        return new AdapterFilter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ThumbnailItem item = listFilter.get(position);

        holder.imageViewfiltro.setImageBitmap( item.image );
        holder.textViewFiltro.setText( item.filterName );
    }

    @Override
    public int getItemCount() {
        return listFilter.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageViewfiltro;
        TextView textViewFiltro;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewfiltro = itemView.findViewById(R.id.imageViewfiltro);
            textViewFiltro = itemView.findViewById(R.id.textViewFiltro);

        }
    }
}
