package com.example.ai_chatbot_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class AlarmData(val hour:Int,val minutes:Int,val message: String)

class AlarmViewModel: ViewModel(){
    private val _alarmData = MutableLiveData<AlarmData?>()
    val alarmData: LiveData<AlarmData?> get()= _alarmData

    fun setAlarm(hour:Int,minutes:Int,message: String){
        _alarmData.value = AlarmData(hour,minutes,message)
    }

    fun resetAlarm(){
        _alarmData.value = null
    }

}