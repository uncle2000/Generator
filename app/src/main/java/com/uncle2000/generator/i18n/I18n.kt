package com.uncle2000.generator.i18n

import java.util.regex.Pattern


abstract class I18n(
        val name: String,
        val inAndroid: Boolean = true,
        val inIos: Boolean = true
) : Comparable<I18n>, AnyRow {
    override fun compareTo(other: I18n): Int {
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

    override fun toString(): String {
        return "name:$name"
    }

    fun getNameUpperCase() = name.toUpperCase()
    fun getNameLowerCase() = name.toLowerCase()

    val androidReg = "\\%[0-9]+\\$[dDsS]"
    val parrernAndroid = Pattern.compile(androidReg)
    fun convert2IosStr(str: String?): String {
        if (str == null) return ""
        val m = parrernAndroid.matcher(str)
        var temp: String = str
        while (m.find()) {
            val s = m.group()
            if (s.contains('d') || s.contains('D')) {
                temp = temp.replace(s, int4Ios)
            } else if (s.contains('s') || s.contains('S')) {
                temp = temp.replace(s, string4Ios)
            }
        }
        return temp
    }

    val iosReg = "\\%[d\\@]"
    val parrernIos = Pattern.compile(iosReg)
    fun convert2AndroidStr(str: String?): String {
        if (str == null) return ""
        val m = parrernIos.matcher(str)
        var temp: String = str
        var i = 0
        while (m.find()) {
            val s = m.group()
            i++
            if (s.contains('d') || s.contains('D')) {
                temp = temp.replace(s, "%$i$int4Android")
            } else if (s.contains('@')) {
                temp = temp.replace(s, "%$i$string4Android")
            }
        }
        return temp.replace("\'", "\\\'").replace(".", "\\.")
    }
}

class I18nList(
        name: String,
        val enList: ArrayList<String> = ArrayList<String>(),
        val zhHKList: ArrayList<String> = ArrayList<String>(),
        val zhTWList: ArrayList<String> = ArrayList<String>(),
        val zhCNList: ArrayList<String> = ArrayList<String>()
) : I18n(name) {

    override fun getEnRow(platform: Platform): String {
        val s = StringBuffer()
        s.append("<array name=\"${getNameLowerCase()}\">")
        s.append("\r\n")
        enList.forEach {
            s.append("        <item>$it</item>")
            s.append("\r\n")
        }
        s.append("    </array>\r\n")
        return s.toString()
    }

    override fun getZhHKRow(platform: Platform): String {
        val s = StringBuffer()
        s.append("<array name=\"${getNameLowerCase()}\">")
        s.append("\r\n")
        zhHKList.forEach {
            s.append("        <item>$it</item>")
            s.append("\r\n")
        }
        s.append("    </array>\r\n")
        return s.toString()
    }

    override fun getZhTWRow(platform: Platform): String {
        val s = StringBuffer()
        s.append("<array name=\"${getNameLowerCase()}\">")
        s.append("\r\n")
        zhTWList.forEach {
            s.append("        <item>$it</item>")
            s.append("\r\n")
        }
        s.append("    </array>\r\n")
        return s.toString()
    }

    override fun getZhCNRow(platform: Platform): String {
        val s = StringBuffer()
        s.append("<array name=\"${getNameLowerCase()}\">")
        s.append("\r\n")
        zhCNList.forEach {
            s.append("        <item>$it</item>")
            s.append("\r\n")
        }
        s.append("    </array>\r\n")
        return s.toString()
    }

}

class I18nObj(
        name: String,
        val enStr: String? = null,
        val zhHKStr: String? = null,
        val zhTWStr: String? = null,
        val zhCNStr: String? = null
) : I18n(name) {

    override fun equals(other: Any?): Boolean {
        return other != null
                && (other is I18nObj)
                && name == other.name
                && (enStr == other.enStr || zhHKStr == other.zhHKStr || zhTWStr == other.zhTWStr || zhCNStr == other.zhCNStr)
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun getEnRow(platform: Platform): String {
        return when (platform) {
            Platform.Android -> {
                "<string name=\"${getNameLowerCase()}\">${convert2AndroidStr(enStr)}</string>"
            }
            else -> {
                "\"${getNameUpperCase()}\"=\"${convert2IosStr(enStr)}\";"
            }
        }
    }

    override fun getZhHKRow(platform: Platform): String {
        return when (platform) {
            Platform.Android -> {
                "<string name=\"${getNameLowerCase()}\">${convert2AndroidStr(zhHKStr)}</string>"
            }
            else -> {
                "\"${getNameUpperCase()}\"=\"${convert2IosStr(zhHKStr)}\";"
            }
        }
    }

    override fun getZhTWRow(platform: Platform): String {
        return when (platform) {
            Platform.Android -> {
                "<string name=\"${getNameLowerCase()}\">${convert2AndroidStr(zhTWStr)}</string>"
            }
            else -> {
                "\"${getNameUpperCase()}\"=\"${convert2IosStr(zhTWStr)}\";"
            }
        }
    }

    override fun getZhCNRow(platform: Platform): String {
        return when (platform) {
            Platform.Android -> {
                "<string name=\"${getNameLowerCase()}\">${convert2AndroidStr(zhCNStr)}</string>"
            }
            else -> {
                "\"${getNameUpperCase()}\"=\"${convert2IosStr(zhCNStr)}\";"
            }
        }
    }
}


interface AnyRow {
    fun getEnRow(platform: Platform): String
    fun getZhHKRow(platform: Platform): String
    fun getZhTWRow(platform: Platform): String
    fun getZhCNRow(platform: Platform): String
}