package zibal.misc

import java.io.FileInputStream
import spinal.core.{Data, Mem}

object BinTools {
  def initRam[T <: Data](
      ram: Mem[T],
      onChipRamBinFile: String,
      swapEndianness: Boolean = false
  ): Unit = {
    val initContent = Array.fill[BigInt](ram.wordCount)(0)
    val readTmp = Array.fill[Byte](ram.width / 8)(0)
    val initFile = new FileInputStream(onChipRamBinFile)
    for ((e, i) <- initContent.zipWithIndex) {
      if (initFile.read(readTmp) == 0) {
        initContent(i) = 13
      } else {
        initContent(i) = BigInt(1, if (swapEndianness) readTmp else readTmp.reverse)
      }
    }
    ram.initBigInt(initContent)
  }
}
