package au.com.qantas.downstream.web

import au.com.qantas.GetWebResourceException
import mu.KotlinLogging
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Whitelist
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import java.net.UnknownHostException


@Repository
class WebRepository {
    private val logger = KotlinLogging.logger { }

    @Cacheable("webResource")
    fun getWebResource(url: String, pageable: Pageable): WebResource {
        logger.info { "Fetching $url" }
        return runCatching {
            execute(url, pageable)
        }.getOrElse {
            logger.error(it) { "Error fetching url: $url" }
            it.convertAndRethrow(url)
        }
    }

    private fun execute(url: String, pageable: Pageable) =
        Jsoup.connect(url)
            .followRedirects(true)
            .timeout(2000)
            .userAgent("Mozilla")
            .execute()
            .run { Jsoup.clean(body(), Whitelist.relaxed().addTags("title")) }
            .run { Jsoup.parse(this).toWebResource(url, pageable) }


    private fun Document.toWebResource(url: String, pageable: Pageable): WebResource {
        val links = getLinks()
        return WebResource(
            url = url,
            title = title(),
            links = if (pageable.pageNumber == 1) {
                links.asSequence().take(pageable.pageSize).toList()
            } else {
                links.asSequence().drop(pageable.pageSize.minus(1).times(pageable.pageNumber)).take(pageable.pageSize)
                    .toList()
            },
            totalLinkCount = links.size
        )
    }

    private fun Document.getLinks() = select("a")
        .asSequence()
        .map { it.attr("abs:href") }
        .filter { it.isNotEmpty() }
        .filter { it.startsWith("http") }
        .distinct()
        .toList()

    private fun Throwable.convertAndRethrow(url: String): Nothing = when (this) {
        is UnknownHostException -> throw GetWebResourceException(
            HttpStatus.NOT_FOUND.value(),
            "Resource not found:$url",
            this
        )
        is HttpStatusException -> throw GetWebResourceException(statusCode, message, this)
        else -> throw GetWebResourceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, this)
    }

}