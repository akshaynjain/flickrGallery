package cp.wc.akshay.flickrgallery.retrofitService;

import java.util.LinkedHashMap;

import cp.wc.akshay.flickrgallery.model.FlickrResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by akshay on 15/01/18.
 */

public interface FlickrService {

    @GET("services/rest/")
    Call<FlickrResponse> getImages(@QueryMap LinkedHashMap<String, String> params);

}
