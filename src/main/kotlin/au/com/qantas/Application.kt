package au.com.qantas

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.hateoas.config.EnableHypermediaSupport
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@SpringBootApplication(
    exclude = [HypermediaAutoConfiguration::class, DataSourceAutoConfiguration::class]
)
class WebCrawlerAPI

@Configuration
@EnableHypermediaSupport(type = [EnableHypermediaSupport.HypermediaType.HAL])
class HateoasConfig

@Configuration
@EnableCaching
class CacheConfig

@Configuration
@EnableSwagger2
class SwaggerConfig {

    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
    }
}


fun main(args: Array<String>) {
    SpringApplication.run(WebCrawlerAPI::class.java, *args)

}