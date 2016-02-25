package models.retriever.odds.actors

import akka.actor.Actor
import models.util.db.GameDetailsDBHandler
import org.jsoup.Jsoup
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import play.api.Logger

/**
  * Created by misha on 24/02/16.
  */
class WorkerRetriever extends Actor {

  private val log = Logger(getClass)

  private val driver: WebDriver = {
    val driver = new FirefoxDriver()
    log.info("Driver initialised")
    driver
  }

  def receive = {
    case ScrapingWork(work) =>
      log.debug(s"Got work: $work")
      val oddsCollection = work.retrieve(this)

      log.debug(s"Inserting ${oddsCollection.odds.size} into database")
      oddsCollection.odds.foreach(GameDetailsDBHandler.insertOdd)

      log.debug(s"Done with work: $work")
      sender() ! DoneScraping(work)
    case NoMoreWork() =>
      log.debug("Told no more work")
  }

  def getUrl(url: String) = {
    log.info(s"Told to get $url")
    driver.get(url)
    Jsoup.parse(driver.getPageSource)
  }
}
