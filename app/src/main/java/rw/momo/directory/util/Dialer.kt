package rw.momo.directory.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object Dialer {
    fun open(context: Context, ussd: String) {
        val cleaned = ussd.replace("#", Uri.encode("#"))
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleaned"))
        try { context.startActivity(intent) }
        catch (e: Exception) {
            Toast.makeText(context, "No dialer found", Toast.LENGTH_SHORT).show()
        }
    }
    fun buildIrembo(billId: String): String? {
        val clean = billId.trim()
        if (clean.isEmpty() || clean.any { !it.isLetterOrDigit() }) return null
        return "*182*3*7*$clean#"
    }
}