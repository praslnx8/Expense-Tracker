package app.expense.presentation.viewModels

import androidx.lifecycle.ViewModel
import app.expense.domain.expense.models.Expense
import app.expense.domain.expense.usecases.AddExpenseUseCase
import app.expense.domain.expense.usecases.DeleteExpenseUseCase
import app.expense.domain.expense.usecases.FetchExpenseUseCase
import app.expense.domain.suggestion.usecases.DeleteSuggestionUseCase
import app.expense.domain.suggestion.usecases.FetchSuggestionUseCase
import app.expense.presentation.viewStates.AddExpenseViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val fetchExpenseUseCase: FetchExpenseUseCase,
    private val fetchSuggestionUseCase: FetchSuggestionUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val deleteSuggestionUseCase: DeleteSuggestionUseCase
) : ViewModel() {

    private val _addExpenseViewStateFlow = MutableStateFlow(AddExpenseViewState())
    val addExpenseViewState: StateFlow<AddExpenseViewState>
        get() = _addExpenseViewStateFlow

    suspend fun getAddExpenseViewState(
        expenseId: Long? = null,
        suggestionId: Long? = null
    ) {
        if (expenseId != null) {
            fetchExpenseUseCase.getExpense(expenseId).first().also { expense ->

                if (expense != null) {
                    _addExpenseViewStateFlow.value = AddExpenseViewState(
                        amount = expense.amount,
                        paidTo = expense.paidTo ?: "",
                        categories = expense.categories.toMutableList(),
                        time = expense.time
                    )
                }
            }
        } else if (suggestionId != null) {
            fetchSuggestionUseCase.getSuggestion(suggestionId).first().also { suggestion ->
                // TODO Get category based on paidTo by ML or other intelligent way
                if (suggestion != null) {
                    _addExpenseViewStateFlow.value = AddExpenseViewState(
                        amount = suggestion.amount,
                        paidTo = suggestion.paidTo ?: "",
                        time = suggestion.time,
                        suggestionMessage = suggestion.referenceMessage
                    )
                }
            }
        }
    }

    suspend fun addExpense(
        expenseId: Long?,
        suggestionId: Long?,
        amount: Double,
        paidTo: String?,
        categories: List<String>,
        time: Long
    ) {
        addExpenseUseCase.addExpense(
            expense = Expense(
                id = expenseId,
                amount = amount,
                paidTo = paidTo,
                categories = categories,
                time = time
            ),
            fromSuggestionId = suggestionId
        )
    }

    suspend fun deleteSuggestion(id: Long) {
        deleteSuggestionUseCase.deleteSuggestion(id)
    }

    suspend fun deleteExpense(id: Long) {
        deleteExpenseUseCase.deleteExpense(id)
    }
}
