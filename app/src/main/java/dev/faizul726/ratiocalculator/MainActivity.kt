package dev.faizul726.ratiocalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.faizul726.ratiocalculator.Numbers.numA
import dev.faizul726.ratiocalculator.Numbers.numB
import dev.faizul726.ratiocalculator.Numbers.numC
import dev.faizul726.ratiocalculator.Numbers.numD

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold(Modifier.fillMaxSize()) { App(Modifier.padding(it)) }
            }
        }
    }
}

private object Numbers {
    var numA = mutableStateOf("")
    val numB = mutableStateOf("")
    val numC = mutableStateOf("")
    val numD = mutableStateOf("")

    fun calculateLHS() = numA.value.isBlank().xor(numB.value.isBlank()) && (numC.value.isNotBlank() && numD.value.isNotBlank())
    fun calculateRHS() = (numA.value.isNotBlank() && numB.value.isNotBlank()) && numC.value.isBlank().xor(numD.value.isBlank())
    fun areAllFilled() = numA.value.isNotBlank() && numB.value.isNotBlank() && numC.value.isNotBlank() && numD.value.isNotBlank()
    fun calculateOneSide() = (numA.value.isNotBlank() && numB.value.isNotBlank()).xor(numC.value.isNotBlank() && numD.value.isNotBlank())
}

@Composable
private fun App(modifier: Modifier = Modifier) {
    var result by remember { mutableStateOf("") }

    var isNumAEnabled by remember { mutableStateOf(true) }
    var isNumBEnabled by remember { mutableStateOf(true) }
    var isNumCEnabled by remember { mutableStateOf(true) }
    var isNumDEnabled by remember { mutableStateOf(true) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
        ) {
            NumberField(Modifier.weight(1f), "A", numA, isNumAEnabled)
            Text(":")
            NumberField(Modifier.weight(1f), "B", numB, isNumBEnabled)
            Text("=")
            NumberField(Modifier.weight(1f), "C", numC, isNumCEnabled)
            Text(":")
            NumberField(Modifier.weight(1f), "D", numD, isNumDEnabled, ImeAction.Done)
        }

        Button(onClick = {
            result = try {
                val a = numA.value.toLongOrNull()
                val b = numB.value.toLongOrNull()
                val c = numC.value.toLongOrNull()
                val d = numD.value.toLongOrNull()

                when {
                    Numbers.areAllFilled() -> {
                        "$a:$b ${if (a!! * d!! == b!! * c!!) "=" else "≠"} $c:$d"
                    }
                    Numbers.calculateLHS() -> {
                        if (a == null) {
                            calculateRatio(d!!, b!!, c!!).let {
                                numA.value = it.toString()
                                "A = $it"
                            }
                        } else {
                            calculateRatio(c!!, a, d!!).let {
                                numB.value = it.toString()
                                "B = $it"
                            }
                        }
                    }
                    Numbers.calculateRHS() -> {
                        if (c == null) {
                            calculateRatio(b!!, a!!, d!!).let {
                                numC.value = it.toString()
                                "C = $it"
                            }
                        } else {
                            calculateRatio(a!!, b!!, c).let {
                                numD.value = it.toString()
                                "D = $it"
                            }
                        }
                    }
                    Numbers.calculateOneSide() -> {
                        if (a == null && b == null) {
                            val gcd = calculateGCD(c!!, d!!)
                            numA.value = (c / gcd).toString()
                            numB.value = (d / gcd).toString()

                            """
                                A = ${c / gcd}
                                B = ${d / gcd}
                            """.trimIndent()
                        } else {
                            val gcd = calculateGCD(a!!, b!!)
                            numC.value = (a / gcd).toString()
                            numD.value = (b / gcd).toString()

                            """
                                C = ${a / gcd}
                                D = ${b / gcd}
                            """.trimIndent()
                        }
                    }
                    else -> "Nothing to calculate..."
                }
            } catch (e: Exception) {
                e.message ?: "Unknown error"
                //e.printStackTrace().toString()
            }
        }) {
            Text("Calculate")
        }

        Text(
            text = result,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 12.dp)
        )
    }
}

@Composable
private fun NumberField(modifier: Modifier = Modifier, label: String, num: MutableState<String>, isEnabled: Boolean, imeAction: ImeAction = ImeAction.Next) {
    OutlinedTextField(
        value = num.value,
        onValueChange = { num.value = it },
        label = { Text(label) },
        modifier = modifier,
        enabled = isEnabled,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Decimal,
            imeAction = imeAction
        )
    )
}

private fun calculateRatio(x: Long, y: Long, z: Long): Long {
    return (y * z) / x
}

private fun calculateGCD(a: Long, b: Long): Long {
    var num1 = a
    var num2 = b
    while (num2 != 0L) {
        val temp = num2
        num2 = num1 % num2
        num1 = temp
    }
    return num1
}
