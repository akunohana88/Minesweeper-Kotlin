import kotlin.random.Random

val COL = 9
val ROW = 9
var MINES = 0

fun change(answerSheet: MutableList<MutableList<String>>, r: Int, c: Int) {
    if (answerSheet[r][c] == ".") {
        answerSheet[r][c] = "1"
    }
    else if (answerSheet[r][c] == "X") {
        answerSheet[r][c] = "X"
    }
    else {
        val num = answerSheet[r][c].toInt() + 1
        answerSheet[r][c] = num.toString()
    }
}

fun numbering(answerSheet: MutableList<MutableList<String>>, mineLocationList: MutableList<String>) {
    for (mines in mineLocationList) {
        val coordinate = mines.split(",")
        val r = coordinate[0].toInt()
        val c = coordinate[1].toInt()
        if (r != 0) {change(answerSheet, r - 1, c)}
        if (c != 0) {change(answerSheet, r, c - 1)}
        if (r != ROW - 1) {change(answerSheet, r + 1, c)}
        if (c != COL - 1) {change(answerSheet, r, c + 1)}
        if (r != 0 && c != 0) {change(answerSheet, r - 1, c - 1)}
        if (r != 0 && c != COL - 1) {change(answerSheet, r - 1, c + 1)}
        if (r != ROW - 1 && c != 0) {change(answerSheet, r + 1, c - 1)}
        if (r != ROW - 1 && c != COL - 1) {change(answerSheet, r + 1, c + 1)}
    }
}

fun initBoard(answerSheet: MutableList<MutableList<String>>, mineLocationList: MutableList<String>) {
    var count = 0
    while (count != MINES) { // init mines
        var curRow = Random.nextInt(0, ROW)
        var curCol = Random.nextInt(0, COL)
        var coordinate = curRow.toString() + "," + curCol.toString()
        if (answerSheet[curRow][curCol] == ".") {
            answerSheet[curRow][curCol] = "X"
            mineLocationList.add("$curRow,$curCol")
            count += 1
        }
    }
    numbering(answerSheet, mineLocationList)
}

fun printBoard(board: MutableList<MutableList<String>>) {
    println(" │123456789│")
    println("—│—————————│")
    for (i in 0 until ROW) {
        print(i + 1)
        print("|")
        print(board[i].joinToString(""))
        println("|")
    }
    println("—│—————————│")
}

fun free(answerSheet: MutableList<MutableList<String>>, minefieldList: MutableList<MutableList<String>>,
         r: Int, c: Int) {
    if (answerSheet[r][c] == ".") {
        minefieldList[r][c] = "/"
        if (r != 0 && minefieldList[r - 1][c] != "/") {free(answerSheet, minefieldList, r - 1, c)}
        if (c != 0 && minefieldList[r][c - 1] != "/") {free(answerSheet, minefieldList, r, c - 1)}
        if (r != ROW - 1 && minefieldList[r + 1][c] != "/") {free(answerSheet, minefieldList, r + 1, c)}
        if (c != COL - 1 && minefieldList[r][c + 1] != "/") {free(answerSheet, minefieldList, r, c + 1)}
        if (r != 0 && c != 0 && minefieldList[r - 1][c - 1] != "/") {free(answerSheet, minefieldList, r - 1, c - 1)}
        if (r != 0 && c != COL - 1 && minefieldList[r - 1][c + 1] != "/") {free(answerSheet, minefieldList, r - 1, c + 1)}      
        if (r != ROW - 1 && c != 0 && minefieldList[r + 1][c - 1] != "/") {free(answerSheet, minefieldList, r + 1, c - 1)}
        if (r != ROW - 1 && c != COL - 1 && minefieldList[r + 1][c + 1] != "/") {free(answerSheet, minefieldList, r + 1, c + 1)}
    }
    else {
        minefieldList[r][c] = answerSheet[r][c]
    }

}

fun move(answerSheet: MutableList<MutableList<String>>, minefieldList: MutableList<MutableList<String>>,
         mineLocationList: MutableList<String>, r: Int, c: Int, cell: String): Boolean { // false = player lost
    var coordinate = r.toString() + "," + c.toString()
    if (cell == "mine") {
        if (minefieldList[r][c] == ".") {
            minefieldList[r][c] = "*"
        }
        else if (minefieldList[r][c] == "*") {
            minefieldList[r][c] = "."
        }
        else {
            println("There is a number here!")
        }
    }
    else { // cell == "free"
        if (mineLocationList.contains(coordinate)) {
            return false
        }
        else {
            free(answerSheet, minefieldList, r, c)
        }
    }
    return true
}

fun complete(minefieldList: MutableList<MutableList<String>>, mineLocationList: MutableList<String>):
        Boolean{
    for (mines in mineLocationList) { // checks all mines are marked
        var count = 0
        val coordinate = mines.split(",")
        val r = coordinate[0].toInt()
        val c = coordinate[1].toInt()
        if (minefieldList[r][c] == "*") {
            count += 1
        }
        if (count == MINES) {
            return true
        }
    }
    // check that all free cells are explored

    return false
}

fun main() {
    println("How many mines do you want on the field?")
    MINES = readln().toInt()
    val minefieldList = MutableList(ROW) { MutableList(COL) { "." } }

    val answerSheet = MutableList(ROW) { MutableList(COL) { "." } }
    var mineLocationList = mutableListOf<String>()
    initBoard(answerSheet, mineLocationList)

    while (!complete(minefieldList, mineLocationList)) {
        printBoard(minefieldList)
        println("Set/unset mines marks or claim a cell as free:")
        val (x, y, cell) = readln()!!.split(' ')
        val c = x.toInt() - 1
        val r = y.toInt() - 1
        val validMove = move(answerSheet, minefieldList, mineLocationList, r, c, cell)
        if (!validMove) {
            break
        }
    }

    if (complete(minefieldList, mineLocationList)) {
        println("Congratulations! You found all the mines!")
    }
    else {
        printBoard(answerSheet)
        println("You stepped on a mine and failed!")
    }
}
