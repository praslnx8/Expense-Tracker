package app.expense.presentation.viewModels

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import app.expense.domain.expense.models.Expense
import app.expense.domain.expense.usecases.FetchExpenseUseCase
import app.expense.presentation.viewStates.ExpenseStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.NumberFormat
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale.getDefault
import javax.inject.Inject

@HiltViewModel
class ExpenseStatsViewModel @Inject constructor(
    private val fetchExpenseUseCase: FetchExpenseUseCase
) : ViewModel() {

    fun getExpenseStats(): Flow<ExpenseStats> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_YEAR, 1)

        return fetchExpenseUseCase.getExpenses(from = calendar.timeInMillis).map { expenses ->
            ExpenseStats(
                monthlySpent = getMonthlySpent(expenses)
            )
        }
    }

    private fun getMonthlySpent(expenses: List<Expense>): Map<String, String> =
        expenses
            .groupBy { expense ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = expense.time
                calendar.get(Calendar.MONTH)
            }.toSortedMap()
            .mapValues { mapEntry ->
                val totalAmount = mapEntry.value.sumOf { expense -> expense.amount }
                NumberFormat.getCurrencyInstance().format(totalAmount)
            }.mapKeys { mapEntry ->
                Month.of(mapEntry.key + 1).getDisplayName(TextStyle.SHORT, getDefault())
            }
}
