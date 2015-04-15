import java.text.SimpleDateFormat;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;


public class NLP {
    static StanfordCoreNLP pipeline;
    public static boolean isNumeric(String str)  
    {  
      try  
      {  
        double d = Double.parseDouble(str);  
      }  
      catch(NumberFormatException nfe)  
      {  
        return false;  
      }  
      return true;  
    }
	public static String convert(long unix) {
		Date date = new Date(unix); // *1000 is to convert seconds to milliseconds
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
		sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); // give a timezone reference for formating (see comment at the bottom
		String formattedDate = sdf.format(date);
		return formattedDate;
	}

    public static int findSentiment(String tweet) {

        try {
        	 	Properties props = new Properties();
        	    props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		        	int mainSentiment = 0;
		        if (tweet != null && tweet.length() > 0) {
		            int longest = 0;
		            Annotation annotation = pipeline.process(tweet);
//		            System.out.println(annotation);
		            for (CoreMap sentence : annotation
		                    .get(CoreAnnotations.SentencesAnnotation.class)) {
		                Tree tree = sentence
		                        .get(SentimentCoreAnnotations.AnnotatedTree.class);
		                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
		                String partText = sentence.toString();
		                if (partText.length() > longest) {
		                    mainSentiment = sentiment;
		                    longest = partText.length();
		                }
		
		            }
		        }
		        return mainSentiment;
        } catch(NullPointerException e) {
        	System.out.println("nullpointerexception");
        	return 0;
        }
		
    }
    public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("/Users/xiaokaisun/Desktop/test"));
		StringBuilder sb = new StringBuilder();
		String line;
        StringBuilder word = new StringBuilder();
		Map<StringBuilder, Integer> output = new HashMap<StringBuilder, Integer>();
		while ((line = br.readLine()) != null) {
			StringTokenizer tokenizer = new StringTokenizer(line);
			int i = 0;
			int whitePos = 0;
			while (tokenizer.hasMoreTokens()) {
				i++;
				String token=tokenizer.nextToken();
				if(i == 3) {
					long timeNum = Long.parseLong(token);
					String realTime = convert(timeNum);
					String[] timeSplit = realTime.split(" ");
					System.out.println(timeSplit[0]);
				}
	         word.append(token);
	         output.put(word, 1);
	       }
			String[] elements = line.split("\t");
			String tweet = elements[elements.length - 1];
			System.out.println(NLP.findSentiment(tweet));
			
		}
	}
}