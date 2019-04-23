package com.uncle2000.generator.i18n

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*


const val filePath = "sheep.tsv"
fun main(args: Array<String>) {
    val gI18n = GenerartorI18n()
    gI18n.killingSheep(filePath)
    if (gI18n.dataList.isEmpty())
        System.exit(1)

    gI18n.dataList.sort()
    gI18n.createSheep4Android()



    System.exit(1)
}

class GenerartorI18n {
    private val separator = "\t"
    val dataList = ArrayList<I18nObj>()


    fun killingSheep(filePath: String) {
        val errorList = ArrayList<String>()
        //BufferedReader是可以按行读取文件
        val inputStream = FileInputStream(filePath)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))

        bufferedReader.lineSequence().forEach {
            when {
                it.length <= (4 + 5) -> {//,,,,
                }
                it.split(separator).size > 5 -> {
                    errorList.add(it)
                }
                else -> {
                    val row = it.split(separator)
                    dataList.add(I18nObj(row[0], row[1], row[2], row[2], row[3]))
                }
            }
        }

        //close
        inputStream.close()
        bufferedReader.close()

        errorList.forEach {
            println("=================error==============" + it)
        }
        dataList.forEach {
            println("===============================" + it)
        }
    }

    fun createSheep4Android() {
        dataList.forEach {

        }
    }
}


class I18nObj(
    val name: String,
    val enStr: String? = null,
    val zhHKStr: String? = null,
    val zhTWStr: String? = null,
    val zhCNStr: String? = null,
    val justInAndroid: Boolean = false
) : Comparable<I18nObj> {

    override fun compareTo(other: I18nObj): Int {
        val length1 = name.length
        val length2 = other.name.length

        var limit = Math.min(length1, length2)

        val a = name.toCharArray()
        val b = other.name.toCharArray()

        for (i in 0 until limit) {
            val c1 = if (a[i] >= 'a') a[i] else (a[i] + 32)
            val c2 = if (b[i] >= 'a') b[i] else (b[i] + 32)
            if (c1 != c2) {
                return c1 - c2
            }
        }

        return length1 - length2
    }

    override fun equals(other: Any?): Boolean {
        return other != null
                && (other is I18nObj)
                && name == other.name
                && (enStr == other.enStr || zhHKStr == other.zhHKStr || zhTWStr == other.zhTWStr || zhCNStr == other.zhCNStr)
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "name:$name"
    }

    fun getNameUpperCase() = name.toUpperCase()
    fun getNameLowerCase() = name.toLowerCase()
}
