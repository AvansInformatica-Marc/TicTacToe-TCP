package nl.marc.tictactoe.domain

class TicTacToeGame(val player: Player) {
    val cells = cellValues.associateWith<Cells, Player?> { null }.toMutableMap()

    val winner: Player?
        get() = getRowOwner(CELLS_IN_ROW) { row, pos -> (pos / CELLS_IN_ROW) == row }
            ?: getRowOwner(CELLS_IN_COLUMN) { column, pos -> (pos % CELLS_IN_ROW) == column }
            ?: getRowOwner(1) { _, pos -> (pos % CELLS_IN_ROW) == (pos / CELLS_IN_ROW) }
            ?: getRowOwner(1) { _, pos -> (pos % CELLS_IN_ROW) == LAST_CELL_IN_ROW - (pos / CELLS_IN_ROW) }

    val isGameRunning: Boolean
        get() = winner == null && cells.any { it.value == null }

    private fun getRowOwner(repeat: Int, getPosition: (Int, Int) -> Boolean): Player? {
        for (row in 0 until repeat) {
            val cellsInRow = cellValues.filterIndexed { pos, _ -> getPosition(row, pos) }
            val rowOwner = getRowOwner(cellsInRow)
            if (rowOwner != null) {
                return rowOwner
            }
        }

        return null
    }

    private fun getRowOwner(row: List<Cells>): Player? {
        if (cells[row[0]] != null && cells[row[0]] == cells[row[1]] && cells[row[1]] == cells[row[2]]) {
            return cells[row[0]]
        }

        return null
    }

    fun clear() {
        for ((cell, _) in cells) {
            cells[cell] = null
        }
    }

    fun canMarkCell(cell: Cells): Boolean {
        return cells[cell] == null
    }

    fun markCell(cell: Cells, currentPlayer: Boolean) {
        require(canMarkCell(cell))
        cells[cell] = when {
            currentPlayer -> player
            player is Player1 -> Player2
            else -> Player1
        }
    }

    override fun toString(): String {
        return convertGridToString {
            getCellValue(it)
        }
    }

    private fun getCellValue(cell: Cells): String {
        return cells[cell]?.mark?.let { " $it " } ?: ">${cell.name}"
    }

    enum class Cells {
        TL, TC, TR,
        ML, MC, MR,
        BL, BC, BR
    }

    sealed interface Player {
        val mark: String
    }

    object Player1 : Player {
        override val mark = "X"

        override fun equals(other: Any?) = other is Player1

        override fun hashCode() = mark.hashCode()
    }

    object Player2 : Player {
        override val mark = "O"

        override fun equals(other: Any?) = other is Player2

        override fun hashCode() = mark.hashCode()
    }

    companion object {
        private const val CELLS_IN_ROW = 3

        private const val CELLS_IN_COLUMN = 3

        private const val LAST_CELL_IN_ROW = CELLS_IN_ROW - 1

        val cellValues = Cells.values()

        fun convertGridToString(getContent: (Cells) -> String): String {
            val content = cellValues.associateWith { getContent(it) }
            val contentMaxLength = content.values.maxOf { it.length }
            val dashes = "-".repeat(contentMaxLength)

            var string = ""

            for ((location, cell) in content) {
                val index = cellValues.indexOf(location)
                val row = index / CELLS_IN_ROW
                val column = index % CELLS_IN_ROW

                string += " $cell "

                if (column < LAST_CELL_IN_ROW) {
                    string += "|"
                } else if (row != 2) {
                    string += "\n $dashes | $dashes | $dashes \n"
                }
            }

            return string
        }
    }
}
