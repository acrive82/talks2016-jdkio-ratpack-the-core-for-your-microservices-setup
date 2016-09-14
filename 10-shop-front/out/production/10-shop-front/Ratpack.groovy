import io.jdk.services.Book
import io.jdk.services.BookService
import io.jdk.services.CurcuitBreakerReviewService
import io.jdk.services.ReviewService
import ratpack.groovy.template.MarkupTemplateModule
import ratpack.http.client.HttpClient
import ratpack.hystrix.HystrixMetricsEventStreamHandler
import ratpack.hystrix.HystrixModule

import static ratpack.groovy.Groovy.groovyMarkupTemplate
import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.json

ratpack {
  bindings {
    module(MarkupTemplateModule) { configuration ->
      configuration.autoNewLine = true
      configuration.useDoubleQuotes = true
      configuration.autoIndent = true
      configuration.expandEmptyElements = true
    }
    bind BookService
    bind ReviewService
    bind CurcuitBreakerReviewService
    module new HystrixModule().sse()
  }
  handlers { BookService booksService, CurcuitBreakerReviewService reviewService ->
    get {
      booksService.catalog.blockingOp { books ->
        books.each { Book book ->
          reviewService.getScore(book.id).subscribe { Double score -> book.score = score }
        }
       // booksService.catalog.blockingOp { books ->
       //   books.each { Book book ->
       //     book.score = Blocking.on(reviewService.getScore(book.id))?.doubleValue() ?: 0.0d
       //   }

      }.then { books ->
        render groovyMarkupTemplate("index.gtpl", title: "Groovy Book Shop", books: books)
      }
    }
    get ('order/:bookId') { HttpClient client ->
      client.post(new URI("http://localhost:8901/order/${pathTokens['bookId']}")) { spec ->
        spec.body { b -> b.text "order data" }
      }.onError { Throwable t ->
        render json(message: 'FAIL', exception: t)
      }.then {
        render json(message: 'OK')
      }
    }
    get("hystrix.stream", new HystrixMetricsEventStreamHandler())
    files { dir "public" }
    files { dir "content" }
  }
}
