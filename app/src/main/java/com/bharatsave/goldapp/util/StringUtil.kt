package com.bharatsave.goldapp.util

class StringUtil {
    companion object {
        private const val EMAIL_PATTERN =
            "^[\\w!#\$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#\$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}\$"

        @JvmStatic
        fun isEmailValid(email: String): Boolean {
            return email.matches(Regex(EMAIL_PATTERN))
        }
    }
}