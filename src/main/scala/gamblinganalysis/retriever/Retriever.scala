package gamblinganalysis.retriever

import java.net.SocketTimeoutException

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
  * Created by misha on 12/02/16.
  */
trait Retriever {
  protected def getHtml(url: String, triesLeft: Int = 3): Element = {
    try {
      Jsoup.connect(url).userAgent("Mozilla").get()
    } catch {
      case e: SocketTimeoutException => getHtml(url, triesLeft - 1)
    }
  }

  protected def makeArray(es: Elements): Seq[Element] = es
    .toArray()
    .toSeq
    .map(_.asInstanceOf[Element])
    .asInstanceOf[Seq[Element]]
}
