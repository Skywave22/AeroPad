package com.bluepilot.remote.ui.screens.wifi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bluepilot.remote.ui.components.GlassCard
import com.bluepilot.remote.viewmodel.WifiConnectViewModel
import com.bluepilot.remote.wifi.WifiEngine
import com.bluepilot.remote.wifi.WifiProtocol

/**
 * AEROPAD v1.0 — WiFi LAN connection screen.
 *
 * Discovery via mDNS/NSD, manual IP fallback, QR-payload paste, PIN
 * confirmation dialog, live status + real measured RTT. Requires the
 * AeroPad companion receiver on the host PC (clearly stated in-UI).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiConnectScreen(
    onBack: () -> Unit,
    viewModel: WifiConnectViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val discovered by viewModel.discovered.collectAsState()
    val latency by viewModel.latencyMs.collectAsState()
    val mode by viewModel.mode.collectAsState()

    var manualIp by remember { mutableStateOf("") }
    var qrPayload by remember { mutableStateOf("") }
    var pinInput by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        viewModel.startDiscovery()
        onDispose { viewModel.stopDiscovery() }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("WiFi Control") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ---------- Transport mode switch ----------
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text("Input transport", style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = mode == com.bluepilot.remote.wifi.TransportManager.Mode.BLUETOOTH,
                            onClick = { viewModel.setMode(com.bluepilot.remote.wifi.TransportManager.Mode.BLUETOOTH) },
                            label = { Text("Bluetooth HID") }
                        )
                        FilterChip(
                            selected = mode == com.bluepilot.remote.wifi.TransportManager.Mode.WIFI,
                            onClick = { viewModel.setMode(com.bluepilot.remote.wifi.TransportManager.Mode.WIFI) },
                            label = { Text("WiFi LAN") }
                        )
                    }
                    Text(
                        "All controls (keyboard, mouse, gamepad, combos) work identically on both transports.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ---------- V2 MATRIX 4 — saved hosts (one-tap quick-switch) ----------
            val hostProfiles by viewModel.hostProfiles.collectAsState()
            if (hostProfiles.isNotEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Text("Saved hosts", style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary)
                        // Edit mode toggles ✕ chips (no fragile long-press).
                        var editHosts by remember { mutableStateOf(false) }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                if (editHosts) "Tap ✕ to forget a host." else "Tap to switch instantly.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = { editHosts = !editHosts }) {
                                Text(if (editHosts) "Done" else "Edit")
                            }
                        }
                        Spacer(Modifier.height(2.dp))
                        // STITCH v3 (02_Connect.html) — host CARDS, not chips:
                        // horizontal shelf of min-width tiles with a 40dp
                        // round icon coin (w-10 h-10 rounded-full + hairline)
                        // and a status dot. Active host glows in the accent.
                        val activeHost = (state as? WifiEngine.WifiState.Connected)?.host
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val spec = com.bluepilot.remote.ui.theme.LocalAppTheme.current
                            hostProfiles.forEach { p ->
                                val isWifi = p.transport ==
                                    com.bluepilot.remote.data.hosts.HostProfile.TRANSPORT_WIFI
                                val isActive = isWifi && p.address == activeHost
                                Column(
                                    modifier = Modifier
                                        .background(
                                            spec.surface.copy(alpha = spec.surfaceAlpha),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            width = if (isActive) 1.5.dp else 1.dp,
                                            color = if (isActive) spec.primary.copy(alpha = 0.7f)
                                            else spec.outline,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable {
                                            if (editHosts) viewModel.removeProfile(p.id)
                                            else viewModel.quickConnect(p)
                                        }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(spec.surfaceVariant, CircleShape)
                                                .border(
                                                    1.dp,
                                                    if (isActive) spec.primary.copy(alpha = 0.5f)
                                                    else spec.outline.copy(alpha = 0.5f),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                if (editHosts) "✕" else if (isWifi) "🖥" else "💻",
                                                color = if (editHosts)
                                                    MaterialTheme.colorScheme.error
                                                else Color.Unspecified
                                            )
                                        }
                                        Spacer(Modifier.size(10.dp))
                                        Column {
                                            Text(
                                                p.label.take(14),
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    Modifier
                                                        .size(6.dp)
                                                        .background(
                                                            if (isActive) spec.connected
                                                            else MaterialTheme.colorScheme.outline,
                                                            CircleShape
                                                        )
                                                )
                                                Spacer(Modifier.size(4.dp))
                                                Text(
                                                    if (isActive) "connected"
                                                    else if (isWifi) "WiFi" else "Bluetooth",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (isActive) spec.connected
                                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ---------- Status ----------
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    val (dotColor, label) = when (val s = state) {
                        is WifiEngine.WifiState.Connected ->
                            Color(0xFF2ECC71) to "Connected — ${s.name} (${s.host})" +
                                (latency?.let { "  •  ${it}ms RTT" } ?: "")
                        is WifiEngine.WifiState.Connecting -> Color(0xFFF1C40F) to "Connecting to ${s.host}…"
                        is WifiEngine.WifiState.AwaitingPin -> Color(0xFFF1C40F) to "Enter the PIN shown on the receiver"
                        is WifiEngine.WifiState.Discovering -> MaterialTheme.colorScheme.primary to "Scanning your network…"
                        is WifiEngine.WifiState.Error -> Color(0xFFE74C3C) to s.message
                        else -> MaterialTheme.colorScheme.outline to "Not connected"
                    }
                    Box(Modifier.size(12.dp).background(dotColor, CircleShape))
                    Spacer(Modifier.size(10.dp))
                    Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    if (state is WifiEngine.WifiState.Connected) {
                        TextButton(onClick = { viewModel.disconnect() }) { Text("Disconnect") }
                    }
                }
            }

            // ---------- Receiver requirement (honest) ----------
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text("Requires the AeroPad receiver on your PC",
                        style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Run the free companion script (Windows/Mac/Linux) from the AeroPad GitHub repo: " +
                            "python aeropad_receiver.py — it shows a PIN and appears in the list below.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ---------- Discovered hosts ----------
            Text("Devices on your network", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary)
            if (discovered.isEmpty()) {
                Text(
                    "No receivers found yet — make sure the PC receiver is running on the same WiFi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            discovered.forEach { host ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(host.name, style = MaterialTheme.typography.titleMedium)
                            Text("${host.host}:${host.port}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(onClick = { viewModel.requestConnect(host.host, host.port) }) {
                            Text("Connect")
                        }
                    }
                }
            }
            OutlinedButton(onClick = { viewModel.startDiscovery() }, modifier = Modifier.fillMaxWidth()) {
                Text("Rescan network")
            }

            // ---------- Manual IP fallback ----------
            Text("Manual connection", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary)
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = manualIp,
                    onValueChange = { manualIp = it },
                    singleLine = true,
                    placeholder = { Text("IP address e.g. 192.168.1.20") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.size(8.dp))
                Button(
                    onClick = { viewModel.requestConnect(manualIp.trim()) },
                    enabled = manualIp.isNotBlank()
                ) { Text("Go") }
            }

            // ---------- QR quick pair (paste payload) ----------
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = qrPayload,
                    onValueChange = { qrPayload = it },
                    singleLine = true,
                    placeholder = { Text("Paste QR payload: aeropad://ip:port/pin") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.size(8.dp))
                Button(
                    onClick = {
                        WifiProtocol.parseQrPayload(qrPayload)?.let { (h, p, pin) ->
                            viewModel.requestConnect(h, p)
                            viewModel.connectWithPin(pin)
                        }
                    },
                    enabled = WifiProtocol.parseQrPayload(qrPayload) != null
                ) { Text("Pair") }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    // ---------- STITCH v3 REBUILD — PIN pairing (19_PINPairing.html) ----------
    // Glowing lock hero (w-20 h-20 circle + blur pool), six w-12 h-16
    // digit boxes with the typed digits glowing in the accent, opt-in
    // remember toggle, full-width connect button. The invisible text
    // field underneath keeps the system keyboard + paste working.
    if (state is WifiEngine.WifiState.AwaitingPin) {
        val spec = com.bluepilot.remote.ui.theme.LocalAppTheme.current
        var savePin by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { viewModel.disconnect() },
            title = null,
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Lock hero with radial glow pool.
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                androidx.compose.ui.graphics.Brush.radialGradient(
                                    listOf(spec.primary.copy(alpha = 0.20f), Color.Transparent)
                                ),
                                androidx.compose.foundation.shape.CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔐", style = MaterialTheme.typography.headlineLarge)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Enter the PIN shown on your PC",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "This proves you own both devices",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(14.dp))
                    // Six digit boxes (w-12 h-16) over an invisible field.
                    Box {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(6) { i ->
                                val ch = pinInput.getOrNull(i)?.toString() ?: ""
                                val filled = ch.isNotEmpty()
                                Box(
                                    modifier = Modifier
                                        .size(width = 40.dp, height = 56.dp)
                                        .background(
                                            spec.surface.copy(alpha = spec.surfaceAlpha),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .androidxPinBorder(filled, spec.primary, spec.outline),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        ch,
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = spec.primary
                                    )
                                }
                            }
                        }
                        // Invisible input capturing digits (keyboard+paste).
                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = {
                                if (it.length <= 6 && it.all { c -> c.isDigit() }) pinInput = it
                            },
                            modifier = Modifier
                                .matchParentSize()
                                .androidxAlpha(0.02f),
                            singleLine = true
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        androidx.compose.material3.Switch(
                            checked = savePin,
                            onCheckedChange = { savePin = it }
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(
                            "Remember this host + PIN\n(stored on this phone only)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.connectWithPin(pinInput, savePin); pinInput = "" },
                    enabled = pinInput.length == 6,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Connect") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.disconnect() }) { Text("Cancel") }
            }
        )
    }
}

/** PIN box border: accent when the slot is filled, quiet outline when empty. */
private fun Modifier.androidxPinBorder(
    filled: Boolean,
    accent: Color,
    outline: Color
): Modifier = this.then(
    Modifier.border(
        width = if (filled) 1.5.dp else 1.dp,
        color = if (filled) accent.copy(alpha = 0.7f) else outline,
        shape = RoundedCornerShape(10.dp)
    )
)

/** Near-invisible but focusable overlay field. */
private fun Modifier.androidxAlpha(a: Float): Modifier =
    this.then(Modifier.graphicsLayer { alpha = a })
