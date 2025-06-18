package com.akuntansimandiri.app.domain.model

import java.time.LocalDate

sealed class FilterType {
    object All : FilterType()
    data class Daily(val date: LocalDate) : FilterType()
    object Weekly : FilterType()
    data class Monthly(val month: String) : FilterType()
}

