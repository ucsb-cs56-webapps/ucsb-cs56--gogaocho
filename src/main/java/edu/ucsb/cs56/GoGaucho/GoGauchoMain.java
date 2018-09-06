package edu.ucsb.cs56.gogaucho;

import static spark.Spark.port;

import org.apache.log4j.Logger;


import java.util.HashMap;
import java.util.Map;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import static spark.Spark.get;
import static spark.Spark.post;

public class GoGauchoMain {

	public static final String CLASSNAME="GoGauchoMain";
	
	public static final Logger log = Logger.getLogger(CLASSNAME);

	public static void main(String[] args) {

        port(getHerokuAssignedPort());
		
		Map map = new HashMap();
        map.put("name", "Sam");
		
        // hello.mustache file is in resources/templates directory
        get("/", (rq, rs) -> new ModelAndView(map, "intro.mustache"), new MustacheTemplateEngine());

		get("/profile/user", (rq, rs) -> new ModelAndView(map, "profile.mustache"), new MustacheTemplateEngine());

		post("/course/menu", (rq, rs) -> new ModelAndView(map, "course.mustache"), new MustacheTemplateEngine());
		
	}
	
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

	
}
