package models.retriever

import java.net.SocketTimeoutException

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
  * Created by misha on 12/02/16.
  */
trait Retriever {
  /**
    * Get HTML
    * @param url the location of the HTML to retrieve
    * @param triesLeft amount of tries left
    * @return the html parsed by Jsoup
    */
  protected def getHtml(url: String, triesLeft: Int = 3): Element = {
    try {
      Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1").get()
    } catch {
      case e: SocketTimeoutException =>
        if (triesLeft < 0) {
          throw e
        } else {
          getHtml(url, triesLeft - 1)
        }
    }
  }

  /**
    * Cast Elements object to a Sequence of Elements
    * @param es Elements object
    * @return Seq[Elements] object
    */
  protected def makeArray(es: Elements): Seq[Element] = es
    .toArray()
    .toSeq
    .map(_.asInstanceOf[Element])
    .asInstanceOf[Seq[Element]]
}
