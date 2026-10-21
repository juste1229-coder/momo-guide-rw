package rw.momo.directory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import rw.momo.directory.data.Service
import rw.momo.directory.ui.*
import rw.momo.directory.ui.theme.MoMoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoMoTheme {
                val vm: MainViewModel = viewModel()
                val state by vm.state.collectAsState()
                var selected by remember { mutableStateOf<Service?>(null) }

                if (selected == null) {
                    HomeScreen(
                        state = state,
                        categories = vm.allCategories,
                        onQuery = vm::setQuery,
                        onCategory = vm::setCategory,
                        onOpen = { vm.pushRecent(it.id); selected = it },
                        onFavorite = vm::toggleFavorite
                    )
                } else {
                    ServiceDetailScreen(service = selected!!, onBack = { selected = null })
                }
            }
        }
    }
}
