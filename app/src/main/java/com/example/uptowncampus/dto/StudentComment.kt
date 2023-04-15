package com.example.uptowncampus.dto

/**
 * A data class representing a StudentComment.
 *
 * Used to store student comments into Firestore database
 * @property commentId The id of the comment.
 * @property commentContent The comment's contents.
 * @property buildingId The id of the building.
 * @property buildingName The name of the building.
 */
data class StudentComment(
    var commentId : String = "",
    var commentContent : String = "",
    var buildingId : Int = 0,
    var buildingName : String = "")
{
    // TODO: Add an attribute that takes in a photo.
}