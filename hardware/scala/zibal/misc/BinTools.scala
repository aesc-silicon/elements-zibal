/*
 * Copyright (c) 2019 Phytec Messtechnik GmbH
 */

package zibal.misc

import java.io.FileInputStream
import spinal.core.{Data, Mem}

object BinTools {
  def initRam[T <: Data](ram : Mem[T], onChipRamBinFile : String): Unit ={
    val initContent = Array.fill[BigInt](ram.wordCount)(0)
    val readTmp = Array.fill[Byte](4)(0)
    val initFile = new FileInputStream(onChipRamBinFile)
    for ((e, i) <- initContent.zipWithIndex) {
      if (initFile.read(readTmp) == 0) {
        initContent(i) = 13
      } else {
        initContent(i) = BigInt(1, readTmp.reverse)
      }
    }
    ram.initBigInt(initContent)
  }
}
