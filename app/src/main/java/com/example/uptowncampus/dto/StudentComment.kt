package com.example.uptowncampus.dto

data class StudentComment(
    var commentId : String = "",
    var commentContent : String = "",
    var buildingId : Int = 0,
    var buildingName : String = "")
{
    // TODO: Add an attribute that takes in a photo.
}