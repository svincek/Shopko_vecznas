package com.example.shopko.utils.general

fun levenshtein(str1: String, str2: String): Int {
    val lenStr1 = str1.length
    val lenStr2 = str2.length
    val d = Array(lenStr1 + 1) { IntArray(lenStr2 + 1) }

    for (i in 0..lenStr1) {
        d[i][0] = i
    }
    for (j in 0..lenStr2) {
        d[0][j] = j
    }

    for (i in 1..lenStr1) {
        for (j in 1..lenStr2) {
            val cost = if (str1[i - 1] == str2[j - 1]) 0 else 1
            d[i][j] = minOf(
                d[i - 1][j] + 1,
                d[i][j - 1] + 1,
                d[i - 1][j - 1] + cost
            )
        }
    }
    return d[lenStr1][lenStr2]
}