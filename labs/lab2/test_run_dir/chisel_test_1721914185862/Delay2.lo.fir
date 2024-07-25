FIRRTL version 1.2.0
circuit Delay2 :
  module Delay2 :
    input clock : Clock
    input reset : UInt<1>
    input io_in : UInt<5> @[cmd2.sc 2:16]
    output io_out : UInt<5> @[cmd2.sc 2:16]

    reg delay1 : UInt<5>, clock with :
      reset => (UInt<1>("h0"), delay1) @[cmd2.sc 8:21]
    io_out <= delay1 @[cmd2.sc 10:12]
    delay1 <= io_in @[cmd2.sc 9:12]
