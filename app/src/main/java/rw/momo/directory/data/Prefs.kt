package rw.momo.directory.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("momo_prefs")

class Prefs(private val context: Context) {
    private val FAV = stringSetPreferencesKey("favorites")
    private val RECENT = stringPreferencesKey("recent_csv")
    val favorites: Flow<Set<String>> = context.dataStore.data.map { it[FAV] ?: emptySet() }
    val recents: Flow<List<String>> = context.dataStore.data.map { p ->
        (p[RECENT] ?: "").split(",").filter { it.isNotBlank() }
    }
    suspend fun toggleFavorite(id: String) {
        context.dataStore.edit { p ->
            val cur = p[FAV] ?: emptySet()
            p[FAV] = if (id in cur) cur - id else cur + id
        }
    }
    suspend fun pushRecent(id: String) {
        context.dataStore.edit { p ->
            val cur = (p[RECENT] ?: "").split(",").filter { it.isNotBlank() }.toMutableList()
            cur.remove(id)
            cur.add(0, id)
            p[RECENT] = cur.take(10).joinToString(",")
        }
    }
}