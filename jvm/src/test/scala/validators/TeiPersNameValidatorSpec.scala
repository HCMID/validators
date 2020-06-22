package edu.holycross.shot.mid.validators
import org.scalatest.FlatSpec

import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import edu.holycross.shot.scm._

import scala.xml._

class TeiPersNameValidatorSpec extends FlatSpec {


  val libraryShell = CiteLibrary(
    "unit test",
    Cite2Urn("urn:cite2:mid:validatortests.v1:minimum"),
    "public domain",
    Vector.empty[CiteNamespace]
  )

  "The TeiNamedEntityValidator object" should "recursively find valid instances of a specified named entity element in XML containers"  in  {
    val validIds = Vector(Cite2Urn("urn:cite2:mid:namedentityunits.v1:1"))
    val validator = TeiNamedEntityValidator(libraryShell, validIds, "persName")

    val xml = """<div n="1"><ab>Text including name of <persName n="urn:cite2:mid:namedentityunits.v1:1">Agamemnon</persName></ab></div>"""
    val root = XML.loadString(xml)
    val textUrn = CtsUrn("urn:cts:dummy:madeUp.v1:1")
    val actual = validator.validateXmlNode(textUrn, root)

    val expectedId = Cite2Urn("urn:cite2:du//mmy:test.v1:1")
    val expectedSize = 1
    val expectedText = "Agamemnon"

    assert(actual.size == expectedSize)
    assert(actual.head.unit == expectedText)
  }

  it should "identify an occurrence of the element without @n attribute as an error" in {
    val validIds = Vector(Cite2Urn("urn:cite2:mid:namedentityunits.v1:1"))
    val validator = TeiNamedEntityValidator(libraryShell, validIds, "persName")
    val xml = """<div n="1"><ab>Text including name of <persName>Agamemnon</persName> with no N attribute</ab></div>"""

    val root = XML.loadString(xml)
    val textUrn = CtsUrn("urn:cts:dummy:madeUp.v1:noAttribute")
    val actual = validator.validateXmlNode(textUrn, root)
    assert(actual.size == 1)
    assert(actual.head.success == false)
    println(actual.head.summary.contains("could not parse URN on element persName"))
  }

  it should "report if a syntactically valid URN does not appear in the authority list" in {
    val validIds = Vector(Cite2Urn("urn:cite2:mid:namedentityunits.v1:1"))
    val validator = TeiNamedEntityValidator(libraryShell, validIds, "persName")
    val xml = """<div n="1"><ab>Text including name of <persName n="urn:cite2:mid:namedentityunits.v1:NOT_IN_LIST">Agamemnon</persName> with no N attribute</ab></div>"""

    val root = XML.loadString(xml)
    val textUrn = CtsUrn("urn:cts:dummy:madeUp.v1:notInList")
    val actual = validator.validateXmlNode(textUrn, root)
    assert(actual.size == 1)
    assert(actual.head.success == false)
    assert(actual.head.summary.contains("not in authority list"))
  }

  it should "report if an identifier is not a syntacitcally valid URN" in {
    val validIds = Vector(Cite2Urn("urn:cite2:mid:namedentityunits.v1:1"))
    val validator = TeiNamedEntityValidator(libraryShell, validIds, "persName")
    val xml = """<div n="1"><ab>Text including name of <persName n="NOT_A_URN">Agamemnon</persName> with invalid attribute</ab></div>"""

    val root = XML.loadString(xml)
    val textUrn = CtsUrn("urn:cts:dummy:madeUp.v1:notAUrn")
    val actual = validator.validateXmlNode(textUrn, root)
    assert(actual.size == 1)
    assert(actual.head.success == false)
    assert(actual.head.summary.contains("could not parse URN"))
  }

  it should "recursively collect TextRsults from a Citable Node" in {
    val cex = "jvm/src/test/resources/sample1.cex"
    val lib = CiteLibrarySource.fromFile(cex)
    val person1 = Cite2Urn("urn:cite2:mid:unittestnames.v1:1")
    val validIds = Vector(person1)
    val validator = TeiNamedEntityValidator(lib, validIds, "persName")


    val surface = Cite2Urn("urn:cite2:unittest:mspages.v1:page1")
    val rslts = validator.validate(surface)

    val expectedGood = 1
    val expectedBad = 2
    assert(rslts.size == expectedGood + expectedBad)
    assert(rslts.filter(_.success).size == expectedGood)

  }

  it should "object if given an empty authority list" in pending


  it should "determine if the authority list is comosed of Cite2Urns or CtsUrns" in pending

}
