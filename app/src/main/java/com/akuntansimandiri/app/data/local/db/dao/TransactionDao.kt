package com.akuntansimandiri.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.akuntansimandiri.app.data.local.entity.TransactionEntity
import com.akuntansimandiri.app.domain.model.NameSummary
import com.akuntansimandiri.app.domain.model.PlaceSummary

@Dao
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    suspend fun getAllTransactions(userId: Int): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun getTransactionById(id: Int, userId: Int): TransactionEntity?

    @Query("DELETE FROM transactions WHERE userId = :userId")
    suspend fun deleteAll(userId: Int)

    @Query("""
    SELECT * FROM transactions
    WHERE userId = :userId
    AND strftime('%m', date) = strftime('%m', 'now')
    AND strftime('%Y', date) = strftime('%Y', 'now')
    ORDER BY date DESC
""")
    suspend fun getTransactionsThisMonth(userId: Int): List<TransactionEntity>

    @Query("""
    SELECT * FROM transactions
    WHERE userId = :userId
    AND date = date('now')
    ORDER BY date DESC
""")
    suspend fun getTransactionsToday(userId: Int): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC LIMIT 5")
    suspend fun getLast5(userId: Int): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE userId = :userId")
    suspend fun getAll(userId: Int): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND date = :date")
    suspend fun getByDate(date: String, userId: Int): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND date >= :date")
    suspend fun getAfterDate(date: String, userId: Int): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND strftime('%m', date) = :month")
    suspend fun getByMonthInternal(month: String, userId: Int): List<TransactionEntity>

    suspend fun getByMonth(month: String, userId: Int): List<TransactionEntity> {
        val monthStr = month.padStart(2, '0')
        return getByMonthInternal(monthStr, userId)
    }

    // Total pengeluaran
    @Query("SELECT SUM(price) FROM transactions WHERE userId = :userId")
    suspend fun getTotal(userId: Int): Int?

    @Query("SELECT SUM(price) FROM transactions WHERE userId = :userId AND date = :date")
    suspend fun getTotalByDate(date: String, userId: Int): Int?

    @Query("SELECT SUM(price) FROM transactions WHERE userId = :userId AND date BETWEEN :start AND :end")
    suspend fun getTotalBetween(start: String, end: String, userId: Int): Int?

    @Query("SELECT SUM(price) FROM transactions WHERE userId = :userId AND strftime('%m', date) = :month")
    suspend fun getTotalByMonth(month: String, userId: Int): Int?

    // Summary by name
    @Query("SELECT name, SUM(price) as total FROM transactions WHERE userId = :userId GROUP BY name")
    suspend fun getSummaryByName(userId: Int): List<NameSummary>

    @Query("SELECT name, SUM(price) as total FROM transactions WHERE userId = :userId AND date = :date GROUP BY name")
    suspend fun getSummaryByNameByDate(date: String, userId: Int): List<NameSummary>

    @Query("SELECT name, SUM(price) as total FROM transactions WHERE userId = :userId AND date BETWEEN :start AND :end GROUP BY name")
    suspend fun getSummaryByNameBetween(start: String, end: String, userId: Int): List<NameSummary>

    @Query("SELECT name, SUM(price) as total FROM transactions WHERE userId = :userId AND strftime('%m', date) = :month GROUP BY name")
    suspend fun getSummaryByNameByMonth(month: String, userId: Int): List<NameSummary>

    // Summary by place
    @Query("SELECT place, SUM(price) as total FROM transactions WHERE userId = :userId GROUP BY place")
    suspend fun getSummaryByPlace(userId: Int): List<PlaceSummary>

    @Query("SELECT place, SUM(price) as total FROM transactions WHERE userId = :userId AND date = :date GROUP BY place")
    suspend fun getSummaryByPlaceByDate(date: String, userId: Int): List<PlaceSummary>

    @Query("SELECT place, SUM(price) as total FROM transactions WHERE userId = :userId AND date BETWEEN :start AND :end GROUP BY place")
    suspend fun getSummaryByPlaceBetween(start: String, end: String, userId: Int): List<PlaceSummary>

    @Query("SELECT place, SUM(price) as total FROM transactions WHERE userId = :userId AND strftime('%m', date) = :month GROUP BY place")
    suspend fun getSummaryByPlaceByMonth(month: String, userId: Int): List<PlaceSummary>
}
