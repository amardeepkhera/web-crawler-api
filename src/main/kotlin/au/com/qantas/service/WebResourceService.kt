package au.com.qantas.service

import au.com.qantas.downstream.web.WebRepository
import au.com.qantas.web.WebResource
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.function.Supplier
import au.com.qantas.downstream.web.WebResource as DownStreamWebResource

@Service
class WebResourceService(
    private val webRepository: WebRepository,
    @Value("\${app.http-threads}") private val httpThreads: Int
) {

    private val executorService = Executors.newFixedThreadPool(httpThreads)

    fun getWebResource(url: String, depth: Int, pageable: Pageable): Mono<WebResource> =
        url.fetch(pageable)
            .onErrorResume { Mono.error(it) }
            .flatMap { page ->
                Mono.just(page).expand {
                    it.links
                }
            }


    private fun String.fetch(pageable: Pageable) = webRepository.getWebResource(this, pageable)


    private fun DownStreamWebResource.recurse(depth: Int, pageable: Pageable): WebResource {
        return if (depth == 0) {
            WebResource(
                url = url,
                title = title,
                nodes = emptyList(),
                totalNodeCount = 0
            )
        } else {
            WebResource(
                url = url,
                title = title,
                nodes = links.parallel(pageable)
                    .mapNotNull {
                        it?.recurse(depth - 1, pageable)
                    },
                totalNodeCount = totalLinkCount
            )
        }
    }

    private fun List<String>.parallel(pageable: Pageable) =
        asSequence()
            .map { it.fetchOrNull(pageable) }
            .map {
                it.join()
            }.toList()

    private fun String.fetchOrNull(pageable: Pageable) = CompletableFuture.supplyAsync(Supplier {
        webRepository.getWebResource(
            this,
            pageable
        )
    }, executorService).exceptionally { null }
}



