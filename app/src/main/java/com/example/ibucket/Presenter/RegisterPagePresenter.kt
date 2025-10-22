package com.example.ibucket.Presenter

class RegisterPagePresenter {
    fun CheckMinLengthPassword(pass:String):Boolean = pass.length < 8

    fun ConfirmPassword (Inputpass: String,InputConfirmpass:String) : Boolean = Inputpass != InputConfirmpass
    fun ValidateEmail (Inputemail:String , email:String): Boolean = Inputemail == email
    fun ValidateEmpty(Input:String): Boolean = Input.isEmpty()
    fun ValidatePhonenumberMax(Input:String): Boolean = Input.length > 11
}