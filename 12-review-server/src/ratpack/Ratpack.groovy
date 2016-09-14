import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.json

ratpack {
  bindings {
  }
  handlers {
    get('score/:itemId') {
      Double score = new Random().nextDouble() + 4
      println "Score for ${pathTokens['itemId']} is ${score}"
      render json(itemId: pathTokens['itemId'], score: score)
    }
    post('review/:itemId') {
      render json(message: 'OK')
    }
    get {
      response.send "Review API"
    }
  }
}
