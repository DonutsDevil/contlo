package com.swapnil.contlo.utility


sealed class Status<T>(val data: T? = null, val errorMsg: String? = null) {
    class Loading<T> : Status<T>()
    class Success<T>(data: T) : Status<T>(data = data)
    class Error<T>(errorMsg: String) : Status<T>(errorMsg = errorMsg)
}