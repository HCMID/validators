package edu.holycross.shot.mid.validators
import org.scalatest.FlatSpec

import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import edu.holycross.shot.scm._

import scala.xml._

class TeiPersNameVerificationSpec extends FlatSpec {


  "The TeiNamedEntityValidator object" should "format a verification report" in  {
    val cex = "jvm/src/test/resources/sample1.cex"
    val lib = CiteLibrarySource.fromFile(cex)
    val person1 = Cite2Urn("urn:cite2:mid:unittestnames.v1:1")
    val validIds = Vector(person1)
    val validator = TeiNamedEntityValidator(lib, validIds, "persName")


    val surface = Cite2Urn("urn:cite2:unittest:mspages.v1:page1")
    val verifyMd = validator.verify(surface)
    import java.io.PrintWriter
    new PrintWriter("out.md"){write(verifyMd);close;}
  }





}
