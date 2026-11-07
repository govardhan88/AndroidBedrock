package govi.core.ui.component.nointernet

import android.content.Context
import android.content.Intent
import android.provider.Settings

/**
 * Failsafe launcher for Android system wireless and network settings.
 *
 * Tries `ACTION_WIRELESS_SETTINGS` first, falling back to `ACTION_WIFI_SETTINGS`
 * and general `ACTION_SETTINGS` if OEM customization prevents opening wireless directly.
 */
fun openWirelessSettings(context: Context) {
    val intents =
        listOf(
            Intent(Settings.ACTION_WIRELESS_SETTINGS),
            Intent(Settings.ACTION_WIFI_SETTINGS),
            Intent(Settings.ACTION_SETTINGS),
        )

    for (intent in intents) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            return
        } catch (_: Exception) {
            // Try next fallback intent
        }
    }
}
