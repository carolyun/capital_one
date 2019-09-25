package code_checker;
public class Language {
    private String lineStartLabel;
    private String lineEndLabel;
    private String blockStartLabel;
    private String blockMidLabel;
    private String blockEndLabel;
    
    public String getLineStartLabel() {
    	return this.lineStartLabel;
    }
    
    public String getLineEndLabel() {
    	return this.lineEndLabel;
    }
    
    public String getBlockStartLabel() {
    	return this.blockStartLabel;
    }
    
    public String getBlockMidLabel() {
    	return this.blockMidLabel;
    }
    
    public String getBlockEndLabel() {
    	return this.blockEndLabel;
    }
}