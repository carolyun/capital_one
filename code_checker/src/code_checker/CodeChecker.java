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
//    	System.out.print("\n "+totalLines+"		");
    	int lineCommentPosition;
    	int blockCommentPosition;
		while(line != null) {
			lineCommentPosition = line.indexOf(language.getLineStartLabel());
			blockCommentPosition = line.indexOf(language.getBlockStartLabel());
			if (isInBlock == true) {
				if (line.contains(language.getBlockEndLabel())) {
					line = handleBlockCommentEnd(line);
				} else if (line.contains(language.getBlockMidLabel())) { 
					line = handleBlockComment();
				} else {
					line = null;
				}
			} else  {
				if (lineCommentPosition != -1 && line.contains(language.getLineEndLabel())) {
		    		if (blockCommentPosition != -1 && blockCommentPosition < lineCommentPosition) {
		    			line = handleBlockCommentStart(line);
		    		} else {
		    			line = handleLineComment(line);
		    		}	    	
		    	} else if (blockCommentPosition != -1) {
		    		if (lineCommentPosition != -1 && blockCommentPosition > lineCommentPosition) {
		    			line = handleLineComment(line);
		    		} else {
		    			line = handleBlockCommentStart(line);
		    		}
	    		} else if (line.contains(language.getBlockEndLabel())) {
	    			line = handleBlockCommentEnd(line);
				} else {
					line = null;
				}
			}
		} 
    }
    
    private static String handleBlockCommentStart(String line) {
//    	System.out.print("handleBlockCommentStart 	");
    	isInBlock = true;
		totalCommentLines++;
		totalLinesInBlockComments++;
		totalBlockLineComments++;
		line = substring(line, language.getBlockStartLabel());
		if (line != null && line.contains(language.getBlockEndLabel())) {
			isInBlock = false;
			line = substring(line, language.getBlockEndLabel());
		}
		return line;
    }
    
    private static String handleLineComment(String line) {
//    	System.out.print("handleLineComment 	");
    	totalCommentLines++;
		totalSingleLineComments++;
		if (line.contains("TODO")) {
    		totalTodos++;
    	}
		if (language.getLineEndLabel() == "") {
			return null;
		} else {
			return substring(line, language.getLineEndLabel());
		}
    }
    
    private static String handleBlockCommentEnd(String line) {
//    	System.out.print("handleBlockCommentEnd 	");
    	isInBlock = false;
		totalCommentLines++;
		totalLinesInBlockComments++;
		return substring(line, language.getBlockEndLabel());
    }
    
    private static String handleBlockComment() {
//    	System.out.print("handleBlockComment	");
    	totalCommentLines++;
		totalLinesInBlockComments++;
		return null;
    }
    
    private static String substring(String line, String indicator) {
    	int index = line.indexOf(indicator) + indicator.length();
    	if (index < line.length()) {
    		return line.substring(index);
    	} else {
    		return null;
    	}
    }
}