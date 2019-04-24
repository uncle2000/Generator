package com.uncle2000.generator.utils

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader


object FileUtils {
    /**
     * 将文本文件中的内容读入到buffer中
     * @param buffer buffer
     * @param filePath 文件路径
     * @throws IOException 异常
     * @author cn.outofmemory
     * @date 2013-1-7
     */
    @Throws(IOException::class)
    fun readToBuffer(buffer: StringBuffer, filePath: String) {
        val `is` = FileInputStream(filePath)
        var line: String? // 用来保存每行读取的内容
        val reader = BufferedReader(InputStreamReader(`is`))
        line = reader.readLine() // 读取第一行
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line) // 将读到的内容添加到 buffer 中
            buffer.append("\n") // 添加换行符
            line = reader.readLine() // 读取下一行
        }
        reader.close()
        `is`.close()
    }

    /**
     * 读取文本文件内容
     * @param filePath 文件所在路径
     * @return 文本内容
     * @throws IOException 异常
     * @author cn.outofmemory
     * @date 2013-1-7
     */
    @Throws(IOException::class)
    fun readFile(filePath: String): String {
        val sb = StringBuffer()
        FileUtils.readToBuffer(sb, filePath)
        return sb.toString()
    }

}