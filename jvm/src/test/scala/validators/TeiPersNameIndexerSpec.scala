package edu.holycross.shot.mid.validators
import org.scalatest.FlatSpec

import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import edu.holycross.shot.scm._

import scala.xml._

class TeiPersNameValidatorrSpec extends FlatSpec {


  val xml = """<div n="1"><ab>Text including name of <persName n="urn:cite2:dummy:test.v1:1">Agamemnon</persName></ab></div>"""
  val root = XML.loadString(xml)
  val expectedId = Cite2Urn("urn:cite2:du//mmy:test.v1:1")
  val textUrn = CtsUrn("urn:cts:dummy:madeUp.v1:1")
  val cn = CitableNode(textUrn, xml)
  val validIds = Vector(Cite2Urn("urn:cite2:dummy:test.v1:1"))

  "The TeiNamedEntityValidator object" should "recursively test  elements of a specified name in XML containers"  in  {
    val expectedSize = 1
    val expectedText = "Agamemnon"
    val validator = TeiNamedEntityValidator(validIds, "persName")
    val actual = validator.validateXmlNode(textUrn, root)
    println(actual)

    assert(actual.size == expectedSize)
    assert(actual.head.unit == expectedText)
  }

  it should "recursively collect TextRsults from a Citable Node" in pending /* {

    val actual = TEIpersNameIndexer.indexedNode(cn).toVector

    val expectedSize = 1


    assert(actual.size == expectedSize)
    assert(actual.head.urn1 == expectedId)
    println(actual.head.urn2 == textUrn)

  }*/

  it should "object if given an empty authority list" in pending

  it should "report if a syntactically valid URN does not appear in the authority list" in pending

  it should "report if an identifier is not a syntacitcally valid URN" in pending

  it should "determine if the authority list is comosed of Cite2Urns or CtsUrns" in pending

}
