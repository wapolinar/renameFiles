package de.apolinarski.renameFiles;

import java.io.File;

public class DateFile implements Comparable<DateFile> {

	private final long timestamp;
	private final File file;
	
	public DateFile(File file)
	{
		this.file=file;
		this.timestamp=file.lastModified();
	}
	
	@Override
	public int compareTo(DateFile remoteCompare) {
		long compareTimestamp = remoteCompare.timestamp;
        return timestamp < compareTimestamp ? -1 : timestamp == compareTimestamp ? 0 : 1;
	}

	public File getFile() {
		return file;
	}
	
	public boolean renameFile(File newName) {
		return file.renameTo(newName);
	}

	public static String filenameQuoter(String filename)
	{
		String result=filename;
		result=result.replace('/', '_').replace('\\', '_').replace(":","").replace('?', '_').replace('*', '_').replace('%', '_').replace('|', '_').replace("\"", "").replace('<','_').replace('>', '_');
		return result;
	}
	
}
