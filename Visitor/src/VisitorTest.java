import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ca.uqac.lif.bullwinkle.ParseNode;
import ca.uqac.lif.bullwinkle.output.OutputFormatVisitor;


public class VisitorTest implements OutputFormatVisitor {

	//need one visit method which can get all the information for all the different existing processors
	//how to do that? TODO
	
	String name, parameter;
	
	int size = 0;
	Boolean options = false, file = false, after_name = false;
	Boolean params = false, trace = false;
	ArrayList<String> opts = new ArrayList<String>();
	Map<String, String> map;
	
	public VisitorTest() {
		map = new LinkedHashMap<String, String>();
	}
	
	//TODO in visit(), how to create new processor when output is processor
	
	public void fillMap() {
		String processor_non_terminal = null, processor_shortname = null;	
		try {
			BufferedReader reader = new BufferedReader(new FileReader("C:/Users/Gabrielle/github/BeepBeep2/Grammar_Beepbeep2/processorsWithShortnames"));
			while (true) {
				processor_non_terminal = reader.readLine();
				if (processor_non_terminal == null) 
					break;
				processor_shortname = reader.readLine();
				map.put(processor_non_terminal, processor_shortname);
			}
			reader.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
			 
	}
	
	public void visit(ParseNode node) {
		final String CLASS_PATH = "ca.uqac.lif.beepbeep2.";
		
		String token = node.getToken();
		
		//later : at every new grammar addition, put new <shortname> and SHORTNAME in map
		//getProcessor() from processorFactory ?		
		
		List<ParseNode> ChildrenList = null;
		ChildrenList = node.getChildren();
		
		if (map.containsKey(token)) { //is a new processor, so can get the name
			name = CLASS_PATH + map.get(token).toLowerCase().replaceAll("\\s","");
			System.out.println(name);		
			after_name = true;
		}
		if (after_name) {
			if (!ChildrenList.isEmpty()) {
				if (token.contains("_opt")) { //children will be options
					size = node.getSize();
					options = true;
				}
				else {
					params = true;
				}
				after_name = false;
			}
		}
		//add options into arrayList if there are some to add
		if (size >= 0 && options) {
			if (ChildrenList.isEmpty()) { //build arrayList of String options, should later match All of, <num> of, At <num> per sec...
				opts.add(token); //TODO build the options. Here they are separated by words.
				System.out.println(token);
			}
			size--;
			if (size == 0) { 
				options = false;
				params = true; //after options, there are the parameters (event, filename or processor)
			}
		}
		
		
		if (params) {
			if (token.equals(".")) {
				trace = false;
				file = false;
				params = false;
			}
			if (token.equals("<trace>")) {
				trace = true;
			}
			else if (token.equals("<filename>")) {
				file = true;
			}
			else if (token.contains("processor")) {
				//TODO start building a new processor
				parameter = token + ".output()";
				if (!opts.isEmpty()) {
					System.out.print("getProcessor(" + name + ", " + opts + ", " + parameter + ")" + "\n");
				}
				else {
					System.out.print("getProcessor(" + name + ", " + parameter + ")" + "\n");
				}
				
				//1stprocessor = getProcessor(name, opts, secondprocessor.output());
				opts.clear();
				name = null;
				parameter = null;
				params = false;
			}
			if (trace) {
				if (ChildrenList.isEmpty()) {
					parameter = token;
					// getProcessor("name", token);
					// OR
					// String parameter = token; //and pass it later to processor
					// could be an arrayList of parameters if there are 2 inputs or more
				}
			} 
			if (file) {
				if (ChildrenList.isEmpty()) {
					//Get filename into String and pass to processor?
					parameter = token;
					System.out.println(token);
					file = false;
				}
			}
			
		}
		
	}
	
	public void pop() {
		// TODO Auto-generated method stub

	}

	public String toOutputString() {
		// TODO Auto-generated method stub
		return null;
	}

}
