package edu.ucsb.cs56.GoGaucho;

import static spark.Spark.port;
import static spark.Spark.get;
import static spark.Spark.post;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import org.apache.log4j.Logger;

import java.util.*;
import java.io.FileInputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.api.core.ApiFuture;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;

public class GoGauchoMain {

    private static final String CLASSNAME="GoGauchoMain";
    private static Firestore db;
    public static final Logger log = Logger.getLogger(CLASSNAME);

    public static void main(String[] args) throws Exception {

        db = setupFirebase();

        port(getHerokuAssignedPort());
        
        Map map = new HashMap();
        map.put("name", "Sam");

        // Front end template render
        get("/", (rq, rs) -> new ModelAndView(map, "intro.mustache"), new MustacheTemplateEngine());

        get("/personalinfo", (rq, rs) -> new ModelAndView(map, "/personalinfo.mustache"), new MustacheTemplateEngine());

        get("/course/menu", (rq, rs) -> new ModelAndView(map, "course.mustache"), new MustacheTemplateEngine());

        /*
        Get all course from DB:
            Method: POST
            Param:  Major, Grade
            Return: 400 if param missing
                    200 if success
         */
        post("/api/v1/getcourse", (request, response) -> {
            System.out.print("getcourse is called with" + request.queryParams());
            response.type("application/json");
            response.status(400);
            if (!request.queryParams().contains("Major") || !request.queryParams().contains("Grade"))
                return new Gson().toJson("Missing parameter");

            response.status(200);
            //asynchronously retrieve all documents
            ApiFuture<QuerySnapshot> c = db.collection("courses").document("20181").collection("all").get();
            List<QueryDocumentSnapshot> documents = c.get().getDocuments();

            ArrayList<Courses> courseArr = new ArrayList<>();
            for (QueryDocumentSnapshot document : documents) {
                courseArr.add(document.toObject(Courses.class));
            }

            return new Gson().toJson(courseArr);
        });

        /*
        Post data to DB (internal testing only):
            Method: POST
            Param:  courseName, prerequisite, time, date, unit, uid
            Return: 400 if param missing
                    200 if success
         */
        post("/api/v1/postdata", (request, response) -> {
            response.type("application/json");
            response.status(400);
            if (!request.queryParams().contains("courseName") || !request.queryParams().contains("prerequisite") ||
            !request.queryParams().contains("time") || !request.queryParams().contains("date") ||
            !request.queryParams().contains("unit") || !request.queryParams().contains("uid"))
                return "Missing parameter";

            response.status(200);
            Courses c = new Courses(request.queryParams("courseName"), request.queryParams("prerequisite"),
                    request.queryParams("time"), Integer.parseInt(request.queryParams("date")),
                    Integer.parseInt(request.queryParams("unit")));
            DocumentReference docRef = db.collection("courses").document("20181")
                    .collection("all").document(request.queryParams("uid"));
            docRef.set(c);
            return c;
        });

        /*
        Login:
            Method: POST
            Param:  userName, password
            Return: 400 if parameter missing
                    401 if login failed, ie userName or password incorrect
                    200 if login success
         */
        post("/api/v1/login", (request, response) -> {
            response.type("application/json");
            response.status(400);
            if (!request.queryParams().contains("userName") || !request.queryParams().contains("password"))
                return new Gson().toJson("Missing parameter");
            response.status(401);
            DocumentReference docRef = db.collection("users").document(request.queryParams("userName"));
            DocumentSnapshot document = docRef.get().get();
            if (!document.exists())
                return new Gson().toJson("Invalid user name or not registered");
            if (!document.get("password").equals(request.queryParams("password")))
                return new Gson().toJson("Incorrect password");
            response.status(200);
            return new Gson().toJson("Login success");
        });
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
