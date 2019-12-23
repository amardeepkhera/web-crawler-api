package au.com.qantas

data class GetWebResourceException(val code: Int, val msg: String? = "", val throwable: Throwable? = null) :
    RuntimeException(msg, throwable)