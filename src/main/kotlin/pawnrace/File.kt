package pawnrace

class File {
    val file: Char
    val intRep: Int
    constructor(file: Char) {
        this.file = file.toLowerCase()
        this.intRep = Character.getNumericValue(this.file) - 1
    }
    override fun toString(): String = file.toString().toLowerCase()

    override fun equals(other: Any?): Boolean {
        if (other is File) return (other.file == file)
        else return false
    }
}