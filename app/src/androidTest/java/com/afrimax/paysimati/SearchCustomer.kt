/*
package ccom.afrimax.paysimati
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class SearchCustomer {
    @When("I click on pay to person")
    fun iClickOnPayPerson(){
        Espresso.onView(withId(R.id.payPersonButton)).perform(ViewActions.click())
    }
    @Then("I should be redirected to pay to person screen")
    fun iShouldBeRedirectedToPayPersonScreen(){
        Espresso.onView(withId(R.id.payPersonScreen)).check(matches(isDisplayed()))
    }
    @When(" I search {string} to pay person")
    fun searchAgent(searchInput: String) {
        Espresso.onView(withId(R.id.selfCashOutSearchActivitySearchET)).perform(
            ViewActions.replaceText(searchInput), ViewActions.closeSoftKeyboard()
        )
        Thread.sleep(5000)
    }
    @When("I should read a message stating {string} for pay person")
    fun pinPasswordUpdationSuccessfully(message: String) {
        Espresso.onView(withId(R.id.sheetPinPasswordChangeTitleTV))
            .check(matches(withText(message)))
    }
    @Then("I should see the list of customers")
    fun seeListOfAgents() {
        Espresso.onView(withId(R.id.customerList)).check(matches(isDisplayed()))
    }
    @When("I click on search contacts")
    fun iClickOnSearchContacts(){
        Espresso.onView(withId(R.id.searchContact)).perform(ViewActions.click())
    }
}*/
