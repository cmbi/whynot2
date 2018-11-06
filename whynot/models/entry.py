from time import time

from whynot.models.status import Status


class Entry:
    def __init__(self, databank_name, pdbid, status, mtime=time(), comment=None):
        self.databank_name = databank_name
        self.pdbid = pdbid
        self.status = status
        self.mtime = mtime
        self.comment = comment

    def __dict__(self):
        d = {'databank_name': self.databank_name,
             'pdbid': self.pdbid,
             'mtime': str(self.mtime),
             'status': str(self.status)}

        if self.comment is not None:
            d['comment'] = self.comment

        return d

    @staticmethod
    def from_dict(d):
        databank_name = d['databank_name']
        pdbid = d['pdbid']
        status = Status.from_string(d['status'])
        mtime = float(d['mtime'])

        if 'comment' in d:
            comment = d['comment']
        else:
            comment = None

        return Entry(databank_name, pdbid, status, mtime, comment)
