package app.expense.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * DB Model to hold Categories added in Expenses
 */
@Entity(tableName = "category", indices = [Index(value = ["name"], unique = true)])
data class CategoryDTO(
    @PrimaryKey val id: Long? = null,
    @ColumnInfo(name = "name") val name: String,
)
