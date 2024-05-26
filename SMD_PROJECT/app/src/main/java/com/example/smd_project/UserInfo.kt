package com.example.smd_project

data class UserInfo(val nameOfNewUser: String  = "" ,
                    val emailOfNewUser: String= "",
                    val rollNumber: String= "",
                    val department: String= "",
                    val fcmToken: String= "",
                    val fine : Int,
                    val image : String
)

{
    constructor() : this("", "", "","","" ,
        0,"")
}