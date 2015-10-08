/**
 * @author Chris Willette
 * SID 11444663
 */

import java.util.*;
import java.lang.*;
import java.io.*;


@SuppressWarnings("unused")
public class FAStateGen {

	public int stateCount = 0;
	private final FA result;
	
	private class FA{
		public ArrayList<State> stateList = new ArrayList<State>();
		public State start;
		public State end;		
	}
	
	private class State{
		int number = stateCount;
		char input;
		State next;
		Boolean empty = false;
		Boolean isStart = false;
		Boolean isEnd = false;
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
			if(st.size() <2 && ((input == '+')||(input == '&')) ){
				System.err.println("invalid input: " + line);
				stateCount = 0; //notifies caller of bad input
				break;
			}
			if(input == '&'){//fa1&fa2
				FA fa2 = st.pop();
				FA fa1 = st.pop();
				FA faNew = new FA();
				faNew.stateList.addAll(fa1.stateList);
				State newState = new State();
				newState.number = fa1.end.number;
				newState.input = 'E';
				newState.next.number = fa2.start.number;
				faNew.stateList.add(newState);
				faNew.stateList.addAll(fa2.stateList);
				faNew.start.number = fa1.start.number;
				faNew.end = fa2.end;
				st.push(faNew);
			}else if(input == '+'){//fa1+fa2
				FA fa2 = st.pop();
				FA fa1 = st.pop();
				FA faNew = new FA();
				stateCount++;
				State pre = new State();
				faNew.start = pre;				
				pre.next.number = fa1.start.number;
				pre.input = 'E';
				faNew.stateList.add(pre);
				
				State pre2 = new State();
				pre2.next.number = fa2.start.number;
				pre2.input = 'E';
				faNew.stateList.add(pre2);
				
		
				
			}else if(input == '*'){
				
				
				
				
			}else{
				
				
				
				
			}			
		}
		if(st.size() > 1){		
			System.err.println("invalid input: " + line);
			stateCount = 0;
		}return machine;
		
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
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			int lineNumber = 0;
			while ((line = bufferedReader.readLine()) != null) {
				if(line.isEmpty())continue;
				lineNumber++;
				FAStateGen automata = new FAStateGen(line);
				if(automata.stateCount == 0) continue; //skip past bad input
				String name = "\"FA" + lineNumber + ".txt\"";
				FileOutputStream out = new FileOutputStream(name);
				FAPrinter(automata.getResult());
				out.close();
			}
			fileReader.close();
			
		}catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}


