import static spark.Spark.*;

public class redirectToNewPages (String routes) {
	get("/", (request, response) -> {
            response.redirect("/" + routes);
            return null;
        });
}