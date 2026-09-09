package com.zykrave.toolixhub.ui.screens.tools.calculators

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import java.text.DecimalFormat

@Composable
fun StandardCalculatorScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var expression by remember { mutableStateOf("0") }
    var previousResult by remember { mutableStateOf("") }
    var showHistory by remember { mutableStateOf(false) }
    val history = remember { mutableStateListOf<Pair<String, String>>() }

    fun evaluateExpression(expr: String): String {
        return try {
            val sanitized = expr.replace("×", "*").replace("÷", "/")
            val result = simpleEval(sanitized)
            val format = DecimalFormat("#.########")
            format.format(result)
        } catch (e: Exception) {
            "Error"
        }
    }

    fun onButtonClick(label: String) {
        when (label) {
            "AC" -> {
                expression = "0"
                previousResult = ""
            }
            "⌫" -> {
                expression = if (expression.length > 1) expression.dropLast(1) else "0"
            }
            "=" -> {
                val res = evaluateExpression(expression)
                if (res != "Error") {
                    history.add(0, expression to res)
                    previousResult = expression
                    expression = res
                }
            }
            "+/-" -> {
                expression = if (expression.startsWith("-")) expression.substring(1) else "-$expression"
            }
            "%" -> {
                try {
                    val num = expression.toDouble()
                    expression = (num / 100.0).toString()
                } catch (e: Exception) {
                    expression += "%"
                }
            }
            "+", "-", "×", "÷" -> {
                if (expression.isNotEmpty() && (expression.last() == '+' || expression.last() == '-' || expression.last() == '×' || expression.last() == '÷')) {
                    expression = expression.dropLast(1) + label
                } else {
                    expression += label
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

            Spacer(modifier = Modifier.height(16.dp))

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
                            val isOperator = key in listOf("÷", "×", "-", "+", "=")
                            val isSpecial = key in listOf("AC", "+/-", "%", "⌫")

                            val containerColor = when {
                                key == "=" -> MaterialTheme.colorScheme.primary
                                isOperator -> MaterialTheme.colorScheme.primaryContainer
                                isSpecial -> MaterialTheme.colorScheme.surfaceVariant
                                else -> MaterialTheme.colorScheme.surface
                            }

                            val contentColor = when {
                                key == "=" -> MaterialTheme.colorScheme.onPrimary
                                isOperator -> MaterialTheme.colorScheme.onPrimaryContainer
                                else -> MaterialTheme.colorScheme.onSurface
                            }

                            Button(
                                onClick = { onButtonClick(key) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp)
                                    .testTag("calc_key_$key"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = containerColor,
                                    contentColor = contentColor
                                ),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                            ) {
                                if (key == "⌫") {
                                    Icon(imageVector = Icons.Default.Backspace, contentDescription = "Backspace")
                                } else {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = if (isOperator || isSpecial) FontWeight.Bold else FontWeight.Medium
                                        )
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

// Basic recursive descent expression evaluator
private fun simpleEval(expr: String): Double {
    val tokens = mutableListOf<String>()
    var currentNumber = StringBuilder()

    var i = 0
    while (i < expr.length) {
        val c = expr[i]
        if (c.isDigit() || c == '.') {
            currentNumber.append(c)
        } else if (c in listOf('+', '-', '*', '/')) {
            if (currentNumber.isNotEmpty()) {
                tokens.add(currentNumber.toString())
                currentNumber = StringBuilder()
            } else if (c == '-' && (tokens.isEmpty() || tokens.last() in listOf("+", "-", "*", "/"))) {
                currentNumber.append('-')
                i++
                continue
            }
            tokens.add(c.toString())
        }
        i++
    }
    if (currentNumber.isNotEmpty()) {
        tokens.add(currentNumber.toString())
    }

    if (tokens.isEmpty()) return 0.0

    // Multiply and Divide pass
    val postMultTokens = mutableListOf<String>()
    var idx = 0
    while (idx < tokens.size) {
        val t = tokens[idx]
        if (t == "*" || t == "/") {
            val prev = postMultTokens.removeAt(postMultTokens.size - 1).toDouble()
            val next = tokens.getOrNull(idx + 1)?.toDouble() ?: 1.0
            val res = if (t == "*") prev * next else if (next != 0.0) prev / next else Double.NaN
            postMultTokens.add(res.toString())
            idx += 2
        } else {
            postMultTokens.add(t)
            idx++
        }
    }

    // Add and Subtract pass
    var total = postMultTokens.getOrNull(0)?.toDoubleOrNull() ?: 0.0
    var opIdx = 1
    while (opIdx < postMultTokens.size) {
        val op = postMultTokens[opIdx]
        val num = postMultTokens.getOrNull(opIdx + 1)?.toDoubleOrNull() ?: 0.0
        if (op == "+") total += num
        else if (op == "-") total -= num
        opIdx += 2
    }

    return total
}
