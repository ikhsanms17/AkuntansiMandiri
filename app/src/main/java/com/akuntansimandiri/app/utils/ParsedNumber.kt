package com.akuntansimandiri.app.utils

fun convertWordsToNumbers(text: String): String {
    val units = mapOf(
        "nol" to 0, "satu" to 1, "dua" to 2, "tiga" to 3, "empat" to 4, "lima" to 5,
        "enam" to 6, "tujuh" to 7, "delapan" to 8, "sembilan" to 9
    )
    val teens = mapOf(
        "sepuluh" to 10, "sebelas" to 11, "dua belas" to 12, "tiga belas" to 13,
        "empat belas" to 14, "lima belas" to 15, "enam belas" to 16,
        "tujuh belas" to 17, "delapan belas" to 18, "sembilan belas" to 19
    )
    val tens = mapOf(
        "dua puluh" to 20, "tiga puluh" to 30, "empat puluh" to 40,
        "lima puluh" to 50, "enam puluh" to 60, "tujuh puluh" to 70,
        "delapan puluh" to 80, "sembilan puluh" to 90
    )
    val multipliers = mapOf("ratus" to 100, "ribu" to 1000)

    fun parseWordsToNumber(words: List<String>): Pair<Int?, Int> {
        var result = 0
        var temp = 0
        var i = 0
        var matchedWords = 0

        while (i < words.size) {
            val word = words[i]
            val next = if (i + 1 < words.size) "${word} ${words[i + 1]}" else null

            when {
                next != null && teens.containsKey(next) -> {
                    result += teens[next]!!
                    i += 2
                    matchedWords += 2
                }
                next != null && tens.containsKey(next) -> {
                    temp += tens[next]!!
                    i += 2
                    matchedWords += 2
                }
                multipliers.containsKey(word) -> {
                    val multiplier = multipliers[word]!!
                    temp = if (temp == 0) 1 else temp
                    result += temp * multiplier
                    temp = 0
                    i++
                    matchedWords++
                }
                units.containsKey(word) -> {
                    temp += units[word]!!
                    i++
                    matchedWords++
                }
                else -> break
            }
        }

        val total = result + temp
        return if (matchedWords > 0) Pair(total, matchedWords) else Pair(null, 0)
    }

    val cleanedText = text.replace(Regex("(?i)rp\\s?"), "") // Hapus Rp tanpa menghapus nilai

    val words = cleanedText.split("\\s+".toRegex()).toMutableList()
    val resultWords = mutableListOf<String>()

    var i = 0
    while (i < words.size) {
        val subList = words.subList(i, minOf(i + 4, words.size))
        val (number, usedWords) = parseWordsToNumber(subList)

        if (number != null) {
            resultWords.add(number.toString())
            i += usedWords
        } else {
            resultWords.add(words[i])
            i++
        }
    }

    return resultWords.joinToString(" ")
}
