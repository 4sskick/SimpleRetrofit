package androdev;

import androdev.pojo.Model;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 28-Apr-16.
 */
public interface RetrofitInterface {

    @GET("weather?q=Malang,ID&appid=YOUR_API_KEY_HERE")
    Call<Model> getWeatherReport();

    @GET("weather")
    Call<Model> getWeatherReport(@Query("q") String location, @Query("appid") String apiKey);
}
