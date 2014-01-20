package com.teknogeek.dictionaryBot;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import org.jibble.pircbot.PircBot;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;

public class myBot extends PircBot
{
	public IWordID wordID;
	public IWord word;
	public IIndexWord idxWord;
	public String glossary, wordToDefine;
	public String[] UDResults;
	public boolean wordFound;
	
	public myBot()
	{
		this.setName("Dictionary");
	}
	
	public void onMessage(String channel, String sender, String login, String hostname, String message)
	{
		Pattern commandBase = Pattern.compile("^\\!define");
		Matcher parameter = commandBase.matcher(message);
		if (parameter.find())
		{
			String wordToDefine = new String("");
			if (message.length() > 8)
			{
				wordToDefine = message.substring(8);
				try {
					lookupWord(wordToDefine);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if(wordFound)
				{
					sendMessage(channel, wordToDefine + ": " + glossary);
				}
				else
				{
					sendMessage(channel, glossary);
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	public void lookupWord(String wordToLookup) throws IOException
	{
		// construct the URL to the Wordnet dictionary directory
		String wnhome = "WordNet";
		String path = wnhome + File.separator + "dict";
		File file = null;
		file = new File(path);
		if(file == null) return;

		// construct the dictionary object and open it
		IDictionary dict = new Dictionary(file);
		dict.open();
		
		// look up first sense of the word
		idxWord = dict.getIndexWord(wordToLookup, POS.NOUN);
		try {
			wordID = idxWord.getWordIDs().get(0);
		} catch(NullPointerException e) {
			int i = 0;
	 		UDResults = new String[10]; 
			
	 		String replaceString = wordToLookup.replace(" ", "+");
	 		System.out.println(replaceString);
	 		
			URL url = new URL("http://api.urbandictionary.com/v0/define?term=" + wordToLookup.replace(" ", "+"));
			try (InputStream is = url.openStream();
				JsonParser parser = Json.createParser(is))
				{
					while (parser.hasNext())
					{
						Event e1 = parser.next();
						if (e1 == Event.KEY_NAME)
						{
							switch (parser.getString())
							{
							 	case "definition":
							 		parser.next();
							 		UDResults[i] = parser.getString();
							 		i += 1;
							 		break;
							}
						}
					}
				}
				glossary = UDResults[0];
				wordFound = true;
				return;
			}
		
			try {
				word = dict.getWord(wordID);
			} catch(NullPointerException e2) {
				
			}
			
			glossary = word.getSynset().getGloss();
			String[] parts = glossary.split(";");
			glossary = parts[0];
			wordFound = true;
			return;
		}
	}