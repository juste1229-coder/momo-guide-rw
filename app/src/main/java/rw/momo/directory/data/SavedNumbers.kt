package rw.momo.directory.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.savedDataStore by preferencesDataStore("saved_numbers")

class SavedNumbers(private val context: Context) {
    // Keyed by service ID (e.g., "send_momo")
    private fun keyFor(serviceId: String) = stringSetPreferencesKey("saved_$serviceId")

    fun getSaved(serviceId: String): Flow<Set<String>> = context.savedDataStore.data.map { prefs ->
        prefs[keyFor(serviceId)] ?: emptySet()
    }

    suspend fun saveNumber(serviceId: String, number: String) {
        context.savedDataStore.edit { prefs ->
            val current = prefs[keyFor(serviceId)] ?: emptySet()
            prefs[keyFor(serviceId)] = current + number
        }
    }
}
