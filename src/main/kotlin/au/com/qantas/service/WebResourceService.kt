package au.com.qantas.service

import au.com.qantas.downstream.web.WebRepository
import au.com.qantas.web.WebResource
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class WebResourceService(
    private val webRepository: WebRepository
) {

    fun getWebResource(url: String, depth: Int, pageable: Pageable): Mono<WebResource> =
        url.fetch(pageable)
            .onErrorResume {
                Mono.error(it)
            }.flatMap { webResource ->
                Mono.just(webResource).flatMapMany {
                    Flux.fromIterable(it.links.take(pageable.pageSize))
                        .map {
                            FlatWebResource(it, 1)
                        }.expandDeep { fwr ->
                            if (fwr.depth >= depth) {
                                return@expandDeep Flux.empty()
                            }
                            fwr.url.fetch(pageable)
                                .map {
                                    it.links
                                }.flatMapMany {
                                    Flux.fromIterable(it).map {
                                        FlatWebResource(it, fwr.depth + 1, fwr)
                                    }.subscribeOn(Schedulers.parallel())
                                }
                        }.subscribeOn(Schedulers.parallel())
                }.collectList()
                    .flatMapMany {
                        Flux.fromIterable(it).flatMapSequential {
                            it.fetch(pageable)
                        }.subscribeOn(Schedulers.parallel())
                    }.collectList().map { newWebResource(webResource, it, depth) }
            }


    private data class FlatWebResource(
        val url: String,
        val depth: Int,
        val parent: FlatWebResource? = null,
        val webResource: au.com.qantas.downstream.web.WebResource? = null
    )


    private fun String.fetch(pageable: Pageable) = webRepository.getWebResource(this, pageable)
        .onErrorResume {
            it.printStackTrace()
            Mono.empty()
        }

    private fun FlatWebResource.fetch(pageable: Pageable) = url.fetch(pageable).map {
        copy(webResource = it)
    }

    private fun newWebResource(
        webResource: au.com.qantas.downstream.web.WebResource,
        flatWebResources: List<FlatWebResource>,
        depth: Int
    ): WebResource {
        val depthMap = flatWebResources.groupBy { it.depth }

        return WebResource(
            url = webResource.url,
            title = webResource.title,
            totalNodeCount = webResource.totalLinkCount,
            nodes = recurse(depth, 1, null, depthMap)
        )
    }

    private fun recurse(
        depth: Int,
        depthCounter: Int,
        flatWebResource: FlatWebResource?,
        depthMap: Map<Int, List<FlatWebResource>>
    ): List<WebResource> {
        if (depthCounter > depth) {
            return emptyList()
        }
        return depthMap.getOrDefault(depthCounter, emptyList())
            .filter {
                it.parent?.url == flatWebResource?.url
            }
            .map {
                WebResource(
                    it.url,
                    it.webResource!!.title,
                    recurse(depth, depthCounter + 1, it, depthMap),
                    it.webResource!!.totalLinkCount
                )
            }

    }
}



