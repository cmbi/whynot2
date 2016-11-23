import logging
import os
import re

from pymongo import ReplaceOne


_log = logging.getLogger(__name__)


def crawl(databank):
    """
    Crawls the given databank using its crawler.
    """
    _log.info("Crawling %s" % databank['name'])

    # Get the raw entries using the crawler
    Crawler = databank['crawler']
    _log.info("Using '{}'".format(Crawler.__name__))
    entries = Crawler.crawl(databank['source'], databank['regex'])

    # Update the entries in the database in bulk
    _log.info("Updating {} entries in database".format(len(entries)))
    ops = []
    for entry in entries:
        entry['databank_name'] = databank['name']
        ops.append(ReplaceOne({
            'databank_name': databank['name'],
            'pdb_id': entry['pdb_id'],
        }, entry, upsert=True))
    storage.db.entries.bulk_write(ops)

    # Delete entries removed since the last update
    _log.info("Deleting removed entries from database")
    raw_entry_pdb_ids = [e['pdb_id'] for e in entries]
    storage.db.entries.delete_many({
        'databank_name': databank['name'],
        'pdb_id': { '$nin': raw_entry_pdb_ids },
    })


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


