package com.uncle2000.generator.i18n

import com.uncle2000.generator.utils.convert
import java.io.*

enum class Platform { Android, IOS }

const val destinationDirL1 = "C:\\Users\\"
const val destinationDirL2 = "Downloads"
const val destinationDirL3 = "Desktop"
const val fileMime = ".tsv"

const val int4Ios = """%d"""
const val string4Ios = """%@"""

const val int4Android = """${'$'}d"""
const val string4Android = """${'$'}s"""
const val list4Android = """--------"""

fun main(args: Array<String>) {
    val gI18n = GeneratorI18n()

    println("============寻找目标文件=================\n")
    gI18n.catchChicken()
    println("==============寻找完毕=================\n")

    println("==============读入内存=================\n")
    gI18n.killingChicken(gI18n.filePath)
    println("==============读入完毕=================\n")
    if (gI18n.dataList.isEmpty())
        System.exit(1)
    println("==============开始排序=================\n")
    gI18n.dataList.sort()
    println("==============排序完毕=================\n")

    println("==============检查重复=================\n")
    gI18n.cookChicken()
    println("==============检查完毕=================\n")

    println("==============写入文件=================\n")
    gI18n.writeFile(gI18n.filePathList, gI18n.dataList, gI18n.listMap)
    println("==============写入完毕=================\n")

    System.exit(1)
}

class GeneratorI18n {
    var filePath = ""
    private val separator = "\t"
    val dataList = ArrayList<I18nObj>()
    val listMap = HashMap<String, I18nList>()
    val fileNameAndroid = "strings.xml"
    val fileNameIos = "Localizable.strings"
    val filePathList = arrayListOf(
        "../owoh-android/app/src/main/res-common/values-en",
        "../owoh-android/app/src/main/res-common/values",
        "../owoh-android/app/src/main/res-common/values-zh-rTW",
        "../owoh-android/app/src/main/res-common/values-zh-rCN"/*,
            "strings/Base.Iproj",
            "strings/en.Iproj",
            "strings/zh-Hans.Iproj",
            "strings/zh-Hant.Iproj"*/
    )


    fun catchChicken() {
        val dirL1 = File(destinationDirL1)
        val dirL2 = ArrayList<String>()
        val dirL3 = ArrayList<String>()
        if (dirL1.isDirectory) {
            dirL1.listFiles().forEach { file ->
                if (file.isDirectory) {
                    file.listFiles { dir, name ->
                        if (name == destinationDirL2 || name == destinationDirL3) {
                            dirL2.add(dir.toString() + File.separator + name)
                        }
                        false
                    }
                }
            }
            if (!dirL2.isEmpty()) {
                dirL2.forEach { path ->
                    if (File(path).isDirectory) {
                        File(path).listFiles().forEach { file ->
                            if (file.isFile && file.path.endsWith(fileMime)) {
                                dirL3.add(file.path)
                            }
                        }
                    }
                }
            } else {
                System.err.println("==error [2]==")
                System.exit(1)
            }

            when (dirL3.size) {
                0 -> {
                    System.err.println("==error [3]==")
                    System.exit(1)
                }
                2 -> {
                    System.err.println("==error [4]==")
                    System.exit(1)
                }
                else -> {
                    filePath = dirL3[0]
                    return
                }
            }
        }
        System.err.println("==error [1]==")
        System.exit(1)
    }

