/**
 * @author Chris Willette
 * SID 11444663
 */

import java.util.*;
import java.lang.*;
import java.io.*;


@SuppressWarnings("unused")//looking at you, state.
public class FAStateGen {

	public int stateCount = 0;
	private final FA result;
	
	private class FA {
		public ArrayList<State> stateList = new ArrayList<State>();
		int start; //used when chaining fa state lists together
		int end;  //same
		Boolean isEmpty; //deals with empty set
	}
	
//later code would have been much less painful had i created proper
	//constructors for state and fa.

	
	
	private class State implements Comparable<State> {
		int number;
		char input;
		int next;
	
	
	@Override
	public int compareTo(State other){
		if(this.number == other.number)		
			return 0;
		else if (this.number < other.number)
			return -1;
		else
			return 1;
	}	
}	
	
	
	
	public FAStateGen(String line){
		result = parse(line);
	}
	
	public FA getResult(){
		return result;
	}
	
	
	public FA parse(String line){
		stateCount = 0;
		Stack<FA> st = new Stack<FA>();
		FA machine = new FA();
		for(int i = 0; i < line.length(); i++) {
			char input = line.charAt(i);
			
			//test input
			if(st.size() <2 && ((input == '+')||(input == '&')) ){
				System.err.println("invalid input: " + line);
				stateCount = 0; //notifies caller of bad input
				break;
			}
			
			if(input == '&'){//fa1&fa2
				FA fa2 = st.pop();
				FA fa1 = st.pop();
				if (fa1.isEmpty == true || fa2.isEmpty == true){
					continue;
				}
				FA faNew = new FA();
				faNew.stateList.addAll(fa1.stateList);
				State newState = new State();
				newState.number = fa1.end;
				newState.input = 'E';
				newState.next = fa2.start;
				faNew.stateList.add(newState);
				faNew.stateList.addAll(fa2.stateList);
				faNew.start = fa1.start;
				faNew.end = fa2.end;
				faNew.isEmpty = false;
				st.push(faNew);
				
				   
			}else if(input == '+'){//fa1+fa2
				FA fa2 = st.pop();
				FA fa1 = st.pop();
				if(fa1.isEmpty == true && fa2.isEmpty == true) {
					//we're just gonna act like it's an input of empty set
					stateCount++;
					State ns1 = new State();
					ns1.number = stateCount;
					ns1.next = stateCount;
					ns1.input = 'E';		
					FA fa = new FA();
					fa.stateList.add(ns1);				
					fa.start = ns1.number;
					fa.end = ns1.number;
					fa.isEmpty = true;
					st.push(fa);					
					continue;
				}else if(fa1.isEmpty == true){
					st.push(fa2);//0+fa2 = fa2					
					continue;
				}
				else if(fa2.isEmpty == true){
					st.push(fa1);//fa1+0 = fa1					
					continue;
				} 				
				FA faNew = new FA();
				stateCount++;
				State pre = new State();
				pre.number = stateCount; //test if needed
				faNew.start = pre.number;				
				pre.next = fa1.start;
				pre.input = 'E';
				faNew.stateList.add(pre);				
				State pre2 = new State(); 
				pre2.number = stateCount; //test if needed
				pre2.next = fa2.start;
				pre2.input = 'E';
				faNew.stateList.add(pre2);
				faNew.stateList.addAll(fa1.stateList);
				faNew.stateList.addAll(fa2.stateList);
				stateCount++;
				State post1 = new State();
				State post2 = new State();
				post1.number = fa1.end;
				post2.number = fa2.end;
				post1.input = 'E';
				post2.input = 'E';
				post1.next = stateCount;
				post2.next = stateCount;
				faNew.stateList.add(post1);
				faNew.stateList.add(post2);
				faNew.end = stateCount;
				faNew.isEmpty = false;
				st.push(faNew);
				
			}else if(input == '*'){//fa*				
				FA fa = st.pop();
				stateCount++;
				State pre = new State();
				State post = new State();
				FA faNew = new FA();
				pre.input = 'E';
				post.input = 'E';
				pre.number = stateCount;
				post.number = fa.end;
				pre.next = fa.start;
				post.next = pre.number;
				faNew.stateList.add(pre);
				faNew.stateList.addAll(fa.stateList);
				faNew.stateList.add(post);
				faNew.start = pre.number;
				faNew.end = pre.number;
				faNew.isEmpty = false;
				st.push(faNew);
				
			}else if(input == '0'){//fa for empty set
				stateCount++;
				State ns1 = new State();
				ns1.number = stateCount;
				ns1.next = stateCount;
				ns1.input = input;		
				FA fa = new FA();
				fa.stateList.add(ns1);				
				fa.start = ns1.number;
				fa.end = ns1.number;
				fa.isEmpty = true;
				st.push(fa);
			}else{
				stateCount++;
				State ns1 = new State();
				State ns2 = new State();
				ns1.number = stateCount;
				stateCount++;
				ns1.next = stateCount;
				ns1.input = input;
				ns2.number = ns1.next;
				FA fa = new FA();
				fa.stateList.add(ns1);
				fa.start = ns1.number;
				fa.end = ns2.number;
				fa.isEmpty = false;
				st.push(fa);
			}			
		}
		if(st.size() > 1){		
			System.err.println("invalid input: " + line);
			stateCount = 0;
		}
		machine = st.pop();
		return machine;		
	}
	
	
	 
	

	private void printTable(String name) {
		try{
			String home = System.getProperty("user.home");
			name = home + "\\Desktop\\" + name;
			File out = new File(name);
			if(!out.exists()) out.createNewFile();
			Collections.sort(result.stateList);
			FileWriter fw = new FileWriter(out.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write("start state: q" + result.start);
			for(int i = 0; i < result.stateList.size(); i++){
				String entry = "\r\n" + "(q" + result.stateList.get(i).number +  ", " +
						result.stateList.get(i).input + ") --> q" +
						result.stateList.get(i).next;				
				bw.write(entry);
			}
			bw.write("\r\nend state: q" + result.end);
			bw.close();
			
		}catch (IOException e){
			System.err.println(e.getMessage());
		}
	}
	
	
	
	
	
	public static void main(String[] args){
		Scanner scanner = new Scanner(System.in);
		System.out.print("enter file name: ");
		System.out.flush();
		String filename = scanner.nextLine();
		try{
			File file = new File(filename);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			int lineNumber = 0;
			while ((line = bufferedReader.readLine()) != null) {
				if(line.isEmpty())continue;
				lineNumber++;
				FAStateGen automata = new FAStateGen(line);				
				String name = "willette-FA" + lineNumber + ".txt";
				if(automata.stateCount == 0) continue; //skip past bad input				
				automata.printTable(name);
				
			}
			fileReader.close();			
		}catch (IOException e) {
			System.err.println(e.getMessage());
		}finally{
			scanner.close();
		}
	}//end main
}


