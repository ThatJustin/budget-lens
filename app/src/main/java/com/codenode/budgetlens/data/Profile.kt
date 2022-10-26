package com.codenode.budgetlens.data

/**
 * Profile for users, do not add any logic here
 * it goes in the UserProfile class.
 */
data class Profile(
    var isLoaded: Boolean = false,
    var username: String,
    var firstName: String,
    var lastName: String,
    var email: String,
    var telephoneNumber: String,
    var dateOfBirth:String
) {
    constructor() : this(false, "", "", "", "", "","")
}