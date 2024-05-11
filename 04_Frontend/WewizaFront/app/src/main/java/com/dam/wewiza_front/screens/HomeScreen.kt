package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.dam.wewiza_front.R
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.models.Category
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.ui.theme.MyLightTheme
import com.dam.wewiza_front.viewModels.HomeScreenViewModel
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    navController: NavController
) {
    Scaffold(
        bottomBar = {
            Constants.BottomMenu(navController)
        }
    ) {

        MyLightTheme {
            HomeBodyContent(navController, viewModel)
        }
    }
}

@Composable
fun HomeBodyContent(
    navController: NavController,
    viewModel: HomeScreenViewModel,
) {
    Column() {
        WewizaLogoSection()
        FeaturedCategoriesSection(navController, viewModel)
        FeaturedProductsSection(navController, viewModel)


    }


}

@Composable
fun FeaturedProductsSection(navController: NavController, viewModel: HomeScreenViewModel) {
    Column() {
        ProductsTextSubSection(viewModel, navController)
        ProductsSection(viewModel, navController)
    }
}

@Composable
fun ProductsSection(viewModel: HomeScreenViewModel, navController: NavController) {

    val products = viewModel.allProductsList
    val firstSixProducts = products.shuffled().take(20)
    val productsLoaded = firstSixProducts.isNotEmpty()


    LaunchedEffect(productsLoaded) {
        if (productsLoaded) {
            delay(100) // Añade un pequeño retraso opcional si es necesario

   }
    }

    if (!productsLoaded) {
        LoadingIndicator()
    } else {
        Box {
            LazyRow(
                modifier = Modifier.padding(16.dp),
                content = {
                    items(firstSixProducts!!.size) { index ->
                        HomeProductItem(product = firstSixProducts[index])
                    }
                }
            )
        }
    }

}


@Composable
fun ProductsTextSubSection(viewModel: HomeScreenViewModel, navController: NavController) {
    Column(
        modifier = Modifier
            .padding(top = 30.dp)
    ) {
        Row {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("Productos Destacados")
                    }
                },
                modifier = Modifier
                    .padding(start = 20.dp),
                fontFamily = FirsNeue, fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.weight(0.1f))

            Text(
                text = "Ver más",
                modifier = Modifier
                    .padding(end = 20.dp)
                    .clickable {
                        sharedViewModel.initializeSelectedCategories()
                        viewModel.navigateToProducts(navController)
                    },
                color = Color.Blue,
                fontFamily = FirsNeue
            )
        }
    }

}

@Composable
fun FeaturedCategoriesSection(navController: NavController, viewModel: HomeScreenViewModel) {
    Column() {
        CategoryTextsSubSection(viewModel, navController)
        CategoriesSection(viewModel, navController)
    }

}

@Composable
fun CategoriesSection(viewModel: HomeScreenViewModel, navController: NavController) {

    val categories = viewModel.allCategoriesList
    val firstSixCategories = categories.take(6)
    val categoriesLoaded = firstSixCategories.isNotEmpty()

    LaunchedEffect(categoriesLoaded) {
        delay(100) // Añade un pequeño retraso opcional si es necesario
    }


    if (!categoriesLoaded) {
        LoadingIndicator()
    } else {
        Box {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.padding(16.dp),
                content = {
                    items(firstSixCategories.size) { index ->
                        HomeCategoryItem(category = firstSixCategories[index])
                    }
                }
            )
        }
    }
}


@Composable
fun HomeProductItem(product: Product) {
    val painter: Painter = rememberImagePainter(data = product.image_url)

    Card(
        modifier = Modifier
            .padding(20.dp)
            .size(80.dp)
            .clip(CircleShape), // Hace que el Card tenga una forma circular
        shape = CircleShape, // Asegura que el Card mantenga su forma circular incluso si tiene un elevación

    ) {
        Image(
            painter = painter,
            contentDescription = product.name,
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}


@Composable
fun HomeCategoryItem(category: Category) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(category.icon)
            .decoderFactory(SvgDecoder.Factory())
            .build()
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            modifier = Modifier
                .padding(20.dp)
                .size(80.dp)
                .clip(CircleShape), // Hace que el Card tenga una forma circular
            shape = CircleShape, // Asegura que el Card mantenga su forma circular incluso si tiene un elevación

        ) {
            Image(
                painter = painter,
                contentDescription = category.name,
                modifier = Modifier
                    .padding(10.dp)
            )
        }

        Text(
            text = category.name,
            modifier = Modifier
                .width(110.dp)
                .padding(top = 1.dp)
                .align(Alignment.CenterHorizontally),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }

}

@Composable
private fun CategoryTextsSubSection(
    viewModel: HomeScreenViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .padding(top = 30.dp)
    ) {
        Row {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("Categorías Destacadas")
                    }
                },
                modifier = Modifier
                    .padding(start = 20.dp),
                fontFamily = FirsNeue, fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.weight(0.1f))

            Text(
                text = "Ver más",
                modifier = Modifier
                    .padding(end = 20.dp)
                    .clickable { viewModel.navigateToCategories(navController) },
                color = Color.Blue,
                fontFamily = FirsNeue
            )
        }
    }
}

@Composable
fun WewizaLogoSection() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.wewiza_temporal_logo),
            contentDescription = "Wewiza Logo",
            modifier = Modifier
                .height(100.dp)
                .width(200.dp)
        )
    }

}


@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
