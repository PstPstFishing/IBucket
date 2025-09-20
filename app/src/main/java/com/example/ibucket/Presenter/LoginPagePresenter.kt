package com.example.ibucket.Presenter

class LoginPagePresenter {

    fun CheckMinLengthPassword(pass:String):Boolean{
        if (pass.length < 6)
            return true
        return false
    }

    fun ValidatePassword(Inputpass:String,pass:String) : Boolean{
        if(Inputpass == pass )
            return false
        return true
    }


    fun ValidateEmail (Inputemail:String , email:String): Boolean{
        if(Inputemail == email)
            return false
        return true
    }
    fun ValidateEmpty(Input:String): Boolean{
        if(Input.length == 0)
            return true
        return false
    }
}