#!/usr/bin/env python3

import binascii
import os

romsize = 4096
filename = "kernel.img"

def swap32(x):
    return x[6:8] + x[4:6] + x[2:4] + x[0:2]

def hex2bin(x):
    tmp = ""
    for i in range(0, 8):
        tmp = tmp + bin(int(x[i], 16))[2:].zfill(4)
    return tmp


data = open(filename, "rb")

fsz = int(os.path.getsize(filename) / 4)
for c in range(0, fsz):
    opcode = swap32(binascii.hexlify(data.read(4)))
    print(hex2bin(str(opcode.decode('utf-8'))))

for i in range(fsz, int(romsize / 4)):
    print("00000000000000000000000000010011")
