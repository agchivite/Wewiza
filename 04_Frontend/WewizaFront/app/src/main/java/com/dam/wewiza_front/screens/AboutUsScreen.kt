package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.dam.wewiza_front.R
import com.dam.wewiza_front.viewModels.AboutUsScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AboutUsScreen(
    viewModel: AboutUsScreenViewModel,
    navController: NavHostController,
) {
    val context = LocalContext.current
    Scaffold() {
        AboutUsBodyContent(context, viewModel, navController)
    }
}

@Composable
fun AboutUsBodyContent(
    context: Context,
    viewModel: AboutUsScreenViewModel,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painterResource(id = R.drawable.provisional_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(200.dp)
        )
        Text(text = "Wewiza", style = TextStyle(fontSize = 40.sp))

        ImageFields(context, viewModel)
    }
}

@Composable
private fun ImageFields(context: Context, viewModel: AboutUsScreenViewModel) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
            .background(Color(0xFFFFE797))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = rememberImagePainter("https://avatars.githubusercontent.com/u/113429920?v=4"),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                    )

                    Text(text = "Angel Maroto Chivite")
                }
                Spacer(modifier = Modifier.size(20.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = rememberImagePainter("https://avatars.githubusercontent.com/u/113969561?v=4"),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                    )

                    Text(text = "JiaCheng Zhang")
                }
            }
            LinkFields(context, viewModel)
        }
    }
}

@Composable
fun LinkField(label: String, url: String, context: Context, viewModel: AboutUsScreenViewModel) {
    val painter = rememberImagePainter(
        data = if (label.contains("GitHub")) R.drawable.github_logo else R.drawable.linkedin_logo,
    )
    Image(
        painter = painter,
        contentDescription = label,
        modifier = Modifier
            .size(40.dp)
            .clickable {
                viewModel.openLink(url, context)
            }
    )
}

@Composable
fun LinkFields(context: Context, viewModel: AboutUsScreenViewModel) {
        Row(horizontalArrangement = Arrangement.Center) {
            LinkField("Angel's GitHub", "https://github.com/agchivite", context, viewModel)
            Spacer(modifier = Modifier.width(16.dp))
            LinkField(
                "Angel's Linkedin",
                "https://www.linkedin.com/in/angel-maroto-chivite-14b0a0162/",
                context,
                viewModel
            )
            Spacer(modifier = Modifier.width(80.dp))
            LinkField("Jia's GitHub", "https://github.com/JiaChengZhang14", context, viewModel)
            Spacer(modifier = Modifier.width(16.dp))
            LinkField(
                "Jia's Linkedin",
                "https://www.linkedin.com/in/jiacheng-zhang-a69739251/",
                context,
                viewModel
            )


        }
}

