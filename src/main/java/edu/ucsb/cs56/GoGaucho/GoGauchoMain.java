package edu.ucsb.cs56.gogaucho;

import static spark.Spark.port;
import static spark.Spark.get;
import static spark.Spark.post;

import com.google.api.core.ApiFutures;
import org.apache.log4j.Logger;

import java.util.*;
import java.io.FileInputStream;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.api.core.ApiFuture;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;

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

		get("/course/menu", (rq, rs) -> new ModelAndView(map, "course.mustache"), new MustacheTemplateEngine());

		post("/api/v1/course", ((request, response) -> {
		    if (request.queryParams().contains("Major") && request.queryParams().contains("Grade")){
		        response.status(200);
                String major = request.queryParams("Major");
                String year = request.queryParams(("Grade"));
            } else {
                response.status(400);
                return "Bad parameter";
            }
            //asynchronously retrieve all documents
            ApiFuture<QuerySnapshot> c = db.collection("courses").document("20181")
                    .collection("all").get();
            List<QueryDocumentSnapshot> documents = c.get().getDocuments();
            ArrayList<Courses> courseArr = new ArrayList<>();
            for (QueryDocumentSnapshot document : documents) {
                courseArr.add(document.toObject(Courses.class));
            }
            response.type("application/json");
            return new Gson().toJson(courseArr);
        }));

		post("/api/v1/data", (request, response) -> {
		    if (request.queryParams().contains("courseName") && request.queryParams().contains("prerequisite") &&
            request.queryParams().contains("time") && request.queryParams().contains("date") &&
            request.queryParams().contains("unit") && request.queryParams().contains("uid")) {
		        response.status(200);
		        Courses c = new Courses(request.queryParams("courseName"), request.queryParams("prerequisite"),
                        request.queryParams("time"), Integer.parseInt(request.queryParams("date")),
                        Integer.parseInt(request.queryParams("unit")));
                DocumentReference docRef = db.collection("courses").document("20181")
                        .collection("all").document(request.queryParams("uid"));
                docRef.set(c);
                return c;
            } else {
		        response.status(400);
		        return "Bad parameter";
            }
        });
    }

    private static void tryDB() throws Exception {
	    String courseUid = "00000";
        DocumentReference docRef = db.collection("courses").document("20181")
                .collection("all").document(courseUid);
        Courses c = new Courses("CS8", "N/A", "N/A", 24, 4);
        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(c);
        // result.get() blocks on response
        System.out.println("Update time : " + result.get().getUpdateTime());
    }
    
    private static Firestore setupFirebase() throws Exception {

        FileInputStream serviceAccount = new FileInputStream("src/main/java/edu/ucsb/cs56/gogaucho/ucsb-cs56-gogaucho-firebase-adminsdk-u3h8g-7d394cdd1c.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl("https://ucsb-cs56-gogaucho.firebaseio.com")
            .build();

        FirebaseApp.initializeApp(options);
        
        return FirestoreClient.getFirestore();
    }
	
    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
