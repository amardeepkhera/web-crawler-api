package au.com.qantas.downstream.web

import mu.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Whitelist
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI


@Repository
class WebRepository {
    private val logger = KotlinLogging.logger { }

    private val webClient = WebClient.builder().build()


    fun getWebResource(url: String, pageable: Pageable): Mono<WebResource> {
        logger.info { "Fetching $url" }

        return execute(url, pageable)
    }

    private fun execute(url: String, pageable: Pageable) =
        webClient.method(HttpMethod.GET).uri(URI.create(url)).retrieve().bodyToMono(String::class.java)
            .map {
                Jsoup.clean(it, Whitelist.relaxed().addTags("title"))
                    .run { Jsoup.parse(this).toWebResource(url, pageable) }
            }


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

//    private fun Throwable.convertAndRethrow(url: String): Nothing = when (this) {
//        is UnknownHostException -> throw GetWebResourceException(
//            HttpStatus.NOT_FOUND.value(),
//            "Resource not found:$url",
//            this
//        )
//        is HttpStatusException -> throw GetWebResourceException(statusCode, message, this)
//        else -> throw GetWebResourceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, this)
//    }

}