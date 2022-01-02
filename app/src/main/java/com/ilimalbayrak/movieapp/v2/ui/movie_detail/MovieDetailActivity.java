package com.ilimalbayrak.movieapp.v2.ui.movie_detail;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.classification.MLImageClassification;
import com.huawei.hms.mlsdk.classification.MLImageClassificationAnalyzer;
import com.huawei.hms.mlsdk.classification.MLLocalClassificationAnalyzerSetting;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.ilimalbayrak.movieapp.v2.R;
import com.ilimalbayrak.movieapp.v2.data.movie_detail.Genre;
import com.ilimalbayrak.movieapp.v2.data.movie_detail.MovieDetailModel;
import com.ilimalbayrak.movieapp.v2.databinding.ActivityMovieDetailBinding;
import com.ilimalbayrak.movieapp.v2.util.Constants;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MovieDetailActivity extends AppCompatActivity {

    private ActivityMovieDetailBinding binding;
    private MovieDetailViewModel mViewModel;
    private Bitmap posterBitmap;

    private MLImageClassificationAnalyzer analyzer;
    private final String TAG = "ImageDetectionFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mViewModel = ViewModelProviders.of(this).get(MovieDetailViewModel.class);

        checkArguments();
        initObservers();
    }

    private void checkArguments() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int movieId = bundle.getInt(Constants.ARG_MOVIE_ID);
            mViewModel.getMovieDetail(movieId);
        } else finish();
    }

    private void initObservers() {
        mViewModel.getMovieDetail().observe(this, this::prepareComponents);
    }

    private void prepareComponents(MovieDetailModel movie) {

        if (!TextUtils.isEmpty(movie.getBackdropPath())) {
            String posterPath = Constants.BACKDROP_BASE_PATH + movie.getBackdropPath();


            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                try {
                    posterBitmap = Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(posterPath)
                            .placeholder(R.drawable.ic_baseline_broken_image_24)
                            .submit().get();


                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(() -> {

                    binding.ivBackdrop.setImageBitmap(posterBitmap);


                    Button imageClassificationButton = findViewById(R.id.image_classification_btn);
                    TextView detectedClassesTv = findViewById(R.id.detected_image_class_tv);


                    analyzer = createImageAnalyzer();


                    MLFrame frame = MLFrame.fromBitmap(posterBitmap);
                    imageClassificationButton.setOnClickListener(view1 -> {
                        Task<List<MLImageClassification>> task = analyzer.asyncAnalyseFrame(frame);
                        task.addOnSuccessListener(classifications -> {

                            StringBuilder sb = new StringBuilder();
                            sb.append("Results: \n\n");

                            for (int i = 0; i < classifications.size(); i++) {
                                sb.append("[")
                                        .append(i)
                                        .append("] ")
                                        .append(classifications.get(i).getName())
                                        .append("\n");

                            }
                            if (classifications.size() > 0) {
                                detectedClassesTv.setText(sb.toString());
                            } else {
                                detectedClassesTv.setText("Results: \n\n" + "[0] Others");
                            }

                            releaseImageDetectionResources();

                        }).addOnFailureListener(e -> {

                            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();;
                        });
                    });
                });
            });
        }

        if (movie.getGenres() != null & movie.getGenres().size() > 0) {
            String genres = "";
            for (Genre genre : movie.getGenres()) {
                if (genres.equals(""))
                    genres = genre.getName();
                else genres += ", " + genre.getName();
            }
            binding.tvCategory.setText(genres);
        }

        if (!TextUtils.isEmpty(movie.getOverview()))
            binding.tvOverview.setText(movie.getOverview());

        if (!TextUtils.isEmpty(movie.getReleaseDate()))
            binding.tvDate.setText(movie.getReleaseDate());

        if (movie.getVoteAverage() != 0)
            binding.tvScore.setText(movie.getVoteAverage().toString());

        if (TextUtils.isEmpty(movie.getBackdropPath()))
            Toast.makeText(this, "Image could not be found", Toast.LENGTH_SHORT).show();
    }

    private MLImageClassificationAnalyzer createImageAnalyzer() {
        MLLocalClassificationAnalyzerSetting setting =
                new MLLocalClassificationAnalyzerSetting.Factory()
                        .setMinAcceptablePossibility(0.8f)
                        .create();
        return MLAnalyzerFactory.getInstance().getLocalImageClassificationAnalyzer(setting);
    }

    private void releaseImageDetectionResources() {
        try {
            if (analyzer != null) {
                analyzer.stop();
            }
        } catch (IOException e) {

            Log.e(TAG, e.getLocalizedMessage());
        }
    }
}