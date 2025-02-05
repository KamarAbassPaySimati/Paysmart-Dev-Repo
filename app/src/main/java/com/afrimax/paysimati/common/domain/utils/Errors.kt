package com.afrimax.paysimati.common.domain.utils

sealed interface Errors : GenericError {

    /**Errors that can occur while performing network calls*/
    enum class Network : Errors {
        NO_RESPONSE, NO_INTERNET, INTERNAL_ERROR, REQUEST_TIMEOUT, PAYLOAD_TOO_LARGE,MAPPING_FAILED, UNKNOWN, UNABLE_TO_UPLOAD, ACCOUNT_LOCKED, FILE_NOT_FOUND, UNPROCESSABLE_ENTITY,CONFLICT_FOUND, UNAUTHORIZED, BAD_REQUEST, UNABLE_TO_CONNECT

    }

    /**Errors that can occur while performing operations on SharedPreferences*/
    enum class Prefs : Errors {
        NO_SUCH_DATA, UNKNOWN
    }

    /** Errors that can occur while accessing local storage files */
    enum class Storage : Errors {
        UNABLE_TO_ACCESS_FILE, FILE_NOT_FOUND, PERMISSION_DENIED, IO_ERROR, INSUFFICIENT_STORAGE, INVALID_URI, UNSUPPORTED_FILE_TYPE, SECURITY_EXCEPTION, FILE_TOO_LARGE, FILE_ALREADY_EXISTS
    }

    /**Errors that can occur while performing validations*/
    enum class Validation : Errors {
        TOO_LONG, TOO_SMALL, BLANK, INVALID, UNSUPPORTED_FILE, NOT_VERIFIED
    }
}