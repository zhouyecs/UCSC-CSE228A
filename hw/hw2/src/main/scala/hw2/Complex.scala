package hw2

import chisel3._

// Note ??? will compile but not work at runtime.

/**
  * @param width : Int
  * ___________________
  * @field real:  SInt
  * @field imag:  SInt
  * @method def sumReal(that:  ComplexNum): SInt 
  * @method def sumImag(that:  ComplexNum): SInt 
  * @method def diffReal(that: ComplexNum): SInt
  * @method def diffImag(that: ComplexNum): SInt
  */
class ComplexNum(width: Int) extends Bundle {
  val real = SInt(width.W)
  val imag = SInt(width.W)

  def sumReal(that: ComplexNum): SInt = {
    this.real + that.real
  }

  def sumImag(that: ComplexNum): SInt = {
    this.imag + that.imag
  }

  def diffReal(that: ComplexNum): SInt = {
    this.real - that.real
  }

  def diffImag(that: ComplexNum): SInt = {
    this.imag - that.imag
  }
}


/**
  * @param width     : Int
  * @param onlyAdder : Boolean
  * ___________________
  * @field doAdd: Option[Bool]  (Input)
  * @field c0:  ComplexNum      (Input)
  * @field c1:  ComplexNum      (Input)
  * @field out: ComplexNum      (Output)
  */
class ComplexALUIO(width: Int, onlyAdder: Boolean) extends Bundle {
  val doAdd = if(onlyAdder) Some(Input(Bool())) else None
  val c0 = Input(new ComplexNum(width))
  val c1 = Input(new ComplexNum(width))
  val out = if(onlyAdder) Some(Output(new ComplexNum(width + 1))) else Some(Output(new ComplexNum(width)))
}


/**
  * @param width     : Int
  * @param onlyAdder : Boolean
  */
class ComplexALU(width: Int, onlyAdder: Boolean) extends Module {
  val io = IO(new ComplexALUIO(width, onlyAdder))
  when (io.doAdd.getOrElse(false.B)) {
    io.out.get.real := io.c0.sumReal(io.c1)
    io.out.get.imag := io.c0.sumImag(io.c1)
  } .otherwise {
    io.out.get.real := io.c0.diffReal(io.c1)
    io.out.get.imag := io.c0.diffReal(io.c1)
  }
}
