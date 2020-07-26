import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.gzip
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

val shoppingList = mutableListOf(
    ShoppingListItem("Cucumbers 🥒", 1),
    ShoppingListItem("Tomatoes 🍅", 2),
    ShoppingListItem("Orange Juice 🍊", 3)
)

fun main() {
    embeddedServer(Netty, 9090) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Delete)
            anyHost()
        }
        install(Compression) {
            gzip()
        }
        routing {
            route(ShoppingListItem.path) {
                get {
                    call.respond(shoppingList)
                }
                post {
                    shoppingList += call.receive<ShoppingListItem>()
                    call.respond(HttpStatusCode.OK)
                }
                delete("/{id}") {
                    val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
                    shoppingList.removeIf { it.id == id }
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }.start(wait = true)
}
