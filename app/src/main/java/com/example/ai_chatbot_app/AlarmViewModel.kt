package com.example.ai_chatbot_app

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


fun  setSystemAlarm(context: Context, hour: Int, minute: Int, message: String) {
    val alarmIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
        putExtra(AlarmClock.EXTRA_HOUR,hour)
        putExtra(AlarmClock.EXTRA_MINUTES,minute)
        putExtra(AlarmClock.EXTRA_MESSAGE,message)
        putExtra(AlarmClock.EXTRA_SKIP_UI,true)
    }

    context.startActivity(alarmIntent)
}

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