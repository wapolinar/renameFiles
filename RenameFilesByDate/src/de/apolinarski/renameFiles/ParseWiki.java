package de.apolinarski.renameFiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ParseWiki {

	
	private static final String episodesStart="<h2><span class=\"mw-headline\" id=\"Episodes\">Episodes</span>";
	private static final String seasonsStart_1="<h3><span class=\"mw-headline\" id=";
	private static final String seasonsStart_2="Season ";
	private static final String tableEnds="</table>";
	private static final String endRow="</tr>";
	private static final String startField_1="<td>";
	private static final String startField_2="<td ";
	
	private List<Season> seasons=new ArrayList<Season>();
	
	private static String readTD(String currentLine)
	{
		StringBuilder result=new StringBuilder();
		boolean skip=false;
		for(char c:currentLine.toCharArray())
		{
			if(c=='<')
			{
				skip=true;
			} else if(c=='>')
			{
				skip=false;
				continue;
			}
			if(!skip)
			{
				result.append(c);
			}
		}
		return result.toString().trim();
	}
	
	private static String readHTMLLine(String currentLine)
	{
		return readTD(currentLine.substring(0, currentLine.indexOf("editsection")));
	}
	
	public static ParseWiki parseWiki(Reader wiki) throws IOException
	{
		ParseWiki currentPage=new ParseWiki();
		BufferedReader br=new BufferedReader(wiki);
		boolean episodeStartBool=false;
		boolean seasonStartBool=false;
		int skipRow=0;
		boolean episodeNumberBool=false;
		String currentLine;
		Season currentSeason=null;
		Episode currentEpisode=null;
		while((currentLine=br.readLine())!=null)
		{
			if(!episodeStartBool)
			{
				if(currentLine.contains(episodesStart))
				{
					episodeStartBool=true;
				}
				continue;
			}
			if(!seasonStartBool)
			{
				if(currentLine.contains(seasonsStart_1) && currentLine.contains(seasonsStart_2))
				{
					seasonStartBool=true;
					currentSeason=new Season(readHTMLLine(currentLine));
					skipRow++; //First row contains headers
				}
				continue;
			}
			//Episode & Season start booleans are both TRUE
			if(currentLine.contains(tableEnds))
			{
				currentPage.seasons.add(currentSeason);
				seasonStartBool=false;
				continue;
			}
			if(skipRow>0)
			{
				if(currentLine.contains(endRow))
				{
					skipRow--;
				}
				continue;
			}
			if(currentLine.contains(startField_1) || currentLine.contains(startField_2))
			{
				if(!episodeNumberBool)
				{
					currentEpisode=new Episode();
					currentEpisode.setNumber(readTD(currentLine));
					episodeNumberBool=true;
				}
				else
				{
					if(currentEpisode!=null)
					{
						currentEpisode.setTitle(readTD(currentLine));
						currentSeason.addEpisode(currentEpisode);
					}
					episodeNumberBool=false;
					skipRow+=2;
				}
				continue;
			}
		}
		return currentPage;
	}

	public List<Season> getSeasons() {
		return seasons;
	}
	
	@Override
	public String toString() {
		StringBuilder result=new StringBuilder();
		for(Season s:seasons)
		{
			result.append(s.toString());
		}
		return result.toString();
	}
	
}
