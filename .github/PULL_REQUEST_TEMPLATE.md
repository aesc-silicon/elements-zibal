<!--
SPDX-FileCopyrightText: 2026 aesc silicon

SPDX-License-Identifier: CERN-OHL-W-2.0
-->

## What does this change?

<!-- One or two sentences. Link the issue it closes, if there is one. -->

## How was it tested?

<!--
Zibal produces no design on its own, so "it compiles" is a weak claim. Name what
you generated and which flow you ran — RTL simulation, a bitstream build, an ASIC
flow, or hardware. For generated collateral such as constraints or an ORFS export,
a before/after diff is the most convincing thing you can include.

If you have no board, say so. That is normal.
-->

## Checklist

- [ ] `sbt compile` passes
- [ ] `sbt scalafmtCheck` passes
- [ ] `reuse lint` passes — SPDX headers on new files
- [ ] A design using the change was generated and the relevant flow run
- [ ] Every commit has `Signed-off-by:` (`git commit -s`)
- [ ] One logical change per commit
