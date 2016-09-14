package io.jdk.services

import com.google.inject.Inject
import groovy.json.JsonSlurper
import ratpack.exec.Promise
import ratpack.http.client.HttpClient
import ratpack.http.client.ReceivedResponse

class ReviewService {

  boolean curcuitBroken = false

  @Inject
  HttpClient client

  Promise<Double> getScore(String bookId) {
    if (!curcuitBroken) {
      client.get(
              new URI("http://localhost:8902/score/${bookId}".toString())
      ).blockingMap { ReceivedResponse receivedResponse ->
        String bodyText = receivedResponse.body.text
        Double score = new JsonSlurper().parseText(bodyText).'score' as Double
        return Math.round(score * 100) / 100d as Double
      }.onNull {
        return 0
      }.onError { Throwable t ->
        if (t instanceof ConnectException) {
          curcuitBroken = true
        }
        println t
        return 0
      }
    } else {
      Promise.async {
        return 0 as Double
      }
    }
  }

}
