package au.com.qantas.service

import au.com.qantas.downstream.web.WebRepository
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [WebResourceService::class, WebRepository::class], properties = ["app.http-threads=1"])
class WebResourceServiceTest {

    @Autowired
    lateinit var webRepository: WebRepository

    @Autowired
    lateinit var webResourceService: WebResourceService

    @get:Rule
    val wireMockRule = WireMockRule()

    @After
    fun cleanUp() {
        wireMockRule.resetAll()
    }

    private fun stub(url: String, htmlFile: String = url, responseStatus: HttpStatus = HttpStatus.OK) {
        WireMock.stubFor(
            WireMock.get(WireMock.urlEqualTo("/$url")).willReturn(
                WireMock.aResponse()
                    .withStatus(responseStatus.value())
                    .withBodyFile("$htmlFile.html")
            )
        )

    }

    @Test
    fun verify() {
        stub(url = "qantas", responseStatus = HttpStatus.NOT_FOUND)
        stub("hotels")
        stub("shopping")
        stub("frequent-flyers", "frequent_flyers")
        stub("careers")
        stub("airbnb")
        stub("airbnb-offers", "airbnb_offers")
        stub("the-good-guys", "good_guys")
        stub("david-jones", "david_jones")
        stub("search")

        webResourceService.getWebResource("http://localhost:8080/qantas", 2, PageRequest.of(1, 2))
            .block()
    }
}