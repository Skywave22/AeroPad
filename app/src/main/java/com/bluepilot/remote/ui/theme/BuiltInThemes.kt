package com.bluepilot.remote.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * THEME CATALOG v3 — full rebuild (all legacy themes removed).
 *
 * 16 premium themes in 6 design families, every family with a dark and a
 * light face so the one-tap mode-follows-theme behavior always has a
 * counterpart:
 *
 *   3D        — Obsidian 3D · Clay 3D          (deep shadows, pressable slabs)
 *   LIQUID    — Liquid Dark · Liquid Aqua      (flowing gradient pools)
 *   GLASS     — Smoke Glass · Glass Light      (frosted translucent panes)
 *   MATERIAL  — Material You Dark/Light        (Material 3 tonal surfaces)
 *   PIXEL     — Pixel Night · Pixel Snow       (Google Pixel launcher feel)
 *   EXTRA     — AMOLED Void · Aurora 3D · Sunset Liquid · Cyber Neon ·
 *               Frost 3D · Terminal (mono)
 *
 * Legacy stored ids resolve through [byId]'s safe fallback (Obsidian 3D),
 * so upgrading users never crash — they just land on the new default.
 */
object BuiltInThemes {

    // ==================== 3D FAMILY ====================

    /** STITCH flagship — pitch-black 3D slabs, glowing cyan. DEFAULT. */
    val OBSIDIAN_3D = AppThemeSpec(
        id = "obsidian_3d", name = "Obsidian 3D", isDark = true,
        primary = Color(0xFF2FD9F4), onPrimary = Color(0xFF00363E),
        secondary = Color(0xFF7C6CF5),
        background = Color(0xFF050505), onBackground = Color(0xFFE5E2E1),
        surface = Color(0xFF131313), onSurface = Color(0xFFE5E2E1),
        surfaceVariant = Color(0xFF1C1C20), onSurfaceVariant = Color(0xFF9BA3AE),
        outline = Color(0xFF2A2A30),
        backgroundOrbs = listOf(
            ThemeOrb(Color(0xFF2FD9F4), 0.50f, 0.14f, 0.44f, 0.10f),
            ThemeOrb(Color(0xFF7C6CF5), 0.85f, 0.92f, 0.34f, 0.07f)
        ),
        cornerRadius = 24, surfaceAlpha = 0.94f, edgeGlow = true, elevation = 4,
        glowColor = Color(0xFF2FD9F4), connected = Color(0xFF66F796)
    )

    /** Soft-clay neumorphic light: embossed surfaces, gentle indigo. */
    val CLAY_3D = AppThemeSpec(
        id = "clay_3d", name = "Clay 3D", isDark = false,
        primary = Color(0xFF6C8CFF), onPrimary = Color.White,
        secondary = Color(0xFF9F7CFF),
        background = Color(0xFFE8ECF4), onBackground = Color(0xFF2A3040),
        surface = Color(0xFFF2F5FA), onSurface = Color(0xFF2A3040),
        surfaceVariant = Color(0xFFDDE4F0), onSurfaceVariant = Color(0xFF6B7690),
        outline = Color(0xFFC9D2E4),
        cornerRadius = 26, surfaceAlpha = 1f, edgeGlow = false, elevation = 6,
        glowColor = Color(0xFF6C8CFF)
    )

    // ==================== LIQUID FAMILY ====================

    /** Deep-sea liquid: indigo ink with flowing violet/cyan pools. */
    val LIQUID_DARK = AppThemeSpec(
        id = "liquid_dark", name = "Liquid Dark", isDark = true,
        primary = Color(0xFF64B5FF), onPrimary = Color(0xFF002B52),
        secondary = Color(0xFF9C7BFF),
        background = Color(0xFF0A0E1E), onBackground = Color(0xFFE9EEFC),
        surface = Color(0xFF141A30), onSurface = Color(0xFFE9EEFC),
        surfaceVariant = Color(0xFF1D2440), onSurfaceVariant = Color(0xFFA2AECB),
        outline = Color(0xFF32406A),
        backgroundOrbs = listOf(
            ThemeOrb(Color(0xFF3B5BFF), 0.15f, 0.10f, 0.48f, 0.30f),
            ThemeOrb(Color(0xFF8A4DFF), 0.90f, 0.40f, 0.42f, 0.24f),
            ThemeOrb(Color(0xFF22D3EE), 0.35f, 0.92f, 0.40f, 0.20f)
        ),
        cornerRadius = 26, surfaceAlpha = 0.82f, edgeGlow = true, elevation = 0,
        glowColor = Color(0xFF64B5FF)
    )

