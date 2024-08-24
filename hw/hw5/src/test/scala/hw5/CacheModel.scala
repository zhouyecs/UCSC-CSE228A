package hw5

import CacheModel.CacheBlockModel
import scala.collection.mutable.ArrayBuffer


abstract class CacheModel(p: CacheParams, externalMem: ArrayBuffer[CacheBlockModel]) {
  require(p.bitsPerWord < 32)

  def isHit(addr: Int): Boolean

  def getReferenceToBlock(addr: Int): CacheBlockModel

  def findCacheAddressFields(addr: Int): (Int, Int, Int) = {
    def extractBits(hiIndex: Int, loIndex: Int): Int = { // bit indices are inclusive (like Chisel/Verilog)
      val nativeLength = 32
      require(p.addrLen < nativeLength - 1)
      val lShamt = nativeLength - hiIndex - 1
      val rShamt = loIndex + lShamt
      (addr << lShamt) >>> rShamt
    }
    val offset = extractBits(p.numOffsetBits - 1, 0)
    val index = extractBits(p.numOffsetBits + p.numIndexBits - 1, p.numOffsetBits)
    val tag = extractBits(p.addrLen - 1, p.numOffsetBits + p.numIndexBits)
    (tag, index, offset)
  }

  def calcBlockAddr(tag: Int, index: Int): Int = (tag << p.numIndexBits) | index

  def read(addr: Int): Int = {
    val (tag, index, offset) = findCacheAddressFields(addr)
    getReferenceToBlock(addr)(offset)
  }

  def write(addr: Int, wData: Int): Unit = {
    val (tag, index, offset) = findCacheAddressFields(addr)
    getReferenceToBlock(addr)(offset) = wData
  }
}


class DMCacheModel(p: CacheParams, externalMem: ArrayBuffer[CacheBlockModel]) extends CacheModel(p, externalMem) {
  require(p.associativity == 1)
  // BEGIN SOLUTION
  val tags:   ArrayBuffer[Int]             = ArrayBuffer.fill(p.numSets)(0)
  val data:   ArrayBuffer[CacheBlockModel] = ArrayBuffer.fill(p.numSets)(ArrayBuffer.fill(p.blockSize)(0))
  val valids: ArrayBuffer[Boolean]         = ArrayBuffer.fill(p.numSets)(false)

  def getReferenceToBlock(addr: Int): CacheBlockModel = {
    val (tag, index, offset) = findCacheAddressFields(addr)
    if (!isHit(addr)) {
      if (valids(index)) {
        externalMem(calcBlockAddr(tags(index), index)) = data(index).clone()
      }
      tags(index)   = tag
      data(index)   = externalMem(calcBlockAddr(tag, index)).clone()
      valids(index) = true
    }
    data(index)
  }

  def isHit(addr: Int): Boolean = {
    val (tag, index, offset) = findCacheAddressFields(addr)
    if (valids(index) && tags(index) == tag) {
      true
    } else {
      false
    }
  }
}


class SACacheModel(p: CacheParams, externalMem: ArrayBuffer[CacheBlockModel]) extends CacheModel(p, externalMem) {
  val wayParams          = p.copy(capacity = p.capacity / p.associativity, associativity = 1)
  val ways               = Seq.fill(p.associativity)(new DMCacheModel(wayParams, externalMem))
  val replacementIndices = ArrayBuffer.fill(p.numSets)(0)
  val fillIndices        = ArrayBuffer.fill(p.numSets)(0)

  // BEGIN SOLUTION
  def getReferenceToBlock(addr: Int): CacheBlockModel = {
    val way = ways.zipWithIndex.filter{ case(way, wayIndex) => way.isHit(addr)}
    val hit = way.size == 1
    if (!hit) {
      val wayReplaceIndex      = wayToReplace(addr)

      val (tag, index, offset) = findCacheAddressFields(addr)
      if (fillIndices(index) < p.associativity) {
        fillIndices(index) = fillIndices(index) + 1
      } else {
        replacementIndices(index) = (replacementIndices(index) + 1) % p.associativity
      }
      
      ways(wayReplaceIndex).getReferenceToBlock(addr)
    } else {
      ways(way.head._2).getReferenceToBlock(addr)
    }
  }

  def isHit(addr: Int): Boolean = {
    val theWay = ways.filter(_.isHit(addr))
    if (theWay.size == 1) {
      true
    } else {
      false
    } 
  }

  def wayToReplace(addr: Int): Int = {
    val (tag, index, offset) = findCacheAddressFields(addr)
    if (fillIndices(index) < p.associativity) {
      fillIndices(index)
    } else {
      replacementIndices(index)
    }
  }
}


object CacheModel {
  type CacheBlockModel = ArrayBuffer[Int]

  def apply(p: CacheParams)
           (externalMem: ArrayBuffer[CacheBlockModel] = ArrayBuffer.fill(p.numExtMemBlocks)(ArrayBuffer.fill(p.blockSize)(0))): CacheModel = {
    if (p.associativity == 1) new DMCacheModel(p, externalMem)
    else new SACacheModel(p, externalMem)
  }
}
