import java.io.File
import java.lang.System.`in`
import java.util.Scanner

fun main(vararg args: String?) {
    println("Welcome to AWESOME BOOKSHELF!")

    val file = File("books.txt")
    val input = Scanner(`in`)

    val listBooks = mutableListOf<Book>().apply {
        file.forEachLine { line ->
            if (line.isNotEmpty()) {
                this.add(
                    line.split(";;").let {
                        Book(title = it[0], author = it[1], synopsis = it[2])
                    }
                )
            }
        }
    }

    if (args.isNullOrEmpty()) showOptions() else runsBookshelf(args[0], listBooks, input, file)

    println("\nHave a nice day ^.~")
    input.close()
}

enum class Action(val value: String, val description: String) {
    ADD("add", "to add a new book"),
    REMOVE("remove", "to remove a book"),
    BOOKS("books", "to list all books"),
    AUTHORS("authors", "to list authors"),
    BOOKS_BY_AUTHOR("author", "to see all books from an author"),
    FIND_BOOK("find", "to find a book")
}

data class Book(
    val title: String,
    val author: String,
    val synopsis: String
)

fun runsBookshelf(args: String?, listBooks: List<Book>, input: Scanner, file: File) {
    val command = args ?: "nothing"

    when (command) {
        Action.BOOKS.value -> showAllBooks(listBooks)
        Action.AUTHORS.value -> showAllAuthors(listBooks)
        Action.BOOKS_BY_AUTHOR.value -> findByAuthor(listBooks, input)
        Action.FIND_BOOK.value -> findBook(listBooks, input)
        Action.ADD.value -> addNewBook(file, input)
        Action.REMOVE.value -> removeBook(listBooks, input, file)
        else -> showOptions()
    }
}

fun showOptions() = run {
    println("The options are:")
    Action.values().forEach { println("${it.value} -> ${it.description}") }
}

fun showAllBooks(list: List<Book>) {
    list.forEach { book ->
        book.run {
            println("\nTitle: $title \nSynopsis: $synopsis \nAuthor: $author")
        }
    }
}

fun showAllAuthors(list: List<Book>) {
    list.distinctBy { it.author }
        .also { println("Our Authors:") }
        .run {
            this.forEach { println(it.author) }
        }
}

fun addNewBook(file: File, input: Scanner) = input.run {
    println("Let's add a new book!")
    println("Write the Title of the book you want to add:")
    nextLine().let { title ->
        println("Who's the author?")
        nextLine().let { author ->
            println("Now tell us a little about this book:")
            nextLine().let { synopsis ->
                file.appendText("\n$title;;$author;;$synopsis")
            }
        }
    }
}

fun findByAuthor(list: List<Book>, input: Scanner) {
    println("Give us the Author's name and we'll give you his/her books!")
    input.nextLine().toLowerCase().let { author ->
        list.filter { it.author.toLowerCase().contains(author) }.let { books ->
            if (books.isNotEmpty()) {
                println("$author wrote these books:")
                books.forEach {
                    println("${it.author} --> ${it.title}")
                }
            } else {
                println("\nWe don't know this author. Write another name:")
                findByAuthor(list, input)
            }
        }
    }
}

fun findBook(list: List<Book>, input: Scanner) {
    with(list) {
        println("Give us a tip to find a book:")
        input.nextLine().toLowerCase().let {
            this.filter { book ->
                book.title.toLowerCase().contains(it) ||
                        book.author.toLowerCase().contains(it) ||
                        book.synopsis.toLowerCase().contains(it)
            }.also { books ->
                if (books.isEmpty()) {
                    println("We didn't find any book that matches \"$it\". Try again.")
                    findBook(this, input)
                } else {
                    println("\nWe found these books that matches your search:").also {
                        showAllBooks(books)
                    }
                    println("\nDid you find what you were looking for? (yes/no)")
                    input.nextLine().let { answer ->
                        when (answer) {
                            "no" -> findBook(this, input)
                            "yes" -> println("Great!!!")
                            else -> {
                                println("We could't understand your answer. Let's start again!")
                                findBook(this, input)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun removeBook(list: List<Book>, input: Scanner, file: File) {
    list.run {
        println("What book do you want to remove?")
        input.nextLine().toLowerCase().let {
            filter { book ->
                book.title.toLowerCase().contains(it) ||
                        book.author.toLowerCase().contains(it) ||
                        book.synopsis.toLowerCase().contains(it)
            }
        }
    }.also {
        if (it.isEmpty()) {
            println("We didn't find anything that matches your search. Try again.")
            removeBook(list, input, file)
        } else {
            println("We found these books that matches your search:")
            showAllBooks(it)
            println("Tell me the title of the one you want to remove.")
            input.nextLine().toLowerCase().let { title ->
                it.filter { book -> book.title.toLowerCase() != title }
            }.apply {
                file.writeText("")
                this.forEach { book -> file.appendText("${book.title};;${book.author};;${book.synopsis}\n") }
            }
        }
    }
}