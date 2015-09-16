package de.apolinarski.renameFiles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Season {

	private static final String PART_ONE=", Part I";
	private static final String PART_TWO=", Part II";
	
	private List<Episode> episodes=new ArrayList<Episode>();
	private final String title;
	
	public Season(String title)
	{
		this.title=DateFile.filenameQuoter(title);
	}
	
	public void addEpisode(Episode episode)
	{
		episodes.add(episode);
	}

	public List<Episode> getEpisodes() {
		return episodes;
	}

	public String getTitle() {
		return title;
	}
	
	@Override
	public String toString() {
		StringBuilder result=new StringBuilder();
		result.append('\n');
		result.append(title);
		result.append('\n');
		for(Episode e:episodes)
		{
			result.append(title);
			result.append(' ');
			result.append(e.toString());
			result.append('\n');
		}
		return result.toString();
	}
	
	public String[] getFilenames() {
		List<String> filenames=new ArrayList<String>();
		for(Episode e:episodes)
		{
			filenames.add(e.toString());
		}
		return filenames.toArray(new String[filenames.size()]);
	}
	
	public void mergeParts()
	{
		Iterator<Episode> episodeIterator=episodes.iterator();
		boolean removeNext=false;
		boolean removed=false;
		while(episodeIterator.hasNext())
		{
			Episode currentEpisode=episodeIterator.next();
			if(removeNext)
			{
				episodeIterator.remove();
				removeNext=false;
				removed=true;
				continue;
			}
			if(currentEpisode.getTitle().endsWith(PART_ONE))
			{
				removeNext=true;
				continue;
			}
		}
		if(removed)
		{
			for(int i=0;i<episodes.size();i++)
			{
				if(episodes.get(i).getTitle().endsWith(PART_ONE))
				{
					episodes.set(i, new Episode(episodes.get(i).getTitle().replace(PART_ONE, ""),episodes.get(i).getNumber()));
				}
			}
		}
	}
	
}
