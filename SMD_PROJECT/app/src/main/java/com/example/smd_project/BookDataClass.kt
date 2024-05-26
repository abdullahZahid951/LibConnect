package com.example.smd_project

class BookDataClass(val titleOfTheBook : String,
                    val autherOfTheBook : String,
                    val isbnnum : String,
                    val downloadBookCoverUrl : String,
                    val downloadUrl : String,
                    val selectedBookGenre : String,
                    val timestamp : Long,
                    val ratting  : Int,
                    val synopsis:String)
{
    constructor() :this("","","","" , ""  , "",
    0 , 0,"")
}