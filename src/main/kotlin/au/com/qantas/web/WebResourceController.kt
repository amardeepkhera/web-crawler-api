package au.com.qantas.web

import au.com.qantas.service.WebResourceService
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class WebResourceController(private val webResourceService: WebResourceService) {


    @GetMapping("/web-resource")
    fun getWebResource(
        @RequestParam("url") url: String,
        @RequestParam(value = "depth", required = false) depth: Int?,
        pageable: Pageable
    ): Resource<GetWebResourceResponse> {
        return webResourceService.getWebResource(url, depth ?: 1, pageable).run {
            Resource(
                GetWebResourceResponse(this), selfLink(depth ?: 1, pageable)
            ).apply {
                nextLink(depth ?: 1, pageable)?.let { add(it) }
                previousLink(depth ?: 1, pageable)?.let { add(it) }
            }

        }
    }
}