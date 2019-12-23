package au.com.qantas.downstream.web


data class WebResource(
    val url: String,
    val title: String,
    val links: List<String>,
    val totalLinkCount: Int
)

