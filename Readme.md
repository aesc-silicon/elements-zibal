# Elements MCU

The elements MCU is a microcntroller written in spinalHDL.

## Prerquisites:

### Xilinx Vivado:
Download and install the latest Xilinx Vivado.
https://www.xilinx.com/products/design-tools/vivado.html
Note I: You just need to install the free WepPack edition of Vivado.
Note II: Install path should be: "/opt/xilinx/Vivado/<Version>"

### Scala and sbt
Follow this guide to install Scala and sbt:
https://scala-lang.org/download/
Note: We don't use IntelliJ, so you have to take "Download sbt" at step 2.

### GDB
`sudo apt install gdb`

### Create the project folder:
`mkdir elements-project && cd elements-project`

### Clone all needed repos:
The MCU:
`git clone git@github.com:phytec/elements-mcu-dev.git`

Zephyr:
`git clone --recursive -b stable git@github.com:phytec/elements-zephyr-dev.git elements-zephyr-dev/zephyr`

VexRiscv
`git clone git@github.com:phytec/elements-VexRiscv-dev.git`

OpenoCD:
`git clone --recursive git@github.com:phytec/elements-openocd-dev.git`

### Install Zephyr SDK and West:
Follow  this guide:
https://wiki.phytec.com/pages/viewpage.action?pageId=173540482#Hydrogen-SetupzepyhrforHydrogen
Note I: Zephyr SKD should be also installed in the "elements-project" folder
Note II: Use the previously cloned zephyr repo

### Build OpenOCD:
```  
cd elements-openocd-dev

./configure

./bootstrap

make

cd ..
```

## Working with the MCU:

Go to MCU Folder:
`cd elements-mcu-dev`

### Run full simulation of the MCU
`make allSim`
 
### Run full synthesis and bitstream generation
`make allSynth`

### Flash the bitsream to the FPGA
`make flash`


