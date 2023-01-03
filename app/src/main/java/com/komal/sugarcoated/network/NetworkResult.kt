package com.komal.sugarcoated.network

class NetworkResult {

  sealed class ResultOf<out T> {
    data class Success<out R>(val value: R) : ResultOf<R>()
    data class Failure(
      val message: String?,
      val throwable: Throwable?
    ) : ResultOf<Nothing>()
    object Loading: ResultOf<Nothing>()
  }

}