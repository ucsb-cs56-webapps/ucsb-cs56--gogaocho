//This is the function used for find optimal schedule with max weight(user preference) with O(nlogn) time

#include <iostream>
#include <tuple>
#include <vector>
#include <string>

using namespace std;

//Helper function for DP solution to find the closes non-conflicting end time
//The input is a vector of three item tuple
//Eg {<1,2,3>,<4,5,6>}
//Each item representing start time, end time, and weight, respectively

int BinarySearchFinish(vector<tuple<int,int,int>> &v, int start, int end, int s){
	if(end <= start){
		if(s < get<1>(v[start])){
			return -1;
		}else{
			return start;
		}
	}
	
	int mIndex = start + ((end - start)/2);
	int f = get<1>(v[mIndex]);
	if (f <= s){
		if(mIndex < end){
			int f1 = get<1>(v[mIndex+1]);
			if(f1 > s){
				return mIndex;
			}else{
				return BinarySearchFinish(v, mIndex+1, end, s);
			}
		}else{
			return -1;	
		}
	}else{
		return BinarySearchFinish(v, start, mIndex-1, s);
	}
}




//The function use dynamic programming technique to find out optimal schedule plan with no conflict

void SchedulePLan(vector<tuple<int, int, int>> input){
	
	string line = "";
	int maxPayoff, start, finish, payoff = 0;
	vector<tuple<int, int, int>> v = input;


	//Base cases
	if(v.size() == 0){
		cout << "Maximum Payoff: " << 0;
	}else if(v.size() == 1){
		cout << "Maximum Payoff: " << get<2>(v[0]) << endl;
		cout << get<0>(v[0]) << " " << get<1>(v[0]) << " " << get<2>(v[0]) << endl; 
	}else{
		sort(begin(v), end(v), [](tuple<int,int,int> const &t1, tuple<int,int,int> const &t2) {
        return get<1>(t1) < get<1>(t2);
	});


	//Building the DP table for storing max payoffs for each i
	vector<int> payoffs;
	int currentMax = 0;
	vector<bool> flags;
	vector <int> prevIndex; 
	payoffs.push_back(get<2>(v[0]));
	prevIndex.push_back(-1);
	flags.push_back(true);
	
	for(int i = 1; i < v.size(); i++){
		
		int thisPayoff = get<2>(v[i]);
		int nonConflictIndex = BinarySearchFinish(v, 0, v.size()-1, get<0>(v[i]));
		if(nonConflictIndex != -1){
			thisPayoff += payoffs[nonConflictIndex];
		}
		if(thisPayoff >= payoffs.back()){
			currentMax = i;
			if(nonConflictIndex != -1){
				prevIndex.push_back(nonConflictIndex);
				payoffs.push_back(thisPayoff);
				flags.push_back(true);
			}else{
				payoffs.push_back(thisPayoff);
				prevIndex.push_back(-1);
				flags.push_back(true);
			}
		}else{
				flags.push_back(false);
				prevIndex.push_back(currentMax);
				payoffs.push_back(payoffs.back());
		}
	}
	cout << "Max Payoff: " << payoffs.back() << endl;
	vector<tuple<int,int,int>> output;
	while(currentMax != -1){
		if(flags[currentMax] == true){
			output.push_back(v[currentMax]);
		}
		currentMax = prevIndex[currentMax];
	}
	reverse(output.begin(), output.end());
	for(tuple<int,int,int> t : output){
		cout <<  get<0>(t) << " " << get<1>(t) << " " << get<2>(t) << endl; 
	}
}
}

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
