package edu.gwu.androidtweets


// A class in Kotlin with constructor
/*class Tweet constructor(username: String, handle: String, content: String, iconUrl: String){

    private val username = username
}*/

// Getters, setters, equals, hashcode, toString all generated automatically for a data class
// Data class uses parentheses
data class Tweet (
    val username: String,
    val handle: String,
    val content: String,
    val iconUrl: String
)