/*
 * Copyright (c) 2021 Phytec Messtechnik GmbH
 */

package zibal.misc

import java.io._
import spinal.core._
import zibal.blackboxes.xilinx.a7._


object XilinxTools {

  case class Xdc(config: ElementsConfig.ElementsConfig) {
    def generate(io: Data, filename: String) = {
      val file = s"${config.zibalBuildPath}${filename}.xdc"
      val writer = new PrintWriter(new File(file))

      io.component.getOrdredNodeIo.foreach { baseType =>
        val name = baseType.getName()
        val instance = baseType.parent match {
          case inst: XilinxCmosIo.XilinxCmosIo => inst.asInstanceOf[XilinxCmosIo.XilinxCmosIo]
          case inst: XilinxLvdsIo.Pos => inst.asInstanceOf[XilinxLvdsIo.Pos]
          case inst: XilinxLvdsIo.Neg => inst.asInstanceOf[XilinxLvdsIo.Neg]
        }
        val pin = instance.getPin()
        val ioStandard = instance.getIoStandard()
        val clockSpeed = instance.getClockSpeed().toTime.toBigDecimal
        if (clockSpeed != 1) {
          val time = (clockSpeed / 1.0e-9).floatValue()
          writer.write(s"create_clock -name ${name}_pin -period $time [get_ports {$name}];\n")
        }
        writer.write(s"set_property PACKAGE_PIN $pin [get_ports {$name}]\n")
        writer.write(s"set_property IOSTANDARD $ioStandard [get_ports {$name}]\n")
        if (!instance.comment_.equals("")) {
          writer.write(s"${instance.comment_}\n")
        }
      }
      writer.close()
    }
  }
}
