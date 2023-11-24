package zibal.misc

import java.io._
import spinal.core._
import scala.collection.mutable
import nafarr.blackboxes.lattice.ecp5._

object LatticeTools {
  case class Lpf(config: ElementsConfig.ElementsConfig) {
    def generate(io: Data) = {
      val file = s"${config.zibalBuildPath}${config.className}.lpf"
      val writer = new PrintWriter(new File(file))
      SpinalInfo(s"Generate ${config.className}.lpf")
      writer.write("# IOs\n")
      io.component.getOrdredNodeIo.foreach { baseType =>
        val name = baseType.getName()
        val instance = baseType.parent match {
          case inst: LatticeCmosIo.LatticeCmosIo => inst.asInstanceOf[LatticeCmosIo.LatticeCmosIo]
        }
        val pin = instance.getPin()
        writer.write(s"""LOCATE COMP "${name}" SITE "${pin}";\n""")

        val clockSpeed = instance.getClockSpeed().toBigDecimal.toBigInt
        if (clockSpeed != 1) {
          writer.write(s"""FREQUENCY PORT "${name}" ${clockSpeed} Hz;\n""")
        }

        val ioStandard = instance.getIoStandard()
        writer.write(s"""IOBUF PORT "${name}" IO_TYPE=${ioStandard}""")
        if (!instance.pullType.equals("")) {
          writer.write(s" PULLMODE=${instance.pullType}")
        }
        if (instance.isOpendrain) {
          writer.write(s" OPENDRAIN=ON")
        }
        writer.write(s";\n")
        if (!instance.comment_.equals("")) {
          writer.write(s"${instance.comment_}\n")
        }
      }
      writer.close()
    }
  }
}
