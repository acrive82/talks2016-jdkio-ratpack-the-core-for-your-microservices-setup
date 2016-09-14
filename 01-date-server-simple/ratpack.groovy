@Grab("io.ratpack:ratpack-groovy:1.4.1")
@Grab('org.slf4j:slf4j-simple:1.7.21')
import static ratpack.groovy.Groovy.ratpack

ratpack {
  handlers {
    get {
      response.send "Time on JDK.IO is " + new Date().toString() 
    }
  }
}


