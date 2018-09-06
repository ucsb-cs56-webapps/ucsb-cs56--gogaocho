package edu.ucsb.cs56.gogaucho;

import static spark.Spark.port;

import org.apache.log4j.Logger;

import java.util.*;
import java.io.*;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import static spark.Spark.get;
import static spark.Spark.post;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;

import com.google.api.core.ApiFuture;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

public class GoGauchoMain {

	public static final String CLASSNAME="GoGauchoMain";
	public static Firestore db;
	public static final Logger log = Logger.getLogger(CLASSNAME);

	public static void main(String[] args) throws Exception {

        db = setupFirebase();

        port(getHerokuAssignedPort());
		
		Map map = new HashMap();
        map.put("name", "Sam");
		

        get("/tryDB", (rq, rs) -> {
            tryDB();
            return "success";
        });

        get("/", (rq, rs) -> new ModelAndView(map, "intro.mustache"), new MustacheTemplateEngine());

		get("/profile/user", (rq, rs) -> new ModelAndView(map, "profile.mustache"), new MustacheTemplateEngine());

		post("/course/menu", (rq, rs) -> new ModelAndView(map, "course.mustache"), new MustacheTemplateEngine());
		
    }

    static void tryDB() throws Exception {
        DocumentReference docRef = db.collection("users").document("alovelace");
        // Add document data  with id "alovelace" using a hashmap
        Map<String, Object> data = new HashMap<>();
        data.put("first", "Ada");
        data.put("last", "Lovelace");
        data.put("born", 1815);
        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
        // ...
        // result.get() blocks on response
        System.out.println("Update time : " + result.get().getUpdateTime());
    }
    
    static Firestore setupFirebase() throws Exception {

        FileInputStream serviceAccount = new FileInputStream("src/main/java/edu/ucsb/cs56/gogaucho/ucsb-cs56-gogaucho-firebase-adminsdk-u3h8g-7d394cdd1c.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl("https://ucsb-cs56-gogaucho.firebaseio.com")
            .build();

        FirebaseApp.initializeApp(options);
        
        return FirestoreClient.getFirestore();
    }
	
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
