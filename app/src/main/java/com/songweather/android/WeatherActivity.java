package com.songweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.songweather.android.gson.Forecast;
import com.songweather.android.gson.Utility;
import com.songweather.android.gson.Weather;
import com.songweather.android.util.HttpUtill;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView aqiText;
    private  TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView pm25Text;
    private TextView comforText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT>=21){
            View decorView =getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        weatherLayout=(ScrollView)findViewById(R.id.weather_layout);
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView)findViewById(R.id.title_update_time);
        degreeText=(TextView)findViewById(R.id.degree_text);
        weatherInfoText =(TextView)findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        aqiText=(TextView)findViewById(R.id.aqi_text);
        pm25Text= (TextView)findViewById(R.id.pm25_text);
        comforText=(TextView)findViewById(R.id.comfort_text);
        carWashText=(TextView)findViewById(R.id.car_wash_text);
        sportText=(TextView)findViewById(R.id.sport_text);
        bingPicImg= (ImageView)findViewById(R.id.bing_pic_img);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        String bingPic=prefs.getString("bing_pic",null);
        if (weatherString !=null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);

        }else {
            String weatherID= getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherID);

        }
        if (bingPic !=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }
        else {
            loadBingPic();
        }

    }
    private  void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtill.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            final String bingPic=response.body().string();
            SharedPreferences.Editor editor =PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
            editor.putString("bing_pic",bingPic);
            editor.apply();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                }
            });
            }
        });
    }
    public  void requestWeather(final String weatherId){
        String wetherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=58085f47e8e14d268a8f18c1a8419f06";
        HttpUtill.sendOkHttpRequest(wetherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
               e.printStackTrace();
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_LONG).show();
                   }
               });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText= response.body().string();
                final  Weather weather=Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }
                        else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }

    private  void showWeatherInfo(Weather weather){
        String cityName =weather.basic.cityName;
        String updateTime=weather.basic.update.updateTime;//.split("")[1];
        String degree =weather.now.temperature+"*C";
        String weatherInfo =weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();

        Toast.makeText(WeatherActivity.this,"weather.forecastList is"+weather.forecastList.size(),Toast.LENGTH_LONG).show();
        int i=0;
        for (Forecast forecast: weather.forecastList){

            Toast.makeText(WeatherActivity.this,"view "+i,Toast.LENGTH_LONG).show();
            i++;
            View view=LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText =(TextView)view.findViewById(R.id.date_text);
            TextView infoText=(TextView)view.findViewById(R.id.info_text);
            TextView maxText=(TextView)view.findViewById(R.id.max_text);
            TextView minText=(TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);

        }
        if (weather.aqi!= null){
            Toast.makeText(WeatherActivity.this,"AQI: "+weather.aqi.city.aqi,Toast.LENGTH_LONG).show();
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort="舒适度："+weather.suggestion.comfort.info;
        String carWash="洗车指数："+weather.suggestion.carWash.info;
        String sport="运动建议："+weather.suggestion.sport.info;
        comforText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);

    }
   public boolean onKeyDown(int keyCode,KeyEvent event){
    if (keyCode==KeyEvent.KEYCODE_BACK)  {
        Intent myInent= new Intent(WeatherActivity.this,MainActivity.class);
        startActivity(myInent);
        this.finish();
    }
    return super.onKeyDown(keyCode,event);
   }
}