    /** Flowing water light: aqua gradients over a bright surface. */
    val LIQUID_AQUA = AppThemeSpec(
        id = "liquid_aqua", name = "Liquid Aqua", isDark = false,
        primary = Color(0xFF0097A7), onPrimary = Color.White,
        secondary = Color(0xFF00BFA5),
        background = Color(0xFFEDFBFC), onBackground = Color(0xFF10333A),
        surface = Color(0xFFFFFFFF), onSurface = Color(0xFF10333A),
        surfaceVariant = Color(0xFFD9F3F5), onSurfaceVariant = Color(0xFF48717A),
        outline = Color(0xFFB6E2E8),
        backgroundOrbs = listOf(
            ThemeOrb(Color(0xFF4DD0E1), 0.20f, 0.08f, 0.50f, 0.30f),
            ThemeOrb(Color(0xFF80DEEA), 0.85f, 0.55f, 0.44f, 0.26f),
            ThemeOrb(Color(0xFFA7FFEB), 0.40f, 0.95f, 0.50f, 0.30f)
        ),
        cornerRadius = 26, surfaceAlpha = 0.86f, edgeGlow = true, elevation = 2,
        glowColor = Color(0xFF0097A7)
    )

    // ==================== GLASS FAMILY ====================

    /** Dark smoked glass: graphite panes with a silver sheen. */
    val SMOKE_GLASS = AppThemeSpec(
        id = "smoke_glass", name = "Smoke Glass", isDark = true,
        primary = Color(0xFF8EF6FF), onPrimary = Color(0xFF00363E),
        secondary = Color(0xFFB0BEC5),
        background = Color(0xFF0C0E14), onBackground = Color(0xFFEEF0FA),
        surface = Color(0xFF181C28), onSurface = Color(0xFFEEF0FA),
        surfaceVariant = Color(0xFF232A3C), onSurfaceVariant = Color(0xFF8891AB),
        outline = Color(0xFF323A54),
        backgroundOrbs = listOf(
            ThemeOrb(Color(0xFF8EF6FF), 0.80f, 0.12f, 0.36f, 0.10f),
            ThemeOrb(Color(0xFFFF9AF5), 0.15f, 0.85f, 0.34f, 0.08f)
        ),
        cornerRadius = 20, surfaceAlpha = 0.70f, edgeGlow = true, elevation = 0,
        glowColor = Color(0xFF8EF6FF)
    )

    /** STITCH flagship — visionOS liquid-glass light. */
    val STITCH_GLASS_LIGHT = AppThemeSpec(
        id = "stitch_glass_light", name = "Glass Light", isDark = false,
        primary = Color(0xFF2563EB), onPrimary = Color.White,
        secondary = Color(0xFF06B6D4),
        background = Color(0xFFF9F9FF), onBackground = Color(0xFF111C2D),
        surface = Color(0xFFFFFFFF), onSurface = Color(0xFF111C2D),
        surfaceVariant = Color(0xFFECF1FF), onSurfaceVariant = Color(0xFF52565A),
        outline = Color(0xFFD8E3FB),
        backgroundOrbs = listOf(
            ThemeOrb(Color(0xFFB4C5FF), 0.20f, 0.10f, 0.50f, 0.30f),
            ThemeOrb(Color(0xFF57DFFE), 0.85f, 0.30f, 0.40f, 0.20f),
            ThemeOrb(Color(0xFFDEE8FF), 0.50f, 0.95f, 0.55f, 0.45f)
        ),
        cornerRadius = 24, surfaceAlpha = 0.88f, edgeGlow = true, elevation = 3,
        glowColor = Color(0xFF2563EB), connected = Color(0xFF16A34A)
    )

