package com.ilimalbayrak.movieapp.v2.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ilimalbayrak.movieapp.v2.R;
import com.ilimalbayrak.movieapp.v2.data.movie_search.Result;
import com.ilimalbayrak.movieapp.v2.databinding.ItemMovieBinding;
import com.ilimalbayrak.movieapp.v2.util.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<com.ilimalbayrak.movieapp.v2.adapter.MovieAdapter.MovieViewHolders> {

    private List<Result> movies;
    private Context context;
    private ItemClickListener itemClickListener;

    public MovieAdapter(Context context) {
        this.context = context;
        this.movies = new ArrayList<>();
    }

    public void setAdapterList(List<Result> movies) {
        this.movies.clear();
        this.movies.addAll(movies);
        this.notifyDataSetChanged();
    }

    @NotNull
    @Override
    public MovieViewHolders onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        ItemMovieBinding binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MovieViewHolders(binding);
    }

    @Override
    public void onBindViewHolder(MovieViewHolders holder, int position) {
        Result movie = movies.get(position);
        if (!TextUtils.isEmpty(movie.getPosterPath())) {
            String posterPath = Constants.POSTER_BASE_PATH + movie.getPosterPath();
            Glide.with(context)
                    .load(posterPath)
                    .placeholder(R.drawable.ic_baseline_broken_image_24)
                    .into(holder.binding.ivPoster);
        }
        if (!TextUtils.isEmpty(movie.getOriginalTitle()))
            holder.binding.tvTitle.setText(movie.getOriginalTitle());
        if (!TextUtils.isEmpty(movie.getPopularity().toString())) {
            String popularity = "Popularity: " + movie.getPopularity().toString();
            holder.binding.tvPopularity.setText(popularity);
        }
    }

    private Result getItem(int pos) {
        return movies.get(pos);
    }

    @Override
    public int getItemCount() {
        return this.movies.size();
    }

    public void setOnClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onClick(int pos, Result movie);
    }


    public class MovieViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemMovieBinding binding;

        public MovieViewHolders(ItemMovieBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(getAdapterPosition(), getItem(getAdapterPosition()));
        }
    }
}