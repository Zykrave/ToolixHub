package com.zykrave.toolixhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zykrave.toolixhub.data.AppThemeMode
import com.zykrave.toolixhub.data.PreferencesManager
import com.zykrave.toolixhub.ui.screens.HomeScreen
import com.zykrave.toolixhub.ui.screens.SettingsScreen
import com.zykrave.toolixhub.ui.screens.tools.calculators.AgeCalculatorScreen
import com.zykrave.toolixhub.ui.screens.tools.calculators.CurrencyConverterScreen
import com.zykrave.toolixhub.ui.screens.tools.calculators.DateDiffCalculatorScreen
import com.zykrave.toolixhub.ui.screens.tools.calculators.DiscountTipCalculatorScreen
import com.zykrave.toolixhub.ui.screens.tools.calculators.PercentageCalculatorScreen
import com.zykrave.toolixhub.ui.screens.tools.calculators.StandardCalculatorScreen
import com.zykrave.toolixhub.ui.screens.tools.calculators.UnitConverterScreen
import com.zykrave.toolixhub.ui.screens.tools.color.ColorPaletteScreen
import com.zykrave.toolixhub.ui.screens.tools.color.ColorPickerScreen
import com.zykrave.toolixhub.ui.screens.tools.color.GradientGeneratorScreen
import com.zykrave.toolixhub.ui.screens.tools.finance.InterestCalculatorScreen
import com.zykrave.toolixhub.ui.screens.tools.finance.LoanEmiScreen
import com.zykrave.toolixhub.ui.screens.tools.finance.SavingsGoalScreen
import com.zykrave.toolixhub.ui.screens.tools.finance.VatTaxScreen
import com.zykrave.toolixhub.ui.screens.tools.image.ImageBase64Screen
import com.zykrave.toolixhub.ui.screens.tools.image.ImageCompressorScreen
import com.zykrave.toolixhub.ui.screens.tools.image.ImageCropperScreen
import com.zykrave.toolixhub.ui.screens.tools.image.ImageFormatConverterScreen
import com.zykrave.toolixhub.ui.screens.tools.image.ImageResizerScreen
import com.zykrave.toolixhub.ui.screens.tools.random.CoinFlipScreen
import com.zykrave.toolixhub.ui.screens.tools.random.DecisionSpinnerScreen
import com.zykrave.toolixhub.ui.screens.tools.random.DiceRollerScreen
import com.zykrave.toolixhub.ui.screens.tools.random.NameGeneratorScreen
import com.zykrave.toolixhub.ui.screens.tools.random.PasswordGeneratorScreen
import com.zykrave.toolixhub.ui.screens.tools.random.RandomNumberScreen
import com.zykrave.toolixhub.ui.screens.tools.random.TeamRandomizerScreen
import com.zykrave.toolixhub.ui.screens.tools.sensors.BubbleLevelScreen
import com.zykrave.toolixhub.ui.screens.tools.sensors.CompassScreen
import com.zykrave.toolixhub.ui.screens.tools.sensors.FlashlightScreen
import com.zykrave.toolixhub.ui.screens.tools.sensors.RulerScreen
import com.zykrave.toolixhub.ui.screens.tools.sensors.SoundMeterScreen
import com.zykrave.toolixhub.ui.screens.tools.text.CaseConverterScreen
import com.zykrave.toolixhub.ui.screens.tools.text.DuplicateRemoverScreen
import com.zykrave.toolixhub.ui.screens.tools.text.FindReplaceScreen
import com.zykrave.toolixhub.ui.screens.tools.text.LineSorterScreen
import com.zykrave.toolixhub.ui.screens.tools.text.LoremIpsumScreen
import com.zykrave.toolixhub.ui.screens.tools.text.TextCounterScreen
import com.zykrave.toolixhub.ui.screens.tools.text.TextReverserScreen
import com.zykrave.toolixhub.ui.screens.tools.timers.CountdownTimerScreen
import com.zykrave.toolixhub.ui.screens.tools.timers.IntervalTrainerScreen
import com.zykrave.toolixhub.ui.screens.tools.timers.PomodoroScreen
import com.zykrave.toolixhub.ui.screens.tools.timers.StopwatchScreen
import com.zykrave.toolixhub.ui.screens.tools.utility.BarcodeScannerScreen
import com.zykrave.toolixhub.ui.screens.tools.utility.HashGeneratorScreen
import com.zykrave.toolixhub.ui.screens.tools.utility.QrGeneratorScreen
import com.zykrave.toolixhub.ui.screens.tools.utility.SystemInfoScreen
import com.zykrave.toolixhub.ui.screens.tools.utility.UnitPriceComparatorScreen
import com.zykrave.toolixhub.ui.theme.ToolixTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = PreferencesManager(this)

        setContent {
            val themeMode by prefs.themeModeFlow.collectAsState(initial = AppThemeMode.DARK)
            val useDynamicColor by prefs.dynamicColorFlow.collectAsState(initial = true)
            val favorites by prefs.favoritesFlow.collectAsState(initial = emptySet())
            val recents by prefs.recentsFlow.collectAsState(initial = emptyList())

            val systemInDark = isSystemInDarkTheme()
            val isDark = when (themeMode) {
                AppThemeMode.DARK -> true
                AppThemeMode.LIGHT -> false
                AppThemeMode.SYSTEM -> systemInDark
            }

            ToolixTheme(
                darkTheme = isDark,
                dynamicColor = useDynamicColor
            ) {
                ToolixApp(
                    prefs = prefs,
                    themeMode = themeMode,
                    useDynamicColor = useDynamicColor,
                    favorites = favorites,
                    recents = recents
                )
            }
        }
    }
}

