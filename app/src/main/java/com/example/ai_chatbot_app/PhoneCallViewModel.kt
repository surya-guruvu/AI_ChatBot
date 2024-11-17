package com.example.ai_chatbot_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PhoneCallViewModel: ViewModel(){
    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber:LiveData<String> get() = _phoneNumber

    fun setPhoneNumber(number: String){
        _phoneNumber.value = number
    }

    fun resetPhoneNumber(){
        _phoneNumber.value = ""
    }


}