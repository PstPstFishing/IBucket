package com.example.ibucket.Presenter

class RegisterPagePresenter {
    fun CheckMinLengthPassword(pass:String):Boolean{
        if (pass.length > 6)
            return false
        return true
    }

    fun ConfirmPassword (Inputpass: String,InputConfirmpass:String) : Boolean{
        if(Inputpass == InputConfirmpass)
            return false
        return true
    }
    fun ValidateEmail (Inputemail:String , email:String): Boolean{
        if(Inputemail != email)
            return false
        return true
    }
    fun ValidateEmpty(Input:String): Boolean{
        if(Input.length == 0)
            return true
        return false
    }
    fun ValidatePhonenumberMax(Input:String): Boolean {
        if (Input.length < 11)
            return false
        return true
    }
}