package com.akuntansimandiri.app.utils

class ExtractData(text: String) {
    val result: Map<String, String>

    init {
//        val text = convertWordsToNumbers(originalText)

        val jumlahRegex = Regex("membeli (\\d+)\\s?(buah|bungkus|porsi|botol)?", RegexOption.IGNORE_CASE)
        val barangRegex = Regex("\\d+\\s?(buah|bungkus|porsi|botol)?\\s+(.*?)\\s+dengan", RegexOption.IGNORE_CASE)
        val hargaRegex = Regex("harga\\s+([\\d.,]+)", RegexOption.IGNORE_CASE)
        val tempatRegex = Regex("di\\s+([\\w\\s]+?)(\\s|$)", RegexOption.IGNORE_CASE)

        val jumlah = jumlahRegex.find(text)?.groupValues?.getOrNull(1)?.trim() ?: "Tidak ditemukan"
        val barang = barangRegex.find(text)?.groupValues?.getOrNull(2)?.trim() ?: "Tidak ditemukan"
        val harga = hargaRegex.find(text)?.groupValues?.getOrNull(1)
            ?.replace(".", "")
            ?.replace("rp", "", ignoreCase = true)
            ?.trim() ?: "Tidak ditemukan"
        val tempat = tempatRegex.find(text)?.groupValues?.getOrNull(1)?.trim() ?: "Tidak ditemukan"

        result = mapOf(
            "jumlah" to jumlah,
            "barang" to barang,
            "harga" to harga,
            "tempat" to tempat
        )
    }
}