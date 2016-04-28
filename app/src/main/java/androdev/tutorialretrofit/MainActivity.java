package androdev.tutorialretrofit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import androdev.RetrofitInterface;
import androdev.pojo.Model;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    String url = "http://api.openweathermap.org/data/2.5/";
    TextView cityTxt, statusTxt, humidityTxt, pressureTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityTxt = (TextView) findViewById(R.id.txtCity);
        statusTxt = (TextView) findViewById(R.id.txtStatus);
        humidityTxt = (TextView) findViewById(R.id.txtHumidity);
        pressureTxt = (TextView) findViewById(R.id.txtPress);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        Call<Model> modelCall = service.getWeatherReport("Malang,ID",
                "create your API key!");

        //execute call
        modelCall.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                try{
                    String city = response.body().getName();
                    String status = response.body().getWeather().get(0).getDescription();
                    String humidity = response.body().getMain().getHumidity().toString();
                    String pressure = response.body().getMain().getPressure().toString();

                    cityTxt.setText("City: "+city);
                    statusTxt.setText("Status: "+status);
                    humidityTxt.setText("Humidity: "+humidity);
                    pressureTxt.setText("Pressure: "+pressure);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {

            }
        });
    }
}
