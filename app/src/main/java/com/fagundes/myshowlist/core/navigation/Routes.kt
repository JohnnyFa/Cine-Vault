import com.fagundes.myshowlist.core.data.local.enum.ContentType

object AppRoutes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val CATALOG = "catalog"

    const val DETAIL = "detail/{id}/{type}"

    fun detail(
        id: Int,
        type: ContentType,
    ) = "detail/$id/${type.name}"
}
