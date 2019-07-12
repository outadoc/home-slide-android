package fr.outadoc.quickhass.rest

class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause)