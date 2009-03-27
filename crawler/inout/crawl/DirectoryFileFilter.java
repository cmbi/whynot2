package inout.crawl;

import java.io.File;
import java.io.FileFilter;

public class DirectoryFileFilter implements FileFilter {
	private FileFilter excludeFilter;

	public DirectoryFileFilter(FileFilter excludeFilter) {
		this.excludeFilter = excludeFilter;
	}

	public boolean accept(File pathname) {
		return !this.excludeFilter.accept(pathname) && pathname.isDirectory();
	}
}
