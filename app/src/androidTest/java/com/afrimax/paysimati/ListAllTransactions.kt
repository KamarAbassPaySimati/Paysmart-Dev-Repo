package com.afrimax.paysimati

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class ListAllTransactions {
    @When("I click on drop down for transactions")
    fun iClickOnDropDownTransactions() {
        Espresso.onView(withId(R.id.homeActivityTransactionsTExpandButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I should see recent four transactions")
    fun iShouldSeeRecentTransactions() {
        Espresso.onView(withId(R.id.homeActivityTransactionsRecyclerView))
            .check(matches(isDisplayed()))
        Thread.sleep(5000)
    }

    @When("I click on see all link")
    fun iClickOnSeeAll() {
        Espresso.onView(withId(R.id.homeActivityTransactionsSeeAllTV)).perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I should be redirected to transaction history screen")
    fun iShouldBeRedirectedToHistoryScreen() {
        Espresso.onView(withId(R.id.transactionHistoryActivity)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }

    @When("I search {string}")
    fun iSearch(searchValue: String) {
        Espresso.onView(withId(R.id.transactionHistoryActivitySearchET)).perform(
            ViewActions.replaceText(searchValue), ViewActions.closeSoftKeyboard()
        )
        Thread.sleep(5000)
    }

    @Then("I should see the list of afrimax transactions")
    fun iShouldAfrimaxTransactions() {
        Espresso.onView(withId(R.id.transactionHistoryActivityRV)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }

    @Then("I should see the searched transactions")
    fun iShouldSearchedTransactions() {
        Espresso.onView(withId(R.id.transactionHistoryActivityRV)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }

    @Then("I should see list of Transactions of last 90 days")
    fun listOfTransaction() {
        onView(withId(R.id.transactionHistoryActivity)).check(
            matches(
                isDisplayed()
            )
        )
    }

    @When("I click the back button in transaction history screen")
    fun iClickBackButton() {
        onView(withId(R.id.transactionHistoryBackButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @When("I open menu and navigate to Wallet Statement")
    fun openWalletStatement() {
        Espresso.onView(ViewMatchers.withId(R.id.homeActivityMenuIcon)).perform(ViewActions.click())
        Thread.sleep(3000)
        Espresso.onView(ViewMatchers.withId(R.id.walletStatement))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(3000)

    }

    @When("I click on export Wallet statement")
    fun iClickExportWalletStatement() {
        onView(withId(R.id.exportWalletStatement))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @When("I select {string} as time period for downloading wallet statement")
    fun selectTimePeriod(timePeriod: String) {
        when (timePeriod) {
            "Today" -> {
                Espresso.onView(withId(R.id.timePeriodOption1))
                    .perform(ViewActions.click())
                Thread.sleep(2000)
            }
            "Yesterday" -> {
                Espresso.onView(withId(R.id.timePeriodOption2))
                    .perform(ViewActions.click())
                Thread.sleep(2000)
            }
            "Last 7 Days" -> {
                Espresso.onView(withId(R.id.timePeriodOption3))
                    .perform(ViewActions.click())
                Thread.sleep(2000)
            }
            "This Month" -> {
                Espresso.onView(withId(R.id.timePeriodOption4))
                    .perform(ViewActions.click())
                Thread.sleep(2000)
            }
            "Last Month" -> {
                Espresso.onView(withId(R.id.timePeriodOption5))
                    .perform(ViewActions.click())
                Thread.sleep(2000)
            }
            "Last 60 Days" -> {
                Espresso.onView(withId(R.id.timePeriodOption6))
                    .perform(ViewActions.click())
                Thread.sleep(2000)
            }
            "Last 90 Days" -> {
                Espresso.onView(withId(R.id.timePeriodOption7))
                    .perform(ViewActions.click())
                Thread.sleep(2000)
            }
        }
    }

    @Then("I should see a pop-up asking type of file to be downloaded")
    fun popupForTypeOfFile() {
        Espresso.onView(withId(R.id.typesOfFile))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @When("I select {string} as file type")
    fun selectTypeOfFile(fileType: String) {
        when (fileType) {
            "PDF" -> {
                Espresso.onView(withId(R.id.typeOfFileOption1))
                    .perform(ViewActions.click())
                Thread.sleep(2000)
            }

            "CSV" -> {
                Espresso.onView(withId(R.id.typeOfFileOption2))
                    .perform(ViewActions.click())
                Thread.sleep(2000)
            }
        }

    }

    @Then("I should read a message stating {string} for commission settlement date")
    fun iShouldReadMessageStatingTimePeriodSelectedSuccessfully() {
        Espresso.onView(withId(R.id.balanceActivityCommissionLastUpdatedTV)).check(
            matches(
                isDisplayed()
            )
        )
    }

    @Then("I should read a message stating Exported Successfully for downloading wallet statement")
    fun iShouldReadMessageStatngExportedSuccessfully() {
        Espresso.onView(withId(R.id.exportSuccessfullMessage)).check(
            matches(
                isDisplayed()
            )
        )
    }

}