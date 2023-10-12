package com.example.weatherappcompose

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherappcompose.data.WeatherData
import com.example.weatherappcompose.screen.DialogSearch
import com.example.weatherappcompose.screen.MainCard
import com.example.weatherappcompose.screen.TabLayout
import com.example.weatherappcompose.ui.theme.BlueLight
import com.example.weatherappcompose.ui.theme.WeatherAppComposeTheme
import org.json.JSONObject

const val API_KEY = "8c3c09367f9d4fee9c9151439230210"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppComposeTheme {
                window.statusBarColor = getColor(R.color.blueDark)
                window.navigationBarColor = getColor(R.color.blueLight)
                val daysList = remember{
                    mutableStateOf(listOf<WeatherData>())
                }
                val dialogState = remember{
                    mutableStateOf(false)
                }
                val currentDay = remember{
                    mutableStateOf(WeatherData(
                        "",
                        "",
                        "0.0",
                        "",
                        "",
                        "0.0",
                        "0.0",
                        ""
                    ))
                }
                if (dialogState.value){
                    DialogSearch(dialogState, onSubmit = {
                        getData(it,this,daysList,currentDay)
                    })
                }
                getData("Nukus",this,daysList,currentDay)
                    Image(
                        painter = painterResource(id = R.drawable.weather_bg),
                        contentDescription = "Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds,

                    )
                Column {
                    MainCard(currentDay, onClickSync = {
                        getData("Nukus",this@MainActivity,daysList,currentDay)
                    }, onClickSearch = {
                        dialogState.value = true
                    })
                    TabLayout(daysList,currentDay)
                }
            }
        }
    }
}

private fun getData(city:String,
                    context:Context,
                    daysList : MutableState<List<WeatherData>>,
                    currentDay : MutableState<WeatherData>){
    val url = "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY"+
            "&q=$city" +
            "&days=3" +
            "&aqi=no&alerts=no"

    val queue = Volley.newRequestQueue(context)

    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        { response ->
            Log.d("LOGGETDATA", "response: $response")
            val list = getWeatherByDays(response)
            currentDay.value = list[0]
            daysList.value = list
        },
        {
            Log.d("LOGGETDATA", "getData: $it")
        }
    )
    queue.add(sRequest)
}

private fun getWeatherByDays(response:String):List<WeatherData>{
    if (response.isEmpty()) return listOf()
    val list = ArrayList<WeatherData>()
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")

    for (i in 0 until days.length()){
        val item = days[i] as JSONObject
        list.add(
            WeatherData(
                city,
                time = item.getString("date"),
                currentTemp = "",
                condition = item.getJSONObject("day").getJSONObject("condition").getString("text"),
                icon = item.getJSONObject("day").getJSONObject("condition").getString("icon"),
                maxTemp = item.getJSONObject("day").getString("maxtemp_c"),
                minTemp = item.getJSONObject("day").getString("mintemp_c"),
                hours = item.getJSONArray("hour").toString()
            )
        )
    }
    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObject.getJSONObject("current").getString("temp_c")
    )
    return list
}

