package sorting

import java.io.File
import java.io.InputStream
import java.util.Scanner

const val COMMAND_OPTION = "-dataType"
const val LONG_DATA_TYPE = "long"
const val LINE_DATA_TYPE = "line"
const val WORD_DATA_TYPE = "word"

const val SORTING_COMMAND_OPTION = "-sortingType"
const val NATURAL_SORTING_OPTION = "natural"
const val BY_COUNT_SORTING_OPTION = "byCount"

const val INPUT_FILE_COMMAND_OPTION = "-inputFile"
const val OUTPUT_FILE_COMMAND_OPTION = "-outputFile"

fun main(args: Array<String>) {
    val sortingType = if (args.contains(SORTING_COMMAND_OPTION)) {
        args.getOrElse(args.indexOf(SORTING_COMMAND_OPTION) + 1) {
            println("No sorting type defined!")
            return
        }.takeIf {
            it in listOf(NATURAL_SORTING_OPTION, BY_COUNT_SORTING_OPTION)
        } ?: run {
            println("No sorting type defined!")
            return
        }
    } else {
        NATURAL_SORTING_OPTION
    }

    val dataType = if (args.contains(COMMAND_OPTION)) {
        args.getOrElse(args.indexOf(COMMAND_OPTION) + 1) {
            println("No data type defined!")
            return
        }.takeIf {
            it in listOf(LONG_DATA_TYPE, LINE_DATA_TYPE, WORD_DATA_TYPE)
        } ?: run {
            println("No data type defined!")
            return
        }
    } else {
        WORD_DATA_TYPE
    }

    args.filter {
        it.startsWith(prefix = "-") && it !in listOf(
            COMMAND_OPTION, SORTING_COMMAND_OPTION, INPUT_FILE_COMMAND_OPTION, OUTPUT_FILE_COMMAND_OPTION
        )
    }.forEach {
        println("\"$it\" is not a valid parameter. It will be skipped.")
    }

    val inputFile = if (args.contains(INPUT_FILE_COMMAND_OPTION)) {
        args.getOrNull(args.indexOf(INPUT_FILE_COMMAND_OPTION) + 1)?.let {
            File(it)
        }
    } else null

    val outputFile = if (args.contains(OUTPUT_FILE_COMMAND_OPTION)) {
        args.getOrNull(args.indexOf(OUTPUT_FILE_COMMAND_OPTION) + 1)?.let {
            File(it)
        }
    } else null

    val out: (String) -> Unit = { toOut: String ->
        outputFile?.writer()?.appendLine(toOut) ?: run {
            println(toOut)
        }
    }

    when (dataType) {
        LONG_DATA_TYPE -> numberTreatment(inputFile?.inputStream(), sortingType == NATURAL_SORTING_OPTION, out)
        LINE_DATA_TYPE -> lineTreatment(inputFile?.inputStream(), sortingType == NATURAL_SORTING_OPTION, out)
        else -> wordTreatment(inputFile?.inputStream(), sortingType == NATURAL_SORTING_OPTION, out)
    }
}

fun wordTreatment(inputStream: InputStream?, isNaturalSort: Boolean, outputResult: (String) -> Unit) {
    (inputStream ?: System.`in`).use { input ->
        val scanner = Scanner(input)
        val inputInteger = mutableListOf<String>()
        while (scanner.hasNext()) {
            val line = scanner.nextLine()
            inputInteger.addAll(line.split("\\s+".toRegex()))
        }
        val totalNum = inputInteger.size
        if (isNaturalSort) {
            outputResult(
                """
        Total words: $totalNum.
        Sorted data: ${inputInteger.sorted().joinToString(separator = " ")}
    """.trimIndent()
            )
        } else {
            val finalSortedElements = inputInteger.sortByCount()
            outputResult("Total words: $totalNum.")
            finalSortedElements.printSortedByCount(totalNum, outputResult)
        }
    }
}

fun lineTreatment(inputStream: InputStream?, isNaturalSort: Boolean, outputResult: (String) -> Unit) {
    (inputStream ?: System.`in`).use { input ->
        val scanner = Scanner(input)
        val inputInteger = mutableListOf<String>()
        while (scanner.hasNext()) {
            inputInteger.add(scanner.nextLine())
        }
        val totalNum = inputInteger.size
        if (isNaturalSort) {
            outputResult(
                """
        Total lines: $totalNum.
        Sorted data:
    """.trimIndent()
            )
            inputInteger.sorted().forEach(::println)
        } else {
            val finalSortedElements = inputInteger.sortByCount()
            outputResult("Total lines: $totalNum.")
            finalSortedElements.printSortedByCount(totalNum, outputResult)
        }
    }
}

fun numberTreatment(inputStream: InputStream?, isNaturalSort: Boolean, outputResult: (String) -> Unit) {
    (inputStream ?: System.`in`).use { input ->
        val scanner = Scanner(input)
        val inputInteger = mutableListOf<Int>()
        while (scanner.hasNext()) {
            val line = scanner.nextLine()
            inputInteger.addAll(line.split("\\s+".toRegex()).mapNotNull {
                try {
                    it.toInt()
                } catch (ex: Exception) {
                    println("\"$it\" is not a long. It will be skipped.")
                    null
                }
            })
        }
        val totalNum = inputInteger.size
        if (isNaturalSort) {
            outputResult(
                """
        Total numbers: $totalNum.
        Sorted data: ${inputInteger.sorted().joinToString(separator = " ")}
    """.trimIndent()
            )
        } else {
            val finalSortedElements = inputInteger.sortByCount()
            outputResult("Total numbers: $totalNum.")
            finalSortedElements.printSortedByCount(totalNum, outputResult)
        }
    }
}

private fun <E> Map<Int, List<E>>.printSortedByCount(totalNum: Int, outputResult: (String) -> Unit) {
    forEach { mapElement ->
        mapElement.value.forEach {
            outputResult("$it: ${mapElement.key} time(s), ${(mapElement.key * 100) / totalNum}%")
        }
    }
}

private fun <E : Comparable<E>> Iterable<E>.sortByCount(): Map<Int, List<E>> = buildMap {
    this@sortByCount.groupBy {
        this@sortByCount.count { current ->
            current == it
        }
    }.toSortedMap().forEach {
        put(it.key, it.value.toSet().sorted())
    }
}
