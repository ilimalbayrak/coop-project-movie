package com.ilimalbayrak.movieapp.v2.ui.movie_search;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ilimalbayrak.movieapp.v2.data.movie_search.Result;
import com.ilimalbayrak.movieapp.v2.data.movie_search.SearchModel;
import com.ilimalbayrak.movieapp.v2.service.ClientMovie;
import com.ilimalbayrak.movieapp.v2.service.IRequest;
import com.ilimalbayrak.movieapp.v2.util.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ilimalbayrak.movieapp.v2.util.Constants.CUSTOM_TAG;

public class MovieSearchViewModel extends ViewModel {

    private MutableLiveData<List<Result>> searchList = new MutableLiveData<>();
    private MutableLiveData<Boolean> searchControl = new MutableLiveData<>();


    public void search(String query) {
        if (!query.isEmpty()) {
            searchControl.postValue(false);
            searchMovies(query);
        } else
            searchControl.postValue(true);
    }


    private void searchMovies(String query) {
        IRequest request = ClientMovie.getApiClient().create(IRequest.class);
        Call<SearchModel> call = request.searchMovie(Constants.TEST_API_KEY, query);
        call.enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(Call<SearchModel> call, Response<SearchModel> response) {
                if (response.isSuccessful()) {
                    searchList.postValue(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<SearchModel> call, Throwable t) {
                Log.d(CUSTOM_TAG, "onFailure: ");
            }
        });
    }


    public MutableLiveData<List<Result>> getSearchList() {
        return searchList;
    }

    public MutableLiveData<Boolean> getSearchControl() {
        return searchControl;
    }
}