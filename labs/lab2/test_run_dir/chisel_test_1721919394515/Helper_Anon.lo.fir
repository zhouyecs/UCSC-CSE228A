FIRRTL version 1.2.0
circuit Helper_Anon :
  module Helper_Anon :
    input clock : Clock
    input reset : UInt<1>
    input io_in1 : UInt<4> @[cmd34.sc 3:24]
    input io_in2 : UInt<4> @[cmd34.sc 3:24]
    output io_out : UInt<5> @[cmd34.sc 3:24]

    node io_out_aExtended = io_in1 @[cmd33.sc 8:{29,29}]
    node io_out_bExtended = io_in2 @[cmd33.sc 9:{29,29}]
    node io_out_sum = add(io_out_aExtended, io_out_bExtended) @[cmd33.sc 12:23]
    io_out <= io_out_sum @[cmd34.sc 8:20]
