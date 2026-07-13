package com.bluepilot.remote.domain

import com.bluepilot.remote.data.hosts.HostProfile
import com.bluepilot.remote.data.hosts.HostProfiles
import com.bluepilot.remote.model.HidConnectionState
import com.bluepilot.remote.wifi.TransportManager
import com.bluepilot.remote.wifi.WifiEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * V2 MATRIX 4 b2 — auto-reconnect to the most recent saved host on app
 * start. OPT-IN via Settings ("Reconnect on launch", default OFF —
 * silent auto-connections surprise people).
 *
 * Rules (deliberate, conservative):
 *  - fires at most ONCE per process lifetime (no reconnect loops);
 *  - skipped when something is already connected/connecting;
 *  - WiFi hosts need a saved PIN (never pops a PIN dialog uninvited);
 *  - failures are silent — the user simply connects manually as before.
 */
@Singleton
class AutoReconnector @Inject constructor(
    private val settings: SettingsStore,
    private val hosts: HostProfiles,
    private val hid: HidController,
    private val transport: TransportManager,
    private val wifi: WifiEngine
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var attempted = false

    /** Call once from MainActivity.onCreate — all gating happens inside. */
    fun maybeReconnect(pinFor: (HostProfile) -> String) {
        if (attempted) return
        attempted = true
        scope.launch {
            runCatching {
                val app = settings.appSettings.first()
                if (!app.autoReconnectLast) return@launch
                // Already live/being established? Don't interfere.
                if (hid.state.value !is HidConnectionState.Idle &&
                    hid.state.value !is HidConnectionState.Error
                ) return@launch
                if (wifi.state.value is WifiEngine.WifiState.Connected) return@launch
                val last = hosts.profiles.value.maxByOrNull { it.lastUsedAt } ?: return@launch
                Timber.i("auto-reconnect: trying '%s' (%s)", last.label, last.transport)
                if (last.transport == HostProfile.TRANSPORT_WIFI) {
                    val pin = pinFor(last)
                    if (pin.isEmpty()) return@launch   // no saved PIN → stay silent
                    transport.setMode(TransportManager.Mode.WIFI)
                    wifi.requestConnect(last.address, last.port)
                    wifi.connectWithPin(pin)
                } else {
                    transport.setMode(TransportManager.Mode.BLUETOOTH)
                    hid.start()
                    hid.connectTo(last.address)
                }
            }.onFailure { Timber.w(it, "auto-reconnect failed (silent)") }
        }
    }
}
