package de.apolinarski.renameFiles;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class RenameMain {
	
	private static final String ACCEPTED_FILE_TYPE=".mp4";
	private static final long FILE_LENGTH_HEURISTIC=250L*1024L*1024L; //250MB

	public static void main(String[] args) throws IOException, InterruptedException {
		if(args.length<2)
		{
			System.out.println("Usage: RenameMain <WIKITEXT_FILENAME> <DIRECTORY_OF_VIDEO_FILES>");
			System.out.println("Add -f to force renaming.");
			System.out.println("Add -m to merge parts.");
			return;
		}
		boolean force=false;
		boolean merge=false;
		if(args.length>2)
		{
			for(int i=2;i<args.length;i++)
			{
				if(args[i].equals("-f"))
				{
					force=true;
				}
				else if(args[i].equals("-m"))
				{
					merge=true;
				}
			}
		}
		Reader fileReader=new InputStreamReader(new FileInputStream(args[0]),"UTF-8");
		File mp4Directory=new File(args[1]);
		if(!mp4Directory.isDirectory())
		{
			System.out.println(args[1]+" is not a directoy!");
			fileReader.close();
			return;
		}
		File[] videoFiles=mp4Directory.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if(pathname.isDirectory())
				{
					return false;
				}
				if(pathname.getName().endsWith(ACCEPTED_FILE_TYPE) && pathname.length()>FILE_LENGTH_HEURISTIC)
				{
					return true;
				}
				return false;
			}
		});
		
		DateFile[] sortedVideoFiles=new DateFile[videoFiles.length];
		
		for(int i=0;i<videoFiles.length;i++)
		{
			sortedVideoFiles[i]=new DateFile(videoFiles[i]);
		}
		
		Arrays.sort(sortedVideoFiles);
		
		ParseWiki pw=ParseWiki.parseWiki(fileReader);
		fileReader.close();
		List<Season> seasons=pw.getSeasons();
		String[][] episodeName=new String[seasons.size()][];
		int numberOfEpisodes=0;
		for(int i=0;i<seasons.size();i++)
		{
			if(merge)
			{
				seasons.get(i).mergeParts();
			}
			episodeName[i]=seasons.get(i).getFilenames();
			numberOfEpisodes+=episodeName[i].length;
		}
		System.out.println(pw);
		System.out.println("Number of files: "+sortedVideoFiles.length);
		System.out.println("Number of episodes: "+numberOfEpisodes);
		if(!force && numberOfEpisodes!=sortedVideoFiles.length)
		{
			System.out.println("Number mismatch, aborting!");
			return;
		}
		System.out.println("Waiting 5 seconds before renaming files");
		Thread.sleep(5000);
		
		
		int i=0;
RENAMING:	for(int j=0;j<episodeName.length;j++)
			{
				File seasonName=new File(seasons.get(j).getTitle());
				if(seasonName.exists())
				{
					System.out.println("Directory: "+seasonName+" does already exist");
					if(!seasonName.isDirectory())
					{
						System.out.println("It is not a directory, aborting!");
						return;
					}
				}
				else
				{
					seasonName.mkdir();
				}
				for(int k=0;k<episodeName[j].length;k++)
				{
					File newFileName=new File(seasonName,episodeName[j][k]);
					Files.createLink(newFileName.toPath(), videoFiles[i].toPath());
					i++;
					if(i>=videoFiles.length)
					{
						break RENAMING;
					}
				}
			}
	
	}

}
