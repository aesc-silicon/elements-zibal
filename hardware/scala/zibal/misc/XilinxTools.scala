package zibal.misc

import java.io._
import spinal.core._
import scala.collection.mutable
import nafarr.blackboxes.xilinx.a7._

object XilinxTools {

  case class Xdc(config: ElementsConfig.ElementsConfig) {
    var clocks = List[(String, String)]()
    var vrefs = List[(Int, Double)]()
    def addGeneratedClock(clock: Bool) = {
      val path = clock.getComponent().getPath().split("/", 2)(1)
      // FIXME: PLLE2_BASE is not named: soc/[PLLE2_BASE]/CLKOUT0
      // core/Component.scala L285
      // clocks = clocks :+ (clock.getName(), path + "/" + clock.getName())
    }
    def addInternalVref(bank: Int, vref: Double) = {
      vrefs = vrefs :+ (bank, vref)
    }
    def generate(io: Data, emitVoltage: Boolean = true, emitSpi: Boolean = false) = {
      val file = s"${config.zibalBuildPath}${config.className}.xdc"
      val writer = new PrintWriter(new File(file))
      SpinalInfo(s"Generating ${config.className}.xdc")
      writer.write("# CONFIG\n")
      if (emitVoltage) {
        writer.write("set_property CFGBVS VCCO [current_design]\n")
        writer.write("set_property CONFIG_VOLTAGE 3.3 [current_design]\n")
      }
      if (emitSpi) {
        writer.write("set_property BITSTREAM.CONFIG.SPI_BUSWIDTH 4 [current_design]\n")
        writer.write("set_property CONFIG_MODE SPIx4 [current_design]\n")
        writer.write("set_property BITSTREAM.CONFIG.CONFIGRATE 50 [current_design]\n")
      }
      writer.write("# IOs\n")
      for ((bank, vref) <- vrefs) {
        writer.write(s"set_property INTERNAL_VREF ${vref} [get_iobanks ${bank}]\n")
      }
      io.component.getOrdredNodeIo.foreach { baseType =>
        val name = baseType.getName()
        val instance = baseType.parent match {
          case inst: XilinxCmosIo.XilinxCmosIo => inst.asInstanceOf[XilinxCmosIo.XilinxCmosIo]
          case inst: XilinxLvdsInput.Pos => inst.asInstanceOf[XilinxLvdsInput.Pos]
          case inst: XilinxLvdsInput.Neg => inst.asInstanceOf[XilinxLvdsInput.Neg]
          case inst: XilinxLvdsOutput.Pos => inst.asInstanceOf[XilinxLvdsOutput.Pos]
          case inst: XilinxLvdsOutput.Neg => inst.asInstanceOf[XilinxLvdsOutput.Neg]
        }
        val pin = instance.getPin()
        val ioStandard = instance.getIoStandard()
        val clockSpeed = instance.getClockSpeed().toTime.toBigDecimal
        if (clockSpeed != 1) {
          val time = (clockSpeed / 1.0e-9).floatValue()
          writer.write(s"create_clock -name ${name}_pin -period $time [get_ports {$name}]\n")
        }
        if (!instance.dedicatedClockRoute) {
          writer.write(s"set_property CLOCK_DEDICATED_ROUTE FALSE [get_nets {$name}]\n")
        }
        if (!instance.ioSlew.equals("")) {
          writer.write(s"set_property SLEW ${instance.ioSlew} [get_ports {$name}]\n")
        }
        if (!instance.ioTerm.equals("")) {
          writer.write(s"set_property IN_TERM ${instance.ioTerm} [get_ports {$name}]\n")
        }
        if (!instance.pullType.equals("")) {
          writer.write(s"set_property ${instance.pullType} TRUE [get_ports {$name}]\n")
        }
        writer.write(s"set_property PACKAGE_PIN $pin [get_ports {$name}]\n")
        writer.write(s"set_property IOSTANDARD $ioStandard [get_ports {$name}]\n")
        if (!instance.comment_.equals("")) {
          writer.write(s"${instance.comment_}\n")
        }
      }
      writer.write("# GENERATED CLOCKs\n")
      for ((name, pin) <- clocks) {
        writer.write(s"create_generated_clock -name clk_${name} [get_pins ${pin}]\n")
      }
      writer.close()
    }
  }
}
