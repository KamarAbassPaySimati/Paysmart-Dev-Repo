package com.afrimax.paysimati.main.ui.wallet_statement.wallet_statement

sealed class WalletStatementIntent {
    data object ExportPdfData : WalletStatementIntent()
    data object ExportCsvData : WalletStatementIntent()
    data class SetSelectedOption(val option: Int) : WalletStatementIntent()
}