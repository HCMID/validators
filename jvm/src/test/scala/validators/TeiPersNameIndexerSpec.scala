package edu.holycross.shot.mid.validators
import org.scalatest.FlatSpec

import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import edu.holycross.shot.scm._

import scala.xml._

class TeiPersNameValidatorrSpec extends FlatSpec {


  val xml = """<div n="1"><ab>Text including name of <persName n="urn:cite2:dummy:test.v1:1">Agamemnon</persName></ab></div>"""
  val root = XML.loadString(xml)
  val expectedId = Cite2Urn("urn:cite2:dummy:test.v1:1")
  val textUrn = CtsUrn("urn:cts:dummy:madeUp.v1:1")
  val cn = CitableNode(textUrn, xml)

  "The TeiNamedEntityValidator object" should "recursively find persName elements in XML containers"  in  pending /*{
    val expectedSize = 1
    val expectedText = "Agamemnon"

    val actual = TEIpersNameIndexer.indexedNames(textUrn,root)

    assert(actual.size == expectedSize)
    assert(actual.head.text == expectedText)
  }*/

  it should "recursively collect TextRsults from a Citable Node" in pending /* {

    val actual = TEIpersNameIndexer.indexedNode(cn).toVector

    val expectedSize = 1


    assert(actual.size == expectedSize)
    assert(actual.head.urn1 == expectedId)
    println(actual.head.urn2 == textUrn)

  }*/





}
