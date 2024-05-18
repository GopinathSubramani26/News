package com.example.news.view

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.news.R
import com.example.news.Screen
import com.example.news.common.CommonText
import com.example.news.ui.theme.dimen16
import com.example.news.ui.theme.mediumTextStyleSize12
import com.example.news.ui.theme.mediumTextStyleSize16
import com.example.news.ui.theme.mediumTextStyleSize20
import com.example.news.ui.theme.mediumTextStyleSize28
import com.example.news.viewmodel.NewsState
import com.example.news.viewmodel.NewsViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsHomeScreen(
    navController: NavController,
    viewModel: NewsViewModel){

    LaunchedEffect(Unit){
        viewModel.fetchNews()
    }

    val newsArticle = viewModel.newsArticle.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(dimen16),
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {

        Row(modifier = Modifier.fillMaxWidth()) {
            CommonText(
                modifier = Modifier,
                text = "News",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.mediumTextStyleSize28
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        viewModel.sortList()
                    },
                painter = painterResource(id = R.drawable.sort_image),
                contentDescription = null)
        }

        Box(modifier = Modifier.fillMaxSize()) {

            LazyColumn(
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {

                items(newsArticle.value) {
                    NewsItem(
                        imageUrl = it?.urlToImage,
                        author = it?.author,
                        title = it?.title,
                        publishedAt = it?.publishedAt,
                        url =  it?.url?:""
                    ) { url ->
                        val encodeUrl = Uri.encode(url)
                        navController.navigate("${Screen.NewsArticleScreen.route}/$encodeUrl")
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(Alignment.CenterVertically)
                    ) {
                        if (viewModel.newsState.value == NewsState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally),
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsItem(
    imageUrl: String?,
    author: String?,
    title: String?,
    publishedAt: String?,
    url: String,
    onItemClick: (String) -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
            .clickable(onClick = { onItemClick(url) }),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ){
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.width(8.dp))
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(horizontal = 0.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .size(100.dp)
                )

            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                CommonText(
                    text = author ?: "",
                    style = MaterialTheme.typography.mediumTextStyleSize20,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                CommonText(
                    text = title ?: "",
                    style = MaterialTheme.typography.mediumTextStyleSize16,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(4.dp))
                CommonText(
                    text = convertToReadableTime(publishedAt ?: ""),
                    style = MaterialTheme.typography.mediumTextStyleSize12,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

fun convertToReadableTime(timestamp: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    return try {
        val date = inputFormat.parse(timestamp)
        outputFormat.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
    }.toString()
}

