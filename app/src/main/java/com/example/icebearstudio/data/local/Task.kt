package com.example.icebearstudio.data.local

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.icebearstudio.AppDateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import javax.inject.Singleton

@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val createdAt: LocalDate = LocalDate.now(),
    val dueTime: LocalDate,
    val marked: Boolean = false
)


class Converters{
    @TypeConverter
    fun fromTimeStamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it, AppDateFormat.dateFormatter) }
    }

    @TypeConverter
    fun dateToTimeStamp(date: LocalDate?): String? {
        return date?.format(AppDateFormat.dateFormatter)
    }
}
