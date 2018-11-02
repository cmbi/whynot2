

class Entry:
    def __init__(self, databank_name, pdbid, status, comment=None):
        self.databank_name = databank_name
        self.pdbid = pdbid
        self.status = status
        self.comment = comment

    def __dict__(self):
        d = {'databank_name': self.databank_name,
             'pdbid': self.pdbid,
             'status': self.status}

        if self.comment is not None:
            d['comment'] = self.comment

        return d
