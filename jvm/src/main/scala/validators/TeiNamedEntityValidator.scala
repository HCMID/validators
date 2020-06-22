package edu.holycross.shot.mid.validators

import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import edu.holycross.shot.citerelation._
import edu.holycross.shot.scm._
import edu.holycross.shot.citevalidator._
import edu.holycross.shot.dse._

import edu.holycross.shot.xmlutils._
import scala.xml._

import wvlet.log._

import scala.annotation.tailrec

case class TeiNamedEntityValidator(
  library: CiteLibrary,
  authList: Vector[Urn],
  elementName: String,
  typeAttribute: Option[Urn] = None)
  extends CiteValidator[Urn]  with LogSupport {

  require(authList.nonEmpty, "Cannot validate named entity identifiers: authority list is empty!")

  // required
  def label = "Validator for named entities with URN disambiguation"
  // required
  def validate(library: CiteLibrary) : Vector[TestResult[Urn]] = {
    val validatedNodes = library.textRepository.get.corpus.nodes.map(cn => validate(cn))
    validatedNodes.flatten
  }


  // required
  def validate(surface: Cite2Urn) : Vector[TestResult[Urn]] = {
    info(s"Validate named entities tagged as ${elementName} for " + surface + " : start computing DSE relations...")
    val surfaceDse = dsev.passages.filter(_.surface == surface)
    info("Done. Now validating " + surfaceDse.size + " text passages.")
    val validationResults  = for (psg <- surfaceDse.map(_.passage)) yield {
      val matchedCorpus = corpus ~~ psg
      val validatedNodes = for (cn <- matchedCorpus.nodes) yield {
        val root = XML.loadString(cn.text)
        validateXmlNode(cn.urn, root)
      }
      validatedNodes.flatten
    }
    validationResults.flatten
   }

  //required
  def verify(surface: Cite2Urn) : String = {
    val hdr = s"# Verification for ${surface.objectComponent}\n\n## Named entity verification\n\nOn **${surface}**, disambiguation of named entities tagged as `${elementName}`\n\n"
    val goodResults = validate(surface).filter(_.success)
    val clustered = goodResults.groupBy(_.unit).toVector.sortBy{ case (u,items) => u.toString}
    val mdLists = clustered.map{ case (u, items) => s"- **${u}**\n" +
                    items.map(tr => s"    - ${tr.summary}").mkString("\n") + "\n"
    }



    //hdr + goodResults.mkString("\n") + "\n"
    //goodResults.map(tr => )
    hdr + mdLists.mkString("\n")
  }


  // MD list item for a test result
  def verifyCluster(tr: TestResult[Urn]): String = {
    ""
  }


  def validate(cn: CitableNode): Vector[TestResult[Urn]] = {
    val root = XML.loadString(cn.text)
    validateXmlNode(cn.urn, root)
  }


  lazy val corpus = library.textRepository.get.corpus
  lazy val dsev = DseVector.fromCiteLibrary(library)

  def cite2authList: Boolean = {
    authList.head match {
      case c2: Cite2Urn => true
      case _ => false
    }
  }

  def ctsAuthList: Boolean  = {
    (! cite2authList)
  }


  def nullValue: Urn = {
    authList.head match {
      case c2: Cite2Urn => c2.dropSelector.addSelector("null")
      case cts: CtsUrn => cts.dropPassage
    }
  }


  def urnValue(urnString: String): Urn = {
    if (cite2authList) {
      Cite2Urn(urnString)
    } else {
      CtsUrn(urnString)
    }
  }

  // collect Vector of TestResults from an XML node.
  def validateXmlNode(
    context: CtsUrn,
    n: xml.Node,
    results: Vector[TestResult[Urn]] = Vector.empty[TestResult[Urn]]) : Vector[TestResult[Urn]] = {
    val newResults : Vector[TestResult[Urn]] = n match {
      case t: xml.Text =>  {
        results
      }

      case e: xml.Elem =>  {
        val text = TextReader.collectText(e)
        e.label match {
          // Process matched element:
          case `elementName` => {
            try {
              val urnText = e.attribute("n").head.text
              val urnObject = urnValue(urnText)
              debug("found " + urnObject + " " + text)

              val success = authList.contains(urnObject)
              val newResult =  success  match {
                case true => {
                  TestResult(true, s"*${text}* (${context})", urnObject)
                }
                case false => {
                  TestResult(false, s"*${text}* (${context}): ${urnObject} is not in authority list", urnObject)
                }
              }
              val augmented = results  :+ newResult
              debug("augmented list " + augmented)
              augmented

            } catch {
              case t: Throwable => {
                debug("FAILED on " + e)
                val failure = TestResult(false,
                  s"*${text}* (${context}): could not parse URN on element ${elementName}",
                 nullValue)
                results :+ failure
              }
            }
          }
          // Recurse:
          case _ => {
            val collected = for (ch <- e.child) yield {
              validateXmlNode(context, ch, results)
            }
            debug("COLLECTED: " + collected.flatten)
            collected.toVector.flatten
          }
        }
      }
     }
     debug("new results now " + newResults)
     newResults
  }

}
