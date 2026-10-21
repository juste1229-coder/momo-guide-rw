package rw.momo.directory.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import rw.momo.directory.data.Prefs
import rw.momo.directory.data.Service
import rw.momo.directory.data.ServiceRepository

data class HomeState(
    val query: String = "",
    val category: String = "All",
    val favorites: Set<String> = emptySet(),
    val recents: List<String> = emptyList(),
    val results: List<Service> = ServiceRepository.services
)

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = Prefs(app)
    private val _query = MutableStateFlow("")
    private val _category = MutableStateFlow("All")

    val state: StateFlow<HomeState> =
        combine(_query, _category, prefs.favorites, prefs.recents) { q, cat, fav, rec ->
            var list = ServiceRepository.search(q)
            if (cat == "Favorites") list = list.filter { it.id in fav }
            else if (cat != "All") list = list.filter { it.category == cat }
            HomeState(q, cat, fav, rec, list)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeState())

    val allCategories: List<String> get() = listOf("All", "Favorites") + ServiceRepository.categories

    fun setQuery(q: String) { _query.value = q }
    fun setCategory(c: String) { _category.value = c }
    fun toggleFavorite(id: String) = viewModelScope.launch { prefs.toggleFavorite(id) }
    fun pushRecent(id: String) = viewModelScope.launch { prefs.pushRecent(id) }
}