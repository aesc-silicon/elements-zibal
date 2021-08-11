package zibal.cores

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

import spinal.core._
import vexriscv.ip.{DataCacheConfig, InstructionCacheConfig}
import vexriscv.plugin._
import vexriscv.{VexRiscv, VexRiscvConfig, plugin}

case class VexRiscvCoreParameter(
  plugins: ArrayBuffer[Plugin[VexRiscv]]
) {}

object VexRiscvCoreParameter {
  def default(resetAddress: BigInt) = VexRiscvCoreParameter(
    plugins = ArrayBuffer(
      new IBusSimplePlugin(
        resetVector = resetAddress,
        cmdForkOnSecondStage = true,
        cmdForkPersistence = true,
        prediction = NONE,
        catchAccessFault = false,
        compressedGen = false
      ),
      new DBusSimplePlugin(
        catchAddressMisaligned = false,
        catchAccessFault = false,
        earlyInjection = false
      ),
      new StaticMemoryTranslatorPlugin(
        ioRange = _(31 downto 28) === 0xF
      ),
      new DecoderSimplePlugin(
        catchIllegalInstruction = true
      ),
      new RegFilePlugin(
        regFileReadyKind = plugin.ASYNC,
        zeroBoot = false
      ),
      new IntAluPlugin,
      new SrcPlugin(
        separatedAddSub = false,
        executeInsertion = true
      ),
      new FullBarrelShifterPlugin(earlyInjection = true),
      new HazardSimplePlugin(
        bypassExecute = true,
        bypassMemory = true,
        bypassWriteBack = true,
        bypassWriteBackBuffer = true,
        pessimisticUseSrc = false,
        pessimisticWriteRegFile = false,
        pessimisticAddressMatch = false
      ),
      new MulPlugin,
      new DivPlugin,
      new CsrPlugin(
        CsrPluginConfig(
          catchIllegalAccess = true,
          mvendorid = 0,
          marchid = 0,
          mimpid = 0,
          mhartid = 0x0,
          misaExtensionsInit = 0x001100,
          misaAccess = CsrAccess.READ_WRITE,
          mtvecAccess = CsrAccess.READ_WRITE,
          mtvecInit = resetAddress,
          mepcAccess = CsrAccess.READ_WRITE,
          mscratchGen = true,
          mcauseAccess = CsrAccess.READ_WRITE,
          mbadaddrAccess = CsrAccess.READ_WRITE,
          mcycleAccess = CsrAccess.READ_WRITE,
          minstretAccess = CsrAccess.READ_WRITE,
          ucycleAccess = CsrAccess.READ_ONLY,
          wfiGenAsWait = true,
          ecallGen = true
        )
      ),
      new BranchPlugin(
        earlyBranch = true,
        catchAddressMisaligned = true
      ),
      new YamlPlugin("../build/"+System.getenv("SOC")+"/"+System.getenv("BOARD")+
                     "/zibal/VexRiscv.yaml")
    )
  )
}

object VexRiscvCore extends App {
  def cpu() = new VexRiscv(
    config = VexRiscvConfig(
      plugins = VexRiscvCoreParameter.default(0x80000000l).plugins
    )
  )
  SpinalVerilog(cpu())
}
