package com.afrimax.paysimati.main.ui.wallet_statement.wallet_statement

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.afrimax.paysimati.common.presentation.utils.UiText
import com.afrimax.paysimati.common.presentation.utils.showBottomSheet
import com.afrimax.paysimati.common.presentation.utils.showSnack
import com.afrimax.paysimati.common.presentation.utils.showToast
import com.afrimax.paysimati.databinding.ActivityWalletStatementBinding
import com.afrimax.paysimati.main.ui.wallet_statement.choose_export_type.ExportTypeSheet
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe

@AndroidEntryPoint
class WalletStatementActivity : AppCompatActivity() {

    private lateinit var b: ActivityWalletStatementBinding
    private val vm: WalletStatementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityWalletStatementBinding.inflate(layoutInflater)
        setContentView(b.root)
        vm.observe(this, state = ::observeState, sideEffect = ::observeSideEffect)

        // Perform all the UI setup here
        setUpInsets()
        setUpSystemBars()
        setUpTopBar()
        setUpRadioGroup()
        setUpExportButton()
    }

    private fun setUpRadioGroup() {
        b.walletStatementActivityRG.apply {
            setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    b.walletStatementActivityOption1RB.id -> vm(
                        WalletStatementIntent.SetSelectedOption(
                            option = 1
                        )
                    )

                    b.walletStatementActivityOption2RB.id -> vm(
                        WalletStatementIntent.SetSelectedOption(
                            option = 2
                        )
                    )

                    b.walletStatementActivityOption3RB.id -> vm(
                        WalletStatementIntent.SetSelectedOption(
                            option = 3
                        )
                    )

                    b.walletStatementActivityOption4RB.id -> vm(
                        WalletStatementIntent.SetSelectedOption(
                            option = 4
                        )
                    )

                    b.walletStatementActivityOption5RB.id -> vm(
                        WalletStatementIntent.SetSelectedOption(
                            option = 5
                        )
                    )

                    b.walletStatementActivityOption6RB.id -> vm(
                        WalletStatementIntent.SetSelectedOption(
                            option = 6
                        )
                    )

                    b.walletStatementActivityOption7RB.id -> vm(
                        WalletStatementIntent.SetSelectedOption(
                            option = 7
                        )
                    )
                }
            }
        }
    }

    /** Observe changes in the State using Orbit StateFlow */
    private fun observeState(state: WalletStatementState) {
        modifyRadioButtons(state.selectedOption)
    }

    /** Observe side effects using Orbit StateFlow */
    private fun observeSideEffect(action: WalletStatementSideEffect) {
        when (action) {
            is WalletStatementSideEffect.ShowToast -> this.showToast(action.message)
            is WalletStatementSideEffect.ShowSnack -> this.showSnack(b.root, action.message)
        }
    }

    // ====================================================================
    //                        INITIAL SETUP
    // ====================================================================

    private fun setUpInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(b.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    private fun setUpSystemBars() {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
    }

    private fun setUpTopBar() {
        b.walletStatementActivityBTB.apply {
            onClickLeftIcon {
                onBackPressedDispatcher.onBackPressed()
                null
            }
        }
    }

    private fun setUpExportButton() {
        val sheet = ExportTypeSheet(downloadPdf = {
            showToast(UiText.Dynamic("Download Started"))
            vm(WalletStatementIntent.ExportPdfData)
        }, downloadCsv = {
            null
        })

        b.walletStatementActivityExportButton.apply {
            setOnClickListener {
                supportFragmentManager.showBottomSheet(sheet)
                null
            }
        }
    }

    // ====================================================================
    //                        STATE MODIFICATIONS
    // ====================================================================

    private fun modifyRadioButtons(selectedOption: Int) {
        when (selectedOption) {
            1 -> b.walletStatementActivityOption1RB.isChecked = true
            2 -> b.walletStatementActivityOption2RB.isChecked = true
            3 -> b.walletStatementActivityOption3RB.isChecked = true
            4 -> b.walletStatementActivityOption4RB.isChecked = true
            5 -> b.walletStatementActivityOption5RB.isChecked = true
            6 -> b.walletStatementActivityOption6RB.isChecked = true
            7 -> b.walletStatementActivityOption7RB.isChecked = true
        }
    }

}
