package androdev.tutorialretrofit;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androdev.RetrofitInterface;
import androdev.pojo.Model;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//http://www.androidwarriors.com/2015/12/retrofit-20-android-example-web.html
public class MainActivity extends AppCompatActivity {

    String url = "http://api.openweathermap.org/data/2.5/";
    TextView cityTxt, statusTxt, humidityTxt, pressureTxt;
    LocationManager locationManager;
    LocationListener locationListener;
    boolean flag = false;
    final String TAG = "DEBUG";
    String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityTxt = (TextView) findViewById(R.id.txtCity);
        statusTxt = (TextView) findViewById(R.id.txtStatus);
        humidityTxt = (TextView) findViewById(R.id.txtHumidity);
        pressureTxt = (TextView) findViewById(R.id.txtPress);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        flag = displaySttatusGPS();
        if (flag) {
            Log.d(TAG, "onCreate: " + flag);
            locationListener = new MainLocationListener();
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    10,
                    locationListener
            );
        } else {
            alertBox("GPS Status", "Your GPS: OFF");
        }
    }

    private void alertBox(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setTitle(title)
                .setPositiveButton(
                        "GPS ON",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /* this gonna call class of settings then dialog interface disappeared */
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                                dialog.cancel();
                            }
                        }
                );
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*----Method to Check GPS is enable or disable ----- */
    private boolean displaySttatusGPS() {
        boolean status = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (status) {
            return true;
        } else {
            return false;
        }
    }

    /* inner class that implement listener for location */
    private class MainLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            String longitude = "Longitude: " + location.getLongitude();
            Log.d(TAG, "onLocationChanged: " + longitude);

            String latitude = "Latitude: " + location.getLatitude();
            Log.d(TAG, "onLocationChanged: " + latitude);

            /* get city name from coordinates */
//            String cityName;
            Geocoder geocoder = new Geocoder(
                    getBaseContext(),
                    Locale.getDefault()
            );
            List<Address> addressList;
            // getFromLocation (double latitude, double longitude, int maxResults)
            try {
                addressList = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        1
                );
                if (addressList.size() > 0) {
                    cityName = addressList.get(0).getLocality();
                    Log.d(TAG, "onLocationChanged: " + cityName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //builld URL
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            //create service from interface
            RetrofitInterface service = retrofit.create(RetrofitInterface.class);
            //call method from interface to implement its method
            Call<Model> modelCall = service.getWeatherReport(cityName,
                    "YOUR API KEY HERE");

            //execute call service
            modelCall.enqueue(new Callback<Model>() {
                @Override
                public void onResponse(Call<Model> call, Response<Model> response) {
                    //add some try catch to avoide force close
                    try {
                        // get value from response.body then find mutator
                        String city = response.body().getName();
                        String status = response.body().getWeather().get(0).getDescription();
                        String humidity = response.body().getMain().getHumidity().toString();
                        String pressure = response.body().getMain().getPressure().toString();

                        cityTxt.setText("City: " + city);
                        statusTxt.setText("Status: " + status);
                        humidityTxt.setText("Humidity: " + humidity);
                        pressureTxt.setText("Pressure: " + pressure);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<Model> call, Throwable t) {

                    //// TODO: 28-Apr-16
                }
            });
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
