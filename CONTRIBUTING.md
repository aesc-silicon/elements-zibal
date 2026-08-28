<!--
SPDX-FileCopyrightText: 2026 aesc silicon

SPDX-License-Identifier: CERN-OHL-W-2.0
-->

# Contributing to Zibal

Thanks for your interest. Zibal generates SoC designs written in SpinalHDL for
multiple boards and architectures, and carries the build flows that take them from
RTL to a bitstream or a GDSII. Contributions of every size are welcome — a
corrected board pin definition is as useful as a new platform.

## What lives here

Zibal is a library. It contains no chip of its own: a design is assembled by a
project that consumes Zibal and instantiates one of its platforms.

* **Platforms** — `hardware/scala/zibal/platform/`. A platform wires a CPU,
  interconnect, memories and the system peripherals into a reusable skeleton;
  `Common.scala` holds the parts shared between them.
* **Boards** — `hardware/scala/elements/board/`. Pin assignments, clock and reset
  parameters and IO standards for a physical board.
* **EDA tooling** — `hardware/scala/zibal/misc/`. Generators that turn an
  elaborated design into what the downstream tools need: `OpenROADTools`,
  `LatticeTools`, `XilinxTools`, `LibreLaneTools`, plus `RenodeTools`,
  `ReportTools`, `BaremetalTools` and `ZephyrTools`.
* **Flow scripts** — `eda/` for Cadence, Lattice and Xilinx; `openocd/` for JTAG
  and flashing; `gdb/` for debug entry points.
* **Task definitions** — `Taskfile.yaml`, a set of internal `lib-*` tasks that
  consuming projects include rather than reimplement.

Peripherals and system IP do **not** live here — they come from
[Nafarr](https://github.com/aesc-silicon/elements-nafarr), which Zibal depends on.
If your change is to an IP core, its register map, its driver or its
documentation, open it there instead.

## Setting up

You need Java 11 and sbt. Zibal also needs three sibling repositories checked out
next to it, which is the step most easily missed — `build.sbt` refers to them by
relative path, and VexiiRiscv is consumed as a locally published artifact.

```bash
# All of these must sit next to each other in the same directory.
git clone --recurse-submodules https://github.com/aesc-silicon/elements-zibal.git zibal

cd zibal && sbt compile
```

## Before you open a pull request

**Compile** — this is what CI runs, and Zibal currently has no test suite
(`test/scala` is empty), so it is the only automated check on the code itself:

```bash
sbt compile
```

**Scala formatting**:

```bash
sbt scalafmt        # fix
sbt scalafmtCheck   # what CI runs
```

**Licensing** — enforced by `reuse lint`. Every file needs an
`SPDX-FileCopyrightText` and an `SPDX-License-Identifier` header, or an entry in
`REUSE.toml`. Two licences, and the boundary matters:

* `CERN-OHL-W-2.0` for hardware, tooling and documentation
* `Apache-2.0` for software

Copy the header style from a neighbouring file in the same directory.

### Verifying a change

Because Zibal produces no design on its own, "it compiles" is a weak claim. A
change to a platform, a board or a flow generator is only really tested by
generating a design that uses it and running the relevant flow — an RTL
simulation, a bitstream build, or an ASIC flow.

Please say in the pull request which of those you ran and against what. If you
have no board, say so; a simulation or a synthesis run is still far better
evidence than a successful compile, and someone else can cover the rest.

If your change affects generated collateral — an SDC file, an ORFS export, an
OpenOCD configuration — a before/after diff of the generated output is the most
convincing thing you can include, and it is cheap to produce.

## Coding style

**SpinalHDL / Scala.** `scalafmt` handles most of it, with two rules it cannot
enforce:

* Never pad `=` or `:=` with extra spaces to align them across lines. Exactly one
  space on each side.
* A Scala `if` used directly as the right-hand side of a `:=` must be wrapped in
  parentheses, or the parser rejects it:
  `d.data := (if (cond) a else b)`

**Shell and Tcl** in `eda/`, `openocd/` and `gdb/` follow the style of the
surrounding files.

**Comments.** Explain why something is the way it is, not what the next line does.
This matters more here than in most code: a magic delay, a setback distance or a
clock-group decision is rarely self-evident, and the reason is usually a tool
limitation or a physical constraint that the next person cannot re-derive.

## A note on constraints and timing

Several generators in `misc/` emit timing constraints. Be careful with anything
that excludes paths from analysis — an over-broad `set_clock_groups -asynchronous`
silently removes real cross-domain paths from static timing analysis, and the
resulting bug shows up as flaky hardware rather than as a report. Clocks belong in
the same group unless a real clock-domain crossing sits on every path between
them.

## Commits and pull requests

Commit subjects use the area prefix visible in the history:

```
hardware: zibal: platform: Nitrogen: Rename HyperRAM
openocd: vexiiriscv: Reset target after flashing
Taskfile: lib-view-simulate: Add "savefile"
```

One logical change per commit, with a body explaining *why*, wrapped at about 72
columns. Every commit needs a `Signed-off-by:` line, certifying the
[Developer Certificate of Origin](https://developercertificate.org/):

```bash
git commit -s
```

## Reporting bugs

Use the bug report template. The details that matter most are which platform and
board, which flow (simulation, FPGA, ASIC, debug/flashing), and the exact commands
you ran. The same symptom in simulation and on hardware usually has a completely
different cause.

## Questions

Open a
[discussion](https://github.com/aesc-silicon/elements-zibal/discussions) if you
are not sure whether something is a bug, want to propose a new platform or board,
or are looking for a place to start.