    // ==================== MATERIAL FAMILY ====================

    /** Material 3 dark: tonal surfaces, dynamic-color feel. */
    val MATERIAL_YOU_DARK = AppThemeSpec(
        id = "material_you_dark", name = "Material You", isDark = true,
        primary = Color(0xFFD0BCFF), onPrimary = Color(0xFF381E72),
        secondary = Color(0xFFCCC2DC),
        background = Color(0xFF141218), onBackground = Color(0xFFE6E0E9),
        surface = Color(0xFF211F26), onSurface = Color(0xFFE6E0E9),
        surfaceVariant = Color(0xFF2B2930), onSurfaceVariant = Color(0xFFCAC4D0),
        outline = Color(0xFF49454F),
        cornerRadius = 28, surfaceAlpha = 1f, edgeGlow = false, elevation = 1
    )

    /** Material 3 light counterpart. */
    val MATERIAL_YOU_LIGHT = AppThemeSpec(
        id = "material_you_light", name = "Material Light", isDark = false,
        primary = Color(0xFF6750A4), onPrimary = Color.White,
        secondary = Color(0xFF625B71),
        background = Color(0xFFFEF7FF), onBackground = Color(0xFF1D1B20),
        surface = Color(0xFFF7F2FA), onSurface = Color(0xFF1D1B20),
        surfaceVariant = Color(0xFFE7E0EC), onSurfaceVariant = Color(0xFF49454F),
        outline = Color(0xFFCAC4D0),
        cornerRadius = 28, surfaceAlpha = 1f, edgeGlow = false, elevation = 1
    )

    // ==================== PIXEL FAMILY ====================

    /** Pixel launcher at night: soft charcoal + Google blue. */
    val PIXEL_NIGHT = AppThemeSpec(
        id = "pixel_night", name = "Pixel Night", isDark = true,
        primary = Color(0xFF8AB4F8), onPrimary = Color(0xFF0B2A5B),
        secondary = Color(0xFF81C995),
        background = Color(0xFF1F1F1F), onBackground = Color(0xFFE8EAED),
        surface = Color(0xFF2D2E31), onSurface = Color(0xFFE8EAED),
        surfaceVariant = Color(0xFF3C4043), onSurfaceVariant = Color(0xFF9AA0A6),
        outline = Color(0xFF5F6368),
        cornerRadius = 22, surfaceAlpha = 1f, edgeGlow = false, elevation = 2,
        connected = Color(0xFF81C995)
    )

    /** Pixel in daylight: paper white + the four Google hues. */
    val PIXEL_SNOW = AppThemeSpec(
        id = "pixel_snow", name = "Pixel Snow", isDark = false,
        primary = Color(0xFF1A73E8), onPrimary = Color.White,
        secondary = Color(0xFF188038),
        background = Color(0xFFFFFFFF), onBackground = Color(0xFF202124),
        surface = Color(0xFFF8F9FA), onSurface = Color(0xFF202124),
        surfaceVariant = Color(0xFFE8EAED), onSurfaceVariant = Color(0xFF5F6368),
        outline = Color(0xFFDADCE0),
        cornerRadius = 22, surfaceAlpha = 1f, edgeGlow = false, elevation = 2,
        connected = Color(0xFF188038)
    )

    // ==================== EXTRAS ====================

    /** Pure #000 for AMOLED battery savings; minimal chrome. */
    val AMOLED_VOID = AppThemeSpec(
        id = "amoled_void", name = "AMOLED Void", isDark = true,
        primary = Color(0xFF00E5FF), onPrimary = Color(0xFF00363E),
        secondary = Color(0xFF69F0AE),
        background = Color(0xFF000000), onBackground = Color(0xFFEAEAEA),
        surface = Color(0xFF0A0A0A), onSurface = Color(0xFFEAEAEA),
        surfaceVariant = Color(0xFF141414), onSurfaceVariant = Color(0xFF8A8A8A),
        outline = Color(0xFF232323),
        cornerRadius = 18, surfaceAlpha = 1f, edgeGlow = false, elevation = 0,
        glowColor = Color(0xFF00E5FF)
    )

