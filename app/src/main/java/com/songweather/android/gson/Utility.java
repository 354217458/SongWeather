package com.songweather.android.gson;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.songweather.android.db.City;
import com.songweather.android.db.County;
import com.songweather.android.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/5/11.
 */

public class Utility {
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvince = new JSONArray(response);
                for (int i =0 ; i< allProvince.length();i++){
                    JSONObject provinceObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return  true;
            }
            catch (JSONException e){
                e.printStackTrace();
            }

        }
        return  false;
    }

    public  static boolean handleCityResponse(String response, int provinceId){
        if (!TextUtils.isEmpty((response))){
            try{
                JSONArray allCities = new JSONArray(response);
                for (int i =0 ;i<allCities.length();i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }catch ( JSONException E){
                E.printStackTrace();
            }
        }
        return  false;
    }


    public  static  boolean handleCountyResonse(String reponse, int cityId){
        if (!TextUtils.isEmpty(reponse)){
            try{
                JSONArray allCounties = new JSONArray(reponse);
                for (int i =0 ; i< allCounties.length(); i++){
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return  true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return  false;
    }

    public static Weather handleWeatherResponse(String response){
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return  new Gson().fromJson(weatherContent,Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }



}
