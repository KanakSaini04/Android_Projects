package com.example.liquidcalc.engine

object CalculatorEngine {

    fun evaluate(expression: String): String {
        return try {
            val result = parseExpression(expression.trim())
            formatResult(result)
        } catch (e: Exception) {
            "Error"
        }
    }

    private fun formatResult(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "Error"
        return if (value == value.toLong().toDouble() && !value.isInfinite()) {
            value.toLong().toString()
        } else {
            "%.10f".format(value).trimEnd('0').trimEnd('.')
        }
    }

    private fun parseExpression(expr: String): Double {
        val tokens = tokenize(expr)
        val pos = intArrayOf(0)
        return parseAddSub(tokens, pos)
    }

    private fun parseAddSub(tokens: List<String>, pos: IntArray): Double {
        var left = parseMulDiv(tokens, pos)
        while (pos[0] < tokens.size && (tokens[pos[0]] == "+" || tokens[pos[0]] == "-")) {
            val op = tokens[pos[0]++]
            val right = parseMulDiv(tokens, pos)
            left = if (op == "+") left + right else left - right
        }
        return left
    }

    private fun parseMulDiv(tokens: List<String>, pos: IntArray): Double {
        var left = parseUnary(tokens, pos)
        while (pos[0] < tokens.size && (tokens[pos[0]] == "*" || tokens[pos[0]] == "/" || tokens[pos[0]] == "%")) {
            val op = tokens[pos[0]++]
            val right = parseUnary(tokens, pos)
            left = when (op) {
                "*" -> left * right
                "/" -> if (right == 0.0) Double.NaN else left / right
                "%" -> left % right
                else -> left
            }
        }
        return left
    }

    private fun parseUnary(tokens: List<String>, pos: IntArray): Double {
        if (pos[0] < tokens.size && tokens[pos[0]] == "-") {
            pos[0]++
            return -parsePrimary(tokens, pos)
        }
        if (pos[0] < tokens.size && tokens[pos[0]] == "+") {
            pos[0]++
        }
        return parsePrimary(tokens, pos)
    }

    private fun parsePrimary(tokens: List<String>, pos: IntArray): Double {
        if (pos[0] >= tokens.size) throw IllegalArgumentException("Unexpected end")
        val token = tokens[pos[0]]
        return if (token == "(") {
            pos[0]++
            val result = parseAddSub(tokens, pos)
            if (pos[0] < tokens.size && tokens[pos[0]] == ")") pos[0]++
            result
        } else {
            pos[0]++
            token.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid token: $token")
        }
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expr.length) {
            when {
                expr[i].isWhitespace() -> i++
                expr[i].isDigit() || expr[i] == '.' -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                        sb.append(expr[i++])
                    }
                    tokens.add(sb.toString())
                }
                expr[i] in listOf('+', '-', '*', '/', '%', '(', ')') -> {
                    tokens.add(expr[i].toString())
                    i++
                }
                else -> i++
            }
        }
        return tokens
    }
}