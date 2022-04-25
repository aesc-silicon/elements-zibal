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
  val yamlPath =
    "build/" + System.getenv("SOC") + "/" + System.getenv("BOARD") + "/zibal/VexRiscv.yaml"
  def realtime(resetAddress: BigInt) = VexRiscvCoreParameter(
    plugins = ArrayBuffer(
      new IBusSimplePlugin(
        resetVector = resetAddress,
        cmdForkOnSecondStage = true,
        cmdForkPersistence = true,
        prediction = NONE,
        catchAccessFault = false,
        compressedGen = true
      ),
      new DBusSimplePlugin(
        catchAddressMisaligned = false,
        catchAccessFault = false,
        earlyInjection = false
      ),
      new StaticMemoryTranslatorPlugin(
        ioRange = _(31 downto 28) === 0xf
      ),
      new DecoderSimplePlugin(
        catchIllegalInstruction = true
      ),
      new RegFilePlugin(
        regFileReadyKind = plugin.ASYNC
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
          ecallGen = true,
          ebreakGen = true
        )
      ),
      new BranchPlugin(
        earlyBranch = false,
        catchAddressMisaligned = true
      ),
      new YamlPlugin(yamlPath)
    )
  )
  def mcu(resetAddress: BigInt) = VexRiscvCoreParameter(
    plugins = ArrayBuffer(
      new IBusCachedPlugin(
        resetVector = resetAddress,
        // prediction = DYNAMIC_TARGET,
        prediction = DYNAMIC,
        compressedGen = true,
        config = InstructionCacheConfig(
          cacheSize = 4096,
          bytePerLine = 32,
          wayCount = 1,
          addressWidth = 32,
          cpuDataWidth = 32,
          memDataWidth = 32,
          catchIllegalAccess = true,
          catchAccessFault = true,
          asyncTagMemory = false,
          twoCycleRam = false,
          twoCycleCache = true
        )
      ),
      new DBusCachedPlugin(
        config = new DataCacheConfig(
          cacheSize = 4096,
          bytePerLine = 32,
          wayCount = 1,
          addressWidth = 32,
          cpuDataWidth = 32,
          memDataWidth = 32,
          catchAccessError = true,
          catchIllegal = true,
          catchUnaligned = true
        )
      ),
      new StaticMemoryTranslatorPlugin(
        ioRange = _(31 downto 28) === 0xf
      ),
      new DecoderSimplePlugin(
        catchIllegalInstruction = true
      ),
      new RegFilePlugin(
        regFileReadyKind = plugin.ASYNC
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
          ecallGen = true,
          ebreakGen = true
        )
      ),
      new BranchPlugin(
        earlyBranch = false,
        catchAddressMisaligned = true
      ),
      new YamlPlugin(yamlPath)
    )
  )
}

object VexRiscvCore extends App {
  def cpu() = new VexRiscv(
    config = VexRiscvConfig(
      plugins = VexRiscvCoreParameter.realtime(0x80000000L).plugins
    )
  )
  SpinalVerilog(cpu())
}
