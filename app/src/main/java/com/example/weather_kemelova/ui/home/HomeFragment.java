package com.example.weather_kemelova.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.example.weather_kemelova.R;
import com.example.weather_kemelova.databinding.FragmentHomeBinding;
import com.example.weather_kemelova.models.Clouds;
import com.example.weather_kemelova.models.Main;
import com.example.weather_kemelova.models.Model;
import com.example.weather_kemelova.models.Sys;
import com.example.weather_kemelova.models.Wind;
import com.example.weather_kemelova.remote_data.RetrofitBuilder;
import com.example.weather_kemelova.remote_data.WeatherApi;

import java.math.BigInteger;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    Integer temperature;
    Integer tempMaximal;
    Integer tempMinimal;
    int humadity_c;


    String currentTime = java.text.DateFormat.getDateTimeInstance().format(new Date());
    final String apiKey = WeatherApi.URL_KEY;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.rainLotty.setAnimation(R.raw.rain1);
        binding.snowLotty.setAnimation(R.raw.snow_lotty);
        binding.localtime.setText(currentTime);

        Call<Model> call = RetrofitBuilder.getInstance().getCurrentWeather("Bishkek", apiKey);

        call.enqueue(new Callback<Model>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Main main_model = response.body().getMain_model();
                    Wind wind_model = response.body().getWind_model();
                    Clouds clouds_model = response.body().getClouds_model();
                    Sys sys_model = response.body().getSys_model();

                    Double temp = main_model.getTemp();
                    Double tempMax = main_model.getTempMax();
                    Double tempMin = main_model.getTempMin();

                    temperature = makeFromFaringate(temp);
                    tempMaximal = makeFromFaringate(tempMax);
                    tempMinimal = makeFromFaringate(tempMin);

                    binding.tempMain.setText((temperature) + " °C");

                    if (temperature <= 14) {
                        binding.sun1.setVisibility(View.INVISIBLE);
                        setNoHotWeather();
                    }

                    binding.maxMinTemp.setText(String.valueOf(tempMaximal) + " °C↑ \n"
                            + String.valueOf(tempMinimal) + " °C↓");

                    binding.cityName.setText("Bishkek");
                    binding.humidity.setText(main_model.getHumidity() + " %");
                    humadity_c = main_model.getHumidity();
                    if (humadity_c >= 55) {
                        rainy_possible();
                    }
//                    -------------------
                    if (humadity_c >= 95) {
                        snow_possible();
                    }
//                    -------------------

                    binding.pressure.setText(main_model.getPressure() + "\nmBar");
                    binding.wind.setText(wind_model.getSpeed() + " m/s");
                    binding.cloudy.setText(clouds_model.getAll() + "%");

                    binding.sunrise.setText(String.valueOf(getCurrDateTime(sys_model.getSunrise())));
                    binding.sunset.setText(String.valueOf(getCurrDateTime(sys_model.getSunset())));

                    binding.timeZone.setText(String.valueOf(response.body().getTimezone()));
                }

                setCondition();

                if (response.body().getTimezone() <= 6500 && response.body().getTimezone() >= -27500) {
                    setNight();
                } else {
                    setDay();
                }

            }

            @Override
            public void onFailure(Call<Model> call, Throwable throwable) {
                Toast.makeText(requireActivity(), "No weatherForecast data", Toast.LENGTH_SHORT).show();
                Log.e("TAG", throwable.getLocalizedMessage());

            }
        });
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.slideUpBottomSheet.setOnClickListener(v -> {

            if (binding.bottomSheet.getVisibility() == View.GONE) {
                binding.bottomSheet.setVisibility(View.VISIBLE);
            } else {
                binding.bottomSheet.setVisibility(View.GONE);
            }

            binding.rainLotty.setVisibility(View.INVISIBLE);
            binding.blueSky.setVisibility(View.VISIBLE);
            binding.badWeatherSky.setVisibility(View.INVISIBLE);
            binding.inputCity.setText("");
            binding.condition.setText("...");
            binding.isRainOrNot.setText("...");
            binding.sun1.setVisibility(View.INVISIBLE);
        });

        binding.search.setOnClickListener(v1 -> {
            if (!binding.inputCity.getText().toString().isEmpty()) {

                Call<Model> call = RetrofitBuilder.getInstance()
                        .getCurrentWeather(binding.inputCity.getText().toString(), apiKey);

                call.enqueue(new Callback<Model>() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<Model> call, @NonNull Response<Model> response) {

                        Main main_model = response.body().getMain_model();
                        Wind wind_model = response.body().getWind_model();
                        Clouds clouds_model = response.body().getClouds_model();
                        Sys sys_model = response.body().getSys_model();

                        Double temp = main_model.getTemp();
                        Double tempMax = main_model.getTempMax();
                        Double tempMin = main_model.getTempMin();

                        temperature = makeFromFaringate(temp);
                        tempMaximal = makeFromFaringate(tempMax);
                        tempMinimal = makeFromFaringate(tempMin);

                        binding.tempMain.setText((temperature) + " °C");
                        binding.maxMinTemp.setText((tempMaximal) + " °C↑ \n"
                                + (tempMinimal) + " °C↓");
                        binding.cityName.setText(binding.inputCity.getText().toString());
                        binding.humidity.setText(main_model.getHumidity() + " %");
                        binding.pressure.setText(main_model.getPressure() + "\nmBar");
                        binding.wind.setText(wind_model.getSpeed() + " m/s");
                        binding.cloudy.setText(clouds_model.getAll() + "%");
                        binding.sunrise.setText(String.valueOf(getCurrDateTime(sys_model.getSunrise())));
                        binding.sunset.setText(String.valueOf(getCurrDateTime(sys_model.getSunset())));
                        binding.timeZone.setText(String.valueOf(response.body().getTimezone()));

                        setCondition();

                        if (response.body().getTimezone() <= 6500 && response.body().getTimezone() >= -27500) {
                            setNight();
                        } else {
                            setDay();
                        }

                    }

                    @Override
                    public void onFailure(Call<Model> call, @NonNull Throwable throwable) {
                        Toast.makeText(requireActivity(), "No WeatherForecast data"
                                + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("TAG", throwable.getLocalizedMessage());
                    }
                });
                binding.bottomSheet.setVisibility(View.GONE);
            } else {
                Toast.makeText(requireActivity(), "Input name of city", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void setDay() {
        binding.nightSky.setVisibility(View.INVISIBLE);
        binding.blueSky.setVisibility(View.VISIBLE);
    }

    public void setNight() {
        binding.sun1.setVisibility(View.INVISIBLE);
        binding.nightSky.setVisibility(View.VISIBLE);
        binding.blueSky.setVisibility(View.INVISIBLE);
    }

    private int makeFromFaringate(Double tt) {
        Integer gr = (int) (tt - 273.15);
        return gr;

    }

    public void setCondition() {
        if (temperature > 20 && temperature <= 50) {
            binding.blueSky.setVisibility(View.VISIBLE);
            binding.condition.setText("DAY: \n hotter");
            dryWeather();
        }
        if (temperature <= 20 && temperature > 14) {
            binding.blueSky.setVisibility(View.VISIBLE);
            binding.condition.setText("DAY: \nlight \nsunny");
            dryWeather();
        } else {
            if (temperature >= 10 && temperature < 14) {
                setNoHotWeather();
                binding.condition.setText("DAY: \ncold");
                rainy_monitoring();
            } else {
                if (temperature < 9) {
                    setNoHotWeather();
                    binding.condition.setText("DAY: \nvery \ncold");
                }
//                -------------------------------------------
                if (temperature < 0){
                    snow_monitoring();
                    binding.condition.setText("DAY: \nvery very \ncold");
                }
//                -------------------------------------------

            }
        }
    }

    public void rainy_monitoring() {
        if (humadity_c <= 55) {
            binding.rainLotty.setVisibility(View.INVISIBLE);
            binding.isRainOrNot.setText("");
            dryWeather();
        } else {
            rainy_possible();
        }
    }


//    ---------------------------------------------------------
    public void snow_monitoring() {
        if (temperature <= -5 ) {
            binding.snowLotty.setVisibility(View.VISIBLE);
            binding.isRainOrNot.setText("");
            binding.isRainOrNot.setVisibility(View.INVISIBLE);
            binding.rainLotty.setVisibility(View.INVISIBLE);
            snow_possible();
        } else {
            snow_possible();
        }
    }
//    ---------------------------------------------------------

    public void setNoHotWeather() {
        binding.sun1.setVisibility(View.INVISIBLE);
        binding.blueSky.setVisibility(View.INVISIBLE);
        binding.badWeatherSky.setVisibility(View.VISIBLE);
    }

    public String getCurrDateTime(long n) {
        String new_n = java.text.DateFormat.getDateTimeInstance().format(new Date(n));
        return new_n;
    }

    public void rainy_possible() {
        binding.isRainOrNot.setVisibility(View.VISIBLE);
        binding.isRainOrNot.setText("rain is \n possible");
        binding.rainLotty.setVisibility(View.INVISIBLE);
        binding.sun1.setVisibility(View.INVISIBLE);
        binding.badWeatherSky.setVisibility(View.VISIBLE);
    }
//--------------------------------------------------
    private void snow_possible() {
        binding.isRainOrNot.setVisibility(View.VISIBLE);
        binding.snowLotty.setVisibility(View.VISIBLE);
        binding.isRainOrNot.setText("snow is \npossible ");
        binding.rainLotty.setVisibility(View.INVISIBLE);
        binding.badWeatherSky.setVisibility(View.VISIBLE);
    }
//--------------------------------------------------

    public void dryWeather() {
        binding.rainLotty.setVisibility(View.INVISIBLE);
        binding.badWeatherSky.setVisibility(View.INVISIBLE);
        binding.blueSky.setVisibility(View.VISIBLE);
        binding.sun1.setVisibility(View.VISIBLE);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}