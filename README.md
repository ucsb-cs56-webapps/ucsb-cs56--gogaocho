# ucsb-cs56-gogaucho
A web app that arrange your courses automatically.
Link: https://ucsb-cs56-gogaucho.herokuapp.com/

## How to run the code
1. [Go to Firebase](https://firebase.google.com/)
* click get started
* add a new project

2. Go to the DATABASE section and select Get Started button for Cloud Firestore

3. Select **Test Mode** for your Cloud Firestore Security Rule. At later stage you might want to switch it into **Locked Mode**. Then click enable.


4. Add Firebase Admin SDK in Maven:

In dependency ADD:

```
  <groupId>com.google.firebase</groupId>
  
  <artifactId>firebase-admin</artifactId>
  
  <version>6.4.0</version>

```

5. Create and set up new service account on [GCP Console](https://console.cloud.google.com/)
Use the same email address that you used for creating the new project on the Firebase. You should be able to find the exsiting project.

* open **IAM & Admin** page in the GCP Console, select your porject.
* In the left nav, click **Service accounts**
* Click **MORE and dots** (it is on the same row as the email account and located at the far right side) button and then click **Create Key** and select .json. 
* Download the .json file and rename it into **credentials.json**. 
* In the .gitignore add credentials.json

You do not want to upload the credentials.json file into github repo.
It contains private and sensitive information and you don't want other people to see it. 

6. Add FIREBASE_JSON variable

* create a setup.sh file to set the environment variable FIREBASE_JSON:

```
export FIREBASE_JSON=`cat credentials.json`
```
Run setup.sh first. Next, to add the variable FIREBASE_JSON inside Heroku, we use the following command.
```
heroku config:set FIREBASE_JSON=`cat credentials.json`
```
## Story Map
**Trello Link:** https://trello.com/b/Xxvpwo2c/gogaucho-schedule-generator

![Snapshot](cs56/m18/story.png)

## Run Schedule Algorithm
1.First in main create a vector of 3-elements tuple, each element are courses' starting time, ending time, and weight.
 Example:
 
 ```
 //Sample run with small set
	
int main(){
	vector<tuple<int, int, int>> v;
	v.push_back(make_tuple(1,5,10));
	v.push_back(make_tuple(2,5,1));
	v.push_back(make_tuple(3,5,2));
	v.push_back(make_tuple(6,9,5));
	v.push_back(make_tuple(4,6,19));
	SchedulePLan(v);
	
}
  ```


2.Then compile with version 11 or above g++
```
 g++ -std=c++11 schedule_plan.cpp -o schedule
```


3.Then run with ``` ./schedule ```
  It will output the optimal schedule and the max payoff
  Example run from main
  
 ``` 
  Max Payoff: 24
      4 6 19
      6 9 5
```
## Agreement
**Members**: Hengyu Liu, Pual Ren, Leo Lin, Alex Liu, Zhijun Yan, Tiancheng Lin

- Divide the work to each team members
- Contribute as much as possible
