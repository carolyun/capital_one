package code_checker;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CodeChecker {
    private static int totalLines = 0;
    private static int totalCommentLines = 0;
    private static int totalSingleLineComments = 0;
    private static int totalBlockLineComments = 0;
    private static int totalLinesInBlockComments = 0;
    private static int totalTodos = 0;
    private static Language language;
    private static Boolean isInBlock = false;

    public static void main(String[] args) throws IOException, org.json.simple.parser.ParseException {
        if (args[0] == null) {
            throw new IllegalArgumentException();
        }
        String extension = args[0].substring(args[0].lastIndexOf(".") + 1);
    	JSONParser jsonParser = new JSONParser();
        try {
            FileReader reader = new FileReader("src/code_checker/language.json");
            Object languagesJson = jsonParser.parse(reader);
            JSONObject languages = (JSONObject) languagesJson;
            ObjectMapper objectMapper = new ObjectMapper();
            language = objectMapper.readValue(languages.get(extension).toString(), Language.class); 
            runChecker(language, args[0]);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
        System.out.print(String.format("Total # of lines: %s \n", totalLines));  
        System.out.print(String.format("Total # of comment lines: %s \n", totalCommentLines));  
        System.out.print(String.format("Total # of single line comments: %s \n", totalSingleLineComments));
        System.out.print(String.format("Total # of comment lines within block comments: %s \n", totalLinesInBlockComments));  
        System.out.print(String.format("Total # of block line comments: %s \n", totalBlockLineComments));  
        System.out.print(String.format("Total # of TODO's: %s \n", totalTodos));
    }
    
    private static void runChecker(Language language, String filename) throws IOException {
    	BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine())!= null) {
                analyzeLine(language, line);
            }
            reader.close();
        } catch (IOException e) {
        	e.printStackTrace();
            throw new IOException();
        }
    }

    private static void analyzeLine(Language language, String line) {
    	totalLines++;
    	if (line.contains(language.getLineStartLabel())) {
    		totalCommentLines++;
    		totalSingleLineComments++;
    		if (line.contains("TODO")) {
        		totalTodos++;
        	}
    	} else if (isInBlock == false && line.contains(language.getBlockStartLabel())) {
    		isInBlock = true;
    		totalCommentLines++;
    		totalLinesInBlockComments++;
    		totalBlockLineComments++;
    	} else if (isInBlock == true && line.contains(language.getBlockEndLabel())) {  
    		isInBlock = false;
    		totalCommentLines++;
    		totalLinesInBlockComments++;
    	} else if ( isInBlock == true) {	
    		if ((language.getBlockMidLabel() != null && line.contains(language.getBlockMidLabel())) || language.getBlockMidLabel() == null) { 
    			totalCommentLines++;
	    		totalLinesInBlockComments++;
    		}
    	}
    }
}