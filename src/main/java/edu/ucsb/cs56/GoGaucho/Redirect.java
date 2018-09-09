import static spark.Spark.*;

public class Redirect {
	public static void main(String[] args) {
		get("/redirect", (request, response) -> {
            		response.redirect("/newpage");
            		return null;
        	});
	}

}

