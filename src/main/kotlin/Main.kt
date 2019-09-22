import java.io.File
import java.lang.System.`in`
import java.util.Scanner

fun main(vararg args: String?) {
    println("Welcome to AWESOME BOOKSHELF!")

    val listBooks = mutableListOf<Book>()
    val file = File("books.txt")
    val input = Scanner(`in`)

    file.forEachLine {
        if (it.isNotEmpty()) {
            val info = it.split(";;")
            val book = Book(title = info[0], author = info[1], synopsis = info[2])
            listBooks.add(book)
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

fun showOptions() {
    println("The options are:")
    for (action in Action.values()) {
        println("${action.value} -> ${action.description}")
    }
}

fun showAllBooks(list: List<Book>) {
    for (book in list) {
        val message = "\nTitle: ${book.title} \nSynopsis: ${book.synopsis} \nAuthor: ${book.author}"
        println(message)
    }
}

fun showAllAuthors(list: List<Book>) {
    val newList = list.distinctBy { it.author }
    println("Our Authors:")
    for (item in newList) {
        println(item.author)
    }
}

fun addNewBook(file: File, input: Scanner) {
    println("Let's add a new book!")
    println("Write the Title of the book you want to add:")
    val title = input.nextLine()
    println("Who's the author?")
    val author = input.nextLine()
    println("Now tell me a little about this book:")
    val synopsis = input.nextLine()
    file.appendText("\n$title;;$author;;$synopsis")
}

fun findByAuthor(list: List<Book>, input: Scanner) {
    println("Give us the Author's name and we'll give you his/her books!")
    val author = input.nextLine().toLowerCase()
    val books = list.filter { it.author.toLowerCase().contains(author) }
    if (books.isNotEmpty()) {
        println("${books.first().author} wrote these books:")
        for (book in books) {
            println(book.title)
        }
    } else {
        println("\nWe don't know this author. Write another name:")
        findByAuthor(list, input)
    }
}

fun findBook(list: List<Book>, input: Scanner) {
    println("Give us a tip to find a book:")
    val tip = input.nextLine().toLowerCase()
    val listThatMatchTip = list.filter {
        it.title.toLowerCase().contains(tip) ||
                it.author.toLowerCase().contains(tip) ||
                it.synopsis.toLowerCase().contains(tip)
    }
    if (listThatMatchTip.isEmpty()) {
        println("We didn't find any book that matches \"$tip\". Try again.")
        findBook(list, input)
    } else {
        println("\nWe found these books that matches your search:")
        showAllBooks(listThatMatchTip)
        println("\nDid you find what you were looking for? (yes/no)")
        val answer = input.nextLine()
        when (answer) {
            "no" -> findBook(list, input)
            "yes" -> println("Great!!!")
            else -> {
                println("We could't understand your answer. Let's start again!")
                findBook(list, input)
            }
        }
    }
}

fun removeBook(list: List<Book>, input: Scanner, file: File) {
    println("What book do you want to remove?")
    val word = input.nextLine().toLowerCase()
    val listOfPotential = list.filter {
        it.title.toLowerCase().contains(word) ||
                it.author.toLowerCase().contains(word) ||
                it.synopsis.toLowerCase().contains(word)
    }
    if (listOfPotential.isEmpty()) {
        println("We didn't find anything that matches your search. Try again.")
        removeBook(list, input, file)
    } else {
        println("We found these books that matches your search:")
        showAllBooks(listOfPotential)
        println("Tell me the title of the one you want to remove.")
        val titleToDelete = input.nextLine().toLowerCase()
        val booksToKeep = list.filter { it.title.toLowerCase() != titleToDelete }
        file.writeText("")
        booksToKeep.forEach { book -> file.appendText("${book.title};;${book.author};;${book.synopsis}\n") }
    }
}