    /** Northern lights over deep teal-black; drifting mint/violet orbs. */
    val AURORA_3D = AppThemeSpec(
        id = "aurora_3d", name = "Aurora 3D", isDark = true,
        primary = Color(0xFF5EF2C1), onPrimary = Color(0xFF00382A),
        secondary = Color(0xFF7AA8FF),
        background = Color(0xFF071018), onBackground = Color(0xFFE6F7F4),
        surface = Color(0xFF0D1E2A), onSurface = Color(0xFFE6F7F4),
        surfaceVariant = Color(0xFF14293A), onSurfaceVariant = Color(0xFF7BA8A5),
        outline = Color(0xFF1B4A52),
        backgroundOrbs = listOf(
            ThemeOrb(Color(0xFF5EF2C1), 0.25f, 0.12f, 0.50f, 0.22f),
            ThemeOrb(Color(0xFF7AA8FF), 0.80f, 0.30f, 0.44f, 0.18f),
            ThemeOrb(Color(0xFFB388FF), 0.55f, 0.90f, 0.40f, 0.16f)
        ),
        cornerRadius = 26, surfaceAlpha = 0.78f, edgeGlow = true, elevation = 2,
        glowColor = Color(0xFF5EF2C1)
    )

    /** Warm dusk gradients: coral + rose liquid over plum ink. */
    val SUNSET_LIQUID = AppThemeSpec(
        id = "sunset_liquid", name = "Sunset Liquid", isDark = true,
        primary = Color(0xFFFF8A65), onPrimary = Color(0xFF4E1500),
        secondary = Color(0xFFF06292),
        background = Color(0xFF16090F), onBackground = Color(0xFFFFEDEF),
        surface = Color(0xFF261119), onSurface = Color(0xFFFFEDEF),
        surfaceVariant = Color(0xFF351A25), onSurfaceVariant = Color(0xFFB0808F),
        outline = Color(0xFF4A2438),
        backgroundOrbs = listOf(
            ThemeOrb(Color(0xFFFF7043), 0.20f, 0.15f, 0.48f, 0.22f),
            ThemeOrb(Color(0xFFF06292), 0.85f, 0.50f, 0.44f, 0.20f),
            ThemeOrb(Color(0xFFFFB74D), 0.45f, 0.92f, 0.40f, 0.16f)
        ),
        cornerRadius = 26, surfaceAlpha = 0.84f, edgeGlow = true, elevation = 2,
        glowColor = Color(0xFFFF8A65)
    )

    /** Cyberpunk neon: magenta/cyan on near-black violet, sharp corners. */
    val CYBER_NEON = AppThemeSpec(
        id = "cyber_neon", name = "Cyber Neon", isDark = true,
        primary = Color(0xFFFF2EC4), onPrimary = Color(0xFF3A0030),
        secondary = Color(0xFF00E5FF),
        background = Color(0xFF08040E), onBackground = Color(0xFFF2E8FF),
        surface = Color(0xFF150A24), onSurface = Color(0xFFF2E8FF),
        surfaceVariant = Color(0xFF20103A), onSurfaceVariant = Color(0xFF8D7AB0),
        outline = Color(0xFF3D1466),
        backgroundOrbs = listOf(
            ThemeOrb(Color(0xFFFF2EC4), 0.85f, 0.15f, 0.40f, 0.14f),
            ThemeOrb(Color(0xFF00E5FF), 0.15f, 0.85f, 0.38f, 0.12f)
        ),
        cornerRadius = 10, surfaceAlpha = 0.88f, edgeGlow = true, elevation = 0,
        glowColor = Color(0xFFFF2EC4)
    )

