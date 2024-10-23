package com.afrimax.paysimati.main.domain.usecase

import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import com.afrimax.paysimati.main.domain.repository.MainApiRepository
import javax.inject.Inject

class FetchWalletStatementCsvUrlUseCase @Inject constructor(
    private val mainApiRepository: MainApiRepository
) {
    suspend operator fun invoke(timePeriodSelection: Int): GenericResult<String, Errors.Network> {
        return mainApiRepository.fetchWalletStatementCsvUrl(timePeriodOption = timePeriodSelection)
    }
}