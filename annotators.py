class CommentFileAnnotator:
    def __init__(self, comments_dir):
        self._comments_dir = comments_dir

    def annotate(self):
        if not os.path.exists(self._comments_dir):
            raise ValueError("Comments folder '%s' doesn't exist" % comments_dir)

        if not os.path.isdir(self._comments_dir):
            raise ValueError("'%s' is not a folder" % comments_dir)

        entries = []
        for f in os.listdir(self._comments_dir):
            if f.endswith('.txt'):
                p = os.path.join(self._comments_dir, f)
                comments = self._parse_file(p)
                for text, name, pdb_id in comments:
                    entries.append({
                        'databank_name': name,
                        'pdbid': pdb_id.lower(),
                        'comment': text,
                        'mtime': os.path.getmtime(p)
                    })
                os.rename(p, p + '.done')
        return entries

    def _parse_file(self, path):
        d = []
        comment = None
        with open(path, 'r') as f:
            for line in f:
                if line.startswith('COMMENT:'):
                    comment = line[8:].strip()
                elif ',' in line:
                    line = line.strip().replace(' ', '')
                    databank_name, pdb_id = line.split(',')
                    d.append((comment, databank_name, pdb_id))
                elif len(line.strip()) > 0:
                    raise Exception("Invalid format: '%s'" % line)
                else:
                    pass

        return d


class StructureFactorsAnnotator:
    def annotate(databank):
        pass


class NmrAnnotator:
    def annotate(databank):
        pass


class HsspAnnotator:
    def annotate(databank):
        pass


class DsspAnnotator:
    def annotate(databank):
        pass


class BdbAnnotator:
    def annotate(databank):
        pass


class WhatifListAnnotator:
    def annotate(databank):
        pass


class WhatifSceneAnnotator:
    def annotate(databank):
        pass
