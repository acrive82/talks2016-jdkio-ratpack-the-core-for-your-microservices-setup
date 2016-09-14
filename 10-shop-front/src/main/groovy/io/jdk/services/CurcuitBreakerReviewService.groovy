package io.jdk.services

import com.google.inject.Inject
import com.netflix.hystrix.HystrixCommandGroupKey
import com.netflix.hystrix.HystrixCommandKey
import com.netflix.hystrix.HystrixObservableCommand
import groovy.json.JsonSlurper
import ratpack.exec.Blocking
import ratpack.exec.Promise
import ratpack.http.client.HttpClient
import ratpack.http.client.ReceivedResponse
import rx.Observable

import static ratpack.rx.RxRatpack.observe

class CurcuitBreakerReviewService {

  @Inject
  HttpClient client

  Observable<Double> getScore(String bookId) {
    return new HystrixObservableCommand<Double>(commandKey) {
      protected Observable<Double> construct() {
        observe(
          client.get(
            new URI("http://localhost:8902/score/${bookId}".toString())
          ).map { ReceivedResponse receivedResponse ->
            String bodyText = receivedResponse.body.text
            Double score = new JsonSlurper().parseText(bodyText).'score' as Double
            return Math.round(score * 100) / 100d as Double
          }
        )
      }
      protected Observable<Double> resumeWithFallback() {
        observe(Promise.sync {
          return 0.0 as Double
        })
      }
      protected String getCacheKey() {
        return bookId
      }
    }.toObservable()

  }

  private static final HystrixCommandGroupKey hystrixCommandGroupKey = HystrixCommandGroupKey.Factory.asKey("reviews")

  private static HystrixObservableCommand.Setter getCommandKey() {
    HystrixObservableCommand.Setter.withGroupKey(hystrixCommandGroupKey).andCommandKey(HystrixCommandKey.Factory.asKey("score"))
  }

}

