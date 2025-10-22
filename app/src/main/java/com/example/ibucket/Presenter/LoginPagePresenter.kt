package com.example.ibucket.Presenter

class LoginPagePresenter {

    fun CheckMinLengthPassword(pass:String):Boolean = pass.length < 8

    fun ValidatePassword(Inputpass:String,pass:String) : Boolean = Inputpass != pass


    fun ValidateEmail (Inputemail:String , email:String): Boolean = Inputemail != email
    fun ValidateEmpty(Input:String): Boolean = Input.isEmpty()
}