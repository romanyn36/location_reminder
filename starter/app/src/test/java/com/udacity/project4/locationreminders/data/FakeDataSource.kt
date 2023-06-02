package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(
    private val reminders: MutableList<ReminderDTO> = mutableListOf()
) : ReminderDataSource {
    private var shouldReturnError = false
    override suspend fun getReminders(): Result<List<ReminderDTO>> =
        try {
            if (shouldReturnError) { // for make error test manually
                throw Exception("error happened while retrieving the data")
            }else
            {
                reminders.let { return@let Result.Success(reminders) }
            }
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }


    override suspend fun getReminder(id: String): Result<ReminderDTO> =
        try {
            if (shouldReturnError) {// for make error test manually
                Result.Error("error happened while retrieving the data")
            }else
            {
                val reminder = reminders.find { it.id == id }
                if (reminder == null) {
                    Result.Error("Not found")
                } else {
                    Result.Success(reminder)
                }
            }
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }


    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }


    fun setShouldReturnError(shouldReturn: Boolean) {
        this.shouldReturnError = shouldReturn
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }

}