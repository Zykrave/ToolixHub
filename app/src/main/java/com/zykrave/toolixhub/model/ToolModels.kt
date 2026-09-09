package com.zykrave.toolixhub.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Gradient
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.ui.graphics.vector.ImageVector

enum class ToolCategory(val title: String, val description: String) {
    CALCULATORS("Calculators & Converters", "Math, dates, units, and rates"),
    TEXT("Text Tools", "Formatting, counting, and transformations"),
    IMAGE("Image Tools", "Compress, resize, crop, and convert"),
    RANDOM("Random & Generators", "Dice, coin, numbers, and decisions"),
    FINANCE("Finance Calculators", "Loans, interest, tax, and savings"),
    COLOR("Color Tools", "Palette, picker, and code generator"),
    MEASUREMENT("Measurement & Sensors", "Compass, level, sound, and ruler"),
    TIMERS("Timers & Focus", "Stopwatch, countdown, and intervals"),
    QR_UTILITY("QR & Utility Tools", "QR codes, torch, and comparator")
}

data class ToolItem(
    val id: String,
    val title: String,
    val description: String,
    val category: ToolCategory,
    val icon: ImageVector,
    val route: String
)

object ToolsRegistry {
    val allTools: List<ToolItem> = listOf(
        // 1. Calculators & Converters
        ToolItem(
            id = "calc_standard",
            title = "Standard Calculator",
            description = "Clean, fast arithmetic with expression history",
            category = ToolCategory.CALCULATORS,
            icon = Icons.Default.Calculate,
            route = "tool_calc_standard"
        ),
        ToolItem(
            id = "calc_percentage",
            title = "Percentage Calculator",
            description = "Calculate percentage of, increase/decrease, and ratios",
            category = ToolCategory.CALCULATORS,
            icon = Icons.Default.Percent,
            route = "tool_calc_percentage"
        ),
        ToolItem(
            id = "calc_unit_converter",
            title = "Unit Converter",
            description = "Convert length, weight, volume, temp, area, and speed",
            category = ToolCategory.CALCULATORS,
            icon = Icons.Default.Scale,
            route = "tool_calc_unit_converter"
        ),
        ToolItem(
            id = "calc_currency",
            title = "Currency Converter",
            description = "Offline currency converter with custom exchange rates",
            category = ToolCategory.CALCULATORS,
            icon = Icons.Default.CurrencyExchange,
            route = "tool_calc_currency"
        ),
        ToolItem(
            id = "calc_discount_tip",
            title = "Discount & Tip Calculator",
            description = "Compute sale price, sales tax, tip, and bill split",
            category = ToolCategory.CALCULATORS,
            icon = Icons.Default.Discount,
            route = "tool_calc_discount_tip"
        ),
        ToolItem(
            id = "calc_age",
            title = "Age Calculator",
            description = "Exact age in years, months, days, and birthday countdown",
            category = ToolCategory.CALCULATORS,
            icon = Icons.Default.DateRange,
            route = "tool_calc_age"
        ),
        ToolItem(
            id = "calc_date_diff",
            title = "Date Difference",
            description = "Span between two dates in days, weeks, and months",
            category = ToolCategory.CALCULATORS,
            icon = Icons.Default.DateRange,
            route = "tool_calc_date_diff"
        ),

        // 2. Text Tools
        ToolItem(
            id = "text_counter",
            title = "Word & Char Counter",
            description = "Count words, characters, sentences, and reading time",
            category = ToolCategory.TEXT,
            icon = Icons.Default.TextFields,
            route = "tool_text_counter"
        ),
        ToolItem(
            id = "text_case_converter",
            title = "Case Converter",
            description = "UPPER, lower, Title Case, camelCase, snake_case",
            category = ToolCategory.TEXT,
            icon = Icons.Default.Transform,
            route = "tool_text_case_converter"
        ),
        ToolItem(
            id = "text_duplicate_remover",
            title = "Duplicate Line Remover",
            description = "Filter repeated lines with case and trim options",
            category = ToolCategory.TEXT,
            icon = Icons.Default.ContentCut,
            route = "tool_text_duplicate_remover"
        ),
        ToolItem(
            id = "text_reverser",
            title = "Text Reverser",
            description = "Reverse characters, words, or individual lines",
            category = ToolCategory.TEXT,
            icon = Icons.Default.SwapHoriz,
            route = "tool_text_reverser"
        ),
        ToolItem(
            id = "text_find_replace",
            title = "Find and Replace",
            description = "Search text with case sensitivity and match count",
            category = ToolCategory.TEXT,
            icon = Icons.Default.TextFields,
            route = "tool_text_find_replace"
        ),
        ToolItem(
            id = "text_line_sorter",
            title = "Line Sorter",
            description = "Sort lines A-Z, reverse, by length, or shuffle",
            category = ToolCategory.TEXT,
            icon = Icons.Default.SortByAlpha,
            route = "tool_text_line_sorter"
        ),
        ToolItem(
            id = "text_lorem_ipsum",
            title = "Lorem Ipsum Generator",
            description = "Generate placeholder paragraphs, sentences, and words",
            category = ToolCategory.TEXT,
            icon = Icons.Default.FormatQuote,
            route = "tool_text_lorem_ipsum"
        ),

        // 3. Image Tools
        ToolItem(
            id = "image_compressor",
            title = "Image Compressor",
            description = "Adjust compression quality with instant file size comparison",
            category = ToolCategory.IMAGE,
            icon = Icons.Default.Compress,
            route = "tool_image_compressor"
        ),
        ToolItem(
            id = "image_resizer",
            title = "Image Resizer",
            description = "Scale by target pixels or percentage slider",
            category = ToolCategory.IMAGE,
            icon = Icons.Default.LinearScale,
            route = "tool_image_resizer"
        ),
        ToolItem(
            id = "image_format_converter",
            title = "Image Format Converter",
            description = "Convert images between JPEG, PNG, and WebP",
            category = ToolCategory.IMAGE,
            icon = Icons.Default.Image,
            route = "tool_image_format_converter"
        ),
        ToolItem(
            id = "image_cropper",
            title = "Basic Image Cropper",
            description = "Crop with 1:1, 4:3, 16:9, or free aspect ratio",
            category = ToolCategory.IMAGE,
            icon = Icons.Default.ContentCut,
            route = "tool_image_cropper"
        ),
        ToolItem(
            id = "image_base64",
            title = "Base64 Image Converter",
            description = "Convert image to Base64 string and decode Base64 to image",
            category = ToolCategory.IMAGE,
            icon = Icons.Default.Transform,
            route = "tool_image_base64"
        ),

        // 4. Random & Generator Tools
        ToolItem(
            id = "rand_dice",
            title = "Dice Roller",
            description = "Roll multiple dice (D4, D6, D8, D10, D12, D20, D100)",
            category = ToolCategory.RANDOM,
            icon = Icons.Default.Casino,
            route = "tool_rand_dice"
        ),
        ToolItem(
            id = "rand_coin",
            title = "Coin Flip",
            description = "Heads or Tails with streak tracking and statistics",
            category = ToolCategory.RANDOM,
            icon = Icons.Default.PieChart,
            route = "tool_rand_coin"
        ),
        ToolItem(
            id = "rand_number",
            title = "Random Number Generator",
            description = "Generate numbers in custom range with no-duplicates mode",
            category = ToolCategory.RANDOM,
            icon = Icons.Default.Pin,
            route = "tool_rand_number"
        ),
        ToolItem(
            id = "rand_password",
            title = "Password Generator",
            description = "Secure passwords with configurable length and character sets",
            category = ToolCategory.RANDOM,
            icon = Icons.Default.Password,
            route = "tool_rand_password"
        ),
        ToolItem(
            id = "rand_name",
            title = "Name & Username Generator",
            description = "Generate handles and names for tech, fantasy, and more",
            category = ToolCategory.RANDOM,
            icon = Icons.Default.Person,
            route = "tool_rand_name"
        ),
        ToolItem(
            id = "rand_team",
            title = "Team / Group Randomizer",
            description = "Split a list of names into balanced groups or teams",
            category = ToolCategory.RANDOM,
            icon = Icons.Default.Shuffle,
            route = "tool_rand_team"
        ),
        ToolItem(
            id = "rand_decision",
            title = "Yes / No Decision Spinner",
            description = "Make quick decisions with customizable spinning options",
            category = ToolCategory.RANDOM,
            icon = Icons.Default.Casino,
            route = "tool_rand_decision"
        ),

        // 5. Finance Calculators
        ToolItem(
            id = "fin_loan_emi",
            title = "Loan / EMI Calculator",
            description = "Calculate monthly EMI, interest breakdown, and total cost",
            category = ToolCategory.FINANCE,
            icon = Icons.Default.Receipt,
            route = "tool_fin_loan_emi"
        ),
        ToolItem(
            id = "fin_interest",
            title = "Interest Calculator",
            description = "Compare Simple vs Compound interest with compounding periods",
            category = ToolCategory.FINANCE,
            icon = Icons.Default.MonetizationOn,
            route = "tool_fin_interest"
        ),
        ToolItem(
            id = "fin_vat_tax",
            title = "VAT / Tax Calculator",
            description = "Add or extract VAT/Sales tax from net or gross amounts",
            category = ToolCategory.FINANCE,
            icon = Icons.Default.Receipt,
            route = "tool_fin_vat_tax"
        ),
        ToolItem(
            id = "fin_savings",
            title = "Savings Goal Calculator",
            description = "Time and contributions needed to reach your target savings",
            category = ToolCategory.FINANCE,
            icon = Icons.Default.Savings,
            route = "tool_fin_savings"
        ),

        // 6. Color Tools
        ToolItem(
            id = "color_picker",
            title = "Color Picker",
            description = "Interactive color mixer with HEX, RGB, and HSL outputs",
            category = ToolCategory.COLOR,
            icon = Icons.Default.ColorLens,
            route = "tool_color_picker"
        ),
        ToolItem(
            id = "color_palette",
            title = "Palette Generator",
            description = "Generate harmonious palettes (analogous, triadic, complementary)",
            category = ToolCategory.COLOR,
            icon = Icons.Default.FormatColorFill,
            route = "tool_color_palette"
        ),
        ToolItem(
            id = "color_gradient",
            title = "Gradient Generator",
            description = "Design multi-stop gradients with copyable CSS and Compose code",
            category = ToolCategory.COLOR,
            icon = Icons.Default.Gradient,
            route = "tool_color_gradient"
        ),

        // 7. Measurement & Sensor Tools
        ToolItem(
            id = "sensor_compass",
            title = "Digital Compass",
            description = "Real-time azimuth degrees and cardinal heading direction",
            category = ToolCategory.MEASUREMENT,
            icon = Icons.Default.Explore,
            route = "tool_sensor_compass"
        ),
        ToolItem(
            id = "sensor_bubble_level",
            title = "Bubble Level",
            description = "Pitch and roll surface leveler with visual spirit bubble",
            category = ToolCategory.MEASUREMENT,
            icon = Icons.Default.LinearScale,
            route = "tool_sensor_bubble_level"
        ),
        ToolItem(
            id = "sensor_sound_meter",
            title = "Sound Level Meter",
            description = "Real-time decibel (dB) noise monitor using microphone",
            category = ToolCategory.MEASUREMENT,
            icon = Icons.Default.Mic,
            route = "tool_sensor_sound_meter"
        ),
        ToolItem(
            id = "sensor_flashlight",
            title = "Flashlight & Torch",
            description = "Quick device LED torch toggle with SOS strobe feature",
            category = ToolCategory.MEASUREMENT,
            icon = Icons.Default.FlashlightOn,
            route = "tool_sensor_flashlight"
        ),
        ToolItem(
            id = "sensor_ruler",
            title = "Screen Ruler",
            description = "Pixel-calibrated on-screen ruler in centimeters and inches",
            category = ToolCategory.MEASUREMENT,
            icon = Icons.Default.Straighten,
            route = "tool_sensor_ruler"
        ),

        // 8. Timers & Focus Tools
        ToolItem(
            id = "timer_countdown",
            title = "Countdown Timer",
            description = "Precise timer with custom label and visual progress ring",
            category = ToolCategory.TIMERS,
            icon = Icons.Default.HourglassBottom,
            route = "tool_timer_countdown"
        ),
        ToolItem(
            id = "timer_stopwatch",
            title = "Stopwatch with Laps",
            description = "Millisecond precision stopwatch with lap split records",
            category = ToolCategory.TIMERS,
            icon = Icons.Default.Timer,
            route = "tool_timer_stopwatch"
        ),
        ToolItem(
            id = "timer_pomodoro",
            title = "Pomodoro Focus Timer",
            description = "Structured work and rest intervals with cycle counter",
            category = ToolCategory.TIMERS,
            icon = Icons.Default.Timer,
            route = "tool_timer_pomodoro"
        ),
        ToolItem(
            id = "timer_interval",
            title = "Interval / HIIT Trainer",
            description = "Custom rounds, work intervals, and recovery rest timer",
            category = ToolCategory.TIMERS,
            icon = Icons.Default.Timer,
            route = "tool_timer_interval"
        ),

        // 9. QR & Utility Tools
        ToolItem(
            id = "qr_generator",
            title = "QR Code Generator",
            description = "Create QR codes for text, website URLs, and Wi-Fi networks",
            category = ToolCategory.QR_UTILITY,
            icon = Icons.Default.QrCode,
            route = "tool_qr_generator"
        ),
        ToolItem(
            id = "qr_scanner",
            title = "QR Code Scanner",
            description = "Scan QR codes and barcodes using the camera",
            category = ToolCategory.QR_UTILITY,
            icon = Icons.Default.QrCodeScanner,
            route = "tool_qr_scanner"
        ),
        ToolItem(
            id = "util_price_compare",
            title = "Unit Price Comparator",
            description = "Compare price per unit/gram between two products to find deals",
            category = ToolCategory.QR_UTILITY,
            icon = Icons.Default.Compare,
            route = "tool_util_price_compare"
        ),
        ToolItem(
            id = "util_system_info",
            title = "System Info Dashboard",
            description = "Hardware, OS version, RAM, storage, battery, and display specs",
            category = ToolCategory.QR_UTILITY,
            icon = Icons.Default.Info,
            route = "tool_util_system_info"
        ),
        ToolItem(
            id = "util_hash_generator",
            title = "Hash & Checksum Generator",
            description = "Generate MD5, SHA-1, SHA-256, SHA-512, and Base64 hashes",
            category = ToolCategory.QR_UTILITY,
            icon = Icons.Default.Fingerprint,
            route = "tool_util_hash_generator"
        )
    )

    fun getToolById(id: String): ToolItem? = allTools.find { it.id == id }
    fun getToolByRoute(route: String): ToolItem? = allTools.find { it.route == route }
}