@Composable
fun ToolixApp(
    prefs: PreferencesManager,
    themeMode: AppThemeMode,
    useDynamicColor: Boolean,
    favorites: Set<String>,
    recents: List<String>
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    fun toggleFav(toolId: String) {
        scope.launch { prefs.toggleFavorite(toolId) }
    }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                favorites = favorites,
                recents = recents,
                onToggleFavorite = { id -> toggleFav(id) },
                onNavigateToTool = { tool ->
                    scope.launch { prefs.recordRecent(tool.id) }
                    navController.navigate(tool.route)
                },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }

        composable("settings") {
            SettingsScreen(
                prefs = prefs,
                themeMode = themeMode,
                useDynamicColor = useDynamicColor,
                onBack = { navController.popBackStack() }
            )
        }

        // 1. Calculators
        composable("tool_calc_standard") {
            StandardCalculatorScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("calc_standard"),
                onToggleFavorite = { toggleFav("calc_standard") }
            )
        }
        composable("tool_calc_percentage") {
            PercentageCalculatorScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("calc_percentage"),
                onToggleFavorite = { toggleFav("calc_percentage") }
            )
        }
        composable("tool_calc_unit_converter") {
            UnitConverterScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("calc_unit_converter"),
                onToggleFavorite = { toggleFav("calc_unit_converter") }
            )
        }
        composable("tool_calc_currency") {
            CurrencyConverterScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("calc_currency"),
                onToggleFavorite = { toggleFav("calc_currency") }
            )
        }
        composable("tool_calc_discount_tip") {
            DiscountTipCalculatorScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("calc_discount_tip"),
                onToggleFavorite = { toggleFav("calc_discount_tip") }
            )
        }
        composable("tool_calc_age") {
            AgeCalculatorScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("calc_age"),
                onToggleFavorite = { toggleFav("calc_age") }
            )
        }
        composable("tool_calc_date_diff") {
            DateDiffCalculatorScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("calc_date_diff"),
                onToggleFavorite = { toggleFav("calc_date_diff") }
            )
        }

        // 2. Text Tools
        composable("tool_text_counter") {
            TextCounterScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("text_counter"),
                onToggleFavorite = { toggleFav("text_counter") }
            )
        }
        composable("tool_text_case_converter") {
            CaseConverterScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("text_case_converter"),
                onToggleFavorite = { toggleFav("text_case_converter") }
            )
        }
        composable("tool_text_duplicate_remover") {
            DuplicateRemoverScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("text_duplicate_remover"),
                onToggleFavorite = { toggleFav("text_duplicate_remover") }
            )
        }
        composable("tool_text_reverser") {
            TextReverserScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("text_reverser"),
                onToggleFavorite = { toggleFav("text_reverser") }
            )
        }
        composable("tool_text_find_replace") {
            FindReplaceScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("text_find_replace"),
                onToggleFavorite = { toggleFav("text_find_replace") }
            )
        }
        composable("tool_text_line_sorter") {
            LineSorterScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("text_line_sorter"),
                onToggleFavorite = { toggleFav("text_line_sorter") }
            )
        }
        composable("tool_text_lorem_ipsum") {
            LoremIpsumScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("text_lorem_ipsum"),
                onToggleFavorite = { toggleFav("text_lorem_ipsum") }
            )
        }

        // 3. Image Tools
        composable("tool_image_compressor") {
            ImageCompressorScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("image_compressor"),
                onToggleFavorite = { toggleFav("image_compressor") }
            )
        }
        composable("tool_image_resizer") {
            ImageResizerScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("image_resizer"),
                onToggleFavorite = { toggleFav("image_resizer") }
            )
        }
        composable("tool_image_format_converter") {
            ImageFormatConverterScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("image_format_converter"),
                onToggleFavorite = { toggleFav("image_format_converter") }
            )
        }
        composable("tool_image_cropper") {
            ImageCropperScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("image_cropper"),
                onToggleFavorite = { toggleFav("image_cropper") }
            )
        }
        composable("tool_image_base64") {
            ImageBase64Screen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("image_base64"),
                onToggleFavorite = { toggleFav("image_base64") }
            )
        }

        // 4. Random & Generators
        composable("tool_rand_dice") {
            DiceRollerScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("rand_dice"),
                onToggleFavorite = { toggleFav("rand_dice") }
            )
        }
        composable("tool_rand_coin") {
            CoinFlipScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("rand_coin"),
                onToggleFavorite = { toggleFav("rand_coin") }
            )
        }
        composable("tool_rand_number") {
            RandomNumberScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("rand_number"),
                onToggleFavorite = { toggleFav("rand_number") }
            )
        }
        composable("tool_rand_password") {
            PasswordGeneratorScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("rand_password"),
                onToggleFavorite = { toggleFav("rand_password") }
            )
        }
        composable("tool_rand_name") {
            NameGeneratorScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("rand_name"),
                onToggleFavorite = { toggleFav("rand_name") }
            )
        }
        composable("tool_rand_team") {
            TeamRandomizerScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("rand_team"),
                onToggleFavorite = { toggleFav("rand_team") }
            )
        }
        composable("tool_rand_decision") {
            DecisionSpinnerScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("rand_decision"),
                onToggleFavorite = { toggleFav("rand_decision") }
            )
        }

        // 5. Finance
        composable("tool_fin_loan_emi") {
            LoanEmiScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("fin_loan_emi"),
                onToggleFavorite = { toggleFav("fin_loan_emi") }
            )
        }
        composable("tool_fin_interest") {
            InterestCalculatorScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("fin_interest"),
                onToggleFavorite = { toggleFav("fin_interest") }
            )
        }
        composable("tool_fin_vat_tax") {
            VatTaxScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("fin_vat_tax"),
                onToggleFavorite = { toggleFav("fin_vat_tax") }
            )
        }
        composable("tool_fin_savings") {
            SavingsGoalScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("fin_savings"),
                onToggleFavorite = { toggleFav("fin_savings") }
            )
        }

        // 6. Color
        composable("tool_color_picker") {
            ColorPickerScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("color_picker"),
                onToggleFavorite = { toggleFav("color_picker") }
            )
        }
        composable("tool_color_palette") {
            ColorPaletteScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("color_palette"),
                onToggleFavorite = { toggleFav("color_palette") }
            )
        }
        composable("tool_color_gradient") {
            GradientGeneratorScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("color_gradient"),
                onToggleFavorite = { toggleFav("color_gradient") }
            )
        }

        // 7. Measurement & Sensors
        composable("tool_sensor_compass") {
            CompassScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("sensor_compass"),
                onToggleFavorite = { toggleFav("sensor_compass") }
            )
        }
        composable("tool_sensor_bubble_level") {
            BubbleLevelScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("sensor_bubble_level"),
                onToggleFavorite = { toggleFav("sensor_bubble_level") }
            )
        }
        composable("tool_sensor_sound_meter") {
            SoundMeterScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("sensor_sound_meter"),
                onToggleFavorite = { toggleFav("sensor_sound_meter") }
            )
        }
        composable("tool_sensor_flashlight") {
            FlashlightScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("sensor_flashlight"),
                onToggleFavorite = { toggleFav("sensor_flashlight") }
            )
        }
        composable("tool_sensor_ruler") {
            RulerScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("sensor_ruler"),
                onToggleFavorite = { toggleFav("sensor_ruler") }
            )
        }

        // 8. Timers & Focus
        composable("tool_timer_countdown") {
            CountdownTimerScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("timer_countdown"),
                onToggleFavorite = { toggleFav("timer_countdown") }
            )
        }
        composable("tool_timer_stopwatch") {
            StopwatchScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("timer_stopwatch"),
                onToggleFavorite = { toggleFav("timer_stopwatch") }
            )
        }
        composable("tool_timer_pomodoro") {
            PomodoroScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("timer_pomodoro"),
                onToggleFavorite = { toggleFav("timer_pomodoro") }
            )
        }
        composable("tool_timer_interval") {
            IntervalTrainerScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("timer_interval"),
                onToggleFavorite = { toggleFav("timer_interval") }
            )
        }

        // 9. QR & Utilities
        composable("tool_qr_generator") {
            QrGeneratorScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("qr_generator"),
                onToggleFavorite = { toggleFav("qr_generator") }
            )
        }
        composable("tool_qr_scanner") {
            BarcodeScannerScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("qr_scanner"),
                onToggleFavorite = { toggleFav("qr_scanner") }
            )
        }
        composable("tool_util_price_compare") {
            UnitPriceComparatorScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("util_price_compare"),
                onToggleFavorite = { toggleFav("util_price_compare") }
            )
        }
        composable("tool_util_system_info") {
            SystemInfoScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("util_system_info"),
                onToggleFavorite = { toggleFav("util_system_info") }
            )
        }
        composable("tool_util_hash_generator") {
            HashGeneratorScreen(
                onBack = { navController.popBackStack() },
                isFavorite = favorites.contains("util_hash_generator"),
                onToggleFavorite = { toggleFav("util_hash_generator") }
            )
        }
    }
}
