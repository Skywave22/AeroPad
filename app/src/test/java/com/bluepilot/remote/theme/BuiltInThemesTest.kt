package com.bluepilot.remote.theme

import com.bluepilot.remote.ui.theme.BuiltInThemes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * THEME CATALOG v3 contract: catalog integrity.
 * A broken/missing theme id must NEVER crash the app — byId always
 * resolves to a valid spec (the flagship default).
 */
class BuiltInThemesTest {

    @Test
    fun `catalog has the sixteen v3 themes`() {
        assertEquals(16, BuiltInThemes.ALL.size)
    }

    @Test
    fun `theme ids are unique`() {
        val ids = BuiltInThemes.ALL.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `byId resolves every catalog theme`() {
        BuiltInThemes.ALL.forEach { spec ->
            assertEquals(spec, BuiltInThemes.byId(spec.id))
        }
    }

    @Test
    fun `byId falls back safely on unknown legacy or null id`() {
        assertEquals(BuiltInThemes.OBSIDIAN_3D, BuiltInThemes.byId("does_not_exist"))
        assertEquals(BuiltInThemes.OBSIDIAN_3D, BuiltInThemes.byId(null))
        assertEquals(BuiltInThemes.OBSIDIAN_3D, BuiltInThemes.byId(""))
        // Legacy v2 ids (removed) must also fall back, never crash.
        listOf(
            "pilot_dark", "aero_glass", "hawaii_night", "cockpit_hud",
            "oled_black", "minimal_light", "liquid_glass", "cyberpunk"
        ).forEach { legacy ->
            assertEquals(BuiltInThemes.OBSIDIAN_3D, BuiltInThemes.byId(legacy))
        }
    }

    @Test
    fun `catalog contains both dark and light themes`() {
        assertTrue(BuiltInThemes.ALL.count { it.isDark } >= 6)
        assertTrue(BuiltInThemes.ALL.count { !it.isDark } >= 5)
    }

    @Test
    fun `every theme has a counterpart of the opposite brightness`() {
        BuiltInThemes.ALL.forEach { spec ->
            val other = BuiltInThemes.counterpart(spec)
            assertNotEquals("${spec.id} counterpart is itself", spec.id, other.id)
            assertNotEquals(
                "${spec.id} counterpart same brightness", spec.isDark, other.isDark
            )
            assertTrue(
                "${spec.id} counterpart not in catalog",
                BuiltInThemes.ALL.any { it.id == other.id }
            )
        }
    }

    @Test
    fun `all surface alphas and radii are within valid ranges`() {
        BuiltInThemes.ALL.forEach { spec ->
            assertTrue("${spec.id} alpha", spec.surfaceAlpha in 0.1f..1f)
            assertTrue("${spec.id} radius", spec.cornerRadius in 0..60)
            assertTrue("${spec.id} elevation", spec.elevation in 0..24)
            spec.backgroundOrbs.forEach { orb ->
                assertTrue("${spec.id} orb alpha", orb.alpha in 0f..1f)
                assertTrue("${spec.id} orb pos", orb.x in 0f..1f && orb.y in 0f..1f)
            }
        }
    }

    @Test
    fun `v3 family themes are present`() {
        val ids = BuiltInThemes.ALL.map { it.id }
        assertTrue(ids.containsAll(listOf(
            "obsidian_3d", "stitch_glass_light",         // flagships
            "liquid_dark", "liquid_aqua",                // liquid
            "smoke_glass", "frost_3d",                   // glass
            "material_you_dark", "material_you_light",   // material
            "pixel_night", "pixel_snow",                 // pixel experience
            "amoled_void", "aurora_3d", "sunset_liquid",
            "cyber_neon", "clay_3d", "terminal"          // extras
        )))
    }

    @Test
    fun `exactly one mono font theme`() {
        assertEquals(1, BuiltInThemes.ALL.count { it.monoFont })
        assertTrue(BuiltInThemes.TERMINAL.monoFont)
    }
}
