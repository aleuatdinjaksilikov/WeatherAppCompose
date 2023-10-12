package com.example.weatherappcompose.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherappcompose.R
import com.example.weatherappcompose.data.WeatherData
import com.example.weatherappcompose.ui.theme.BlueLight
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun MainCard(currentDay: MutableState<WeatherData>,onClickSync:()->Unit,onClickSearch:()->Unit) {
    Column(
        modifier = Modifier
            .padding(5.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = BlueLight
            ),
            elevation = CardDefaults.cardElevation(
                0.dp
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(
                            top = 8.dp,
                            start = 8.dp
                        ),
                        text = currentDay.value.time,
                        style = TextStyle(fontSize = 15.sp),
                        color = Color.White
                    )
                    AsyncImage(
                        model = "https:" + currentDay.value.icon,
                        contentDescription = "Image",
                        modifier = Modifier
                            .padding(
                                top = 3.dp,
                                end = 8.dp
                            )
                            .size(35.dp)
                    )
                }

                Text(
                    text = currentDay.value.city,
                    style = TextStyle(fontSize = 24.sp),
                    color = Color.White
                )
                Text(
                    text = if (currentDay.value.currentTemp.isNotEmpty())
                        currentDay.value.currentTemp.toFloat().toInt().toString() + "°C"
                    else currentDay.value.maxTemp.toFloat().toInt()
                        .toString() + "°C/${currentDay.value.minTemp.toFloat().toInt()}°C",
                    style = TextStyle (fontSize = 64.sp),
                    color = Color.White
                )
                Text(
                    text = currentDay.value.condition,
                    style = TextStyle(fontSize = 16.sp),
                    color = Color.White
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            onClickSearch.invoke()
                        }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "Img 3",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "${
                            currentDay.value.maxTemp.toFloat().toInt()
                        }°C/${currentDay.value.minTemp.toFloat().toInt()}°C",
                        style = TextStyle(fontSize = 16.sp),
                        color = Color.White
                    )

                    IconButton(
                        onClick = {
                            onClickSync.invoke()
                        }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sync),
                            contentDescription = "Img 4",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TabLayout(daysList: MutableState<List<WeatherData>>, currentDay: MutableState<WeatherData>) {
    val tabList = listOf("HOURS", "DAYS")

    val tabIndex = remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .clip(RoundedCornerShape(5.dp))
    ) {
        TabRow(
            selectedTabIndex = tabIndex.value,
            containerColor = BlueLight,
            contentColor = Color.White
        ) {
            tabList.forEachIndexed { index, text ->
                Tab(
                    selected = tabIndex.value == index,
                    onClick = { tabIndex.value = index },
                    text = {
                        Text(text = text)
                    }
                )
            }
        }
        when (tabIndex.value) {
            0 -> Hours(getWeatherByHours(currentDay.value.hours), currentDay)
            1 -> Days(daysList, currentDay)
        }
    }
}

@Composable
fun Days(daysList: MutableState<List<WeatherData>>, currentDay: MutableState<WeatherData>) {

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(
            daysList.value
        ) { _, item ->
            ListItem(item = item, currentDay)
        }
    }
}

@Composable
fun Hours(list: List<WeatherData>, currentDay: MutableState<WeatherData>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(
            list
        ) { _, item ->
            ListItem(item = item, currentDay)
        }
    }
}

private fun getWeatherByHours(hours: String): List<WeatherData> {
    if (hours.isEmpty()) return listOf()
    val hoursArray = JSONArray(hours)
    var list = ArrayList<WeatherData>()
    for (i in 0 until hoursArray.length()) {
        val item = hoursArray[i] as JSONObject
        list.add(
            WeatherData(
                "",
                item.getString("time"),
                item.getString("temp_c").toFloat().toInt().toString(),
                item.getJSONObject("condition").getString("text"),
                item.getJSONObject("condition").getString("icon"),
                "",
                "",
                ""
            )
        )
    }
    return list
}
