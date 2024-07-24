FIRRTL version 1.2.0
circuit AddTwo :
  module AddTwo :
    input clock : Clock
    input reset : UInt<1>
    input io_in1 : UInt<4> @[cmd22.sc 4:16]
    input io_in2 : UInt<4> @[cmd22.sc 4:16]
    output io_out : UInt<5> @[cmd22.sc 4:16]

    node _io_out_T = add(io_in1, io_in2) @[cmd22.sc 10:22]
    io_out <= _io_out_T @[cmd22.sc 10:12]
