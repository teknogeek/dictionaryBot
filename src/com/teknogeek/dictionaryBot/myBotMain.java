package com.teknogeek.dictionaryBot;

import org.jibble.pircbot.*;
import com.teknogeek.dictionaryBot.myBot;

public class myBotMain
{
 	public static void main(String[] args) throws Exception
 	{
	  myBot bot = new myBot();      // this starts your bot
	  bot.setVerbose(true);             // enable debugging, useful during programming
	  bot.connect("irc.esper.net");  // connect to your IRC server (fill in your own)
	  bot.joinChannel("#Ocelotworks", "unicornshit");      // join your channel
	  //bot.joinChannel("#teknogeek");			//for testing
 	}
}