    /** Frosted 3D light: icy blue-silver with soft embossed depth. */
    val FROST_3D = AppThemeSpec(
        id = "frost_3d", name = "Frost 3D", isDark = false,
        primary = Color(0xFF2196F3), onPrimary = Color.White,
        secondary = Color(0xFF00BCD4),
        background = Color(0xFFEEF3FA), onBackground = Color(0xFF1B2536),
        surface = Color(0xFFFCFDFF), onSurface = Color(0xFF1B2536),
        surfaceVariant = Color(0xFFE2EAF6), onSurfaceVariant = Color(0xFF64718A),
        outline = Color(0xFFD0DCEE),
        backgroundOrbs = listOf(
            ThemeOrb(Color(0xFF90CAF9), 0.18f, 0.12f, 0.46f, 0.28f),
            ThemeOrb(Color(0xFFB3E5FC), 0.85f, 0.85f, 0.50f, 0.30f)
        ),
        cornerRadius = 24, surfaceAlpha = 0.90f, edgeGlow = true, elevation = 4,
        glowColor = Color(0xFF2196F3)
    )

    /** Green-phosphor terminal, monospace everywhere (the mono theme). */
    val TERMINAL = AppThemeSpec(
        id = "terminal", name = "Terminal", isDark = true,
        primary = Color(0xFF00E676), onPrimary = Color(0xFF002812),
        secondary = Color(0xFF69F0AE),
        background = Color(0xFF04120A), onBackground = Color(0xFFDCFFE9),
        surface = Color(0xFF081C12), onSurface = Color(0xFFDCFFE9),
        surfaceVariant = Color(0xFF0D2A1B), onSurfaceVariant = Color(0xFF6DA287),
        outline = Color(0xFF0F4025),
        cornerRadius = 6, surfaceAlpha = 0.92f, edgeGlow = true, elevation = 0,
        glowColor = Color(0xFF00E676), monoFont = true
    )

    // ==================== CATALOG ====================

    /** All themes, gallery order: flagships first, then by family. */
    val ALL: List<AppThemeSpec> = listOf(
        OBSIDIAN_3D, STITCH_GLASS_LIGHT,
        LIQUID_DARK, LIQUID_AQUA,
        SMOKE_GLASS, FROST_3D,
        CLAY_3D, AURORA_3D,
        MATERIAL_YOU_DARK, MATERIAL_YOU_LIGHT,
        PIXEL_NIGHT, PIXEL_SNOW,
        AMOLED_VOID, SUNSET_LIQUID,
        CYBER_NEON, TERMINAL
    )

    /** Safe lookup — unknown/legacy ids land on the flagship default. */
    fun byId(id: String?): AppThemeSpec =
        ALL.firstOrNull { it.id == id } ?: OBSIDIAN_3D

    /**
     * Dark/light counterpart within the same design family, used when the
     * user forces LIGHT or DARK mode while a theme of the other brightness
     * is active. Every theme has a defined partner.
     */
    fun counterpart(spec: AppThemeSpec): AppThemeSpec = when (spec.id) {
        "obsidian_3d" -> STITCH_GLASS_LIGHT
        "stitch_glass_light" -> OBSIDIAN_3D
        "liquid_dark" -> LIQUID_AQUA
        "liquid_aqua" -> LIQUID_DARK
        "smoke_glass" -> FROST_3D
        "frost_3d" -> SMOKE_GLASS
        "clay_3d" -> OBSIDIAN_3D
        "aurora_3d" -> LIQUID_AQUA
        "material_you_dark" -> MATERIAL_YOU_LIGHT
        "material_you_light" -> MATERIAL_YOU_DARK
        "pixel_night" -> PIXEL_SNOW
        "pixel_snow" -> PIXEL_NIGHT
        "amoled_void" -> STITCH_GLASS_LIGHT
        "sunset_liquid" -> LIQUID_AQUA
        "cyber_neon" -> STITCH_GLASS_LIGHT
        "terminal" -> PIXEL_SNOW
        else -> if (spec.isDark) STITCH_GLASS_LIGHT else OBSIDIAN_3D
    }
}
