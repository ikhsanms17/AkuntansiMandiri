package com.akuntansimandiri.app.presentation.transaction.voice.component

data class ParsedTransaction(
    val name: String,
    val quantity: Int,
    val price: Int,
    val place: String,
    val date: String
)

fun parseTransactionFromSpeech(text: String): ParsedTransaction {
    // Contoh kalimat: "Beli kopi 2 gelas 10000 rupiah di Starbucks tanggal 14 Juni"
    val lower = text.lowercase()

    val name = Regex("beli ([a-zA-Z\\s]+)").find(lower)?.groups?.get(1)?.value ?: "Tidak diketahui"
    val quantity = Regex("(\\d+) (buah|gelas|bungkus|pcs)").find(lower)?.groups?.get(1)?.value?.toIntOrNull() ?: 1
    val price = Regex("(\\d{3,}) (rupiah|rp)?").find(lower)?.groups?.get(1)?.value?.toIntOrNull() ?: 0
    val place = Regex("di ([a-zA-Z\\s]+)( tanggal|$)").find(lower)?.groups?.get(1)?.value ?: "Tidak diketahui"
    val date = Regex("tanggal ([\\d\\s\\w]+)").find(lower)?.groups?.get(1)?.value ?: "Hari ini"

    return ParsedTransaction(name.trim(), quantity, price, place.trim(), date.trim())
}
