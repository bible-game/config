package game.bible.config.model.integration

import game.bible.config.bean.Initialisable
import game.bible.config.bean.Reloadable
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Configuration for Bolls.Life Bible API
 * @since 11th October 2025
 */

@Reloadable
(
  prefix = "bolls.api",
  path = "\${application.config.dir}"
)
class BollsLifeApiConfig : Initialisable {

  init {
    println("Created [${this.javaClass.name}]")
  }

  private var baseUrl: String = "https://bolls.life"

  fun getBaseUrl(): String = baseUrl
  fun setBaseUrl(url: String) {
    baseUrl = url
  }

  // API Endpoints
  fun getBooksUrl(version: String): String = "$baseUrl/get-books/$version/"
  fun getTextUrl(version: String, bookId: String, chapter: Int): String =
    "$baseUrl/get-text/$version/$bookId/$chapter/"

}