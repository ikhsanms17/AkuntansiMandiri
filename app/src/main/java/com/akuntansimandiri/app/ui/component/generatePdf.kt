package com.akuntansimandiri.app.ui.component

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.akuntansimandiri.app.data.local.entity.TransactionEntity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate

fun generatePdfAsByteArray(
    context: Context,
    userName: String,
    userEmail: String,
    transactionsAll: List<TransactionEntity>,
    transactionsDaily: List<TransactionEntity>,
    transactionsWeekly: List<TransactionEntity>,
    transactionsMonthly: List<TransactionEntity>
): ByteArray {
    val outputStream = ByteArrayOutputStream()
    val pdfDocument = PdfDocument()

    val paint = Paint()
    val titlePaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        textSize = 16f
    }

    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    var yPos = 40
    canvas.drawText("LAPORAN KEUANGAN VIA AKUNTANSIMANDIRIAPP", 40f, yPos.toFloat(), titlePaint)
    yPos += 30
    canvas.drawText("Nama: $userName", 40f, yPos.toFloat(), paint)
    yPos += 20
    canvas.drawText("Email: $userEmail", 40f, yPos.toFloat(), paint)
    yPos += 30

    fun drawSection(title: String, items: List<TransactionEntity>) {
        canvas.drawText(title, 40f, yPos.toFloat(), titlePaint)
        yPos += 20
        items.forEach {
            val line = "${it.quantity}x ${it.name} - Rp${it.price} @ ${it.place} (${it.date})"
            canvas.drawText(line, 40f, yPos.toFloat(), paint)
            yPos += 20
        }
        yPos += 20
    }

    drawSection("Semua Laporan Keuangan", transactionsAll)
    drawSection("Laporan Harian (${LocalDate.now()})", transactionsDaily)
    drawSection("Laporan Mingguan", transactionsWeekly)
    drawSection("Laporan Bulanan (${LocalDate.now().monthValue})", transactionsMonthly)

    val totalAll = transactionsAll.sumOf { it.price }
    val totalDaily = transactionsDaily.sumOf { it.price }
    val totalWeekly = transactionsWeekly.sumOf { it.price }
    val totalMonthly = transactionsMonthly.sumOf { it.price }

    canvas.drawText("Total semua pengeluaran: Rp$totalAll", 40f, yPos.toFloat(), paint)
    yPos += 20
    canvas.drawText("Total pengeluaran harian: Rp$totalDaily", 40f, yPos.toFloat(), paint)
    yPos += 20
    canvas.drawText("Total pengeluaran mingguan: Rp$totalWeekly", 40f, yPos.toFloat(), paint)
    yPos += 20
    canvas.drawText("Total pengeluaran bulanan: Rp$totalMonthly", 40f, yPos.toFloat(), paint)

    pdfDocument.finishPage(page)

    pdfDocument.writeTo(outputStream)
    pdfDocument.close()

    return outputStream.toByteArray()
}
