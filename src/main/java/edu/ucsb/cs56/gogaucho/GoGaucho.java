package edu.ucsb.cs56.gogaucho;

import static spark.Spark.port;
import spark.template.velocity.VelocityTemplateEngine;
import java.util.HashMap;
import java.util.Map;


/**
 * Hello world!
 *
 */

public class GoGaucho {
    public static void main(String[] args) {

        port(getHerokuAssignedPort());
		
		System.out.println("");
		System.out.println("(Don't worry about the warnings below about SLF4J... we'll deal with those later)");
		System.out.println("");						  
		System.out.println("In browser, visit: http://localhost:" + getHerokuAssignedPort() + "/hello");
		System.out.println("");
		spark.Spark.get("/", (req, res) -> {
		    Map<String, Object> model = new HashMap<>();
            return new VelocityTemplateEngine().render(new spark.ModelAndView(model, "template/hello.vm"));
        });
	}
	
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

	
}
