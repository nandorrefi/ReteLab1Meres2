package hu.bme.mit.yakindu.analysis.workhere;import java.io.IOException;
import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GeneratedCode {
public static void main(String[] args) throws IOException {
BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
ExampleStatemachine s = new ExampleStatemachine();
s.setTimer(new TimerService());
RuntimeService.getInstance().registerStatemachine(s, 200);
s.init();
s.enter();
s.runCycle();
while(true) {
String input = reader.readLine();
executeCommand(input, s);
print(s);
}
}

public static void executeCommand(String input, IExampleStatemachine s) {
if(input.compareTo("start")==0) {
s.getSCInterface().raiseStart();
}
else if(input.compareTo("white")==0) {
s.getSCInterface().raiseWhite();
}
else if(input.compareTo("black")==0) {
s.getSCInterface().raiseBlack();
}
else if(input.compareTo("resign")==0) {
s.getSCInterface().raiseResign();
}
else if(input.compareTo("exit")==0) {
System.exit(0);
}
s.runCycle();
}

public static void print(IExampleStatemachine s){
System.out.println("randomVar = " + s.getSCInterface().getRandomVar());
System.out.println("whiteTime = " + s.getSCInterface().getWhiteTime());
System.out.println("blackTime = " + s.getSCInterface().getBlackTime());
}
}