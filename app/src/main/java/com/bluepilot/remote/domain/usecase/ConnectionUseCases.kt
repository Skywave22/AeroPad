package com.bluepilot.remote.domain.usecase

import com.bluepilot.remote.domain.HidController
import com.bluepilot.remote.model.HidAction
import com.bluepilot.remote.model.HidConnectionState
import com.bluepilot.remote.model.RemoteDevice
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * UseCase layer (Clean Architecture).
 *
 * Each class is one intention. ViewModels compose these instead of talking
 * to the engine directly, so business rules have a single home and tests
 * can inject fakes trivially.
 */

/**
 * WIFI FIX #2 — merged connection truth (singleton: ONE combine collector
 * for the whole app, no per-ViewModel leaks).
 *
 * Every control screen's "connected" banner previously watched ONLY the
 * Bluetooth engine, so a live WiFi session still showed "Not connected"
 * everywhere — the #1 cause of "WiFi not working" reports. Now a WiFi
 * session (while transport mode is WIFI) surfaces as Connected app-wide.
 */
/** Seam for tests: anything exposing merged connection state. */
interface ConnectionStateSource {
    val state: StateFlow<HidConnectionState>
}

@javax.inject.Singleton
class ConnectionStateHub @Inject constructor(
    controller: HidController,
    wifi: com.bluepilot.remote.wifi.WifiEngine,
    transport: com.bluepilot.remote.wifi.TransportManager
) : ConnectionStateSource {
    private val scope = kotlinx.coroutines.CoroutineScope(
        kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.Default
    )

    override val state: StateFlow<HidConnectionState> =
        kotlinx.coroutines.flow.combine(
            controller.state, wifi.state, transport.mode
        ) { bt, wf, mode ->
            if (mode == com.bluepilot.remote.wifi.TransportManager.Mode.WIFI &&
                wf is com.bluepilot.remote.wifi.WifiEngine.WifiState.Connected
            ) {
                HidConnectionState.Connected(
                    RemoteDevice(address = wf.host, name = wf.name, isPaired = true)
                )
            } else bt
        }.stateIn(
            scope,
            kotlinx.coroutines.flow.SharingStarted.Eagerly,
            controller.state.value
        )
}

/** Observe the (merged BT+WiFi) connection state machine. */
class ObserveConnectionUseCase @Inject constructor(
    private val source: ConnectionStateSource
) {
    /** Test convenience: plain BT-only view over a bare controller. */
    constructor(controller: HidController) : this(
        object : ConnectionStateSource {
            override val state: StateFlow<HidConnectionState> = controller.state
        }
    )

    operator fun invoke(): StateFlow<HidConnectionState> = source.state
}

/** Boot the HID engine (registers the combined HID app). */
class StartHidEngineUseCase @Inject constructor(
    private val controller: HidController
) {
    operator fun invoke() = controller.start()
}

/** Shut the HID engine down completely. */
class StopHidEngineUseCase @Inject constructor(
    private val controller: HidController
) {
    operator fun invoke() = controller.stop()
}

/** Connect to a host by MAC address. */
class ConnectDeviceUseCase @Inject constructor(
    private val controller: HidController
) {
    operator fun invoke(address: String) {
        // Input validation: a malformed MAC must never reach the framework.
        val mac = Regex("^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$")
        if (mac.matches(address)) controller.connectTo(address)
    }
}

/** User-initiated disconnect. */
class DisconnectDeviceUseCase @Inject constructor(
    private val controller: HidController
) {
    operator fun invoke() = controller.disconnect()
}

/** List bonded devices for pickers. */
class GetBondedDevicesUseCase @Inject constructor(
    private val controller: HidController
) {
    operator fun invoke(): List<RemoteDevice> = controller.bondedDevices()
}

/** Send any HID input action (keyboard/mouse/media/system/gamepad). */
class SendHidActionUseCase @Inject constructor(
    private val controller: HidController,
    private val recorder: com.bluepilot.remote.domain.MacroRecorder,
    // AEROPAD v1.0 — dual-mode transport: the ONLY routing point.
    private val transport: com.bluepilot.remote.wifi.TransportManager,
    private val wifi: com.bluepilot.remote.wifi.WifiEngine
) {
    operator fun invoke(action: HidAction) {
        // Macro recorder taps the pipeline: captures recordable actions
        // from EVERY screen while armed; zero overhead when idle.
        recorder.capture(action)
        // WiFi mode routes to the LAN receiver; Bluetooth path unchanged.
        if (transport.isWifi && wifi.isConnected) wifi.send(action)
        else controller.send(action)
    }
}
