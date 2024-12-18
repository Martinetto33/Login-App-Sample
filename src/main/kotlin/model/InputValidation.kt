package model

/**
 * This code resembles the business logic that checks the correctness
 * of the credentials input by the user.
 * */

fun validateInput(input: String): Boolean {
    return input.isNotBlank()
}

fun checkCredentials(username: String, password: String): Boolean {
    return username == "prova" && password == "prova"
}