package au.com.qantas.web

import org.springframework.data.domain.Pageable
import org.springframework.hateoas.Link
import org.springframework.hateoas.UriTemplate
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn


data class WebResource(
    val url: String,
    val title: String,
    val nodes: List<WebResource>,
    val totalNodeCount: Int
) {
    fun selfLink(depth: Int, pageable: Pageable): Link =
        methodOn(WebResourceController::class.java).run {
            linkTo(getWebResource(url, depth, pageable)).withSelfRel()
        }


    fun nextLink(depth: Int, pageable: Pageable): Link? = if (pageable.hasNextLink()) {
        methodOn(WebResourceController::class.java).run {
            linkTo(getWebResource(url, depth, pageable))
                .toUriComponentsBuilder().apply {
                    replaceQueryParam(
                        "page", if (pageable.pageNumber == 0) {
                            pageable.pageNumber + 2
                        } else {
                            pageable.pageNumber + 1
                        }
                    )
                    replaceQueryParam("size", pageable.pageSize)
                }.run { Link(UriTemplate(build().toString()), "next") }
        }
    } else {
        null
    }

    fun previousLink(depth: Int, pageable: Pageable): Link? = if (pageable.hasPreviousLink()) {
        methodOn(WebResourceController::class.java).run {
            linkTo(getWebResource(url, depth, pageable))
                .toUriComponentsBuilder().apply {
                    replaceQueryParam(
                        "page", pageable.pageNumber - 1
                    )
                    replaceQueryParam("size", pageable.pageSize)
                }.run { Link(UriTemplate(build().toString()), "previous") }
        }
    } else {
        null
    }

    private fun Pageable.hasNextLink() = totalNodeCount > pageNumber.times(pageSize)
    private fun Pageable.hasPreviousLink() = pageNumber > 1 && totalNodeCount <= pageNumber.times(pageSize)


}

data class GetWebResourceResponse(val data: WebResource)

data class ErrorResponse(
    val status: Int,
    val message: String
)

