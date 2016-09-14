package io.jdk.services

import ratpack.exec.Promise

class BookService {

  Promise<List<Book>> getCatalog() {
    Promise.sync {
      new File('src/ratpack/data/books.csv').readLines().collect { String line ->
        new Book([['id', 'title', 'description'], line.split(';').toList()].transpose().collectEntries {
          it
        } + [price: new Random().nextInt(40)])
      }
    }
  }

}
