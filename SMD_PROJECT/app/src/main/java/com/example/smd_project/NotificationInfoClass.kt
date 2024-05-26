package com.example.smd_project

data class NotificationInfoClass(val noticationtext : String ,
                                 val noticationTime : String,
                                 val noticationDate : String,
                                 val ISBN : String,
                                 val timestamp : Long)
{
    constructor() : this("", "", "","",0)
}