package com.ilimalbayrak.movieapp.v2.ui.movie_search;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ilimalbayrak.movieapp.v2.adapter.MovieAdapter;
import com.ilimalbayrak.movieapp.v2.data.movie_search.Result;
import com.ilimalbayrak.movieapp.v2.databinding.ActivityMovieSearchBinding;
import com.ilimalbayrak.movieapp.v2.ui.movie_detail.MovieDetailActivity;

import java.util.List;

import static com.ilimalbayrak.movieapp.v2.util.Constants.ARG_MOVIE_ID;

public class MovieSearchActivity extends AppCompatActivity {

    private ActivityMovieSearchBinding binding;
    private MovieSearchViewModel mViewModel;
    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mViewModel = ViewModelProviders.of(this).get(MovieSearchViewModel.class);

        showWelcomeMessage();
        initComponents();
        initClicks();
        initObservers();

    }

    private void showWelcomeMessage() {
        Toast.makeText(this, "Welcome to MyApp", Toast.LENGTH_SHORT).show();
    }

    private void initComponents() {

        binding.rvMovies.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMovies.setItemAnimator(new DefaultItemAnimator());
        movieAdapter = new MovieAdapter(this);
        binding.rvMovies.setAdapter(movieAdapter);
    }

    private void initClicks() {

        binding.btnSearch.setOnClickListener(v -> mViewModel.search(binding.etSearch.getText().toString()));
        movieAdapter.setOnClickListener((pos, movie) -> {
            Intent intent = new Intent(MovieSearchActivity.this, MovieDetailActivity.class);
            intent.putExtra(ARG_MOVIE_ID, movie.getId());
            startActivity(intent);
        });
    }

    private void initObservers() {
        mViewModel.getSearchList().observe(this, this::prepareRecycler);
        mViewModel.getSearchControl().observe(this, aBoolean -> {
            if (aBoolean)
                Toast.makeText(this, "You should enter at least one letter", Toast.LENGTH_SHORT).show();
        });
    }

    private void prepareRecycler(List<Result> models) {
        movieAdapter.setAdapterList(models);
    }
}