package com.zykrave.toolixhub.ui.screens.tools.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixFilterChip
import com.zykrave.toolixhub.ui.components.ToolixKeypadButton
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import com.zykrave.toolixhub.ui.theme.HardShadowColor
import com.zykrave.toolixhub.ui.theme.ToolixCyan
import com.zykrave.toolixhub.ui.theme.ToolixEmerald
import com.zykrave.toolixhub.ui.theme.ToolixRose
import java.text.DecimalFormat
import kotlin.math.*

@Composable
fun StandardCalculatorScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var expression by remember { mutableStateOf("0") }
    var previousResult by remember { mutableStateOf("") }
    var showHistory by remember { mutableStateOf(false) }
    var isScientificExpanded by remember { mutableStateOf(false) }
    var isDegrees by remember { mutableStateOf(true) }
    val history = remember { mutableStateListOf<Pair<String, String>>() }

    fun evaluateExpression(expr: String): String {
        return try {
            val sanitized = expr.replace("×", "*").replace("÷", "/").replace("π", "pi")
            val result = simpleEval(sanitized, isDegrees = isDegrees)
            val format = DecimalFormat("#.########")
            format.format(result)
        } catch (e: Exception) {
            "Error"
        }
    }

    fun onButtonClick(label: String) {
        when {
            label == "AC" -> {
                expression = "0"
                previousResult = ""
            }
            label == "⌫" -> {
                expression = if (expression.length > 1) expression.dropLast(1) else "0"
            }
            label == "=" -> {
                val res = evaluateExpression(expression)
                if (res != "Error") {
                    history.add(0, expression to res)
                    previousResult = expression
                    expression = res
                }
            }
            label == "+/-" -> {
                expression = if (expression.startsWith("-")) expression.substring(1) else "-$expression"
            }
            label == "%" -> {
                try {
                    val num = expression.toDouble()
                    expression = (num / 100.0).toString()
                } catch (e: Exception) {
                    expression += "%"
                }
            }
            label in listOf("+", "-", "×", "÷", "^") -> {
                if (expression.isNotEmpty() && expression.last() in listOf('+', '-', '×', '÷', '^')) {
                    expression = expression.dropLast(1) + label
                } else {
                    expression += label
                }
            }
            label.endsWith("(") || label == "π" || label == "e" -> {
                val lastChar = expression.lastOrNull()
                val needsMultiply = lastChar != null && (lastChar.isDigit() || lastChar == ')' || lastChar == '%')
                val prefix = if (needsMultiply) "×" else ""

                if (expression == "0") {
                    expression = label
                } else {
                    expression += prefix + label
                }
            }
            else -> {
                if (expression == "0" && label != ".") {
                    expression = label
                } else {
                    expression += label
                }
            }
        }
    }

    ToolixToolScaffold(
        title = "Standard Calculator",
        onBack = onBack,
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        actions = {
            IconButton(
                onClick = { showHistory = !showHistory },
                modifier = Modifier.testTag("calculator_history_button")
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Calculation History",
                    tint = if (showHistory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (showHistory) {
                ToolixCard(modifier = Modifier.weight(1f).padding(bottom = 8.dp)) {
                    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "HISTORY",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            if (history.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = { history.clear() },
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Clear", fontSize = 11.sp)
                                }
                            }
                        }
                        if (history.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No calculations yet", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(history) { (calcExpr, res) ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp)
                                    ) {
                                        Text(calcExpr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("= $res", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Display Area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    if (previousResult.isNotEmpty()) {
                        Text(
                            text = previousResult,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = expression,
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.End,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Scientific Toggle Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = if (isScientificExpanded) Arrangement.SpaceBetween else Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isScientificExpanded) {
                    ToolixFilterChip(
                        selected = true,
                        onClick = { isDegrees = !isDegrees },
                        label = if (isDegrees) "DEG" else "RAD",
                        modifier = Modifier.testTag("calculator_deg_rad_toggle")
                    )
                }

                ToolixFilterChip(
                    selected = isScientificExpanded,
                    onClick = { isScientificExpanded = !isScientificExpanded },
                    label = if (isScientificExpanded) "Scientific ▲" else "Scientific ▼",
                    modifier = Modifier.testTag("calculator_scientific_toggle")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Scientific Keypad Grid
            if (isScientificExpanded) {
                val scientificButtons = listOf(
                    listOf("sin(", "cos(", "tan(", "^"),
                    listOf("log(", "ln(", "sqrt(", "("),
                    listOf("π", "e", ")", "1/(")
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    scientificButtons.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { key ->
                                ToolixKeypadButton(
                                    text = key,
                                    onClick = { onButtonClick(key) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("calc_key_$key"),
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Keypad Grid
            val buttons = listOf(
                listOf("AC", "+/-", "%", "÷"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", "+"),
                listOf("0", ".", "⌫", "=")
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                buttons.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        row.forEach { key ->
                            val isOperator = key in listOf("÷", "×", "-", "+")
                            val isEquals = key == "="
                            val isSpecial = key in listOf("AC", "+/-", "%", "⌫")

                            val containerColor = when {
                                isEquals -> ToolixEmerald
                                isOperator -> ToolixCyan
                                isSpecial -> ToolixRose
                                else -> MaterialTheme.colorScheme.surface
                            }

                            val contentColor = when {
                                isEquals || isOperator || isSpecial -> HardShadowColor
                                else -> MaterialTheme.colorScheme.onSurface
                            }

                            val icon = if (key == "⌫") Icons.Default.Backspace else null

                            ToolixKeypadButton(
                                text = key,
                                onClick = { onButtonClick(key) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp)
                                    .testTag("calc_key_$key"),
                                containerColor = containerColor,
                                contentColor = contentColor,
                                icon = icon
                            )
                        }
                    }
                }
            }
        }
    }
}

// Recursive descent expression evaluator
private fun simpleEval(expr: String, isDegrees: Boolean = true): Double {
    class Parser(private val str: String) {
        private var pos = -1
        private var ch = -1

        private fun nextChar() {
            pos++
            ch = if (pos < str.length) str[pos].code else -1
        }

        private fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code || ch == '\t'.code || ch == '\r'.code || ch == '\n'.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val v = parseExpression()
            while (ch == ' '.code || ch == '\t'.code || ch == '\r'.code || ch == '\n'.code) nextChar()
            if (pos < str.length) throw IllegalArgumentException("Unexpected character: " + ch.toChar())
            return v
        }

        private fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+'.code) -> x += parseTerm()
                    eat('-'.code) -> x -= parseTerm()
                    else -> return x
                }
            }
        }

        private fun parseTerm(): Double {
            var x = parseUnary()
            while (true) {
                when {
                    eat('*'.code) -> x *= parseUnary()
                    eat('/'.code) -> x /= parseUnary()
                    else -> return x
                }
            }
        }

        private fun parseUnary(): Double {
            if (eat('+'.code)) return parseUnary()
            if (eat('-'.code)) return -parseUnary()
            return parseExponent()
        }

        private fun parseExponent(): Double {
            var x = parsePrimary()
            if (eat('^'.code)) {
                x = x.pow(parseUnary())
            }
            return x
        }

        private fun parsePrimary(): Double {
            while (ch == ' '.code || ch == '\t'.code || ch == '\r'.code || ch == '\n'.code) nextChar()

            val startPos = pos
            if (eat('('.code)) {
                val x = parseExpression()
                if (!eat(')'.code)) throw IllegalArgumentException("Missing closing parenthesis")
                return x
            }

            if ((ch in '0'.code..'9'.code) || ch == '.'.code) {
                while ((ch in '0'.code..'9'.code) || ch == '.'.code) nextChar()
                return str.substring(startPos, pos).toDouble()
            }

            if ((ch in 'a'.code..'z'.code) || (ch in 'A'.code..'Z'.code)) {
                while ((ch in 'a'.code..'z'.code) || (ch in 'A'.code..'Z'.code) || (ch in '0'.code..'9'.code)) nextChar()
                val name = str.substring(startPos, pos).lowercase()
                return when (name) {
                    "pi" -> PI
                    "e" -> E
                    "sin", "cos", "tan", "log", "ln", "sqrt" -> {
                        if (!eat('('.code)) throw IllegalArgumentException("Expected '(' after function $name")
                        val arg = parseExpression()
                        if (!eat(')'.code)) throw IllegalArgumentException("Missing closing parenthesis for $name")
                        val radArg = if (isDegrees) arg * (PI / 180.0) else arg
                        when (name) {
                            "sin" -> sin(radArg)
                            "cos" -> cos(radArg)
                            "tan" -> tan(radArg)
                            "log" -> log10(arg)
                            "ln" -> ln(arg)
                            "sqrt" -> sqrt(arg)
                            else -> error("Unreachable")
                        }
                    }
                    else -> throw IllegalArgumentException("Unknown identifier: $name")
                }
            }

            throw IllegalArgumentException("Unexpected character: " + if (ch != -1) ch.toChar() else "EOF")
        }
    }

    return Parser(expr).parse()
}
