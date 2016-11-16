import logging
import os
import re


_log = logging.getLogger(__name__)


class DirCrawler:
    @staticmethod
    def crawl(path, regex):
        """
        Crawls the directory given by `path` for files that match `regex`.

        Returns a list of entries.
        """
        _log.info("Crawling dir '%s'" % path)

        if not os.path.exists(path):
            raise ValueError("Source '%s' not found" % path)

        if not os.path.isdir(path):
            raise ValueError("Source '%s' must be a directory" % path)

        entries = []
        r = re.compile(regex)

        for root, dirs, files in os.walk(path):
            dirs = [d for d in dirs if d not in ['obsolete']]

            for f in files:
                if os.path.splitext(f)[1] in ['.gif', '.html']:
                    continue

                p = os.path.join(root, f)
                m = r.search(p)
                if not m:
                    continue

                entries.append({
                    'pdb_id': m.group(1).lower(),
                    'file_path': f,
                    'mtime': os.path.getmtime(p),
                    'comment': None,
                })
        return entries


class FileCrawler:
    @staticmethod
    def crawl(path, regex):
        """
        Crawls the file given by `path` for lines that match `regex`.

        Returns a list of entries.
        """
        _log.info("Crawling file '%s'" % path)

        if not os.path.exists(path):
            raise ValueError("Source '%s' not found" % path)

        entries = []
        r = re.compile(regex)
        with open(path, 'r') as f:
            for line in f.readlines():
                m = r.search(line)
                if not m:
                    continue

                entries.append({
                    'pdb_id': m.group(1).lower(),
                    'file_path': path,
                    'mtime': os.path.getmtime(path),
                    'comment': None,
                })
        return entries


