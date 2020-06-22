package edu.holycross.shot.mid.validators

import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import edu.holycross.shot.citerelation._
import edu.holycross.shot.scm._
import edu.holycross.shot.citevalidator._

import edu.holycross.shot.xmlutils._
import scala.xml._

import wvlet.log._

import scala.annotation.tailrec

case class TeiNamedEntityValidator(
  library: CiteLibrary,
  authList: Vector[Urn],
  elementName: String,
  typeAttribute: Option[String] = None)
  extends CiteValidator[String]  with LogSupport {

  require(authList.nonEmpty, "Cannot validate named entity identifiers: authority list is empty!")


  // required
  def label = "Validator for named entities with URN disambiguation"
  // required
  def validate(library: CiteLibrary) : Vector[TestResult[String]] = Vector.empty[TestResult[String]]
  // required
  def validate(surface: Cite2Urn) : Vector[TestResult[String]] = Vector.empty[TestResult[String]]
  //required
  def verify(surface: Cite2Urn) : String = {""}



    def cite2authList: Boolean = {
      authList.head match {
        case c2: Cite2Urn => true
        case _ => false
      }
    }

    def ctsAuthList: Boolean  = {
      (! cite2authList)
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
    results: Vector[TestResult[String]] = Vector.empty[TestResult[String]]) : Vector[TestResult[String]] = {
    val newResults : Vector[TestResult[String]] = n match {
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
                  TestResult(true, s"${context}: valid URN ${urnObject} on ${text}", text)
                }
                case false => {
                  TestResult(false, s"${context}: ${urnObject} on ${text} is not in authority list", text)
                }
              }
              val augmented = results  :+ newResult
              debug("augmented list " + augmented)
              augmented

            } catch {
              case t: Throwable => {
                debug("FAILED on " + e)
                val failure = TestResult(false,
                  s"${context}: could not parse URN on element ${elementName}",
                 text)
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

/*
  def indexedNode(cn: CitableNode): Set[CiteTriple] = {
    val root = XML.loadString(cn.text)
    val nameSet = indexedNames(cn.urn, root)
    nameSet.toVector.map(n => nameToTriple(n)).toSet
  }
  */
}
