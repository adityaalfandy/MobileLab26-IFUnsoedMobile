package com.pemmob.fandy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.pemmob.fandy.ui.screen.DaftarProdukScreen
import com.pemmob.fandy.ui.screen.DetailProductScreen
import com.pemmob.fandy.ui.screen.HubungiKamiScreen
import com.pemmob.fandy.ui.theme.JualanTheme
import com.pemmob.fandy.ui.viewmodel.ProductViewModel

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JualanTheme {
                val navController = rememberNavController()
                val productViewModel: ProductViewModel = viewModel()

                NavHost(navController = navController, startDestination = "daftar_produk") {


                    composable(route = "daftar_produk") {
                        DaftarProdukScreen(
                            navController = navController,
                            viewModel = productViewModel
                        )
                    }


                    composable(
                        route = "detail/{productId}",
                        arguments = listOf(navArgument("productId") {
                            type = NavType.IntType
                        })
                    ) { backStackEntry ->
                        val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                        DetailProductScreen(
                            productId = productId,
                            navController = navController,
                            viewModel = productViewModel
                        )
                    }


                    composable(route = "hubungi_kami") {
                        HubungiKamiScreen(navController = navController)
                    }

                }
            }
        }
    }
}