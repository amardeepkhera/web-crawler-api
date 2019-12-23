package au.com.qantas.web

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class WebResourceControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @get:Rule
    val wireMockRule = WireMockRule()

    @After
    fun cleanUp() {
        wireMockRule.resetAll()
    }

    private fun stub(url: String, htmlFile: String = url, responseStatus: HttpStatus = HttpStatus.OK) {
        stubFor(
            get(urlEqualTo("/$url")).willReturn(
                aResponse()
                    .withStatus(responseStatus.value())
                    .withBodyFile("$htmlFile.html")
            )
        )

    }

    @Test
    fun verifyGetWebResourceResourceForDepth1() {
        stub("qantas")
        stub("hotels")
        stub("shopping")
        stub("frequent-flyers", "frequent_flyers")
        stub("careers")
        stub("airbnb")
        stub("airbnb-offers", "airbnb_offers")
        stub("david-jones", "david_jones")
        stub("search")

        mockMvc.perform(
            get("/web-resource")
                .param("url", "http://localhost:8080/qantas")
                .param("page", "1")
                .param("size", "2")
        ).andExpect(status().isOk)
            .andExpect(
                content().json(
                    """
                        {
                          "data": {
                            "url": "http://localhost:8080/qantas",
                            "title": "Qantas",
                            "nodes": [
                              {
                                "url": "http://localhost:8080/hotels",
                                "title": "Hotels",
                                "nodes": [],
                                "totalNodeCount": 0
                              },
                              {
                                "url": "http://localhost:8080/shopping",
                                "title": "Shopping",
                                "nodes": [],
                                "totalNodeCount": 0
                              }
                            ],
                            "totalNodeCount": 4
                          },
                          "_links": {
                            "self": {
                              "href": "http://localhost/web-resource?url=http://localhost:8080/qantas&depth=1"
                            },
                            "next": {
                              "href": "http://localhost/web-resource?url=http://localhost:8080/qantas&depth=1&page=2&size=2"
                            }
                          }
                        }
           """.trimIndent()
                )
            )
    }

    @Test
    fun verifyGetWebResourceResourceForDepth2() {
        stub("qantas")
        stub("hotels")
        stub("shopping")
        stub("frequent-flyers", "frequent_flyers")
        stub("careers")
        stub("airbnb")
        stub("airbnb-offers", "airbnb_offers")
        stub("david-jones", "david_jones")
        stub("the-good-guys", "good_guys")
        stub("search")

        mockMvc.perform(
            get("/web-resource")
                .param("url", "http://localhost:8080/qantas")
                .param("depth", "2")
                .param("page", "1")
                .param("size", "2")
        ).andExpect(status().isOk)
            .andExpect(
                content().json(
                    """
                        {
                          "data": {
                            "url": "http://localhost:8080/qantas",
                            "title": "Qantas",
                            "nodes": [
                              {
                                "url": "http://localhost:8080/hotels",
                                "title": "Hotels",
                                "nodes": [
                                  {
                                    "url": "http://localhost:8080/airbnb",
                                    "title": "Airbnb",
                                    "nodes": [],
                                    "totalNodeCount": 0
                                  },
                                  {
                                    "url": "http://localhost:8080/careers",
                                    "title": "Careers",
                                    "nodes": [],
                                    "totalNodeCount": 0
                                  }
                                ],
                                "totalNodeCount": 2
                              },
                              {
                                "url": "http://localhost:8080/shopping",
                                "title": "Shopping",
                                "nodes": [
                                  {
                                    "url": "http://localhost:8080/david-jones",
                                    "title": "David Jones",
                                    "nodes": [],
                                    "totalNodeCount": 0
                                  },
                                  {
                                    "url": "http://localhost:8080/the-good-guys",
                                    "title": "The Good Guys",
                                    "nodes": [],
                                    "totalNodeCount": 0
                                  }
                                ],
                                "totalNodeCount": 3
                              }
                            ],
                            "totalNodeCount": 4
                          },
                          "_links": {
                            "self": {
                              "href": "http://localhost/web-resource?url=http://localhost:8080/qantas&depth=2"
                            },
                            "next": {
                              "href": "http://localhost/web-resource?url=http://localhost:8080/qantas&depth=2&page=2&size=2"
                            }
                          }
                        }
           """.trimIndent()
                )
            )
    }

    @Test
    fun verifyGetWebResourceResourceForDepth3() {
        stub("qantas")
        stub("hotels")
        stub("shopping")
        stub("frequent-flyers", "frequent_flyers")
        stub("careers")
        stub("airbnb")
        stub("airbnb-offers", "airbnb_offers")
        stub("david-jones", "david_jones")
        stub("the-good-guys", "good_guys")
        stub("search")

        mockMvc.perform(
            get("/web-resource")
                .param("url", "http://localhost:8080/qantas")
                .param("depth", "3")
                .param("page", "1")
                .param("size", "2")
        ).andExpect(status().isOk)
            .andExpect(
                content().json(
                    """
                        {
                          "data": {
                            "url": "http://localhost:8080/qantas",
                            "title": "Qantas",
                            "nodes": [
                              {
                                "url": "http://localhost:8080/hotels",
                                "title": "Hotels",
                                "nodes": [
                                  {
                                    "url": "http://localhost:8080/airbnb",
                                    "title": "Airbnb",
                                    "nodes": [
                                      {
                                        "url": "http://localhost:8080/airbnb-offers",
                                        "title": "Airbnb Offers",
                                        "nodes": [],
                                        "totalNodeCount": 0
                                      }
                                    ],
                                    "totalNodeCount": 1
                                  },
                                  {
                                    "url": "http://localhost:8080/careers",
                                    "title": "Careers",
                                    "nodes": [
                                      {
                                        "url": "http://localhost:8080/search",
                                        "title": "Search For Jobs",
                                        "nodes": [],
                                        "totalNodeCount": 0
                                      }
                                    ],
                                    "totalNodeCount": 1
                                  }
                                ],
                                "totalNodeCount": 2
                              },
                              {
                                "url": "http://localhost:8080/shopping",
                                "title": "Shopping",
                                "nodes": [
                                  {
                                    "url": "http://localhost:8080/david-jones",
                                    "title": "David Jones",
                                    "nodes": [],
                                    "totalNodeCount": 0
                                  },
                                  {
                                    "url": "http://localhost:8080/the-good-guys",
                                    "title": "The Good Guys",
                                    "nodes": [],
                                    "totalNodeCount": 0
                                  }
                                ],
                                "totalNodeCount": 3
                              }
                            ],
                            "totalNodeCount": 4
                          },
                          "_links": {
                            "self": {
                              "href": "http://localhost/web-resource?url=http://localhost:8080/qantas&depth=3"
                            },
                            "next": {
                              "href": "http://localhost/web-resource?url=http://localhost:8080/qantas&depth=3&page=2&size=2"
                            }
                          }
                        }
           """.trimIndent()
                )
            )
    }

    @Test
    fun verifyOutboundCallsAreCached() {
        stub("qantas")
        stub("hotels")
        stub("shopping")
        stub("frequent-flyers", "frequent_flyers")
        stub("careers")
        stub("airbnb")
        stub("airbnb-offers", "airbnb_offers")
        stub("david-jones", "david_jones")
        stub("the-good-guys", "good_guys")
        stub("search")

        mockMvc.perform(
            get("/web-resource")
                .param("url", "http://localhost:8080/qantas")
                .param("depth", "2")
                .param("page", "1")
                .param("size", "3")
        ).andExpect(status().isOk)
            .andExpect(
                content().json(
                    """
                    {
                      "data": {
                        "url": "http://localhost:8080/qantas",
                        "title": "Qantas",
                        "nodes": [
                          {
                            "url": "http://localhost:8080/hotels",
                            "title": "Hotels",
                            "nodes": [
                              {
                                "url": "http://localhost:8080/airbnb",
                                "title": "Airbnb",
                                "nodes": [],
                                "totalNodeCount": 0
                              },
                              {
                                "url": "http://localhost:8080/careers",
                                "title": "Careers",
                                "nodes": [],
                                "totalNodeCount": 0
                              }
                            ],
                            "totalNodeCount": 2
                          },
                          {
                            "url": "http://localhost:8080/shopping",
                            "title": "Shopping",
                            "nodes": [
                              {
                                "url": "http://localhost:8080/david-jones",
                                "title": "David Jones",
                                "nodes": [],
                                "totalNodeCount": 0
                              },
                              {
                                "url": "http://localhost:8080/the-good-guys",
                                "title": "The Good Guys",
                                "nodes": [],
                                "totalNodeCount": 0
                              },
                              {
                                "url": "http://localhost:8080/careers",
                                "title": "Careers",
                                "nodes": [],
                                "totalNodeCount": 0
                              }
                            ],
                            "totalNodeCount": 3
                          },
                          {
                            "url": "http://localhost:8080/frequent-flyers",
                            "title": "Frequent Flyers",
                            "nodes": [
                              {
                                "url": "http://localhost:8080/careers",
                                "title": "Careers",
                                "nodes": [],
                                "totalNodeCount": 0
                              }
                            ],
                            "totalNodeCount": 1
                          }
                        ],
                        "totalNodeCount": 4
                      },
                      "_links": {
                        "self": {
                          "href": "http://localhost/web-resource?url=http://localhost:8080/qantas&depth=2"
                        },
                        "next": {
                          "href": "http://localhost/web-resource?url=http://localhost:8080/qantas&depth=2&page=2&size=3"
                        }
                      }
                    }
                """.trimIndent()
                )
            )


        verify(1, getRequestedFor(urlEqualTo("/careers")))
    }

    @Test
    fun verifyNodesAreOmittedIfFetchingThemFails() {
        stub("qantas")
        stub(url = "hotels", responseStatus = HttpStatus.NOT_FOUND)
        stub("shopping")
        stub("frequent-flyers", "frequent_flyers", HttpStatus.FORBIDDEN)
        stub("careers")

        mockMvc.perform(
            get("/web-resource")
                .param("url", "http://localhost:8080/qantas")
                .param("depth", "1")
                .param("page", "1")
                .param("size", "4")
        ).andExpect(status().isOk)
            .andExpect(
                content().json(
                    """
                    {
                      "data": {
                        "url": "http://localhost:8080/qantas",
                        "title": "Qantas",
                        "nodes": [
                          {
                            "url": "http://localhost:8080/shopping",
                            "title": "Shopping",
                            "nodes": [],
                            "totalNodeCount": 0
                          },
                          {
                            "url": "http://localhost:8080/careers",
                            "title": "Careers",
                            "nodes": [],
                            "totalNodeCount": 0
                          }
                        ],
                        "totalNodeCount": 4
                      },
                      "_links": {
                        "self": {
                          "href": "http://localhost/web-resource?url=http://localhost:8080/qantas&depth=1"
                        }
                      }
                    }
                """.trimIndent()
                )
            )
    }
}