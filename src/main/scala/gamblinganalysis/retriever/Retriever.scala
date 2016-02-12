package gamblinganalysis.retriever

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
  * Created by misha on 12/02/16.
  */
trait Retriever {
  protected def getHtml(url: String): Element = {
    Jsoup.connect(url).userAgent("Mozilla").get()
  }

  protected def makeArray(es: Elements): Seq[Element] = es
    .toArray()
    .toSeq
    .map(_.asInstanceOf[Element])
    .asInstanceOf[Seq[Element]]
}