    fun killingChicken(filePath: String) {
        val errorList = ArrayList<String>()
        //BufferedReader是可以按行读取文件
        val inputStream = FileInputStream(filePath)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))

        bufferedReader.lineSequence().forEach {
            when {
                it.length <= (4 + 5) -> {//,,,,
                }
                it.split(separator).size > 5 || it.split(separator).size < 4 -> {
                    errorList.add(it)
                }
                it.contains(list4Android) -> {
                    val row = it.split(separator)
                    val name = row[0].replace("-", "")
                    if (listMap.keys.contains(name)) {
                        listMap[name]?.enList?.add(row[1])
                        listMap[name]?.zhHKList?.add(row[2])
                        listMap[name]?.zhTWList?.add(row[2])
                        listMap[name]?.zhCNList?.add(row[3])
                    } else {
                        val l = I18nList(name)
                        l.enList.add(row[1])
                        l.zhHKList.add(row[2])
                        l.zhTWList.add(row[2])
                        l.zhCNList.add(row[3])
                        listMap[name] = l
                    }
                }
                else -> {
                    val row = it.split(separator)
                    if (row[0].isNotBlank() && !row[0].contains(" ")) {
                        val obj = I18nObj(row[0], row[1], row[2], row[2], row[3])
                        if (!dataList.contains(obj)) {
                            dataList.add(obj)
                        }
                    }
                }
            }
        }

        //close
        inputStream.close()
        bufferedReader.close()

        errorList.forEach {
            println("=================error==============" + it)
        }
//        dataList.forEach {
//            println("===============================" + it)
//        }

    }

    fun writeFile(
        filePath: ArrayList<String>,
        dateList: ArrayList<I18nObj>,
        listMap: HashMap<String, I18nList>? = null
    ) {
        filePath.forEachIndexed { index, s ->
            val dir = File(s)
            dir.mkdirs()
            val file = File(s + File.separator + if (index < 4) fileNameAndroid else fileNameIos)
            file.createNewFile()
            val out = BufferedWriter(FileWriter(file))
            if (index in 0..3)
                out.write("<resources>\r\n")
            else {
                out.write("/*\r\n")
                out.write(waringLog.toString())
                out.write("*/\r\n")
            }

            dateList.forEach {
                when (index) {
                    0 -> out.write("    " + it.getEnRow(Platform.Android) + "\r\n")
                    1 -> out.write("    " + it.getZhHKRow(Platform.Android) + "\r\n")
                    2 -> out.write("    " + it.getZhTWRow(Platform.Android) + "\r\n")
                    3 -> out.write("    " + it.getZhCNRow(Platform.Android) + "\r\n")
//                    4 -> out.write(it.getEnRow(Platform.IOS) + "\r\n")
//                    5 -> out.write(it.getEnRow(Platform.IOS) + "\r\n")
//                    6 -> out.write(it.getZhCNRow(Platform.IOS) + "\r\n")
//                    7 -> out.write(it.getZhHKRow(Platform.IOS) + "\r\n")
                }
            }
            listMap?.keys?.forEach {
                when (index) {
                    0 -> out.write("    " + listMap[it]?.getEnRow(Platform.Android))
                    1 -> out.write("    " + listMap[it]?.getZhHKRow(Platform.Android))
                    2 -> out.write("    " + listMap[it]?.getZhTWRow(Platform.Android))
                    3 -> out.write("    " + listMap[it]?.getZhCNRow(Platform.Android))
                }
            }
            if (index in 0..3)
                out.write("</resources>\r\n")

            out.flush()
            out.close()
        }

    }


    val waringLog = StringBuffer()
    val waringList = java.util.ArrayList<String>()
    fun cookChicken() {
        waringLog.setLength(0)
        waringList.clear()
        val list1 = dataList.convert { it.enStr }
        val list2 = dataList.convert { it.zhHKStr }
        val list3 = dataList.convert { it.zhTWStr }
        val list4 = dataList.convert { it.zhCNStr }

        list1.forEach { key ->
            if (!waringList.contains(key)) {
                val count = list1.count { it == key }
                if (count > 1) {
                    waringList.add(key ?: "")
                    waringLog.append("英文中： \"${key}\" 出现了 ${count} 次\n")
                }
            }
        }
        list2.forEach { key ->
            if (!waringList.contains(key)) {
                val count = list2.count { it == key }
                if (count > 1) {
                    waringList.add(key ?: "")
                    waringLog.append("繁体中文中： \"${key}\" 出现了 ${count} 次\n")
                }
            }
        }
        list4.forEach { key ->
            if (!waringList.contains(key)) {
                val count = list4.count { it == key }
                if (count > 1) {
                    waringList.add(key ?: "")
                    waringLog.append("简体体中文中： \"${key}\" 出现了 ${count} 次\n")
                }
            }
        }
    }
}
