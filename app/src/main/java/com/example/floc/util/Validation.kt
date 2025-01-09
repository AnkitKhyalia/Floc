package com.example.floc.util


object ValidatorUtils {

    // Validate Name
    fun validateName(name: String?): String? {
        return when {
            name == null -> null
            name.isBlank() -> "Name cannot be empty"
            name.length < 3 -> "Name should be at least 3 characters"
            else -> null // No error
        }
    }

    // Validate Email
    fun validateEmail(email: String?): String? {
        return when {
            email == null -> null
            email.isBlank() -> "Email cannot be empty"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email address"
            else -> null // No error
        }
    }

    // Validate Mobile Number
    fun validateMobileNumber(mobileNumber: String?): String? {
        return when {
            mobileNumber == null -> null
            mobileNumber.isBlank() -> "Mobile number cannot be empty"
            !mobileNumber.matches(Regex("^[0-9]{10}$")) -> "Invalid mobile number"
            else -> null // No error
        }
    }

    // Validate Password
    fun validatePassword(password: String?): String? {
        return when {
            password == null -> null
            password.isBlank() -> "Password cannot be empty"
            password.length < 8 -> "Password should be at least 8 characters"
            !password.matches(Regex(".*[A-Z].*")) -> "Password should contain at least one uppercase letter"
            !password.matches(Regex(".*[a-z].*")) -> "Password should contain at least one lowercase letter"
            !password.matches(Regex(".*[0-9].*")) -> "Password should contain at least one digit"
            !password.matches(Regex(".*[!@#\$%^&*(),.?\":{}|<>].*")) -> "Password should contain at least one special character"
            else -> null // No error
        }
    }

    // Function to validate all fields at once
    fun validateAllFields(
        name: String? = null,
        email: String? = null,
        mobileNumber: String? = null,
        password: String? = null
    ): Map<String, String?> {
        return mapOf(
            "name" to validateName(name),
            "email" to validateEmail(email),
            "mobileNumber" to validateMobileNumber(mobileNumber),
            "password" to validatePassword(password)
        ).filterValues { it != null } // Remove entries where no validation was needed
    }

}
