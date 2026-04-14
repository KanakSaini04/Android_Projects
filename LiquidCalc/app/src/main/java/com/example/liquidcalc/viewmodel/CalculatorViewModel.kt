package com.example.liquidcalc.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.liquidcalc.data.CalcDataStore
import com.example.liquidcalc.data.HistoryEntry
import com.example.liquidcalc.engine.CalculatorEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = CalcDataStore(application)

    // ── Calculator State ─────────────────────────────────────────────────────

    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _display = MutableStateFlow("0")
    val display: StateFlow<String> = _display.asStateFlow()

    private val _justEvaluated = MutableStateFlow(false)

    // ── History ──────────────────────────────────────────────────────────────

    val history = dataStore.historyFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // ── Background ───────────────────────────────────────────────────────────

    val backgroundUri = dataStore.backgroundUriFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    // ── Button Press Handler ─────────────────────────────────────────────────

    fun onButton(key: String) {
        when (key) {
            "AC" -> clear()
            "⌫" -> backspace()
            "=" -> evaluate()
            "+/-" -> toggleSign()
            "%" -> applyPercent()
            "." -> appendDecimal()
            else -> appendCharacter(key)
        }
    }

    private fun clear() {
        _expression.value = ""
        _display.value = "0"
        _justEvaluated.value = false
    }

    private fun backspace() {
        if (_justEvaluated.value) { clear(); return }
        val expr = _expression.value
        if (expr.isNotEmpty()) {
            _expression.value = expr.dropLast(1)
            _display.value = if (_expression.value.isEmpty()) "0" else _expression.value
        }
    }

    private fun appendCharacter(key: String) {
        val isOperator = key in listOf("+", "-", "×", "÷")
        if (_justEvaluated.value) {
            if (isOperator) {
                _expression.value = _display.value + operatorToSymbol(key)
                _justEvaluated.value = false
                _display.value = _expression.value
                return
            } else {
                _expression.value = ""
                _justEvaluated.value = false
            }
        }

        val expr = _expression.value

        if (isOperator && expr.isNotEmpty() && isOperatorChar(expr.last())) {
            _expression.value = expr.dropLast(1) + operatorToSymbol(key)
            _display.value = _expression.value
            return
        }

        if (isOperator && expr.isEmpty() && key != "-") return

        _expression.value = expr + operatorToSymbol(key)
        _display.value = _expression.value
    }

    private fun appendDecimal() {
        if (_justEvaluated.value) { clear() }
        val expr = _expression.value
        val lastOpIndex = expr.indexOfLast { it in listOf('+', '-', '×', '÷') }
        val currentNumber = if (lastOpIndex == -1) expr else expr.substring(lastOpIndex + 1)
        if (!currentNumber.contains('.')) {
            _expression.value = expr + if (currentNumber.isEmpty()) "0." else "."
            _display.value = _expression.value
        }
    }

    private fun toggleSign() {
        val expr = _expression.value
        if (expr.isEmpty()) return
        val lastOpIndex = expr.indexOfLast { it in listOf('+', '×', '÷') }
        if (lastOpIndex == -1) {
            _expression.value = if (expr.startsWith("-")) expr.substring(1) else "-$expr"
        } else {
            val before = expr.substring(0, lastOpIndex + 1)
            val number = expr.substring(lastOpIndex + 1)
            _expression.value = before + if (number.startsWith("-")) number.substring(1) else "-$number"
        }
        _display.value = _expression.value
    }

    private fun applyPercent() {
        val expr = _expression.value
        if (expr.isEmpty()) return
        val result = CalculatorEngine.evaluate("($expr)/100")
        if (result != "Error") {
            _expression.value = result
            _display.value = result
        }
    }

    private fun evaluate() {
        val expr = _expression.value.trim()
        if (expr.isEmpty()) return

        val mathExpr = expr
            .replace("×", "*")
            .replace("÷", "/")

        val result = CalculatorEngine.evaluate(mathExpr)
        if (result != "Error") {
            viewModelScope.launch {
                dataStore.addHistoryEntry(
                    HistoryEntry(expression = expr, result = result)
                )
            }
            _display.value = result
            _expression.value = result
            _justEvaluated.value = true
        } else {
            _display.value = "Error"
            _justEvaluated.value = true
        }
    }

    // ── History Actions ──────────────────────────────────────────────────────

    fun clearHistory() {
        viewModelScope.launch { dataStore.clearHistory() }
    }

    fun restoreFromHistory(entry: HistoryEntry) {
        _expression.value = entry.result
        _display.value = entry.result
        _justEvaluated.value = true
    }

    // ── Background ───────────────────────────────────────────────────────────

    fun saveBackground(uri: Uri?) {
        viewModelScope.launch {
            dataStore.saveBackgroundUri(uri?.toString())
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private fun operatorToSymbol(key: String): String = when (key) {
        "*" -> "×"
        "/" -> "÷"
        else -> key
    }

    private fun isOperatorChar(c: Char): Boolean = c in listOf('+', '-', '×', '÷')
}